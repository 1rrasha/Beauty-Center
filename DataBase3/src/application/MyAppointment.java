package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class MyAppointment extends Application {

	private TableView<Appointment> appointmentsTableView = new TableView<>();
	private Connection connection;
	private String userId;

	public MyAppointment(String userId) {
		this.userId = userId;
	}

	public MyAppointment() {

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeDatabase();
		primaryStage.setTitle("Customer Appointments");

		BorderPane appointmentsBorderPane = new BorderPane();
		VBox appointmentsVbox = new VBox();

		Label titleLabel = new Label("MY Appointments");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		VBox topVBox = new VBox(titleBox);
		appointmentsBorderPane.setTop(topVBox);

		Button refreshButton = new Button("Refresh");
		refreshButton.setStyle("-fx-font-weight: bold;");

		VBox rightButtonsVbox = new VBox(refreshButton);
		rightButtonsVbox.setSpacing(10);

		HBox buttonsHbox = new HBox(refreshButton);
		buttonsHbox.setSpacing(20);
		buttonsHbox.setAlignment(Pos.CENTER);

		appointmentsVbox.getChildren().addAll(buttonsHbox);
		appointmentsVbox.setSpacing(15);

		appointmentsVbox.setAlignment(Pos.CENTER);

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

		refreshButton.setOnAction(event -> refreshTableView1());

		appointmentsBorderPane.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));

		Scene scene = new Scene(appointmentsBorderPane, 800, 550);
		Image icon = new Image(
				new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\myAppointment.jpg").toURI().toString());
		primaryStage.getIcons().add(icon);

		primaryStage.setScene(scene);
		primaryStage.show();

		loadDataFromAppointmentsTable();
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

	private void loadDataFromAppointmentsTable() {
		try {
			String query = "SELECT * FROM Appointment WHERE Customer_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, Integer.parseInt(userId));
			ResultSet resultSet = preparedStatement.executeQuery();

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
