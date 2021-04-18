package client.controller;

import chat.model.Employee;
import chat.service.IService;
import chat.service.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginController extends UnicastRemoteObject implements Serializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginNotification;

    Parent mainParent;

    private MainController mainController;

    IService server;

    Stage stage;

    public LoginController() throws RemoteException {
    }

    public void setMainParent(Parent mainParent) {
        this.mainParent = mainParent;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize(IService server , Stage stage){

        this.server = server;
        this.stage = stage;
        //this.stage.setMinHeight(400);
        //this.stage.setMaxHeight(800);
        //this.stage.setMaximized(false);

    }

    @FXML
    public void handleLogin(ActionEvent actionEvent){

        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            Employee employee = server.loginEmployee(username,password,mainController);
            //FXMLLoader loader = new FXMLLoader();
            //loader.setLocation(getClass().getResource("/views/MainView.fxml"));
            //AnchorPane root = loader.load();
            if(employee != null){
                Stage stage = new Stage();
                stage.setTitle("Concurs Atletism");
                stage.setScene(new Scene(mainParent));
                //MainController mainController = loader.getController();
                mainController.initialize(server,stage,employee);
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
                }
            else {
                loginNotification.setText("The username and/or password are not matching ! ");
            }

        } catch (ValidationException e) {
            System.out.println("Errors : " + e.getMessage());
            loginNotification.setText("User already logged in");
        }

    }

    @FXML
    public void handleRegister(){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RegisterView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            RegisterController registerController = loader.getController();
            registerController.initialize(server,stage);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}
