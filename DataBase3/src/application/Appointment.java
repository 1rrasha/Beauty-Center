package application;

import java.time.LocalDateTime;

public class Appointment {
	private int appointment_id;

	public Appointment() {
		super();
	}

	private String appointment_time;
	private int Customer_id;
	private int Service_id;

	public Appointment(int appointment_id, String appointment_time, int Customer_id, int Service_id) {
		this.appointment_id = appointment_id;
		this.appointment_time = appointment_time;
		this.Customer_id = Customer_id;
		this.Service_id = Service_id;
	}

	public int getService_id() {
		return Service_id;
	}

	public void setService_id(int service_id) {
		Service_id = service_id;
	}

	public Appointment(int parseInt, String string) {
		// TODO Auto-generated constructor stub
		this.appointment_id = parseInt;
		this.appointment_time = string;
	}

	public int getAppointment_id() {
		return appointment_id;
	}

	public void setAppointment_id(int appointment_id) {
		this.appointment_id = appointment_id;
	}

	public String getAppointment_time() {
		return appointment_time;
	}

	public void setAppointment_time(String appointment_time) {
		this.appointment_time = appointment_time;
	}

	public int getCustomer_id() {
		return Customer_id;
	}

	public void setCustomer_id(int customer_id) {
		Customer_id = customer_id;
	}

}