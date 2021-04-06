package client;

import chat.network.rpcprotocol.ServiceRpcProxy;
import chat.service.IService;
import client.controller.LoginController;
import client.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClientFX extends Application {

    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    public void start(Stage primaryStage) throws IOException {

        Properties clientProperties = new Properties();
        try{
            clientProperties.load(StartRpcClientFX.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set ! ");
            clientProperties.list(System.out);
        } catch (IOException e){
            System.err.println("Cannot find client.propertis " + e);
        }
        String serverIP = clientProperties.getProperty("server.host",defaultServer);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(clientProperties.getProperty("server.port"));
        }catch (NumberFormatException ex){
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port : " + defaultPort);
        }

        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new ServiceRpcProxy(serverIP,serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("/views/LoginView.fxml"));
        loader.setLocation(getClass().getResource("/views/LoginView.fxml"));
        AnchorPane root = loader.load();
        LoginController ctrl = loader.getController();


        FXMLLoader mloader = new FXMLLoader(getClass().getClassLoader().getResource("/views/MainView.fxml"));
        mloader.setLocation(getClass().getResource("/views/MainView.fxml"));
        AnchorPane mroot = mloader.load();
        MainController mctrl = mloader.getController();
        mctrl.setLoginParent(root);
        mctrl.setLoginController(ctrl);

        ctrl.setMainParent(mroot);
        ctrl.setMainController(mctrl);

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(800);
        ctrl.initialize(server,primaryStage);
        primaryStage.show();
        primaryStage.centerOnScreen();

    }

    public static void main(String[] args){
        launch(args);
    }

}
