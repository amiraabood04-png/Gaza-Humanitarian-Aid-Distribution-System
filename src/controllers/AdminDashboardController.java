package controllers;

import config.DBConnection;
import config.Session;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label lblRole;
    @FXML
    private Label lblWelcomeName;

    @FXML
    private Label lblOrganizations;
    @FXML
    private Label lblUsers;
    @FXML
    private Label lblFamilies;
    @FXML
    private Label lblServed;
    @FXML
    private Label lblNotServed;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblDate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUserName.setText(Session.fullName);
        lblRole.setText(Session.role);
        lblWelcomeName.setText(Session.fullName + " 👋");

        loadStatistics();

        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        lblDate.setText(LocalDate.now().format(formatter));
    }

    private void loadStatistics() {
        lblOrganizations.setText(String.valueOf(getCount("SELECT COUNT(*) FROM organization")));
        lblUsers.setText(String.valueOf(getCount("SELECT COUNT(*) FROM `user`")));
        lblFamilies.setText(String.valueOf(getCount("SELECT COUNT(*) FROM family")));

        lblServed.setText(String.valueOf(getCount(
                "SELECT COUNT(DISTINCT family_id) FROM aiddistribution"
        )));

        lblNotServed.setText(String.valueOf(getCount(
                "SELECT COUNT(*) FROM family WHERE family_id NOT IN "
                + "(SELECT DISTINCT family_id FROM aiddistribution)"
        )));
    }

    private int getCount(String sql) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

  
        private void openPage(ActionEvent event, String fxmlFile) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxmlFile));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        boolean wasMaximized = stage.isMaximized();
        double width = stage.getWidth();
        double height = stage.getHeight();

        stage.setScene(new Scene(root, width, height));

        stage.setMaximized(wasMaximized);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    

    @FXML
    private void openDashboard(ActionEvent event) {
        openPage(event, "AdminDashboard.fxml");
    }

    @FXML
    private void openOrganizations(ActionEvent event) {
        openPage(event, "Organizations.fxml");
    }

    @FXML
    private void openUsers(ActionEvent event) {
        openPage(event, "Users.fxml");
    }

    @FXML
    private void openFamilies(ActionEvent event) {
        openPage(event, "Families.fxml");
    }

    @FXML
    private void openAidDistribution(ActionEvent event) {
        openPage(event, "AidDistribution.fxml");
    }

    private void openReports(ActionEvent event) {
        openPage(event, "Reports.fxml");
    }

    @FXML
    private void openChangePassword(ActionEvent event) {
        openPage(event, "ChangePassword.fxml");
    }

    @FXML
    private void logout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void setSmallFont(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot().setStyle("-fx-font-size: 14px;");
    }

    @FXML
    private void setLargeFont(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot().setStyle("-fx-font-size: 22px;");
    }

    @FXML
    private void setArialFont(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot().setStyle("-fx-font-family: Arial;");

    }

    @FXML
    private void setTimesFont(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot().setStyle("-fx-font-family: 'Times New Roman';");
    }

    @FXML
    private void setDefaultTheme(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot()
                .setStyle("-fx-background-color: white;");
    }

    @FXML
    private void setDarkTheme(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        Scene scene = item.getParentPopup()
                .getOwnerWindow()
                .getScene();

        scene.getRoot()
                .setStyle("-fx-background-color: #2b2b2b;");
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Application Name: GHADS System\n"
                + "Purpose: Gaza Humanitarian Aid Distribution System\n"
                + "Developer: Amira AL-Nahhal");
        alert.showAndWait();
    }
}
