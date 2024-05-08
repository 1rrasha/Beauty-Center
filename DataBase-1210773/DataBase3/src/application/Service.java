package application;

import javafx.scene.layout.VBox;

public class Service {
	private int id;
	private String name;
	private String description;
	private int duration;
	private double price;
	private static String imagePath; // New field for image path

	// Example constructor
	public Service(int id, String name, String description, int duration, double price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.duration = duration;
		this.price = price;
	}

	public Service() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public static String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
