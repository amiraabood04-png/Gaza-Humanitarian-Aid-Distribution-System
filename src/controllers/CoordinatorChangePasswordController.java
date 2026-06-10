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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class CoordinatorChangePasswordController implements Initializable {

    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMessage;

    private int currentUserId = Session.userId;
    @FXML
    private Label lblCoordinatorName;
    @FXML
    private Label lblWelcomeName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblMessage.setText("");
        lblCoordinatorName.setText(Session.fullName);
lblWelcomeName.setText(Session.fullName);
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        String current = txtCurrentPassword.getText().trim();
        String newPass = txtNewPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showMessage("Please fill all fields.", false);
            return;
        }

        if (newPass.length() < 8) {
            showMessage("Password must be at least 8 characters.", false);
            return;
        }

        if (!newPass.equals(confirm)) {
            showMessage("Passwords do not match.", false);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            String checkSql = "SELECT * FROM User WHERE user_id=? AND password=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setInt(1, currentUserId);
            checkPs.setString(2, current);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                showMessage("Current password is incorrect.", false);
                return;
            }

            String updateSql = "UPDATE User SET password=? WHERE user_id=?";
            PreparedStatement updatePs = con.prepareStatement(updateSql);
            updatePs.setString(1, newPass);
            updatePs.setInt(2, currentUserId);

            if (updatePs.executeUpdate() > 0) {
                showMessage("Password changed successfully.", true);
                clearFields();
            } else {
                showMessage("Password change failed.", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Database error.", false);
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        clearFields();
        lblMessage.setText("");
    }

    private void clearFields() {
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    private void showMessage(String message, boolean success) {
        lblMessage.setText(message);
        lblMessage.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void openPage(ActionEvent event, String file) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + file));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Cannot open page.", false);
        }
    }
    @FXML private void logout(ActionEvent event) {
    openPage(event, "Login.fxml");
}
    @FXML private void openDashboard(ActionEvent event) { openPage(event, "CoordinatorDashboard.fxml"); }
    @FXML private void openProfile(ActionEvent event) { openPage(event, "Profile.fxml"); }
    @FXML private void openFamilies(ActionEvent event) { openPage(event, "CoordinatorFamilies.fxml"); }
    @FXML private void openAidDistribution(ActionEvent event) { openPage(event, "CoordinatorAidDistribution.fxml"); }
    @FXML private void openChangePassword(ActionEvent event) { openPage(event, "CoordinatorChangePassword.fxml"); }

}