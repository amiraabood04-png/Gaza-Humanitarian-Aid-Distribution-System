package controllers;

import config.DBConnection;
import config.Session;
import dao.AidDistributionDAO;
import dao.FamilyDAO;
import dao.OrganizationDAO;
import dao.UserDAO;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
import models.AidDistribution;
import models.Family;
import models.Organization;
import models.User;

public class AidDistributionController implements Initializable {

    @FXML
    private ComboBox<String> cmbFamily;
    @FXML
    private Button btnAddFamilyInline;
    @FXML
    private ComboBox<String> cmbOrganization;
    @FXML
    private ComboBox<String> cmbAidType;

    @FXML
    private DatePicker dpDistributionDate;
    @FXML
    private ComboBox<String> cmbDistributedBy;

    @FXML
    private Label lblDuplicateMessage;

    @FXML
    private Button btnDistribute;
    @FXML
    private Button btnClear;
    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<AidDistribution> distributionsTable;
    @FXML
    private TableColumn<AidDistribution, Integer> colDistributionId;
    @FXML
    private TableColumn<AidDistribution, String> colFamilyName;
    @FXML
    private TableColumn<AidDistribution, String> colOrganization;
    @FXML
    private TableColumn<AidDistribution, String> colAidType;

    @FXML
    private TableColumn<AidDistribution, Date> colDate;
    @FXML
    private TableColumn<AidDistribution, String> colDistributedBy;

    @FXML
    private Label lblUserName;
    @FXML
    private Label lblRole;

    private AidDistributionDAO aidDAO;
    private FamilyDAO familyDAO;
    private OrganizationDAO organizationDAO;
    private UserDAO userDAO;

    private ObservableList<AidDistribution> distributionList;

    private HashMap<String, Integer> familyMap = new HashMap<>();
    private HashMap<Integer, String> familyNameMap = new HashMap<>();
    private HashMap<Integer, String> familyVulnerabilityMap = new HashMap<>();

    private HashMap<String, Integer> organizationMap = new HashMap<>();
    private HashMap<Integer, String> organizationNameMap = new HashMap<>();

    private HashMap<String, Integer> userMap = new HashMap<>();
    private HashMap<Integer, String> userNameMap = new HashMap<>();
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnFilter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(lblUserName);
        System.out.println(lblRole);
        lblUserName.setText(Session.fullName);
        lblRole.setText(Session.role);

        aidDAO = new AidDistributionDAO();
        familyDAO = new FamilyDAO();
        organizationDAO = new OrganizationDAO();
        userDAO = new UserDAO();

        cmbAidType.setItems(FXCollections.observableArrayList(
                "Food Basket",
                "Cash Assistance",
                "Medical Aid",
                "Clothes",
                "Water",
                "Hygiene Kit"
        ));

       
        dpDistributionDate.setValue(LocalDate.now());

        loadFamiliesCombo();
        loadOrganizationsCombo();
        loadUsersCombo();

        colDistributionId.setCellValueFactory(new PropertyValueFactory<>("distributionId"));

        colFamilyName.setCellValueFactory(data
                -> new SimpleStringProperty(
                        familyNameMap.getOrDefault(data.getValue().getFamilyId(), String.valueOf(data.getValue().getFamilyId()))
                )
        );

        colOrganization.setCellValueFactory(data
                -> new SimpleStringProperty(
                        organizationNameMap.getOrDefault(data.getValue().getOrgId(), String.valueOf(data.getValue().getOrgId()))
                )
        );

        colAidType.setCellValueFactory(data -> new SimpleStringProperty("-"));
       

