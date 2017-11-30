package springbatch_example.model;

public class APSUploadFile {
	private String givenName;
	private String lastName;
	private String email;
	private String DID;
	private String bldg;
	private String location;
	private String officialMobile;
	
	public APSUploadFile(){}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDID() {
		return DID;
	}

	public void setDID(String dID) {
		DID = dID;
	}

	public String getBldg() {
		return bldg;
	}

	public void setBldg(String bldg) {
		this.bldg = bldg;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOfficialMobile() {
		return officialMobile;
	}

	public void setOfficialMobile(String officialMobile) {
		this.officialMobile = officialMobile;
	}
}
