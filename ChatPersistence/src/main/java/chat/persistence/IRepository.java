package chat.persistence;

import chat.model.Entity;

public interface IRepository <ID,E extends Entity<ID>>{

    /**
     * returns an entity with the specified id
     * @param id - the id of the entity to be returned , id must not be null
     * @return the entity with the specified id or null (if there is no entity with the given id)
     */
    E findOne(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity , entity must not be null
     * @return null if the given entity is saved , otherwise returns the entity(id already exists)
     */
    E save(E entity);

    /**
     *
     * @param id , id must not ebe null
     * @return the removed entity or null if there is no entity with the given id
     */
    E delete(ID id);

    /**
     *
     * @param entity , entity must not be null
     * @return null - if the entity is updated , otherwise returns the entity(id does not exist)
     */
    E update(E entity);

    /**
     * return how many entities are in the repository
     * @return int
     */
    public int count();

    /**
     * check if there is any entity with the specified id
     * @param id the id
     * @return true if it exists , false otherwise
     */
    public boolean exists(ID id);
}