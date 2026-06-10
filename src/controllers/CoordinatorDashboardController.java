package controllers;

import config.DBConnection;
import config.Session;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CoordinatorDashboardController implements Initializable {

    @FXML
    private Label lblWelcomeName;
    @FXML
    private Label lblCoordinatorName;
    @FXML
    private Label lblTotalFamilies;
    @FXML
    private Label lblFamiliesServed;
    @FXML
    private Label lblNotServed;
    @FXML
    private HBox quickManageFamilies;
    @FXML
    private HBox quickDistributeAid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCoordinatorName.setText(Session.fullName);
lblWelcomeName.setText(Session.fullName);
        quickDistributeAid.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("/views/CoordinatorAidDistribution.fxml"));

                Stage stage = (Stage) quickDistributeAid.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        quickManageFamilies.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("/views/CoordinatorFamilies.fxml"));

                Stage stage = (Stage) quickManageFamilies.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        if (Session.fullName != null && !Session.fullName.trim().isEmpty()) {
            lblWelcomeName.setText(Session.fullName);
            lblCoordinatorName.setText(Session.fullName);
        } else {
            lblCoordinatorName.setText(Session.fullName);
            lblWelcomeName.setText(Session.fullName);
        }

        loadStatistics();
    }

    private void loadStatistics() {
        int totalFamilies = getCount("SELECT COUNT(*) FROM family");

        int servedFamilies = getCount(
                "SELECT COUNT(DISTINCT family_id) FROM aiddistribution"
        );

        int notServed = totalFamilies - servedFamilies;

        lblTotalFamilies.setText(String.valueOf(totalFamilies));
        lblFamiliesServed.setText(String.valueOf(servedFamilies));
        lblNotServed.setText(String.valueOf(notServed));
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

    private void openPage(ActionEvent event, String file) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + file));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleFontSize(ActionEvent event) {
        showAlert("Format", "Font Size option will be applied later.");
    }

    @FXML
    private void handleFontFamily(ActionEvent event) {
        showAlert("Format", "Font Family option will be applied later.");
    }

    @FXML
    private void handleTheme(ActionEvent event) {
        showAlert("Format", "Background color option will be applied later.");
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        showAlert("About GHADS", "GHADS\nGaza Humanitarian Aid Distribution System");
    }

    @FXML
    private void openDashboard(ActionEvent event) {
        openPage(event, "CoordinatorDashboard.fxml");
    }

    @FXML
    private void openFamilies(ActionEvent event) {
        openPage(event, "CoordinatorFamilies.fxml");
    }

    @FXML
    private void openAidDistribution(ActionEvent event) {
        openPage(event, "CoordinatorAidDistribution.fxml");
    }

    @FXML
    private void openProfile(ActionEvent event) {
        openPage(event, "Profile.fxml");
    }

    @FXML
    private void openChangePassword(ActionEvent event) {
        openPage(event, "CoordinatorChangePassword.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }
}
