package dao;

import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.AidDistribution;

public class AidDistributionDAO {

    public boolean addAidDistribution(AidDistribution aid) {
        String sql = "INSERT INTO AidDistribution (family_id, org_id, distributed_by, distribution_date) VALUES (?, ?, ?, ?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, aid.getFamilyId());
            ps.setInt(2, aid.getOrgId());
            ps.setInt(3, aid.getDistributedBy());
            ps.setDate(4, aid.getDistributionDate());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<AidDistribution> getAllAidDistributions() {
        List<AidDistribution> list = new ArrayList<>();
        String sql = "SELECT * FROM AidDistribution";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AidDistribution aid = new AidDistribution(
                        rs.getInt("distribution_id"),
                        rs.getInt("family_id"),
                        rs.getInt("org_id"),
                        rs.getInt("distributed_by"),
                        rs.getDate("distribution_date")
                );

                list.add(aid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean hasReceivedAidLast30Days(int familyId) {
        String sql = "SELECT * FROM AidDistribution WHERE family_id=? AND distribution_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, familyId);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteAidDistribution(int distributionId) {
        String sql = "DELETE FROM AidDistribution WHERE distribution_id=?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, distributionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}