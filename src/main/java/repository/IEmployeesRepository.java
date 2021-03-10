package repository;

import model.Employee;

public interface IEmployeesRepository extends IRepository<Long, Employee> {
    /**
     * finds an employee by a given username
     * @param username the username of an employee
     * @return Employee
     */
    Employee findByUsername(String username);
}
