package dao;

import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Family;

public class FamilyDAO {

    public boolean addFamily(Family family) {
        String sql = "INSERT INTO Family (household_name, phone, location, family_size, national_id, vulnerability_level, registration_date, last_aid_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setDate(7, family.getRegistrationDate());
            ps.setDate(8, family.getLastAidDate());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean nationalIdExists(String nationalId) {
        String sql = "SELECT * FROM Family WHERE national_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nationalId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Family> getAllFamilies() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM Family";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Family family = new Family(
                        rs.getInt("family_id"),
                        rs.getString("household_name"),
                        rs.getString("phone"),
                        rs.getString("location"),
                        rs.getInt("family_size"),
                        rs.getString("national_id"),
                        rs.getString("vulnerability_level"),
                        rs.getDate("registration_date"),
                        rs.getDate("last_aid_date")
                );

                list.add(family);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateFamily(Family family) {
        String sql = "UPDATE Family SET household_name=?, phone=?, location=?, family_size=?, national_id=?, vulnerability_level=?, registration_date=?, last_aid_date=? WHERE family_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setDate(7, family.getRegistrationDate());
            ps.setDate(8, family.getLastAidDate());
            ps.setInt(9, family.getFamilyId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFamily(int familyId) {
        String sql = "DELETE FROM Family WHERE family_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, familyId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}