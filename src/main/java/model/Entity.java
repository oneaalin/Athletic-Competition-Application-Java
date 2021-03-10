package model;

public abstract class Entity<ID> {

    private ID id;

    /**
     * get id
     * @return id ID
     */
    public ID getId() {
        return id;
    }

    /**
     * set id
     * @param id ID
     */
    public void setId(ID id) {
        this.id = id;
    }
}
