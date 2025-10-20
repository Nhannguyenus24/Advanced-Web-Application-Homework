package demo.repository;

import demo.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RepositoryImpl implements IRepository {
    private final JdbcTemplate jdbc;

    public RepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Actor> ACTOR_ROW_MAPPER = (rs, rowNum) -> {
        Actor a = new Actor();
        a.setActorId(rs.getInt("actor_id"));
        a.setFirstName(rs.getString("first_name"));
        a.setLastName(rs.getString("last_name"));
        LocalDateTime lu = rs.getObject("last_update", LocalDateTime.class);
        a.setLastUpdate(lu);
        return a;
    };

    @Override
    public List<Actor> findAllActors() {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor ORDER BY actor_id";
        return jdbc.query(sql, ACTOR_ROW_MAPPER);
    }

    @Override
    public Optional<Actor> findActorById(int id) {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor WHERE actor_id = ?";
        List<Actor> list = jdbc.query(sql, ACTOR_ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public int insertActor(Actor actor) {
        String sql = "INSERT INTO actor(first_name, last_name) VALUES(?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, actor.getFirstName());
            ps.setString(2, actor.getLastName());
            return ps;
        }, kh);
        Number key = kh.getKey();
        return key != null ? key.intValue() : 0;
    }

    @Override
    public int updateActorById(int id, Actor actor) {
        String sql = "UPDATE actor SET first_name = ?, last_name = ? WHERE actor_id = ?";
        return jdbc.update(sql, actor.getFirstName(), actor.getLastName(), id);
    }

    @Override
    public int deleteActorById(int id) {
        String sql = "DELETE FROM actor WHERE actor_id = ?";
        return jdbc.update(sql, id);
    }

    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> {
        Film f = new Film();
        f.setFilmId(rs.getInt("film_id"));
        f.setTitle(rs.getString("title"));
        f.setDescription(rs.getString("description"));

        Short ry = (Short) rs.getObject("release_year", Short.class);
        f.setReleaseYear(ry == null ? 0 : ry);

        f.setLanguageId(rs.getShort("language_id"));

        Short oly = (Short) rs.getObject("original_language_id", Short.class);
        f.setOriginalLanguageId(oly == null ? (short) 0 : oly);

        f.setRentalDuration(rs.getShort("rental_duration"));
        f.setRentalRate(rs.getDouble("rental_rate"));

        Integer len = (Integer) rs.getObject("length", Integer.class);
        f.setLength(len == null ? 0 : len);

        f.setReplacementCost(rs.getDouble("replacement_cost"));

        String ratingStr = rs.getString("rating"); // e.g. "PG-13"
        f.setRating(mapRatingFromDb(ratingStr));

        String features = rs.getString("special_features"); // e.g. "Trailers,Deleted Scenes"
        f.setSpecialFeatures(parseSpecialFeatures(features));

        LocalDateTime lu = rs.getObject("last_update", LocalDateTime.class);
        f.setLastUpdate(lu);
        return f;
    };

    @Override
    public List<Film> findAllFilms() {
        String sql = """
            SELECT film_id, title, description, release_year, language_id, original_language_id,
                   rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update
            FROM film
            ORDER BY film_id
            """;
        return jdbc.query(sql, FILM_ROW_MAPPER);
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = """
            SELECT film_id, title, description, release_year, language_id, original_language_id,
                   rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update
            FROM film
            WHERE film_id = ?
            """;
        List<Film> list = jdbc.query(sql, FILM_ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public int insertFilm(Film film) {
        String sql = """
            INSERT INTO film
            (title, description, release_year, language_id, original_language_id,
             rental_duration, rental_rate, length, replacement_cost, rating, special_features)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setString(i++, film.getTitle());
            ps.setString(i++, film.getDescription());

            if (film.getReleaseYear() == null) {
                ps.setNull(i++, Types.SMALLINT);
            } else {
                ps.setShort(i++, film.getReleaseYear());
            }

            ps.setShort(i++, film.getLanguageId());

            if (film.getOriginalLanguageId() == null) {
                ps.setNull(i++, Types.TINYINT);
            } else {
                ps.setShort(i++, film.getOriginalLanguageId());
            }

            ps.setShort(i++, film.getRentalDuration());
            ps.setDouble(i++, film.getRentalRate());

            if (film.getLength() == null) {
                ps.setNull(i++, Types.SMALLINT);
            } else {
                ps.setInt(i++, film.getLength());
            }

            ps.setDouble(i++, film.getReplacementCost());

            // rating enum string (e.g. "PG-13")
            ps.setString(i++, film.getRating() == null ? "G" : film.getRating().getLabel());

            // special_features as CSV by label
            String features = specialFeaturesToDb(film.getSpecialFeatures());
            if (features == null) {
                ps.setNull(i++, Types.VARCHAR);
            } else {
                ps.setString(i++, features);
            }

            return ps;
        }, kh);

        Number key = kh.getKey();
        return key != null ? key.intValue() : 0;
    }

    @Override
    public int updateFilmById(int id, Film film) {
        String sql = """
            UPDATE film
            SET title = ?,
                description = ?,
                release_year = ?,
                language_id = ?,
                original_language_id = ?,
                rental_duration = ?,
                rental_rate = ?,
                length = ?,
                replacement_cost = ?,
                rating = ?,
                special_features = ?
            WHERE film_id = ?
            """;

        return jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, film.getTitle());
            ps.setString(i++, film.getDescription());

            if (film.getReleaseYear() == null) {
                ps.setNull(i++, Types.SMALLINT);
            } else {
                ps.setShort(i++, film.getReleaseYear());
            }

            ps.setShort(i++, film.getLanguageId());

            if (film.getOriginalLanguageId() == null) {
                ps.setNull(i++, Types.TINYINT);
            } else {
                ps.setShort(i++, film.getOriginalLanguageId());
            }

            ps.setShort(i++, film.getRentalDuration());
            ps.setDouble(i++, film.getRentalRate());

            if (film.getLength() == null) {
                ps.setNull(i++, Types.SMALLINT);
            } else {
                ps.setInt(i++, film.getLength());
            }

            ps.setDouble(i++, film.getReplacementCost());

            ps.setString(i++, film.getRating() == null ? "G" : film.getRating().getLabel());

            String features = specialFeaturesToDb(film.getSpecialFeatures());
            if (features == null) {
                ps.setNull(i++, Types.VARCHAR);
            } else {
                ps.setString(i++, features);
            }

            ps.setInt(i, id);
            return ps;
        });
    }

    @Override
    public int deleteFilmById(int id) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        return jdbc.update(sql, id);
    }

    private static Film.Rating mapRatingFromDb(String label) {
        if (label == null) return Film.Rating.G;
        switch (label) {
            case "G": return Film.Rating.G;
            case "PG": return Film.Rating.PG;
            case "PG-13": return Film.Rating.PG_13;
            case "R": return Film.Rating.R;
            case "NC-17": return Film.Rating.NC_17;
            default: return Film.Rating.G; // fallback
        }
    }

    private static Set<Film.SpecialFeature> parseSpecialFeatures(String csv) {
        if (csv == null || csv.isBlank()) return null; // tương ứng NULL trong DB
        String[] parts = csv.split("\\s*,\\s*");
        Set<Film.SpecialFeature> set = new LinkedHashSet<>();
        for (String p : parts) {
            switch (p) {
                case "Trailers": set.add(Film.SpecialFeature.TRAILERS); break;
                case "Commentaries": set.add(Film.SpecialFeature.COMMENTARIES); break;
                case "Deleted Scenes": set.add(Film.SpecialFeature.DELETED_SCENES); break;
                case "Behind the Scenes": set.add(Film.SpecialFeature.BEHIND_THE_SCENES); break;
                default: /* ignore unknown */ break;
            }
        }
        return set.isEmpty() ? null : set;
    }

    private static String specialFeaturesToDb(Set<Film.SpecialFeature> features) {
        if (features == null || features.isEmpty()) return null;
        return features.stream()
                .map(f -> {
                    switch (f) {
                        case TRAILERS: return "Trailers";
                        case COMMENTARIES: return "Commentaries";
                        case DELETED_SCENES: return "Deleted Scenes";
                        case BEHIND_THE_SCENES: return "Behind the Scenes";
                        default: return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));
    }
}
