package controllers;

import config.Session;
import dao.OrganizationDAO;
import java.net.URL;
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
import javafx.stage.Stage;
import models.Organization;

public class OrganizationController implements Initializable {

    @FXML
    private TextField txtOrganizationName;
    @FXML
    private ComboBox<String> cmbOrganizationType;
    @FXML
    private TextField txtContactInfo;
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
    private TextField txtSearch;

    @FXML
    private TableView<Organization> organizationsTable;
    @FXML
    private TableColumn<Organization, Integer> colOrgId;
    @FXML
    private TableColumn<Organization, String> colOrgName;
    @FXML
    private TableColumn<Organization, String> colOrgType;
    @FXML
    private TableColumn<Organization, String> colContactInfo;
    @FXML
    private TableColumn<Organization, String> colDateCreated;
    @FXML
    private TableColumn<Organization, String> colActions;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblRole;

    private OrganizationDAO organizationDAO;
    private ObservableList<Organization> organizationList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        organizationDAO = new OrganizationDAO();
        lblUserName.setText(Session.fullName);
        lblRole.setText(Session.role);
        cmbOrganizationType.setItems(FXCollections.observableArrayList(
                "NGO", "UN", "Local", "Medical", "Food", "Charity"
        ));

        colOrgId.setCellValueFactory(new PropertyValueFactory<>("orgId"));
        colOrgName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOrgType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colContactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        if (colDateCreated != null) {
            colDateCreated.setCellValueFactory(data -> new SimpleStringProperty("-"));
        }

        if (colActions != null) {
            colActions.setCellValueFactory(data -> new SimpleStringProperty("-"));
        }

        loadOrganizations();

        organizationsTable.setOnMouseClicked(event -> {
            Organization selected = organizationsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtOrganizationName.setText(selected.getName());
                cmbOrganizationType.setValue(selected.getType());
                txtContactInfo.setText(selected.getContactInfo());
            }
        });

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchOrganizations(newValue);
        });
    }

    @FXML
    private void handleAddOrganization(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        Organization org = new Organization(
                0,
                txtOrganizationName.getText().trim(),
                cmbOrganizationType.getValue(),
                txtContactInfo.getText().trim()
        );

        boolean success = organizationDAO.addOrganization(org);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Organization added successfully.");
            clearFields();
            loadOrganizations();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add organization.");
        }
    }

    @FXML
    private void handleUpdateOrganization(ActionEvent event) {
        Organization selected = organizationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an organization to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        selected.setName(txtOrganizationName.getText().trim());
        selected.setType(cmbOrganizationType.getValue());
        selected.setContactInfo(txtContactInfo.getText().trim());

        boolean success = organizationDAO.updateOrganization(selected);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Organization updated successfully.");
            clearFields();
            loadOrganizations();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update organization.");
        }
    }

    @FXML
    private void handleDeleteOrganization(ActionEvent event) {
        Organization selected = organizationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an organization to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this organization?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = organizationDAO.deleteOrganization(selected.getOrgId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Organization deleted successfully.");
                clearFields();
                loadOrganizations();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete organization.");
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
        loadOrganizations();
    }

    private void loadOrganizations() {
        List<Organization> list = organizationDAO.getAllOrganizations();
        organizationList = FXCollections.observableArrayList(list);
        organizationsTable.setItems(organizationList);
    }

    private void searchOrganizations(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadOrganizations();
            return;
        }

        List<Organization> list = organizationDAO.searchOrganizations(keyword.trim());
        organizationsTable.setItems(FXCollections.observableArrayList(list));
    }

    private boolean validateInputs() {
        if (txtOrganizationName.getText().trim().isEmpty()
                || cmbOrganizationType.getValue() == null
                || txtContactInfo.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        txtOrganizationName.clear();
        txtContactInfo.clear();
        cmbOrganizationType.setValue(null);
        organizationsTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open " + fxmlFile);
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
    private void openChangePassword(ActionEvent event) {
        openPage(event, "ChangePassword.fxml");
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
    private void handleLogout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }
}
