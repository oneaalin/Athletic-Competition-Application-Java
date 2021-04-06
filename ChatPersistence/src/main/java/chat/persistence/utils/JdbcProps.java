package chat.persistence.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JdbcProps {

    private Properties props = new Properties();

    public Properties getProps() {
        try {
            props.load(new FileReader("bd.properties"));
            return props;
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }
        return null;
    }

}
