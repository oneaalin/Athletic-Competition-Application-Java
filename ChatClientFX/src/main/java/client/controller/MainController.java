package client.controller;

import chat.model.*;
import chat.service.IObserver;
import chat.service.IService;
import chat.service.ValidationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController extends UnicastRemoteObject implements IObserver , Serializable {

    public MainController() throws RemoteException {
    }

    @Override
    public void updateChildren(ChildDTO child , List<ChallengeDTO> challenges) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                childrenModel.add(child);
                challengesModel.clear();
                challengesModel.addAll(challenges);
            }
        });
    }

    static class ChallengeCell extends ListCell<ChallengeDTO>{
        HBox hBox = new HBox();
        Label name = new Label("(empty)");
        Label ageInterval = new Label("(empty)");
        Label childrenNumber = new Label("empty");
        Pane pane1 = new Pane();
        Pane pane2 = new Pane();
        ChallengeDTO lastItem;

        public ChallengeCell() {
            super();

            pane1.setMinWidth(2);
            pane2.setMinWidth(2);

            this.setStyle("-fx-cursor: hand");

            hBox.getChildren().addAll(name,pane1,ageInterval,pane2,childrenNumber);
            hBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
            HBox.setHgrow(pane2, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(ChallengeDTO item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if(empty){
                lastItem = null;
                setGraphic(null);
            }else{
                lastItem = item;
                if(item != null){
                    name.setText(item.getName());
                    ageInterval.setText(Integer.toString(item.getMinimumAge()) + " - " + Integer.toString(item.getMaximumAge()));
                    childrenNumber.setText("Participanti (" + item.getChildsNumber() + ")");
                }
                setGraphic(hBox);
            }
        }
    }

    static class ChildCell extends ListCell<Child>{
        HBox hBox = new HBox();
        Label name = new Label("(empty)");
        Label age = new Label("(empty)");
        Pane pane1 = new Pane();
        Child lastItem;

        public ChildCell() {
            super();

            pane1.setMinWidth(2);
            pane1.setMaxWidth(2);

            this.setStyle("-fx-cursor: hand");

            hBox.getChildren().addAll(name,pane1,age);
            hBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(Child item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if(empty){
                lastItem = null;
                setGraphic(null);
            }else{
                lastItem = item;
                if(item != null){
                    name.setText(item.getName() + " , ");
                    age.setText(Integer.toString(item.getAge()) + " ani");
                }
                setGraphic(hBox);
            }
        }
    }

    IService server;
    Stage stage;
    Employee connectedEmployee;
    Parent loginParent;
    LoginController loginController;

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void setLoginParent(Parent loginParent) {
        this.loginParent = loginParent;
    }

    @FXML
    Label connectedEmployeeLabel;
    @FXML
    TextField nameField;
    @FXML
    TextField ageField;
    @FXML
    ListView<ChallengeDTO> challengesListView;
    @FXML
    TableView<ChildDTO> childrenTableView;
    @FXML
    TableColumn<ChildDTO,String> nameColumn;
    @FXML
    TableColumn<ChildDTO,Integer> ageColumn;
    @FXML
    TableColumn<ChildDTO,Integer> challengesNumberColumn;
    @FXML
    ComboBox<String> firstChallengeBox;
    @FXML
    ComboBox<String> secondChallengeBox;
    @FXML
    Label errorLabel;
    @FXML
    Label addSecondChallengeLabel;
    @FXML
    Label deleteSecondChallengeLabel;
    @FXML
    HBox secondChallengeHBox;
    @FXML
    Button registerButton;
    @FXML
    ListView<Child> challengeChildrenView;

    ObservableList<ChallengeDTO> challengesModel = FXCollections.observableArrayList();
    ObservableList<ChildDTO> childrenModel = FXCollections.observableArrayList();

    public void initialize(IService server,Stage stage,Employee employee){

        secondChallengeHBox.setVisible(false);
        secondChallengeHBox.setManaged(false);
        deleteSecondChallengeLabel.setVisible(false);
        deleteSecondChallengeLabel.setManaged(false);
        addSecondChallengeLabel.setStyle("-fx-cursor: hand");
        deleteSecondChallengeLabel.setStyle("-fx-cursor: hand");

        this.server = server;
        this.stage = stage;
        this.stage.setMaximized(false);
        this.connectedEmployee = employee;

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                logout();
                System.exit(0);
            }
        });

        connectedEmployeeLabel.setText("Logged in as: " + connectedEmployee.getUsername());

        initModel();

        challengesListView.setCellFactory(param -> new ChallengeCell());

        challengesListView.setOnMouseClicked((mouseEvent) -> {
            if(mouseEvent.getClickCount() == 2){
                handleShowChildren();
            }
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        challengesNumberColumn.setCellValueFactory(new PropertyValueFactory<>("challengesNumber"));


        challengesListView.setItems(challengesModel);
        childrenTableView.setItems(childrenModel);

        addSecondChallengeLabel.setOnMouseClicked((mouseEvent) ->{
            secondChallengeHBox.setVisible(true);
            secondChallengeHBox.setManaged(true);
            deleteSecondChallengeLabel.setVisible(true);
            deleteSecondChallengeLabel.setManaged(true);
            addSecondChallengeLabel.setVisible(false);
            addSecondChallengeLabel.setManaged(false);
        });

        deleteSecondChallengeLabel.setOnMouseClicked((mouseEvent) -> {
            secondChallengeHBox.setVisible(false);
            secondChallengeHBox.setManaged(false);
            deleteSecondChallengeLabel.setVisible(false);
            deleteSecondChallengeLabel.setManaged(false);
            addSecondChallengeLabel.setVisible(true);
            addSecondChallengeLabel.setManaged(true);
            secondChallengeBox.getSelectionModel().clearSelection();
        });

        ageField.textProperty().addListener(x -> initializeChallengeBoxes());

        initializeChallengeBoxes();


    }

    private void initializeChallengeBoxes(){

        String ageText = ageField.getText();
        firstChallengeBox.getItems().clear();
        secondChallengeBox.getItems().clear();
        try{
            Integer age = Integer.parseInt(ageText);
            Map<String,ChallengeDTO> challengesMap = new HashMap<>();
            List<ChallengeDTO> filtered = server.getAllChallenges().stream().filter(x -> x.getMinimumAge() <= age && x.getMaximumAge() >= age).collect(Collectors.toList());

            for(ChallengeDTO challenge : filtered){
                challengesMap.putIfAbsent(challenge.getName(),challenge);
            }

            for(Map.Entry<String,ChallengeDTO> entry : challengesMap.entrySet()) {
                firstChallengeBox.getItems().add(entry.getKey());
                secondChallengeBox.getItems().add(entry.getKey());
            }
            registerButton.setDisable(false);
        }catch (NumberFormatException e){
            registerButton.setDisable(true);
        }

    }
    private void initModel(){
        childrenModel.addAll(server.getAllChildren());
        challengesModel.addAll(server.getAllChallenges());
    }
    @FXML
    private void handleShowChildren(){
        try {

            ChallengeDTO challengeDTO = challengesListView.getSelectionModel().getSelectedItem();
            Challenge challenge = server.getChallengeByProperties(challengeDTO.getMinimumAge(), challengeDTO.getMaximumAge(), challengeDTO.getName());

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("/views/ChallengeChildrenView.fxml"));
            loader.setLocation(getClass().getResource("/views/ChallengeChildrenView.fxml"));
            AnchorPane root = loader.load();
            Stage newStage = new Stage();


            newStage.setTitle("Participantii la  proba " + challenge.getName() + " - varsta(" + challenge.getMinimumAge() + " - " + challenge.getMaximumAge() + ")");
            newStage.setScene(new Scene(root));
            newStage.show();

            //ListView<Child> challengeChildrenView;
            //challengeChildrenView.setCellFactory(param -> new ChildCell());
            challengeChildrenView = (ListView) newStage.getScene().lookup("#challengeChildrenView");
            ObservableList<Child> challengeChildrenModel = FXCollections.observableArrayList();
            challengeChildrenModel.addAll(server.getChildrenById(challenge.getId()));
            challengeChildrenView.setCellFactory(param -> new ChildCell());
            challengeChildrenView.setItems(challengeChildrenModel);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(){

        try {
            logout();
            //FXMLLoader loader = new FXMLLoader();
            //loader.setLocation(getClass().getResource("/views/LoginView.fxml"));
            //AnchorPane root = loader.load();
            stage.setTitle("Login");
            stage.setScene(new Scene(loginParent));
            //LoginController loginController = loader.getController();
            loginController.initialize(server,stage);
            stage.show();
        }catch(ValidationException e){
            e.printStackTrace();
        }

    }

    void logout() {
        try {
            server.logout(connectedEmployee, this);
        } catch (ValidationException e) {
            System.out.println("Logout error " + e);
        }

    }

    @FXML
    private void handleRegisterChild(){
        String name = nameField.getText();
        String ageText = ageField.getText();
        int age = Integer.parseInt(ageText);
        String challenge1 = "";
        String challenge2 = "";
        if(!firstChallengeBox.getSelectionModel().isEmpty()){
            challenge1 = firstChallengeBox.getSelectionModel().getSelectedItem();
        }
        if(!secondChallengeBox.getSelectionModel().isEmpty()){
            challenge2 = secondChallengeBox.getSelectionModel().getSelectedItem();
        }

        try{
            Child child = server.registerChild(name,age,challenge1,challenge2);
            if(child != null){
                errorLabel.setText("A aparut o eroare ! ");
            }else {
                errorLabel.setText("");
            }
        }catch(ValidationException e){
            errorLabel.setText(e.getErrors());
        }

    }

}
