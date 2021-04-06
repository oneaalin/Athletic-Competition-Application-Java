package chat.persistence;

import chat.model.Child;

public interface IChildRepository extends IRepository<Long, Child>{

    public Child findByProperties(String name,int age);

}
