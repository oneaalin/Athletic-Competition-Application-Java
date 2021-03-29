package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Employee;
import service.Service;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginNotification;

    Service service;

    Stage stage;

    public void initialize(Service service , Stage stage){

        this.service = service;
        this.stage = stage;
        this.stage.setMinHeight(400);
        this.stage.setMaxHeight(800);
        this.stage.setMaximized(false);


    }

    @FXML
    public void handleLogin(){

        String username = usernameField.getText();
        String password = passwordField.getText();
        Employee employee = service.loginEmployee(username,password);
        if(employee != null){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/MainView.fxml"));
                AnchorPane root = loader.load();
                stage.setTitle("Concurs Atletism");
                stage.setScene(new Scene(root));
                MainController mainController = loader.getController();
                mainController.initialize(service,stage,employee);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loginNotification.setText("The username and/or password are not matching ! ");
        }
    }

    @FXML
    public void handleRegister(){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RegisterView.fxml"));
            AnchorPane root = loader.load();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            RegisterController registerController = loader.getController();
            registerController.initialize(service,stage);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}
