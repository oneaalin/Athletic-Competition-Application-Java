package chat.persistence;

import chat.model.Challenge;
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

public class ChallengeRepository implements IChallengeRepository{

    private Jdbc dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public ChallengeRepository(Properties properties){
        logger.info("Initializing Challenge Repository with properties: {}",properties);
        dbUtils = new Jdbc(properties);
    }

    @Override
    public Challenge findByProperties(int minimumAge, int maximumAge, String name) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Challenge challenge = new Challenge(0,0,"");
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Challenge where minimum_age = ? and maximum_age = ? and name = ?")){
            preparedStatement.setInt(1,minimumAge);
            preparedStatement.setInt(2,maximumAge);
            preparedStatement.setString(3,name);
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    challenge.setMinimumAge(minimumAge);
                    challenge.setMaximumAge(maximumAge);
                    challenge.setName(name);
                    Long id = Long.parseLong(Integer.toString(result.getInt("id")));
                    challenge.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(challenge);
        return challenge;

    }

    @Override
    public Challenge findOne(Long id) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Challenge challenge = new Challenge(0,0,"");
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Challenge where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    int minimumAge = result.getInt("minimum_age");
                    int maximumAge = result.getInt("maximum_age");
                    String name = result.getString("name");
                    challenge.setMinimumAge(minimumAge);
                    challenge.setMaximumAge(maximumAge);
                    challenge.setName(name);
                    challenge.setId(id);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(challenge);
        return challenge;

    }

    @Override
    public Iterable<Challenge> findAll() {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Challenge> challenges = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Challenge")){
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()){
                    Long id = result.getLong("id");
                    int minimumAge = result.getInt("minimum_age");
                    int maximumAge = result.getInt("maximum_age");
                    String name = result.getString("name");
                    Challenge challenge = new Challenge(minimumAge,maximumAge,name);
                    challenge.setId(id);
                    challenges.add(challenge);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(challenges);
        return challenges;

    }

    @Override
    public Challenge save(Challenge challenge) {

        logger.traceEntry("Saving Challenge {} ",challenge);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into Challenge (minimum_age,maximum_age,name) values (?,?,?)")){
            preparedStatement.setInt(1,challenge.getMinimumAge());
            preparedStatement.setInt(2,challenge.getMaximumAge());
            preparedStatement.setString(3,challenge.getName());
            int result = preparedStatement.executeUpdate();
            if(result == 1)
                return null;
            logger.trace("Saved {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return challenge;

    }

    @Override
    public Challenge delete(Long id) {

        logger.traceEntry("Deleting Challenge with id {}",id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from Challenge where id = ?")){
            preparedStatement.setInt(1,id.intValue());
            Challenge challenge = findOne(id);
            int result  = preparedStatement.executeUpdate();
            if(result == 1)
                return challenge;
            logger.trace("Deleted {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;

    }

    @Override
    public Challenge update(Challenge challenge) {

        logger.traceEntry("Update Challenge {} ",challenge);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("update Challenge set minimum_age = ? , maximum_age = ? , name = ? where id = ?")){
            preparedStatement.setInt(1,challenge.getMinimumAge());
            preparedStatement.setInt(2,challenge.getMaximumAge());
            preparedStatement.setString(3, challenge.getName());
            preparedStatement.setInt(4,challenge.getId().intValue());
            int result = preparedStatement.executeUpdate();
            if(result != 1)
                return challenge;
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
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count(id) as contor from Challenge")){
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted challenges");
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
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Challenge where id = ?")){
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
