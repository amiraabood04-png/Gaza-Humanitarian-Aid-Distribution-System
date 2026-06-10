
package dao;

import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Organization;

public class OrganizationDAO {

    public boolean addOrganization(Organization org) {
        String sql = "INSERT INTO Organization (name, type, contact_info) VALUES (?, ?, ?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Organization> getAllOrganizations() {
        List<Organization> list = new ArrayList<>();
        String sql = "SELECT * FROM Organization";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Organization org = new Organization(
                        rs.getInt("org_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("contact_info")
                );
                list.add(org);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateOrganization(Organization org) {
        String sql = "UPDATE Organization SET name=?, type=?, contact_info=? WHERE org_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());
            ps.setInt(4, org.getOrgId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteOrganization(int orgId) {
        String sql = "DELETE FROM Organization WHERE org_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, orgId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public List<Organization> searchOrganizations(String keyword) {
        List<Organization> list = new ArrayList<>();

        String sql = "SELECT * FROM Organization WHERE name LIKE ? OR type LIKE ? OR contact_info LIKE ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Organization org = new Organization(
                        rs.getInt("org_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("contact_info")
                );

                list.add(org);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}