        colDate.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));

        colDistributedBy.setCellValueFactory(data
                -> new SimpleStringProperty(
                        userNameMap.getOrDefault(data.getValue().getDistributedBy(), String.valueOf(data.getValue().getDistributedBy()))
                )
        );

        

        loadDistributions();
    }

    private void loadFamiliesCombo() {
        List<Family> families = familyDAO.getAllFamilies();
        ObservableList<String> names = FXCollections.observableArrayList();

        familyMap.clear();
        familyNameMap.clear();
        familyVulnerabilityMap.clear();

        for (Family f : families) {
            String display = f.getHouseholdName() + " - " + f.getNationalId();

            names.add(display);
            familyMap.put(display, f.getFamilyId());
            familyNameMap.put(f.getFamilyId(), f.getHouseholdName());
            familyVulnerabilityMap.put(f.getFamilyId(), f.getVulnerabilityLevel());
        }

        cmbFamily.setItems(names);
    }

    private void loadOrganizationsCombo() {
        List<Organization> organizations = organizationDAO.getAllOrganizations();
        ObservableList<String> names = FXCollections.observableArrayList();

        organizationMap.clear();
        organizationNameMap.clear();

        for (Organization org : organizations) {
            names.add(org.getName());
            organizationMap.put(org.getName(), org.getOrgId());
            organizationNameMap.put(org.getOrgId(), org.getName());
        }

        cmbOrganization.setItems(names);
    }

    private void loadUsersCombo() {
        List<User> users = userDAO.getAllUsers();
        ObservableList<String> names = FXCollections.observableArrayList();

        userMap.clear();
        userNameMap.clear();

        for (User u : users) {
            names.add(u.getFullName());
            userMap.put(u.getFullName(), u.getUserId());
            userNameMap.put(u.getUserId(), u.getFullName());
        }

        cmbDistributedBy.setItems(names);
    }

    private void loadDistributions() {
        distributionList = FXCollections.observableArrayList(aidDAO.getAllAidDistributions());
        distributionsTable.setItems(distributionList);
    }

    @FXML
    private void handleDistribute(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        int familyId = familyMap.get(cmbFamily.getValue());
        int orgId = organizationMap.get(cmbOrganization.getValue());
        int distributedBy = userMap.get(cmbDistributedBy.getValue());

        String vulnerability = familyVulnerabilityMap.get(familyId);

        boolean receivedLast30Days = aidDAO.hasReceivedAidLast30Days(familyId);

        if (receivedLast30Days
                && vulnerability != null
                && (vulnerability.equalsIgnoreCase("MEDIUM") || vulnerability.equalsIgnoreCase("LOW"))) {

            lblDuplicateMessage.setText(
                    "Rejected: This family already received aid within the last 30 days.\n"
                    + "Vulnerability Level: " + vulnerability
            );

            showAlert(Alert.AlertType.WARNING,
                    "Duplicate Aid",
                    "This distribution is rejected because the family received aid within the last 30 days.");

            return;
        }

        AidDistribution aid = new AidDistribution(
                0,
                familyId,
                orgId,
                distributedBy,
                Date.valueOf(dpDistributionDate.getValue())
        );

        if (aidDAO.addAidDistribution(aid)) {
            lblDuplicateMessage.setText("Allowed: Aid distribution saved successfully.");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Aid distributed successfully.");
            clearFields();
            loadDistributions();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save aid distribution.");
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        clearFields();
        loadFamiliesCombo();
        loadOrganizationsCombo();
        loadUsersCombo();
        loadDistributions();
    }

   @FXML
private void handleFilter(ActionEvent event) {

    String keyword = txtSearch.getText().toLowerCase().trim();

    if (keyword.isEmpty()) {
        distributionsTable.setItems(distributionList);
        return;
    }

    ObservableList<AidDistribution> filtered = FXCollections.observableArrayList();

    for (AidDistribution aid : distributionList) {

        String orgName =
                organizationNameMap.getOrDefault(aid.getOrgId(), "").toLowerCase();

        if (orgName.contains(keyword)) {
            filtered.add(aid);
        }
    }

    distributionsTable.setItems(filtered);
}
    private boolean validateInputs() {
        if (cmbFamily.getValue() == null
                || cmbOrganization.getValue() == null
                || cmbAidType.getValue() == null
                || cmbDistributedBy.getValue() == null
                || dpDistributionDate.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        cmbFamily.setValue(null);
        cmbOrganization.setValue(null);
        cmbAidType.setValue(null);
        cmbDistributedBy.setValue(null);
      
        dpDistributionDate.setValue(LocalDate.now());
       
        txtSearch.clear();
        lblDuplicateMessage.setText("System will check if this family has already received this aid type.");
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
