package demo.repository;

import demo.model.Actor;
import java.util.List;
import java.util.Optional;


public interface ActorRepository {
    public List<Actor> findAll();
    public Optional<Actor> findById(int id);
    public int insert(Actor actor);
    public int update(int id, Actor actor);
    public int delete(int id);
}
