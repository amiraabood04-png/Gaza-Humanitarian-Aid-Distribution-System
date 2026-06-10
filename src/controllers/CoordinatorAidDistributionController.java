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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.AidDistribution;
import models.Family;
import models.Organization;
import models.User;

public class CoordinatorAidDistributionController implements Initializable {

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
    private Button btnRefresh;

    @FXML
    private Button btnShowHigh;
    @FXML
    private Button btnShowNotServed;
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
    private Label lblCoordinatorName;
    @FXML
    private Label lblWelcomeName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aidDAO = new AidDistributionDAO();
        familyDAO = new FamilyDAO();
        organizationDAO = new OrganizationDAO();
        userDAO = new UserDAO();
        lblCoordinatorName.setText(Session.fullName);
        lblWelcomeName.setText(Session.fullName);

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

            String familyName = familyNameMap.getOrDefault(familyId, "Unknown Family");
            String lastAidInfo = getLastAidInfo(familyId);

            String alertMessage
                    = "Aid Distribution Rejected\n\n"
                    + "Family Name: " + familyName + "\n"
                    + "Vulnerability Level: " + vulnerability + "\n"
                    + lastAidInfo;

            showAlert(Alert.AlertType.WARNING,
                    "Duplicate Aid",
                    alertMessage);

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

    private boolean hasFamilyReceivedAid(int familyId) {
        return aidDAO.hasReceivedAidLast30Days(familyId);
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
        lblDuplicateMessage.setText("System will check if this family has already received this aid type.");
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
        openPage(event, "CoordinatorChangePassword.fxml");
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
    private void handleShowHighFamilies(ActionEvent event) {

        ObservableList<AidDistribution> filtered
                = FXCollections.observableArrayList();

        for (AidDistribution aid : distributionList) {

            String level = getFamilyVulnerability(aid.getFamilyId());

            if ("HIGH".equalsIgnoreCase(level)) {
                filtered.add(aid);
            }
        }

        distributionsTable.setItems(filtered);
    }

    @FXML
    private void handleShowNotServedFamilies(ActionEvent event) {
        String sql = "SELECT household_name, national_id, vulnerability_level, location "
                + "FROM family WHERE family_id NOT IN "
                + "(SELECT DISTINCT family_id FROM aiddistribution)";

        StringBuilder message = new StringBuilder();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                message.append("Family Name: ").append(rs.getString("household_name"))
                        .append("\nNational ID: ").append(rs.getString("national_id"))
                        .append("\nVulnerability: ").append(rs.getString("vulnerability_level"))
                        .append("\nLocation: ").append(rs.getString("location"))
                        .append("\n----------------------\n");
            }

            if (message.length() == 0) {
                message.append("All families have received aid.");
            }

            showAlert(Alert.AlertType.INFORMATION,
                    "Not Served Families",
                    message.toString());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Cannot load not served families.");
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
    private void handleLogout(ActionEvent event) {
        openPage(event, "Login.fxml");
    }

    @FXML
    private void openProfile(ActionEvent event) {
        openPage(event, "Profile.fxml");
    }

    private String getLastAidInfo(int familyId) {
        String sql = "SELECT o.name, ad.distribution_date "
                + "FROM aiddistribution ad "
                + "JOIN organization o ON ad.org_id = o.org_id "
                + "WHERE ad.family_id = ? "
                + "ORDER BY ad.distribution_date DESC "
                + "LIMIT 1";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, familyId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "Organization: " + rs.getString("name") + "\n"
                        + "Aid Date: " + rs.getDate("distribution_date");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Organization: Unknown\nAid Date: Unknown";
    }

    private String getFamilyVulnerability(int familyId) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT vulnerability_level FROM family WHERE family_id=?"
            );

            ps.setInt(1, familyId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("vulnerability_level");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "-";
    }
}
