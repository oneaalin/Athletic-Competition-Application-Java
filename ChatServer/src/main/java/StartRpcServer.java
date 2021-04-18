import chat.network.utils.AbstractServer;
import chat.network.utils.ChatRpcConcurrentServer;
import chat.network.utils.ServerException;
import chat.persistence.*;
import chat.server.Service;
import chat.service.IService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {

    private static int defaultPort = 55555;

    public static void main(String[] args){
        /*Properties serverProps = new Properties();
        try{
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set ! ");
            serverProps.list(System.out);
        } catch(IOException e){
            System.err.println("Cannot find server properties " + e);
        }

        IChildRepository childRepo  = new ChildRepository(serverProps);
        IChallengeRepository challengeRepo = new ChallengeRepository(serverProps);
        IEmployeesRepository employeesRepo = new EmployeesRepository(serverProps);
        IEntriesRepository entriesRepo = new EntriesRepository(serverProps,childRepo,challengeRepo);
        IService serverImpl = new Service(challengeRepo,childRepo,employeesRepo,entriesRepo);
        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef){
            System.err.println("Wrong Port Number " + nef.getMessage());
            System.err.println("Using default port : " + defaultPort);
        }
        System.out.println("Starting server on port : " + serverPort);
        AbstractServer server = new ChatRpcConcurrentServer(serverPort,serverImpl);
        try{
            server.start();
        } catch (ServerException e){
            System.err.println("Error starting the server " + e.getMessage());
        }finally {
            try{
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server " + e.getMessage());
            }
        }*/

        ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-server.xml");
    }

}
