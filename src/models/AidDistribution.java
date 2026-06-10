package models;

import java.sql.Date;

public class AidDistribution {

    private int distributionId;
    private int familyId;
    private int orgId;
    private int distributedBy;
    private Date distributionDate;

    public AidDistribution() {
    }

    public AidDistribution(int distributionId, int familyId, int orgId, int distributedBy, Date distributionDate) {
        this.distributionId = distributionId;
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.distributionDate = distributionDate;
    }

    public int getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(int distributionId) {
        this.distributionId = distributionId;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

	public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

	public int getDistributedBy() {
        return distributedBy;
    }

    public void setDistributedBy(int distributedBy) {
        this.distributedBy = distributedBy;
    }

	public Date getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }
}