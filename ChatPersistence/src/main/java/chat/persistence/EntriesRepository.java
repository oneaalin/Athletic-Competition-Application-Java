package chat.persistence;

import chat.model.Challenge;
import chat.model.Child;
import chat.model.Entry;
import chat.model.Tuple;
import chat.persistence.utils.Jdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EntriesRepository implements IEntriesRepository{

    private Jdbc dbUtils;
    private IChildRepository childRepo;
    private IChallengeRepository challengeRepo;

    private static final Logger logger = LogManager.getLogger();

    public EntriesRepository(Properties properties, IChildRepository childRepo, IChallengeRepository challengeRepo){
        logger.info("Initializing Entries Repository with properties: {}",properties);
        dbUtils = new Jdbc(properties);
        this.childRepo = childRepo;
        this.challengeRepo = challengeRepo;
    }

    @Override
    public int findChildNumber(Long cid) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        int contor = 0;
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count() as contor from Entries where cid = ?")){
            preparedStatement.setInt(1,cid.intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted children that participates at the challenge with id {} ",cid);
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(contor);
        return contor;

    }

    @Override
    public int findChallengeNumber(Long kid) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        int contor = 0;
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count() as contor from Entries where kid = ?")){
            preparedStatement.setInt(1,kid.intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted the number of challenges of the child with id {} ",kid);
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(contor);
        return contor;


    }

    @Override
    public Entry findOne(Tuple<Long, Long> tupleId) {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        Child child_empty = new Child("",0);
        Challenge challenge_empty = new Challenge(0,0,"");
        Entry entry = new Entry(LocalDateTime.now(),child_empty,challenge_empty);
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Entries where kid = ? and cid = ?")){
            preparedStatement.setInt(1,tupleId.getLeft().intValue());
            preparedStatement.setInt(2,tupleId.getRight().intValue());
            try(ResultSet result = preparedStatement.executeQuery()){
                if(!result.isClosed()) {
                    result.next();
                    Child child = childRepo.findOne(tupleId.getLeft());
                    Challenge challenge = challengeRepo.findOne(tupleId.getRight());
                    LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                    entry.setDate(date);
                    entry.setChild(child);
                    entry.setChallenge(challenge);
                    entry.setId(tupleId);
                }
                else return null;
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e );
        }
        logger.traceExit(entry);
        return entry;


    }

    @Override
    public Iterable<Entry> findAll() {

        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        List<Entry> entries = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Entries")){
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()){
                    Tuple<Long,Long> id = new Tuple(result.getLong("kid"),result.getLong("cid"));
                    Child child = childRepo.findOne(result.getLong("kid"));
                    Challenge challenge = challengeRepo.findOne(result.getLong("cid"));
                    LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                    Entry entry = new Entry(date,child,challenge);
                    entry.setId(id);
                    entries.add(entry);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(entries);
        return entries;

    }

    @Override
    public Entry save(Entry entry) {

        logger.traceEntry("Saving Entry {} ",entry);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into Entries (kid,cid,date) values (?,?,?)")){
            preparedStatement.setInt(1,entry.getId().getLeft().intValue());
            preparedStatement.setInt(2,entry.getId().getRight().intValue());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entry.getDate()));
            int result = preparedStatement.executeUpdate();
            if(result == 1)
                return null;
            logger.trace("Saved {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return entry;

    }

    @Override
    public Entry delete(Tuple<Long, Long> tupleId) {

        logger.traceEntry("Deleting Entry with id {}",tupleId);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from Entries where kid = ? and cid = ?")){
            preparedStatement.setInt(1,tupleId.getLeft().intValue());
            preparedStatement.setInt(2,tupleId.getRight().intValue());
            Entry entry = findOne(tupleId);
            int result  = preparedStatement.executeUpdate();
            if(result == 1)
                return entry;
            logger.trace("Deleted {} instances",result);
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return null;

    }

    @Override
    public Entry update(Entry entry) {

        logger.traceEntry("Update Entry {} ",entry);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("update Entries set date = ? where kid = ? and  cid = ?")){
            preparedStatement.setTimestamp(1,Timestamp.valueOf(entry.getDate()));
            preparedStatement.setInt(2,entry.getId().getLeft().intValue());
            preparedStatement.setInt(3,entry.getId().getRight().intValue());
            int result = preparedStatement.executeUpdate();
            if(result != 1)
                return entry;
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
        try(PreparedStatement preparedStatement = connection.prepareStatement("select count() as contor from Entries")){
            try(ResultSet result = preparedStatement.executeQuery()){
                contor = result.getInt("contor");
                logger.trace("Counted entries");
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(contor);
        return contor;

    }

    @Override
    public boolean exists(Tuple<Long, Long> tupleId) {

        logger.traceEntry("Verifing ... ");
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Entries where kid = ? and cid = ?")){
            preparedStatement.setInt(1,tupleId.getLeft().intValue());
            preparedStatement.setInt(2,tupleId.getRight().intValue());
            try(ResultSet result = preparedStatement.executeQuery()) {
                if(!result.isClosed()) {
                    result.next();
                    if (result.getInt("kid") == tupleId.getLeft().intValue() && result.getInt("cid") == tupleId.getRight().intValue())
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
