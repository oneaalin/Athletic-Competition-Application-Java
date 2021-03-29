import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.*;
import service.Service;
import utils.JdbcProps;

import java.io.IOException;

public class AppFX extends Application {

    private JdbcProps jdbcProps = new JdbcProps();

    IChildRepository childRepository = new ChildRepository(jdbcProps.getProps());
    IChallengeRepository challengeRepository = new ChallengeRepository(jdbcProps.getProps());
    IEmployeesRepository employeesRepository = new EmployeesRepository(jdbcProps.getProps());
    IEntriesRepository entriesRepository = new EntriesRepository(jdbcProps.getProps(),childRepository,challengeRepository);

    Service service = new Service(childRepository,challengeRepository,employeesRepository,entriesRepository);

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/LoginView.fxml"));
        AnchorPane userLayout = loader.load();
        primaryStage.setScene(new Scene(userLayout));
        LoginController loginController = loader.getController();
        loginController.initialize(service,primaryStage);

    }
}
