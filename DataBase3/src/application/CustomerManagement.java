package application;

import javafx.application.Application;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomerManagement extends Application {
	private PasswordManager passwordManager;
	private String userId;
	TableView<PaymentItem> paymentTable;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		passwordManager = new PasswordManager();

		VBox mainVBox = new VBox();
		mainVBox.setSpacing(20);
		mainVBox.setPadding(new Insets(20));

		// Create the login box
		VBox loginBox = createLoginBox();
		loginBox.setAlignment(Pos.CENTER);

		// Add the login box to the left of the mainVBox
		mainVBox.getChildren().addAll(loginBox);

		// Load the background image
		File file = new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\welcomeCustomer.jpg");
		Image backgroundImage = new Image(file.toURI().toString());

		// Set the background
		BackgroundSize backgroundSize = new BackgroundSize(1000, 500, true, true, true, false);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);

		mainVBox.setBackground(new Background(background));
		mainVBox.setAlignment(Pos.CENTER);

		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\logo.png").toURI().toString());

		// Set the icon for the stage
		primaryStage.getIcons().add(icon);
		Scene scene = new Scene(mainVBox, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Hey Customers");
		primaryStage.show();
	}

	private VBox createLoginBox() {
		VBox loginBox = new VBox();
		loginBox.setAlignment(Pos.CENTER_RIGHT); // Align the login box to the bottom left
		loginBox.setSpacing(10);

		TextField userIdField = new TextField();
		PasswordField passwordField = new PasswordField();
		Button signInButton = new Button("Sign In");
		Button cancelButton = new Button("Cancel");

		double buttonWidth = 150; // Adjust the width as needed
		double buttonHeight = 30; // Adjust the height as needed

		signInButton.setMinSize(buttonWidth, buttonHeight);
		cancelButton.setMinSize(buttonWidth, buttonHeight);

		Label hi = new Label("Please login to to your account : ");
		Label userIdLabel = new Label("User ID : ");
		Label passwordLabel = new Label("Password : ");

		// Set styles for labels
		userIdLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
		passwordLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
		hi.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

		HBox userIdBox = new HBox(userIdLabel, userIdField);
		userIdBox.setAlignment(Pos.CENTER); // Align to the bottom left
		userIdBox.setMaxWidth(Double.MAX_VALUE);
		userIdBox.setPadding(new Insets(10, 0, 0, 0));

		HBox passwordBox = new HBox(passwordLabel, passwordField);
		passwordBox.setAlignment(Pos.CENTER); // Align to the bottom left
		passwordBox.setMaxWidth(Double.MAX_VALUE);
		passwordBox.setPadding(new Insets(10, 0, 0, 0));

		signInButton.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FFB6C1; ");
		cancelButton.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FFB6C1;");

		loginBox.getChildren().addAll(hi, userIdBox, passwordBox, signInButton, cancelButton);

		loginBox.setSpacing(10);
		loginBox.setPadding(new Insets(10));

		signInButton.setOnAction(
				event -> signIn(userIdField.getText(), passwordField.getText(), userIdField, passwordField));
		cancelButton.setOnAction(event -> clearLoginFields(userIdField, passwordField));

		return loginBox;
	}

	private void signIn(String userId, String password, TextField userIdField, PasswordField passwordField) {
		if (passwordManager.authenticateUser(userId, password)) {
			this.userId = userId; // Set the userId
			showMainScreen();
			// Clear the text fields after successful authentication
			userIdField.clear();
			passwordField.clear();
		} else {
			showAlert("Error", "Incorrect User ID or Password :(");
			userIdField.clear();
			passwordField.clear();
		}
	}

	private void showMainScreen() {
		BorderPane mainPane = new BorderPane();

		// Adding welcome label
		Label welcomeLabel = new Label("Welcome to Our Beauty Center");
		welcomeLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: black; -fx-font-weight: bold;");

		HBox titleBox = new HBox(welcomeLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));

		// Centering welcome label at the top
		BorderPane.setAlignment(titleBox, Pos.TOP_CENTER);

		mainPane.setTop(titleBox);

		VBox buttonsBox = new VBox(); // Use VBox for vertical alignment
		buttonsBox.setAlignment(Pos.CENTER); // Center align the buttons

		Button servicesButton = createStyledButton("Our Services");
		Button productsButton = createStyledButton("Our Products");
		Button reserveButton = createStyledButton("Reserve Appointment");
		Button myAppointmentsButton = createStyledButton("My Appointments");
		Button myPaymentsButton = createStyledButton("My Bill");

		// Set button size and style
		double buttonWidth = 150; // Adjust the width as needed
		double buttonHeight = 30; // Adjust the height as needed

		servicesButton.setMinSize(buttonWidth, buttonHeight);
		productsButton.setMinSize(buttonWidth, buttonHeight);
		reserveButton.setMinSize(buttonWidth, buttonHeight);
		myAppointmentsButton.setMinSize(buttonWidth, buttonHeight);
		myPaymentsButton.setMinSize(buttonWidth, buttonHeight);

		// Create the rating label
		Label rating = new Label("Rate our services :");

		// Set styles for the rating label
		rating.setStyle(
				"-fx-font-family: 'Brush Script MT'; -fx-font-size: 20; -fx-text-fill: black; -fx-font-weight: bold;");

		// Create the rating stars box
		HBox ratingBox = createRatingStars();
		ratingBox.setAlignment(Pos.CENTER);
		HBox starss = new HBox();
		starss.getChildren().addAll(rating, ratingBox);
		starss.setSpacing(5);
		starss.setAlignment(Pos.CENTER);

		buttonsBox.getChildren().addAll(servicesButton, productsButton, reserveButton, myAppointmentsButton,
				myPaymentsButton, starss);
		buttonsBox.setSpacing(30);

		mainPane.setCenter(buttonsBox); // Set the buttons in the center
		servicesButton.setOnAction(e -> new ServicesTable2().start(new Stage()));
		productsButton.setOnAction(e -> new ProductsTable(userId).start(new Stage()));
		reserveButton.setOnAction(e -> new AppointmentTable(userId).start(new Stage()));
		myAppointmentsButton.setOnAction(e -> new MyAppointment(userId).start(new Stage()));
		myPaymentsButton.setOnAction(e -> showMyPayments(userId));

		// Load the background image
		File file = new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\beauty.jpg");
		Image backgroundImage = new Image(file.toURI().toString());

		// Set the background
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
		mainPane.setBackground(new Background(background));

		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\logo.png").toURI().toString());

		Scene mainScene = new Scene(mainPane, 600, 400);
		Stage mainStage = new Stage();
		// Set the icon for the stage
		mainStage.getIcons().add(icon);
		mainStage.setScene(mainScene);
		mainStage.setTitle("Customer Management - Main Screen");
		mainStage.show();
	}

	private Button createStyledButton(String text) {
		Button button = new Button(text);
		button.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
		return button;
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void clearLoginFields(TextField userIdField, PasswordField passwordField) {
		userIdField.clear();
		passwordField.clear();
	}

	private static class PasswordManager {
		private static final String JDBC_URL = "jdbc:mysql://localhost:3306/customer_database_schema";
		private static final String DB_USER = "rasha";
		private static final String DB_PASSWORD = "1234";

		public boolean authenticateUser(String userId, String password) {
			// Convert the provided password to lowercase for case-insensitive comparison
			String lowercasePassword = password.toLowerCase();

			try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
				String query = "SELECT COUNT(*) FROM customer WHERE id = ? AND LOWER(name) = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setString(1, userId);
					preparedStatement.setString(2, lowercasePassword);

					ResultSet resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						int count = resultSet.getInt(1);
						return count > 0; // If count is greater than 0, the user is authenticated
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false; // Return false in case of any errors
		}
	}

	private HBox createRatingStars() {
		HBox ratingBox = new HBox();
		ratingBox.setSpacing(10);
		// Create 5 star buttons
		for (int i = 0; i < 5; i++) {
			Button starButton = createStarButton();
			int finalI = i;
			starButton.setOnAction(event -> handleStarClick(finalI + 1)); // +1 because indexing starts from 0
			ratingBox.getChildren().addAll(starButton);
		}

		return ratingBox;
	}

	private Button createStarButton() {
		Image starImage = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\star.png").toURI().toString());

		ImageView starImageView = new ImageView(starImage);
		starImageView.setFitWidth(20); // Adjust the width of the image
		starImageView.setFitHeight(20); // Adjust the height of the image

		Button starButton = new Button("", starImageView);
		starButton.setStyle("-fx-background-color: transparent;");
		return starButton;
	}

	private void handleStarClick(int rating) {
		System.out.println("User rated with " + rating + " stars!");
		showAlert1("Rating", "You rated with " + rating + " stars!", rating);
	}

	private void showAlert1(String title, String content, int rating) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);

		// Create a custom DialogPane
		DialogPane dialogPane = new DialogPane();
		dialogPane.setContentText(content);

		HBox contentBox = new HBox();
		contentBox.setSpacing(10);

		Map<Integer, String> ratingImages = createRatingImageMap();

		// Display the image corresponding to the rating
		if (ratingImages.containsKey(rating)) {
			String imagePath = ratingImages.get(rating);
			Image faceImage = new Image(new File(imagePath).toURI().toString(), 30, 30, true, true);
			ImageView faceImageView = new ImageView(faceImage);
			contentBox.getChildren().add(faceImageView);
		}

		dialogPane.setGraphic(contentBox);
		alert.setDialogPane(dialogPane);

		// Add a listener to handle the close event
		ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(closeButtonType);

		alert.setOnCloseRequest(event -> {
			// Handle the close event if needed
			System.out.println("Dialog closed");
		});

		alert.showAndWait();
	}

	private Map<Integer, String> createRatingImageMap() {
		Map<Integer, String> ratingImages = new HashMap<>();
		// Replace these file paths with the actual paths to your images
		ratingImages.put(5, "C:\\Users\\user\\Desktop\\java\\\\DataBase3\\5.png");
		ratingImages.put(4, "C:\\Users\\user\\Desktop\\java\\DataBase3\\4.png");
		ratingImages.put(3, "C:\\Users\\user\\Desktop\\java\\DataBase3\\3.jpg");
		ratingImages.put(2, "C:\\Users\\user\\Desktop\\java\\DataBase3\\2.png");
		ratingImages.put(1, "C:\\Users\\user\\Desktop\\java\\DataBase3\\1.png");
		return ratingImages;
	}

	private void showMyPayments(String userId) {
		// Create a new stage for displaying payments
		Stage paymentStage = new Stage();
		BorderPane paymentPane = new BorderPane();
		Label titleLabel = new Label("My Bill");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));

		VBox topVBox = new VBox(titleBox);
		paymentPane.setTop(topVBox);
		// Create a table to display payment information
		paymentTable = new TableView<>();
		paymentTable.setEditable(false);

		TableColumn<PaymentItem, String> productNameColumn = new TableColumn<>("Name");
		productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<PaymentItem, Integer> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<PaymentItem, Double> priceColumn = new TableColumn<>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableColumn<PaymentItem, Double> totalColumn = new TableColumn<>("Total");
		totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

		paymentTable.getColumns().addAll(productNameColumn, quantityColumn, priceColumn, totalColumn);

		ObservableList<PaymentItem> paymentItems = getPaymentItems(userId);
		Button totalMoneyButton = new Button("Total Money");
		totalMoneyButton.setStyle("-fx-font-weight: bold;");

		totalMoneyButton.setOnAction(event -> calculateTotalMoney(paymentTable.getItems()));
		HBox totalMoney = new HBox();
		totalMoney.getChildren().add(totalMoneyButton);
		totalMoney.setAlignment(Pos.BOTTOM_RIGHT);
		paymentPane.setBottom(totalMoney);

		paymentTable.setItems(paymentItems);

		paymentPane.setCenter(paymentTable);
		paymentPane.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\payment.png").toURI().toString());
		paymentStage.getIcons().add(icon);

		Scene paymentScene = new Scene(paymentPane, 600, 400);
		paymentStage.setScene(paymentScene);
		paymentStage.setTitle("Customer Bill");
		paymentStage.show();
	}

	private ObservableList<PaymentItem> getPaymentItems(String userId) {
		ObservableList<PaymentItem> paymentItems = FXCollections.observableArrayList();

		// Query the database to get payment information for the logged-in customer
		String cartQuery = "SELECT c.product_id, p.product_name, c.quantity, p.product_price, c.quantity * p.product_price AS total "
				+ "FROM user_cart c " + "JOIN beauty_center_products p ON c.product_id = p.product_id "
				+ "WHERE c.customer_id = ? AND c.is_paid = 0";

		String appointmentQuery = "SELECT a.Appointment_Id, a.Appointment_Time, s.name AS product_name, 1 AS quantity, s.price AS product_price, s.price AS total "
				+ "FROM Appointment a " + "JOIN services s ON a.Service_id = s.id " + "WHERE a.Customer_id = ?";

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customer_database_schema",
				"rasha", "1234");
				PreparedStatement cartStatement = connection.prepareStatement(cartQuery);
				PreparedStatement appointmentStatement = connection.prepareStatement(appointmentQuery)) {

			// Retrieve cart information
			cartStatement.setString(1, userId);
			ResultSet cartResultSet = cartStatement.executeQuery();

			while (cartResultSet.next()) {
				int productId = cartResultSet.getInt("product_id");
				String productName = cartResultSet.getString("product_name");
				int quantity = cartResultSet.getInt("quantity");
				double price = cartResultSet.getDouble("product_price");
				double total = cartResultSet.getDouble("total");

				PaymentItem paymentItem = new PaymentItem(productId, productName, quantity, price, total);
				paymentItems.add(paymentItem);
			}

			// Retrieve appointment information
			appointmentStatement.setString(1, userId);
			ResultSet appointmentResultSet = appointmentStatement.executeQuery();

			while (appointmentResultSet.next()) {
				int appointmentId = appointmentResultSet.getInt("Appointment_Id");
				String appointmentTime = appointmentResultSet.getString("Appointment_Time");
				String productName = appointmentResultSet.getString("product_name");
				int quantity = appointmentResultSet.getInt("quantity");
				double price = appointmentResultSet.getDouble("product_price");
				double total = appointmentResultSet.getDouble("total");

				PaymentItem paymentItem = new PaymentItem(appointmentId, appointmentTime, productName, quantity, price,
						total);
				paymentItems.add(paymentItem);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return paymentItems;
	}

	private void calculateTotalMoney(ObservableList<PaymentItem> items) {
		double totalMoney = items.stream().mapToDouble(PaymentItem::getTotal).sum();
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Total Money");
		alert.setHeaderText(null);
		alert.setContentText("Your Total Money: $" + totalMoney);
		alert.showAndWait();
	}

}
