package chat.persistence.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Jdbc {

    private Properties jdbcProps;

    private static final Logger logger = LogManager.getLogger();

    private Connection instance = null;

    public Jdbc(Properties jdbcProps) {
        this.jdbcProps = jdbcProps;
    }

    private Connection getNewConnection(){
        logger.traceEntry();

        String url=jdbcProps.getProperty("jdbc.url");
        String user=jdbcProps.getProperty("jdbc.user");
        String pass=jdbcProps.getProperty("jdbc.pass");
        logger.info("Trying to connect to database ... {}",url);
        logger.info("user: {}",user);
        logger.info("password: {}",pass);
        Connection connection = null;
        try{
            if(user != null && pass != null)
                connection = DriverManager.getConnection(url,user,pass);
            else
                connection = DriverManager.getConnection(url);
        }catch(SQLException e){
            logger.error(e);
            System.out.println("Error getting connection " + e);
        }
        return connection;
    }

    public Connection getConnection(){
        logger.traceEntry();
        try{
            if(instance == null || instance.isClosed())
                instance=getNewConnection();
        }catch (SQLException e){
            logger.error(e);
            System.out.println("Error database " + e);
        }
        logger.traceExit(instance);
        return instance;
    }
}
