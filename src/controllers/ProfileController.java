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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProfileController implements Initializable {

    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtOrganization;
    @FXML private TextField txtRole;

    @FXML private Button btnUpdate;
    @FXML private Button btnReset;

    @FXML private Label lblSideUsername;
    @FXML private Label lblSideOrganization;
    @FXML private Label lblSideRole;
    @FXML
    private Label lblCoordinatorName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadProfile();
        lblCoordinatorName.setText(Session.fullName);
    }

    private void loadProfile() {
        String sql =
                "SELECT u.full_name, u.username, u.email, u.role, o.name AS organization_name " +
                "FROM `user` u LEFT JOIN organization o ON u.org_id = o.org_id " +
                "WHERE u.user_id = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Session.userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtFullName.setText(rs.getString("full_name"));
                txtUsername.setText(rs.getString("username"));
                txtEmail.setText(rs.getString("email"));
                txtRole.setText(rs.getString("role"));

                String orgName = rs.getString("organization_name");
                if (orgName == null) {
                    orgName = "";
                }

                txtOrganization.setText(orgName);

                lblSideUsername.setText(rs.getString("username"));
                lblSideOrganization.setText(orgName);
                lblSideRole.setText(rs.getString("role"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load profile data.");
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()) {
            showAlert("Validation Error", "Please fill full name, username, and email.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@(ghads\\.com|gmail\\.com)$")) {
            showAlert("Invalid Email", "Please enter a valid email.");
            return;
        }

        String sql = "UPDATE `user` SET full_name=?, username=?, email=? WHERE user_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setInt(4, Session.userId);

            if (ps.executeUpdate() > 0) {
                Session.fullName = fullName;
                Session.username = username;

                lblSideUsername.setText(username);
                lblSideOrganization.setText(txtOrganization.getText());
                lblSideRole.setText(txtRole.getText());

                showAlert("Success", "Profile updated successfully.");
            } else {
                showAlert("Error", "Profile update failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Cannot update profile.");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        loadProfile();
    }

    private void openPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxmlFile));
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