package models;

import java.sql.Date;

public class Family {

    private int familyId;
    private String householdName;
    private String phone;
    private String location;
    private int familySize;
    private String nationalId;
    private String vulnerabilityLevel;
    private Date registrationDate;
    private Date lastAidDate;

    public Family() {
    }

    public Family(int familyId, String householdName, String phone, String location,
                  int familySize, String nationalId, String vulnerabilityLevel,
                  Date registrationDate, Date lastAidDate) {
        this.familyId = familyId;
        this.householdName = householdName;
        this.phone = phone;
        this.location = location;
        this.familySize = familySize;
        this.nationalId = nationalId;
        this.vulnerabilityLevel = vulnerabilityLevel;
        this.registrationDate = registrationDate;
        this.lastAidDate = lastAidDate;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

	public int getFamilySize() {
        return familySize;
    }

    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }

	public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

	public String getVulnerabilityLevel() {
        return vulnerabilityLevel;
    }

    public void setVulnerabilityLevel(String vulnerabilityLevel) {
        this.vulnerabilityLevel = vulnerabilityLevel;
    }

	public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

	public Date getLastAidDate() {
        return lastAidDate;
    }

    public void setLastAidDate(Date lastAidDate) {
        this.lastAidDate = lastAidDate;
    }
}