package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServiceTable extends Application {

	private TableView<Service> servicesTableView = new TableView<>();
	private Connection connection;
	private TextField idTextField;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeDatabase();
		primaryStage.setTitle("Services Table");

		BorderPane servicesBorderPane = new BorderPane();
		HBox servicesHbox = new HBox();
		VBox servicesVbox = new VBox();

		Label idLabel = new Label("Enter service ID:");
		// Set styles for the label
		idLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Mono Space'; -fx-font-size: 14px;");
		idTextField = new TextField();
		idTextField.setMaxWidth(100);
		Label titleLabel = new Label("Services");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		BorderPane borderPane = new BorderPane();
		HBox hbox = new HBox();
		VBox topVBox = new VBox(titleBox);
		servicesBorderPane.setTop(topVBox);
		Button insertServiceButton = new Button("Insert Service");
		Button deleteServiceButton = new Button("Delete Service");
		Button searchServiceButton = new Button("Search Service");
		Button updateServiceButton = new Button("Update Service");
		Button clearButton = new Button("Clear Data");
		Button sortButton = new Button("Sort");
		Button exportButton = new Button("Export");
		Button refreshButton = new Button("refresh");

		insertServiceButton.setStyle("-fx-font-weight: bold;");
		deleteServiceButton.setStyle("-fx-font-weight: bold;");
		updateServiceButton.setStyle("-fx-font-weight: bold;");
		searchServiceButton.setStyle(" -fx-font-weight: bold;");
		clearButton.setStyle(" -fx-font-weight: bold;");
		sortButton.setStyle(" -fx-font-weight: bold;");
		exportButton.setStyle(" -fx-font-weight: bold;");
		refreshButton.setStyle("-fx-font-weight: bold;");
		// Create a VBox for the left buttons
		VBox leftButtonsVbox = new VBox(insertServiceButton, searchServiceButton, sortButton, exportButton);
		leftButtonsVbox.setSpacing(10);

		// Create a VBox for the right buttons
		VBox rightButtonsVbox = new VBox(updateServiceButton, deleteServiceButton, clearButton, refreshButton);
		rightButtonsVbox.setSpacing(10);

		// Create an HBox to hold both left and right VBox
		HBox buttonsHbox = new HBox(insertServiceButton, searchServiceButton, updateServiceButton, deleteServiceButton,
				exportButton, sortButton, clearButton, refreshButton);
		buttonsHbox.setSpacing(20);

		// Set the alignment of the buttonsHbox to center
		buttonsHbox.setAlignment(Pos.CENTER);

		servicesVbox.getChildren().addAll(idLabel, idTextField, buttonsHbox);
		servicesVbox.setSpacing(15);

		// Set the alignment of the servicesVbox to center
		servicesVbox.setAlignment(Pos.CENTER);

		servicesBorderPane.setBottom(servicesVbox);
		servicesBorderPane.setCenter(servicesTableView);

		servicesBorderPane.setBottom(servicesVbox);
		servicesBorderPane.setCenter(servicesTableView);
		;

		TableColumn<Service, Integer> serviceIdColumn = new TableColumn<>("ID");
		serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Service, String> serviceNameColumn = new TableColumn<>("Name");
		serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Service, String> serviceDescriptionColumn = new TableColumn<>("Description");
		serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

		TableColumn<Service, Integer> serviceDurationColumn = new TableColumn<>("Duration");
		serviceDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

		TableColumn<Service, Double> servicePriceColumn = new TableColumn<>("Price");
		servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		servicesTableView.getColumns().addAll(serviceIdColumn, serviceNameColumn, serviceDescriptionColumn,
				serviceDurationColumn, servicePriceColumn);

		insertServiceButton.setOnAction(event -> showInsertServiceDialog());
		deleteServiceButton.setOnAction(event -> deleteServiceData());

		searchServiceButton.setOnAction(event -> searchServiceData());
		updateServiceButton.setOnAction(event -> showUpdateServiceDialog());
		clearButton.setOnAction(event -> clearServiceData());
		sortButton.setOnAction(event -> sortTable());
		exportButton.setOnAction(event -> exportData());
		refreshButton.setOnAction(event -> refreshTableView1());

		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\service.png").toURI().toString());
		primaryStage.getIcons().add(icon);

		Scene scene = new Scene(servicesBorderPane, 800, 550);

		primaryStage.setScene(scene);
		primaryStage.show();

		loadDataFromServicesTable();
	}

	private void exportData() {
		try (PrintWriter writer = new PrintWriter(new File("services_data.csv"))) {
			StringBuilder sb = new StringBuilder();
			// Append header
			sb.append("ID,Name,Description,Duration,Price\n");

			// Append data
			for (Service service : servicesTableView.getItems()) {
				sb.append(service.getId()).append(",").append(service.getName()).append(",")
						.append(service.getDescription()).append(",").append(service.getDuration()).append(",")
						.append(service.getPrice()).append("\n");
			}

			writer.write(sb.toString());
			showAlert("Export Success", "services data exported to services_data.csv");
		} catch (IOException e) {
			showAlert("Export Error", "Error exporting services data");
			e.printStackTrace();
		}
	}

	private void sortTable() {
		List<TableColumn<Service, ?>> sortOrder = servicesTableView.getSortOrder();

		if (!sortOrder.isEmpty()) {
			TableColumn<Service, ?> selectedColumn = sortOrder.get(0);

			String columnName;
			if (selectedColumn.getCellData(new Service()) instanceof String) {
				columnName = selectedColumn.getText();
			} else {
				// Handle other data types if needed
				columnName = ""; // Adjust as per your requirements
			}

			try {
				String query = "SELECT * FROM services ORDER BY " + columnName;
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet resultSet = ps.executeQuery();

				// Create a new list to hold the sorted items
				List<Service> sortedList = new ArrayList<>();

				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String name = resultSet.getString("name");
					String description = resultSet.getString("description");
					int duration = resultSet.getInt("duration");
					double price = resultSet.getDouble("price");

					Service service = new Service(id, name, description, duration, price);
					sortedList.add(service);
				}

				// Set the sorted list to the table
				servicesTableView.setItems(FXCollections.observableArrayList(sortedList));

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Sort Error", "Error sorting service data");
			}
		}

		showAlert("Sort", "Data are sorted");
	}

	private void clearServiceData() {
		idTextField.clear();
		showAlert("Clear Data", "id cleared successfully.");
	}

	private void showInsertServiceDialog() {
		Dialog<Service> dialog = new Dialog<>();
		dialog.setTitle("Insert Service");
		dialog.setHeaderText("Please enter service details:");

		ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		// Add a required label
		Label requiredLabel = new Label("*Required");
		requiredLabel.setStyle("-fx-text-fill: red;");
		grid.add(requiredLabel, 0, 0, 2, 1); // Spanning two columns

		TextField nameField = new TextField();
		TextField descriptionField = new TextField();
		TextField durationField = new TextField();
		TextField priceField = new TextField();

		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameField, 1, 1);
		grid.add(new Label("Description:"), 0, 2);
		grid.add(descriptionField, 1, 2);
		grid.add(new Label("Duration:"), 0, 3);
		grid.add(durationField, 1, 3);
		grid.add(new Label("Price:"), 0, 4);
		grid.add(priceField, 1, 4);

		dialog.getDialogPane().setContent(grid);

		Node insertButton = dialog.getDialogPane().lookupButton(insertButtonType);
		insertButton.setDisable(true);

		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			insertButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == insertButtonType) {
				return new Service(0, nameField.getText(), descriptionField.getText(),
						Integer.parseInt(durationField.getText()), Double.parseDouble(priceField.getText()));
			}
			return null;
		});

		Optional<Service> result = dialog.showAndWait();
		result.ifPresent(this::insertServiceData);
	}

	private void deleteServiceData() {
		try {
			int id = Integer.parseInt(idTextField.getText());

			String query = "DELETE FROM services WHERE id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success :)", "Deleted: ID " + id);
				idTextField.clear();
				refreshServicesTableView();
			} else {
				showAlert("Error :(", "Service not found.");
			}
		} catch (NumberFormatException | SQLException e) {
			showAlert("Error :(", "Invalid ID or Database error.");
			e.printStackTrace();
		}
	}

	private void initializeDatabase() {
		try {
			System.out.println("Connecting to the database...");
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customer_database_schema", "rasha",
					"1234");
			System.out.println("Database connected successfully.");

			createServicesTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createServicesTable() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS services (id INTEGER PRIMARY KEY AUTO_INCREMENT, name TEXT, description TEXT, duration INTEGER, price DOUBLE)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertServiceData(Service service) {
		String name = service.getName();
		String description = service.getDescription();
		int duration = service.getDuration();
		double price = service.getPrice();

		try {
			String query = "INSERT INTO services (name, description, duration, price) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, description);
			preparedStatement.setInt(3, duration);
			preparedStatement.setDouble(4, price);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					System.out.println("Generated ID: " + generatedId);
				}
				showAlert("Success :)", "Service inserted: " + name);
				idTextField.clear();
				refreshServicesTableView();
			}

		} catch (SQLException e) {
			showAlert("Error :(", "Database error.");
			e.printStackTrace();
		}
	}

	private void showUpdateServiceDialog() {
		try {
			int id = Integer.parseInt(idTextField.getText());

			String query = "SELECT * FROM services WHERE id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				int currentId = resultSet.getInt("id");
				String currentName = resultSet.getString("name");
				String currentDescription = resultSet.getString("description");
				int currentDuration = resultSet.getInt("duration");
				double currentPrice = resultSet.getDouble("price");

				Dialog<Service> dialog = new Dialog<>();
				dialog.setTitle("Update Service");
				dialog.setHeaderText("Please update service details:");

				ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				// Add an optional label
				Label optionalLabel = new Label("*Optional");
				optionalLabel.setStyle("-fx-text-fill: blue;");
				grid.add(optionalLabel, 0, 0, 2, 1); // Spanning two columns

				TextField nameField = new TextField(currentName);
				TextField descriptionField = new TextField(currentDescription);
				TextField durationField = new TextField(String.valueOf(currentDuration));
				TextField priceField = new TextField(String.valueOf(currentPrice));

				grid.add(new Label("Name:"), 0, 1);
				grid.add(nameField, 1, 1);
				grid.add(new Label("Description:"), 0, 2);
				grid.add(descriptionField, 1, 2);
				grid.add(new Label("Duration:"), 0, 3);
				grid.add(durationField, 1, 3);
				grid.add(new Label("Price:"), 0, 4);
				grid.add(priceField, 1, 4);

				dialog.getDialogPane().setContent(grid);

				Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);

				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == updateButtonType) {
						return new Service(currentId, nameField.getText(), descriptionField.getText(),
								Integer.parseInt(durationField.getText()), Double.parseDouble(priceField.getText()));
					}
					return null;
				});

				Optional<Service> result = dialog.showAndWait();
				result.ifPresent(this::updateServiceData);
			} else {
				showAlert("Error :(", "Service not found.");
			}
		} catch (NumberFormatException | SQLException e) {
			showAlert("Error :(", "Invalid ID or Database error.");
			e.printStackTrace();
		}
	}

	private void searchServiceData() {
		String input = idTextField.getText();

		if (input.isEmpty()) {
			showAlert("Error :(", "Please enter a service ID.");
			return;
		}

		try {
			String query;
			if (input.matches("\\d+")) {
				query = "SELECT * FROM services WHERE id = ?";
			} else {
				showAlert("Error :(", "Invalid ID format.");
				return;
			}

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, input);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				int duration = resultSet.getInt("duration");
				double price = resultSet.getDouble("price");

				// Assume you have a mapping of service IDs to image paths
				Map<Integer, String> serviceImageMap = new HashMap<>();
				serviceImageMap.put(1, "C:\\Users\\user\\Desktop\\java\\DataBase3\\Haircut.jpg");
				serviceImageMap.put(2, "C:\\Users\\user\\Desktop\\java\\DataBase3\\Manicure.jpg");
				serviceImageMap.put(3, "C:\\Users\\user\\Desktop\\java\\DataBase3\\massage.jpg");
				serviceImageMap.put(4, "C:\\Users\\user\\Desktop\\java\\DataBase3\\facial.jpg");
				serviceImageMap.put(5, "C:\\Users\\user\\Desktop\\java\\DataBase3\\piducare.jpg");
				serviceImageMap.put(6, "C:\\Users\\user\\Desktop\\java\\DataBase3\\spa.jpg");
				serviceImageMap.put(7, "C:\\Users\\user\\Desktop\\java\\DataBase3\\haircolor.jpg");
				serviceImageMap.put(8, "C:\\Users\\user\\Desktop\\java\\DataBase3\\laiser.jpg");
				serviceImageMap.put(9, "C:\\Users\\user\\Desktop\\java\\DataBase3\\wax.jpg");
				serviceImageMap.put(10, "C:\\Users\\user\\Desktop\\java\\DataBase3\\makeup.jpg");

				String imageIdentifier = Service.getImagePath(); // Get the image identifier based on your mapping
				String imagePath = serviceImageMap.get(id);

				showAlert1("Service Information", "ID: " + id + "\nName: " + name + "\nDescription: " + description
						+ "\nDuration: " + duration + "\nPrice: " + price, imagePath);
				idTextField.clear();
			} else {
				showAlert1("Search Result", "Service not found.", null); // No image path for not found
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while searching for the service.");
		}
	}

	private void showAlert1(String title, String content, String imagePath) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);

		if (imagePath != null) {
			// Create an ImageView with the specified image path
			Image image = new Image(new File(imagePath).toURI().toString());
			ImageView imageView = new ImageView(image);
			imageView.setFitWidth(350); // Adjust the width as needed
			imageView.setFitHeight(200); // Adjust the height as needed

			// Create an icon for the alert
			Image icon = new Image(
					new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\search.png").toURI().toString());
			ImageView iconView = new ImageView(icon);
			iconView.setFitWidth(30); // Adjust the width as needed
			iconView.setFitHeight(30); // Adjust the height as needed

			// Set both the image and icon as graphics for the alert
			VBox graphicBox = new VBox(iconView, imageView);
			alert.getDialogPane().setGraphic(graphicBox);
		}

		alert.showAndWait();
	}

	private void updateServiceData(Service updatedService) {
		int id = updatedService.getId();
		String name = updatedService.getName();
		String description = updatedService.getDescription();
		int duration = updatedService.getDuration();
		double price = updatedService.getPrice();

		try {
			String updateQuery = "UPDATE services SET name=?, description=?, duration=?, price=? WHERE id=?";
			PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
			updateStatement.setString(1, name);
			updateStatement.setString(2, description);
			updateStatement.setInt(3, duration);
			updateStatement.setDouble(4, price);
			updateStatement.setInt(5, id);

			int rowsAffected = updateStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success :)", "Updated: ID " + id);
				refreshServicesTableView();
			} else {
				showAlert("Error :(", "Update failed.");
			}
		} catch (SQLException e) {
			showAlert("Error :(", "Database error.");
			e.printStackTrace();
		}
	}

	private void refreshServicesTableView() {
		servicesTableView.getItems().clear();
		loadDataFromServicesTable();
	}

	private void loadDataFromServicesTable() {
		try {
			String query = "SELECT * FROM services";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				int duration = resultSet.getInt("duration");
				double price = resultSet.getDouble("price");

				Service service = new Service(id, name, description, duration, price);
				servicesTableView.getItems().add(service);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void refreshTableView1() {
		servicesTableView.getItems().clear();
		loadDataFromServicesTable();
		showAlert("done", "table refrished successfully");
	}
}
