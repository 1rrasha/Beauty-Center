package application;

public class Staff {

	private int Staff_id;
	private String firstName;
	private String lastName;
	private int phone_number;
	private String Email;
	private String dateOfBirth;
	private String HireDate;
	private String Gendr;
	private double salry;
	private String section;

	public Staff(int staff_id, String firstName, String lastName, int phone_number, String email, String dateOfBirth,
			String hireDate, String gendr, double salry, String section) {

		Staff_id = staff_id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone_number = phone_number;
		Email = email;
		this.dateOfBirth = dateOfBirth;
		HireDate = hireDate;
		Gendr = gendr;
		this.salry = salry;
		this.section = section;
	}

	public int getStaff_id() {
		return Staff_id;
	}

	public void setStaff_id(int staff_id) {
		Staff_id = staff_id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(int phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getHireDate() {
		return HireDate;
	}

	public void setHireDate(String hireDate) {
		HireDate = hireDate;
	}

	public String getGendr() {
		return Gendr;
	}

	public void setGendr(String gendr) {
		Gendr = gendr;
	}

	public double getSalry() {
		return salry;
	}

	public void setSalry(double salry) {
		this.salry = salry;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

}