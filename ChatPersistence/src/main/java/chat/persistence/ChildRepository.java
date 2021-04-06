package chat.persistence;

import chat.model.Child;
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

public class ChildRepository implements IChildRepository{

    private Jdbc dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public ChildRepository(Properties properties){
        logger.info("Initializing Child Repository with properties: {}",properties);
        dbUtils = new Jdbc(properties);
    }

    @Override
    public Child findByProperties(String name, int age) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Child child = new Child("0",0);
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Child where name = ? and age = ?")){
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    child.setName(name);
                    child.setAge(age);
                    Long id = Long.parseLong(Integer.toString(result.getInt("id")));
                    child.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(child);
        return child;

    }

    @Override
    public Child findOne(Long id) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Child child = new Child("",0);
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Child where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    child.setName(name);
                    child.setAge(age);
                    child.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(child);
        return child;

    }


    @Override
    public Iterable<Child> findAll() {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Child> children = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Child")){
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()){
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    Child child = new Child(name,age);
                    child.setId(id);
                    children.add(child);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(children);
        return children;

    }

    @Override
    public Child save(Child child) {

        logger.traceEntry("Saving Child {} ",child);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into Child (name,age) values (?,?)")){
            preparedStatement.setString(1,child.getName());
            preparedStatement.setInt(2,child.getAge());
            int result = preparedStatement.executeUpdate();
            if(result == 1)
                return null;
            logger.trace("Saved {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return child;
    }

    @Override
    public Child delete(Long id) {

        logger.traceEntry("Deleting Child with id {}",id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from Child where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            Child child = findOne(id);
            int result  = preparedStatement.executeUpdate();
            if(result == 1)
                return child;
            logger.trace("Deleted {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Child update(Child child) {

        logger.traceEntry("Update Child {} ",child);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("update Child set name = ? , age = ? where id = ?")){
            preparedStatement.setString(1,child.getName());
            preparedStatement.setInt(2,child.getAge());
            preparedStatement.setInt(3,child.getId().intValue());
            int result = preparedStatement.executeUpdate();
            if(result != 1)
                return child;
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
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count(id) as contor from Child")){
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted children");
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
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Child where id = ?")){
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
