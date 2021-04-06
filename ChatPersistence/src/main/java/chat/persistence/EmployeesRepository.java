package chat.persistence;

import chat.model.Employee;
import chat.persistence.utils.Jdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmployeesRepository implements IEmployeesRepository{

    private Jdbc dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public EmployeesRepository(Properties properties){
        logger.info("Initializing Employees Repository with properties: {}",properties);
        dbUtils = new Jdbc(properties);
    }

    @Override
    public Employee findByUsername(String username) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Employee employee = new Employee("","");
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Employees where username = ?")){
            preparedStatement.setString(1,username);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    String password = result.getString("password");
                    employee.setUsername(username);
                    employee.setPassword(password);
                    Long id = Long.parseLong(Integer.toString(result.getInt("id")));
                    employee.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(employee);
        return employee;

    }

    @Override
    public Employee findOne(Long id) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Employee employee = new Employee("","");
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Employees where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    String username = result.getString("username");
                    String password = result.getString("password");
                    employee.setUsername(username);
                    employee.setPassword(password);
                    employee.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(employee);
        return employee;

    }

    @Override
    public Iterable<Employee> findAll() {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Employee> employees = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Employees")){
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()){
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    Employee employee = new Employee(username,password);
                    employee.setId(id);
                    employees.add(employee);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(employees);
        return employees;

    }

    @Override
    public Employee save(Employee employee) {

        logger.traceEntry("Saving Employee {} ",employee);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into Employees (username,password) values (?,?)")){
            preparedStatement.setString(1,employee.getUsername());
            preparedStatement.setString(2,employee.getPassword());
            int result = preparedStatement.executeUpdate();
            if(result == 1)
                return null;
            logger.trace("Saved {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return employee;

    }

    @Override
    public Employee delete(Long id) {

        logger.traceEntry("Deleting Employee with id {}",id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from Employees where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            Employee employee = findOne(id);
            int result  = preparedStatement.executeUpdate();
            if(result == 1)
                return employee;
            logger.trace("Deleted {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;

    }

    @Override
    public Employee update(Employee employee) {

        logger.traceEntry("Update Employee {} ",employee);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("update Employees set username = ? , password = ? where id = ?")){
            preparedStatement.setString(1,employee.getUsername());
            preparedStatement.setString(2,employee.getPassword());
            preparedStatement.setInt(3,employee.getId().intValue());
            int result = preparedStatement.executeUpdate();
            if(result != 1)
                return employee;
            logger.trace("Updates {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;

    }

    @Override
    public int count() {

        logger.traceEntry("Counting ...");
        Connection connection = dbUtils.getConnection();
        int contor = 0;
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count(id) as contor from Employees")){
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted employees");
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(contor);
        return contor;

    }

    @Override
    public boolean exists(Long id) {

        logger.traceEntry("Verifing ... ");
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Employees where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            try(ResultSet result = preparedStatement.executeQuery()) {
                if(!result.isClosed()) {
                    result.next();
                    if (result.getInt("id") == id.intValue())
                        return true;
                    logger.trace("Existence");
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return false;

    }
}
