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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentTable extends Application {

	private TableView<Appointment> appointmentsTableView = new TableView<>();
	private Connection connection;
	private TextField appointmentIdTextField;
	private String userId;

	public AppointmentTable(String userId) {
		this.userId = userId;
	}

	public AppointmentTable() {

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeDatabase();
		primaryStage.setTitle("Appointments Table");

		BorderPane appointmentsBorderPane = new BorderPane();
		HBox appointmentsHbox = new HBox();
		VBox appointmentsVbox = new VBox();

		Label appointmentIdLabel = new Label("Enter appointment ID:");
		appointmentIdLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Mono Space'; -fx-font-size: 14px;");
		appointmentIdTextField = new TextField();
		appointmentIdTextField.setMaxWidth(100);
		Label titleLabel = new Label("Appointments");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		BorderPane borderPane = new BorderPane();
		HBox hbox = new HBox();
		VBox topVBox = new VBox(titleBox);
		appointmentsBorderPane.setTop(topVBox);

		Button insertAppointmentButton = new Button("Resserve ");
		Button searchAppointmentButton = new Button("Search ");
		Button clearButton = new Button("Clear Data");
		Button exportButton = new Button("Export");
		Button refreshButton = new Button("Refresh");

		// Set styles for buttons
		insertAppointmentButton.setStyle("-fx-font-weight: bold;");
		searchAppointmentButton.setStyle(" -fx-font-weight: bold;");
		clearButton.setStyle(" -fx-font-weight: bold;");
		exportButton.setStyle(" -fx-font-weight: bold;");
		refreshButton.setStyle("-fx-font-weight: bold;");

		// Create an HBox to hold both left and right VBox
		HBox buttonsHbox = new HBox(insertAppointmentButton, searchAppointmentButton, exportButton, clearButton,
				refreshButton);
		buttonsHbox.setSpacing(20);

		// Set the alignment of the buttonsHbox to center
		buttonsHbox.setAlignment(Pos.CENTER);

		appointmentsVbox.getChildren().addAll(appointmentIdLabel, appointmentIdTextField, buttonsHbox);
		appointmentsVbox.setSpacing(15);

		// Set the alignment of the appointmentsVbox to center
		appointmentsVbox.setAlignment(Pos.CENTER);

		appointmentsBorderPane.setBottom(appointmentsVbox);
		appointmentsBorderPane.setCenter(appointmentsTableView);

		appointmentsBorderPane.setBottom(appointmentsVbox);
		appointmentsBorderPane.setCenter(appointmentsTableView);

		TableColumn<Appointment, Integer> appointmentIdColumn = new TableColumn<>("ID");
		appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_id"));

		TableColumn<Appointment, String> appointmentTimeColumn = new TableColumn<>("Time");
		appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_time"));

		TableColumn<Appointment, Integer> customerIdColumn = new TableColumn<>("Customer ID");
		customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("Customer_id"));

		TableColumn<Appointment, Integer> serviceIdColumn = new TableColumn<>("Service ID");
		serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("Service_id"));

		appointmentsTableView.getColumns().addAll(appointmentIdColumn, appointmentTimeColumn, customerIdColumn,
				serviceIdColumn);

		insertAppointmentButton.setOnAction(event -> showInsertAppointmentDialog());
		searchAppointmentButton.setOnAction(event -> searchAppointmentData());
		clearButton.setOnAction(event -> clearAppointmentData());
		exportButton.setOnAction(event -> exportData());
		refreshButton.setOnAction(event -> refreshTableView1());

		appointmentsBorderPane.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));

		Image icon = new Image(
				new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\appoinment.png").toURI().toString());
		primaryStage.getIcons().add(icon);
		Scene scene = new Scene(appointmentsBorderPane, 800, 550);

		primaryStage.setScene(scene);
		primaryStage.show();

		loadDataFromAppointmentsTable();
	}

	private void exportData() {
		try (PrintWriter writer = new PrintWriter(new File("appointments_data.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("ID,Time,CustomerID,ServiceID\n");

			for (Appointment appointment : appointmentsTableView.getItems()) {
				sb.append(appointment.getAppointment_id()).append(",").append(appointment.getAppointment_time())
						.append(",").append(appointment.getCustomer_id()).append(",")
						.append(appointment.getService_id()).append("\n");
			}

			writer.write(sb.toString());
			showAlert("Export Success", "Appointments data exported to appointments_data.csv");
		} catch (IOException e) {
			showAlert("Export Error", "Error exporting appointments data");
			e.printStackTrace();
		}
	}

	private void sortTable() {
		List<TableColumn<Appointment, ?>> sortOrder = appointmentsTableView.getSortOrder();

		if (!sortOrder.isEmpty()) {
			TableColumn<Appointment, ?> selectedColumn = sortOrder.get(0);

			String columnName;
			if (selectedColumn.getCellData(new Appointment()) instanceof String) {
				columnName = selectedColumn.getText();
			} else {
				columnName = ""; // Adjust as per your requirements
			}

			try {
				String query = "SELECT * FROM Appointment ORDER BY " + columnName;
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet resultSet = ps.executeQuery();

				List<Appointment> sortedList = new ArrayList<>();

				while (resultSet.next()) {
					int id = resultSet.getInt("appointment_id");
					String time = resultSet.getString("appointment_time");
					int customerID = resultSet.getInt("Customer_id");
					int serviceID = resultSet.getInt("Service_id");

					Appointment appointment = new Appointment(id, time, customerID, serviceID);
					sortedList.add(appointment);
				}

				appointmentsTableView.setItems(FXCollections.observableArrayList(sortedList));

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Sort Error", "Error sorting appointment data");
			}
		}

		showAlert("Sort", "Data are sorted");
	}

	private void clearAppointmentData() {
		appointmentIdTextField.clear();
		showAlert("Clear Data", "Appointment ID cleared successfully.");
	}

	private void showInsertAppointmentDialog() {
		Dialog<Appointment> dialog = new Dialog<>();
		dialog.setTitle("Insert Appointment");
		dialog.setHeaderText("Please enter appointment details:");

		ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		Label requiredLabel = new Label("*Required");
		requiredLabel.setStyle("-fx-text-fill: red;");
		grid.add(requiredLabel, 0, 0, 2, 1);

		LocalDateTime defaultDateTime = LocalDateTime.now();
		TextField timeField = new TextField(defaultDateTime.toString());
		TextField customerIDField = new TextField();
		TextField serviceIDField = new TextField();

		grid.add(new Label("Time:"), 0, 1);
		grid.add(timeField, 1, 1);
		grid.add(new Label("Customer ID:"), 0, 2);
		grid.add(customerIDField, 1, 2);
		grid.add(new Label("Service ID:"), 0, 3);
		grid.add(serviceIDField, 1, 3);

		dialog.getDialogPane().setContent(grid);

		Node insertButton = dialog.getDialogPane().lookupButton(insertButtonType);
		insertButton.setDisable(true);

		customerIDField.textProperty().addListener((observable, oldValue, newValue) -> {
			insertButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == insertButtonType) {
				// Check for time conflicts with the same service
				if (isTimeAvailableForService(Integer.parseInt(serviceIDField.getText()), timeField.getText())) {
					return new Appointment(0, timeField.getText(), Integer.parseInt(customerIDField.getText()),
							Integer.parseInt(serviceIDField.getText()));
				} else {
					showAlert("Time Conflict", "The selected time is not available for the chosen service.");
				}
			}
			return null;
		});

		Optional<Appointment> result = dialog.showAndWait();
		result.ifPresent(this::insertAppointmentData);
	}

	private boolean isTimeAvailableForService(int serviceId, String selectedTime) {
		try {
			String query = "SELECT * FROM Appointment WHERE Service_id = ? AND appointment_time = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, serviceId);
			preparedStatement.setString(2, selectedTime);
			ResultSet resultSet = preparedStatement.executeQuery();

			return !resultSet.next(); // If there is no result, the time is available
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void initializeDatabase() {
		try {
			System.out.println("Connecting to the database...");
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customer_database_schema", "rasha",
					"1234");
			System.out.println("Database connected successfully");

			createAppointmentsTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createAppointmentsTable() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS Appointment (appointment_id INTEGER PRIMARY KEY AUTO_INCREMENT, "
					+ "appointment_time DATETIME, Customer_id INTEGER, Service_id INTEGER)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertAppointmentData(Appointment appointment) {
		String time = appointment.getAppointment_time();
		int customerID = appointment.getCustomer_id();
		int serviceID = appointment.getService_id();

		try {
			// Omitting the 'Appointment_Id' column as it's auto-incremented
			String query = "INSERT INTO Appointment (appointment_time, Customer_id, Service_id) VALUES (?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, time);
			preparedStatement.setInt(2, customerID);
			preparedStatement.setInt(3, serviceID);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					System.out.println("Generated ID: " + generatedId);
				}
				showAlert("Success :)", "Appointment reserved for Customer ID: " + customerID);
				appointmentIdTextField.clear();
				refreshAppointmentsTableView();
			}

		} catch (SQLException e) {
			showAlert("Error :(", "Database error.");
			e.printStackTrace();
		}
	}

	private void searchAppointmentData() {
		try {
			int id = Integer.parseInt(appointmentIdTextField.getText());

			String query = "SELECT * FROM Appointment WHERE appointment_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String time = resultSet.getString("appointment_time");
				int customerID = resultSet.getInt("Customer_id");
				int serviceID = resultSet.getInt("Service_id");

				showAlert("Appointment Details", String.format("ID: %d\nTime: %s\nCustomer ID: %d\nService ID: %d", id,
						time, customerID, serviceID));
			} else {
				showAlert("Error :(", "Appointment not found.");
			}
		} catch (NumberFormatException | SQLException e) {
			showAlert("Error :(", "Invalid ID or Database error.");
			e.printStackTrace();
		}
	}

	private void refreshAppointmentsTableView() {
		loadDataFromAppointmentsTable();
	}

	private void loadDataFromAppointmentsTable() {
		try {
			String query = "SELECT * FROM Appointment";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			List<Appointment> appointmentsList = new ArrayList<>();

			while (resultSet.next()) {
				int id = resultSet.getInt("appointment_id");
				String time = resultSet.getString("appointment_time");
				int customerID = resultSet.getInt("Customer_id");
				int serviceID = resultSet.getInt("Service_id");

				Appointment appointment = new Appointment(id, time, customerID, serviceID);
				appointmentsList.add(appointment);
			}

			appointmentsTableView.setItems(FXCollections.observableArrayList(appointmentsList));

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
		appointmentsTableView.getItems().clear();
		loadDataFromAppointmentsTable();
		showAlert("done", "table refrished successfully");
	}
}
