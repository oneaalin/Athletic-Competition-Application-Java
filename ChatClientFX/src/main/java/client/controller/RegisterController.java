package client.controller;

import chat.model.Employee;
import chat.service.IService;
import chat.service.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterController extends UnicastRemoteObject {

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label registerNotification;


    private IService service;
    Stage stage;

    public RegisterController() throws RemoteException {
    }

    public void initialize(IService service, Stage stage){

        this.service = service;
        this.stage = stage;
        this.stage.setMinHeight(400);
        this.stage.setMinWidth(800);
        this.stage.setMaximized(false);

    }

    @FXML
    public void handleBack(){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/LoginView.fxml"));
            AnchorPane root = loader.load();
            stage.setTitle("Login");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            LoginController loginController = loader.getController();
            loginController.initialize(service,stage);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void handleRegister(){

        try{
            String username = usernameField.getText();
            String password = passwordField.getText();
            try{
                Employee employee = service.registerEmployee(username,password);
                if(employee == null){
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/views/LoginView.fxml"));
                    AnchorPane root = loader.load();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    LoginController loginController = loader.getController();
                    loginController.initialize(service,stage);
                    stage.show();
                } else {
                    registerNotification.setText("Exista deja un utilizator cu acest nume ! ");
                }
            } catch (ValidationException e){
                registerNotification.setText("Date invalide ! ");
                usernameField.setText("");
                passwordField.setText("");
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }


}