package demo.repository;

import demo.model.Actor;
import demo.model.Film;
import java.util.List;
import java.util.Optional;


public interface IRepository {
    // actor
    public List<Actor> findAllActors();
    public Optional<Actor> findActorById(int id);
    public int insertActor(Actor actor);
    public int updateActorById(int id, Actor actor);
    public int deleteActorById(int id);

    // film
    public List<Film> findAllFilms();
    public Optional<Film> findFilmById(int id);
    public int insertFilm(Film film);
    public int updateFilmById(int id, Film film);
    public int deleteFilmById(int id);
}
