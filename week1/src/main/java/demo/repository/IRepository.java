package demo.repository;

import demo.model.Actor;
import demo.model.Film;
import java.util.List;
import java.util.Optional;


public interface IRepository {
    // actor
    public List<Actor> findAll();
    public Optional<Actor> findById(int id);
    public int insert(Actor actor);
    public int update(int id, Actor actor);
    public int delete(int id);

    // film
//    public List<Film> findAll();
//    public Optional<Film> findById(int id);
//    public int insert(Film actor);
//    public int update(int id, Film actor);
//    public int delete(int id);
}
