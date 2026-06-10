package controllers;

import config.Session;
import dao.OrganizationDAO;
import dao.UserDAO;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import models.Organization;
import models.User;

public class UsersController implements Initializable {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private ComboBox<String> cmbOrganization;
    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnRefresh;

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colOrganization;
    @FXML
    private Label lblUserName;

    private UserDAO userDAO;
    private OrganizationDAO organizationDAO;
    private ObservableList<User> usersList;
    private HashMap<String, Integer> organizationMap;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
       lblUserName.setText(Session.fullName);
//        lblRole.setText(Session.role);
        organizationDAO = new OrganizationDAO();
        organizationMap = new HashMap<>();

        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "COORDINATOR"));

        loadOrganizationsCombo();

        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colOrganization.setCellValueFactory(data
                -> new SimpleStringProperty(String.valueOf(data.getValue().getOrgId()))
        );

        loadUsers();

        usersTable.setOnMouseClicked(event -> {
            User selected = usersTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                txtFullName.setText(selected.getFullName());
                txtUsername.setText(selected.getUsername());
                txtEmail.setText(selected.getEmail());
                txtPassword.setText(selected.getPassword());
                cmbRole.setValue(selected.getRole());

                for (String key : organizationMap.keySet()) {
                    if (organizationMap.get(key) == selected.getOrgId()) {
                        cmbOrganization.setValue(key);
                        break;
                    }
                }
            }
        });
    }

    private void loadOrganizationsCombo() {
        List<Organization> orgs = organizationDAO.getAllOrganizations();

        ObservableList<String> names = FXCollections.observableArrayList();

        for (Organization org : orgs) {
            names.add(org.getName());
            organizationMap.put(org.getName(), org.getOrgId());
        }

        cmbOrganization.setItems(names);
    }

    private void loadUsers() {
        usersList = FXCollections.observableArrayList(userDAO.getAllUsers());
        usersTable.setItems(usersList);
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        if (!validateInputs()) {

            return;
        }

        int orgId = getSelectedOrgId();

        User user = new User(
                0,
                txtUsername.getText().trim(),
                txtPassword.getText().trim(),
                txtFullName.getText().trim(),
                txtEmail.getText().trim(),
                cmbRole.getValue(),
                orgId
        );

        if (userDAO.addUser(user)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            clearFields();
            loadUsers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user.");
        }
    }

    @FXML
    private void handleUpdateUser(ActionEvent event) {
        User selected = usersTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        selected.setFullName(txtFullName.getText().trim());
        selected.setUsername(txtUsername.getText().trim());
        selected.setEmail(txtEmail.getText().trim());
        selected.setPassword(txtPassword.getText().trim());
        selected.setRole(cmbRole.getValue());
        selected.setOrgId(getSelectedOrgId());

        if (userDAO.updateUser(selected)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
            clearFields();
            loadUsers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user.");
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        User selected = usersTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.deleteUser(selected.getUserId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                clearFields();
                loadUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        clearFields();
        loadUsers();
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            usersTable.setItems(usersList);
            return;
        }

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : usersList) {
            if (user.getFullName().toLowerCase().contains(keyword)
                    || user.getUsername().toLowerCase().contains(keyword)
                    || user.getEmail().toLowerCase().contains(keyword)
                    || user.getRole().toLowerCase().contains(keyword)) {

                filtered.add(user);
            }
        }

        usersTable.setItems(filtered);
    }

    private boolean validateInputs() {
        if (txtFullName.getText().trim().isEmpty()
                || txtUsername.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()
                || txtPassword.getText().trim().isEmpty()
                || cmbRole.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
            return false;
        }

        if (txtPassword.getText().trim().length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 8 characters.");
            return false;
        }

        if (cmbRole.getValue().equals("COORDINATOR") && cmbOrganization.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select organization for coordinator.");
            return false;
        }
        String email = txtEmail.getText().trim();

        if (!email.matches("^[A-Za-z0-9._%+-]+@ghads\\.com$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Email",
                    "Please enter a valid GHADS email (example@ghads.com)");
            return false;
        }
        return true;
    }

    private int getSelectedOrgId() {
        if (cmbOrganization.getValue() == null) {
            return 0;
        }

        return organizationMap.get(cmbOrganization.getValue());
    }

    private void clearFields() {
        txtFullName.clear();
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRole.setValue(null);
        cmbOrganization.setValue(null);
        usersTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openChangePassword(ActionEvent event) {
        openPage(event, "ChangePassword.fxml");
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

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleFontSize(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Format", "Font Size option will be applied later.");
    }

    @FXML
    private void handleFontFamily(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Format", "Font Family option will be applied later.");
    }

    @FXML
    private void handleTheme(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Format", "Background color option will be applied later.");
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "About GHADS",
                "GHADS\nGaza Humanitarian Aid Distribution System\n\nDeveloped for Programming III Lab.");
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

    private void openSettings(ActionEvent event) {
        openPage(event, "ChangePassword.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }
}
