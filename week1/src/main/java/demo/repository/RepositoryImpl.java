package demo.repository;

import demo.model.Actor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public List<Actor> findAll() {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor ORDER BY actor_id";
        return jdbc.query(sql, ACTOR_ROW_MAPPER);
    }

    @Override
    public Optional<Actor> findById(int id) {
        String sql = "SELECT actor_id, first_name, last_name, last_update FROM actor WHERE actor_id = ?";
        List<Actor> list = jdbc.query(sql, ACTOR_ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public int insert(Actor actor) {
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
    public int update(int id, Actor actor) {
        String sql = "UPDATE actor SET first_name = ?, last_name = ? WHERE actor_id = ?";
        return jdbc.update(sql, actor.getFirstName(), actor.getLastName(), id);
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM actor WHERE actor_id = ?";
        return jdbc.update(sql, id);
    }
}
