package repository;

import model.Child;

public interface IChildRepository extends IRepository<Long, Child>{

    public Child findByProperties(String name,int age);

}
