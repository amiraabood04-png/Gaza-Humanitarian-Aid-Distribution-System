package controllers;

import config.Session;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private void login(ActionEvent event) {

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {

            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Please enter username and password");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {

            Session.userId = user.getUserId();
            Session.fullName = user.getFullName();
            Session.role = user.getRole();
            Session.username = user.getUsername();

            try {

                javafx.fxml.FXMLLoader loader;
                javafx.scene.Parent root;

                if (user.getRole().equalsIgnoreCase("ADMIN")) {

                    loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/views/AdminDashboard.fxml")
                    );

                } else {

                    loader = new javafx.fxml.FXMLLoader(
                            getClass().getResource("/views/CoordinatorDashboard.fxml")
                    );
                }

                root = loader.load();

                javafx.stage.Stage stage = (javafx.stage.Stage) txtUsername.getScene().getWindow();

                stage.setScene(new javafx.scene.Scene(root));
                stage.show();

            } catch (Exception e) {

                e.printStackTrace();

                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Cannot open dashboard");
            }

        } else {

            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Invalid username or password");
        }

    }
}
