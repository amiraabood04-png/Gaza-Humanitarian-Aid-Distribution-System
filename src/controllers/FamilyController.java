package controllers;

import config.Session;
import dao.FamilyDAO;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
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
import models.Family;

public class FamilyController implements Initializable {

    @FXML
    private TextField txtHouseholdName;
    @FXML
    private TextField txtNationalId;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtLocation;
    @FXML
    private Spinner<Integer> spFamilySize;
    @FXML
    private ComboBox<String> cmbVulnerability;
    @FXML
    private DatePicker dpRegistrationDate;
    @FXML
    private DatePicker dpLastAidDate;

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
    private Button btnSearch;

    @FXML
    private TableView<Family> familiesTable;
    @FXML
    private TableColumn<Family, Integer> colFamilyId;
    @FXML
    private TableColumn<Family, String> colHouseholdName;
    @FXML
    private TableColumn<Family, String> colNationalId;
    @FXML
    private TableColumn<Family, String> colPhone;
    @FXML
    private TableColumn<Family, String> colLocation;
    @FXML
    private TableColumn<Family, Integer> colFamilySize;
    @FXML
    private TableColumn<Family, String> colVulnerability;
    @FXML
    private TableColumn<Family, Date> colRegistrationDate;
    @FXML
    private TableColumn<Family, Date> colLastAidDate;
    @FXML
    private TableColumn<Family, String> colActions;
    @FXML
    private Label lblUserName;

    @FXML
    private Label lblRole;

    private FamilyDAO familyDAO;
    private ObservableList<Family> familyList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        familyDAO = new FamilyDAO();
        lblUserName.setText(Session.fullName);
        lblRole.setText(Session.role);
        cmbVulnerability.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
        spFamilySize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        dpRegistrationDate.setValue(LocalDate.now());

        colFamilyId.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        colHouseholdName.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        colNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colFamilySize.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        colVulnerability.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colLastAidDate.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));

        if (colActions != null) {
            colActions.setCellValueFactory(data -> new SimpleStringProperty("-"));
        }

        loadFamilies();

        familiesTable.setOnMouseClicked(event -> {
            Family selected = familiesTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                txtHouseholdName.setText(selected.getHouseholdName());
                txtNationalId.setText(selected.getNationalId());
                txtPhone.setText(selected.getPhone());
                txtLocation.setText(selected.getLocation());
                spFamilySize.getValueFactory().setValue(selected.getFamilySize());
                cmbVulnerability.setValue(selected.getVulnerabilityLevel());

                if (selected.getRegistrationDate() != null) {
                    dpRegistrationDate.setValue(selected.getRegistrationDate().toLocalDate());
                }

                if (selected.getLastAidDate() != null) {
                    dpLastAidDate.setValue(selected.getLastAidDate().toLocalDate());
                } else {
                    dpLastAidDate.setValue(null);
                }
            }
        });
    }

    @FXML
    private void handleAddFamily(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        if (familyDAO.nationalIdExists(txtNationalId.getText().trim())) {
            showAlert(Alert.AlertType.WARNING, "Duplicate National ID",
                    "This family already exists in the system.");
            return;
        }

        Family family = buildFamilyFromInputs(0);

        if (familyDAO.addFamily(family)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Family added successfully.");
            clearFields();
            loadFamilies();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add family.");
        }
    }

    @FXML
    private void handleUpdateFamily(ActionEvent event) {
        Family selected = familiesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a family to update.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        Family family = buildFamilyFromInputs(selected.getFamilyId());

        if (familyDAO.updateFamily(family)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Family updated successfully.");
            clearFields();
            loadFamilies();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update family.");
        }
    }

    @FXML
    private void handleDeleteFamily(ActionEvent event) {
        Family selected = familiesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a family to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this family?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (familyDAO.deleteFamily(selected.getFamilyId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Family deleted successfully.");
                clearFields();
                loadFamilies();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete family.");
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
        loadFamilies();
    }

    @FXML
    private void handleSearch(javafx.scene.input.KeyEvent event) {
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            familiesTable.setItems(familyList);
            return;
        }

        ObservableList<Family> filtered = FXCollections.observableArrayList();

        for (Family f : familyList) {
            if (f.getHouseholdName().toLowerCase().contains(keyword)
                    || f.getNationalId().toLowerCase().contains(keyword)
                    || f.getLocation().toLowerCase().contains(keyword)) {
                filtered.add(f);
            }
        }

        familiesTable.setItems(filtered);
    }

    private void loadFamilies() {
        List<Family> list = familyDAO.getAllFamilies();
        familyList = FXCollections.observableArrayList(list);
        familiesTable.setItems(familyList);
    }

    private Family buildFamilyFromInputs(int familyId) {
        Date regDate = Date.valueOf(dpRegistrationDate.getValue());

        Date lastAidDate = null;
        if (dpLastAidDate.getValue() != null) {
            lastAidDate = Date.valueOf(dpLastAidDate.getValue());
        }

        return new Family(
                familyId,
                txtHouseholdName.getText().trim(),
                txtPhone.getText().trim(),
                txtLocation.getText().trim(),
                spFamilySize.getValue(),
                txtNationalId.getText().trim(),
                cmbVulnerability.getValue(),
                regDate,
                lastAidDate
        );
    }

    private boolean validateInputs() {
        if (txtHouseholdName.getText().trim().isEmpty()
                || txtNationalId.getText().trim().isEmpty()
                || txtLocation.getText().trim().isEmpty()
                || cmbVulnerability.getValue() == null
                || dpRegistrationDate.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
            return false;
        }
        String nationalId = txtNationalId.getText().trim();

        if (!nationalId.matches("\\d{9}")) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid National ID",
                    "National ID must contain exactly 9 digits.");
            return false;
        }
        String phone = txtPhone.getText().trim();

        if (!phone.matches("^(059|056)\\d{7}$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Invalid Phone Number",
                    "Phone number must be 10 digits and start with 059 or 056.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        txtHouseholdName.clear();
        txtNationalId.clear();
        txtPhone.clear();
        txtLocation.clear();
        cmbVulnerability.setValue(null);
        spFamilySize.getValueFactory().setValue(1);
        dpRegistrationDate.setValue(LocalDate.now());
        dpLastAidDate.setValue(null);
        familiesTable.getSelectionModel().clearSelection();
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
        }
    }

    @FXML
    private void openChangePassword(ActionEvent event) {
        openPage(event, "ChangePassword.fxml");
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

    private void openReports(ActionEvent event) {
        openPage(event, "Reports.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }

    @FXML
    private void openAidDistribution(ActionEvent event) {
        openPage(event, "AidDistribution.fxml");
    }

    private void openProfile(ActionEvent event) {
        openPage(event, "Profile.fxml");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
    }

}
