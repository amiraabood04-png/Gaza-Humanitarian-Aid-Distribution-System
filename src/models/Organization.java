package models;

public class Organization {

    private int orgId;
    private String name;
    private String type;
    private String contactInfo;

    public Organization() {
    }

    public Organization(int orgId, String name, String type, String contactInfo) {
        this.orgId = orgId;
        this.name = name;
        this.type = type;
        this.contactInfo = contactInfo;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}