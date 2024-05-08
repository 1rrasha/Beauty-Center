package application;

public class PaymentItem {
	private int id; // Product ID or Appointment ID
	private String name; // Product name or Appointment information
	private int quantity;
	private double price;
	private double total;

	// Constructor for product payment item
	public PaymentItem(int id, String name, int quantity, double price, double total) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.total = total;
	}

	// Constructor for appointment payment item
	public PaymentItem(int id, String time, String name, int quantity, double price, double total) {
		this.id = id;
		this.name = time + " - " + name; // Combine time and service name for appointment
		this.quantity = quantity;
		this.price = price;
		this.total = total;
	}

	// Getters
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public double getTotal() {
		return total;
	}
}
