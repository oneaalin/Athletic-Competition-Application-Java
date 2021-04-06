package chat.model;

import java.io.Serializable;

public abstract class Entity<ID> implements Serializable {

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

