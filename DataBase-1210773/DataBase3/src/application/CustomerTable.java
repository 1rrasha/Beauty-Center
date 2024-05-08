package application;

//rasha mansour-1210773
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerTable extends Application {

	private TableView<Customer> tableView = new TableView<>();
	private TextField customerNameField;
	private Connection connection;
	private TextField addressField;
	private TextField phoneField;
	private TextField emailField;
	private DatePicker datePicker;
	private ComboBox<String> genderComboBox;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeDatabase();
		primaryStage.setTitle("Customer Table");

		Label titleLabel = new Label("Customer");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		BorderPane borderPane = new BorderPane();
		HBox hbox = new HBox();
		VBox topVBox = new VBox(titleBox);
		borderPane.setTop(topVBox);
		customerNameField = new TextField();
		Label idLabel = new Label("Enter Customer ID:");
		// Set styles for the label
		idLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Mono Space'; -fx-font-size: 14px;");

		Button insertButton = new Button("Insert");
		Button deleteButton = new Button("Delete");
		Button updateButton = new Button("Update");
		Button searchButton = new Button("Search");
		Button clearButton = new Button("Clear");
		Button sortButton = new Button("Sort");
		Button exportButton = new Button("Export");
		Button refreshButton = new Button("Refresh");

		// Set the styles for the buttons
//		insertButton.setStyle("-fx-background-color: Blue; -fx-text-fill: white; -fx-font-weight: bold;"); // Blue
//		deleteButton.setStyle("-fx-background-color: Red; -fx-text-fill: white; -fx-font-weight: bold;"); // Red
//		updateButton.setStyle("-fx-background-color: Orange; -fx-text-fill: white; -fx-font-weight: bold;"); // Orange
//		searchButton.setStyle("-fx-background-color: Green; -fx-text-fill: white; -fx-font-weight: bold;"); // Green
//		clearButton.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-weight: bold;"); // purple
//		sortButton.setStyle("-fx-background-color: pink; -fx-text-fill: white; -fx-font-weight: bold;"); // pink
//		exportButton.setStyle("-fx-background-color: navy; -fx-text-fill: white; -fx-font-weight: bold;"); // navy
//		refreshButton.setStyle("-fx-background-color: cyan; -fx-text-fill: white; -fx-font-weight: bold;"); // cyan
		// Create a spacer to add space between buttons
//		Region spacer = new Region();
//		HBox.setHgrow(spacer, Priority.ALWAYS);

		insertButton.setStyle("-fx-font-weight: bold;");
		deleteButton.setStyle("-fx-font-weight: bold;");
		updateButton.setStyle("-fx-font-weight: bold;");
		searchButton.setStyle(" -fx-font-weight: bold;");
		clearButton.setStyle(" -fx-font-weight: bold;");
		sortButton.setStyle(" -fx-font-weight: bold;");
		exportButton.setStyle(" -fx-font-weight: bold;");
		refreshButton.setStyle("-fx-font-weight: bold;");
		hbox.getChildren().addAll(idLabel, customerNameField, insertButton, deleteButton, updateButton, searchButton,
				clearButton, sortButton, exportButton, refreshButton);
		hbox.setSpacing(15); // Set spacing between nodes
		borderPane.setBottom(hbox);
		borderPane.setCenter(tableView);

		TableColumn<Customer, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Customer, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Customer, String> addressColumn = new TableColumn<>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

		TableColumn<Customer, String> phoneColumn = new TableColumn<>("Phone");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

		TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Customer, LocalDate> dobColumn = new TableColumn<>("Date of Birth");
		dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

		TableColumn<Customer, String> genderColumn = new TableColumn<>("Gender");
		genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

		tableView.getColumns().addAll(idColumn, nameColumn, addressColumn, phoneColumn, emailColumn, dobColumn,
				genderColumn);
		tableView.setStyle("-fx-background-color: navy;");
		tableView.setRowFactory(tv -> {
			javafx.scene.control.TableRow<Customer> row = new javafx.scene.control.TableRow<>();
			row.setStyle("-fx-background-color: white;");
			return row;
		});

		// Handle events to the buttons
		insertButton.setOnAction(event -> showInsertDialog());
		deleteButton.setOnAction(event -> deleteData());
		updateButton.setOnAction(event -> updateData());
		searchButton.setOnAction(event -> searchData());
		clearButton.setOnAction(event -> clearFields());
		sortButton.setOnAction(event -> sortTable());
		exportButton.setOnAction(event -> exportData());

//		Button importButton = new Button("Import");
//		importButton.setOnAction(event -> importData());

		refreshButton.setOnAction(event -> refreshTableView1());

		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\customer.png").toURI().toString());

		// Set the icon for the stage
		primaryStage.getIcons().add(icon);
		borderPane.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));


		Scene scene = new Scene(borderPane, 850, 650);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Load data from the customer database
		refreshTableView();
	}

	private void exportData() {
		try (PrintWriter writer = new PrintWriter(new File("customer_data.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("ID,Name,Address,Phone,Email,DateOfBirth,Gender\n");

			for (Customer customer : tableView.getItems()) {
				sb.append(customer.getId()).append(',').append(customer.getName()).append(',')
						.append(customer.getAddress()).append(',').append(customer.getPhone()).append(',')
						.append(customer.getEmail()).append(',').append(customer.getDateOfBirth()).append(',')
						.append(customer.getGender()).append('\n');
			}

			writer.write(sb.toString());
			showAlert("Export Success", "Customer data exported to customer_data.csv");
		} catch (IOException e) {
			showAlert("Export Error", "Error exporting customer data");
			e.printStackTrace();
		}
	}

	private void sortTable() {
		List<TableColumn<Customer, ?>> sortOrder = tableView.getSortOrder();

		if (!sortOrder.isEmpty()) {
			TableColumn<Customer, ?> selectedColumn = sortOrder.get(0);

			String columnName = selectedColumn.getText(); // Assuming the column text is the same as the database column
															// name

			try {
				String query = "SELECT * FROM customer ORDER BY " + columnName;
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery();

				// Create a new list to hold the sorted items
				List<Customer> sortedList = new ArrayList<>();

				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String name = resultSet.getString("name");
					String address = resultSet.getString("address");
					String phone = resultSet.getString("phone");
					String email = resultSet.getString("email");
					LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
					String gender = resultSet.getString("gender");

					Customer customer = new Customer(id, name, address, phone, email, dateOfBirth, gender);
					sortedList.add(customer);
				}

				// Set the sorted list to the table
				tableView.setItems(FXCollections.observableArrayList(sortedList));

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Sort Error", "Error sorting customer data");
			}
		}

		showAlert("Sort", "Data are sorted");
	}

	private void clearFields() {
		customerNameField.clear();

	}

	private void showInsertDialog() {
		Dialog<Customer> dialog = new Dialog<>();
		dialog.setTitle("Insert Customer");
		dialog.setHeaderText("Please enter customer details:");

		// Set the button types
		ButtonType insertButtonType = new ButtonType("Insert", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

		// Create and set the layout for the dialog
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		// Add a required label
		Label requiredLabel = new Label("*Required");
		requiredLabel.setStyle("-fx-text-fill: red;");
		grid.add(requiredLabel, 0, 0, 2, 1); // Spanning two columns

		TextField nameField = new TextField();
		TextField addressField = new TextField();
		TextField phoneField = new TextField();
		TextField emailField = new TextField();
		DatePicker datePicker = new DatePicker();
		ComboBox<String> genderComboBox = new ComboBox<>();
		genderComboBox.getItems().addAll("Male", "Female");

		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameField, 1, 1);
		grid.add(new Label("Address:"), 0, 2);
		grid.add(addressField, 1, 2);
		grid.add(new Label("Phone:"), 0, 3);
		grid.add(phoneField, 1, 3);
		grid.add(new Label("Email:"), 0, 4);
		grid.add(emailField, 1, 4);
		grid.add(new Label("Date of Birth:"), 0, 5);
		grid.add(datePicker, 1, 5);
		grid.add(new Label("Gender:"), 0, 6);
		grid.add(genderComboBox, 1, 6);

		dialog.getDialogPane().setContent(grid);

		// Enable/Disable insert button depending on whether a name was entered.
		Node insertButton = dialog.getDialogPane().lookupButton(insertButtonType);
		insertButton.setDisable(true);

		// Do some validation (e.g., name is not empty)
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			insertButton.setDisable(newValue.trim().isEmpty());
		});

		// Set the result converter
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == insertButtonType) {
				return new Customer(0, // ID will be assigned by the database
						nameField.getText(), addressField.getText(), phoneField.getText(), emailField.getText(),
						datePicker.getValue(), genderComboBox.getValue());
			}
			return null;
		});

		// Show the dialog and process the result
		Optional<Customer> result = dialog.showAndWait();
		result.ifPresent(customer -> insertCustomerData(customer));
	}

	// method to initialize the data base
	private void initializeDatabase() {
		try {
			System.out.println("Connecting to the database...");
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL JDBC driver
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customer_database_schema", "rasha",
					"1234");

			System.out.println("Database connected successfully.");

			createCustomerTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method to create Customer Table
	private void createCustomerTable() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS customer (id INTEGER PRIMARY KEY AUTO_INCREMENT, name TEXT, "
					+ "address TEXT, phone TEXT, email TEXT, date_of_birth DATE, gender TEXT)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// method to delete data
	private void deleteData() {
		try {
			// Parse the ID from the input field
			int id = Integer.parseInt(customerNameField.getText());

			String query = "DELETE FROM customer WHERE id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success :)", "Deleted: ID " + id);
				customerNameField.clear();
				refreshTableView();
			} else {
				showAlert("Error :(", "Customer not found.");
			}
		} catch (NumberFormatException | SQLException e) {
			showAlert("Error :(", "Invalid ID or Database error.");
			e.printStackTrace();
		}
	}

	private void updateData() {
		try {
			// Parse the ID from the input field
			int id = Integer.parseInt(customerNameField.getText());

			String query = "SELECT * FROM customer WHERE id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				// Customer found, display the information
				int currentId = resultSet.getInt("id");
				String currentName = resultSet.getString("name");
				String currentAddress = resultSet.getString("address");
				String currentPhone = resultSet.getString("phone");
				String currentEmail = resultSet.getString("email");
				LocalDate currentDateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
				String currentGender = resultSet.getString("gender");

				// Show the current data in a dialog for editing
				Dialog<Customer> dialog = new Dialog<>();
				dialog.setTitle("Update Customer");
				dialog.setHeaderText("Please update customer details:");

				// Add an optional label
				Label optionalLabel = new Label("*Optional");
				optionalLabel.setStyle("-fx-text-fill: blue;");
				GridPane.setConstraints(optionalLabel, 0, 0, 2, 1); // Spanning two columns
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.getChildren().add(optionalLabel);

				// Set the button types
				ButtonType updateButtonType = new ButtonType("Update", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

				TextField nameField = new TextField(currentName);
				TextField addressField = new TextField(currentAddress);
				TextField phoneField = new TextField(currentPhone);
				TextField emailField = new TextField(currentEmail);
				DatePicker datePicker = new DatePicker(currentDateOfBirth);
				ComboBox<String> genderComboBox = new ComboBox<>();
				genderComboBox.getItems().addAll("Male", "Female", "Other");
				genderComboBox.setValue(currentGender);

				grid.add(new Label("Name:"), 0, 1);
				grid.add(nameField, 1, 1);
				grid.add(new Label("Address:"), 0, 2);
				grid.add(addressField, 1, 2);
				grid.add(new Label("Phone:"), 0, 3);
				grid.add(phoneField, 1, 3);
				grid.add(new Label("Email:"), 0, 4);
				grid.add(emailField, 1, 4);
				grid.add(new Label("Date of Birth:"), 0, 5);
				grid.add(datePicker, 1, 5);
				grid.add(new Label("Gender:"), 0, 6);
				grid.add(genderComboBox, 1, 6);

				dialog.getDialogPane().setContent(grid);

				// Set the result converter
				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == updateButtonType) {
						return new Customer(currentId, nameField.getText(), addressField.getText(),
								phoneField.getText(), emailField.getText(), datePicker.getValue(),
								genderComboBox.getValue());
					}
					return null;
				});

				// Show the dialog and process the result
				Optional<Customer> result = dialog.showAndWait();
				result.ifPresent(this::updateCustomerData);
				customerNameField.clear(); // Clear the customerNameField
			} else {
				showAlert("Error :(", "Customer not found.");
			}
		} catch (NumberFormatException | SQLException e) {
			showAlert("Error :(", "Invalid ID or Database error.");
			e.printStackTrace();
		}
	}

	// helper method to update data
	private void updateCustomerData(Customer updatedCustomer) {
		// Extract data from the updated Customer object
		int id = updatedCustomer.getId();
		String name = updatedCustomer.getName();
		String address = updatedCustomer.getAddress();
		String phone = updatedCustomer.getPhone();
		String email = updatedCustomer.getEmail();
		LocalDate dateOfBirth = updatedCustomer.getDateOfBirth();
		String gender = updatedCustomer.getGender();

		try {
			// Update the data in the database
			String updateQuery = "UPDATE customer SET name=?, address=?, phone=?, email=?, date_of_birth=?, gender=? WHERE id=?";
			PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
			updateStatement.setString(1, name);
			updateStatement.setString(2, address);
			updateStatement.setString(3, phone);
			updateStatement.setString(4, email);
			updateStatement.setDate(5, java.sql.Date.valueOf(dateOfBirth));
			updateStatement.setString(6, gender);
			updateStatement.setInt(7, id);

			int rowsAffected = updateStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success :)", "Updated: ID " + id);
				refreshTableView();
			} else {
				showAlert("Error :(", "Update failed.");
			}
		} catch (SQLException e) {
			showAlert("Error :(", "Database error.");
			e.printStackTrace();
		}
	}

	// method to show the dialog
	private String promptForInput(String prompt, String defaultValue) {
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setHeaderText(null);
		dialog.setContentText(prompt);

		Optional<String> result = dialog.showAndWait();
		return result.orElse(defaultValue);
	}

	// method to make the user to choose a date for the data
	private LocalDate promptForDate(String prompt, LocalDate defaultValue) {
		DatePicker datePicker = new DatePicker(defaultValue);
		datePicker.setEditable(false);

		Dialog<LocalDate> dialog = new Dialog<>();
		dialog.setTitle(prompt);
		dialog.setHeaderText(null);
		dialog.getDialogPane().setContent(datePicker);

		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				return datePicker.getValue();
			}
			return null;
		});

		Optional<LocalDate> result = dialog.showAndWait();
		return result.orElse(defaultValue);
	}

	// method to show an alert
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	// method to search data
	private void searchData() {
		String input = customerNameField.getText();

		if (input.isEmpty()) {
			showAlert("Error :(", "Please enter a customer name or ID.");
			return;
		}

		try {
			String query;
			if (input.matches("\\d+")) {
				// If input is a number, search by ID
				query = "SELECT * FROM customer WHERE id = ?";
			} else {
				// Otherwise, search by name
				query = "SELECT * FROM customer WHERE LOWER(name) = LOWER(?)";
			}

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, input);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				// Display the customer information
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String address = resultSet.getString("address");
				String phone = resultSet.getString("phone");
				String email = resultSet.getString("email");
				LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
				String gender = resultSet.getString("gender");

				showAlert("Customer Information",
						"ID: " + id + "\nName: " + name + "\nAddress: " + address + "\nPhone: " + phone + "\nEmail: "
								+ email + "\nDate of Birth: " + dateOfBirth + "\nGender: " + gender);
				customerNameField.clear();
			} else {
				showAlert("Search Result", "Customer not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while searching for the customer.");
		}
	}

	private void refreshTableView() {
		tableView.getItems().clear();
		loadDataFromCustomerDatabase();
	}

	private void refreshTableView1() {
		tableView.getItems().clear();
		loadDataFromCustomerDatabase();
		showAlert("done", "table refrished successfully");
	}

	// method to load data
	private void loadDataFromCustomerDatabase() {
		try {
			String query = "SELECT * FROM customer";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String address = resultSet.getString("address");
				String phone = resultSet.getString("phone");
				String email = resultSet.getString("email");
				LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
				String gender = resultSet.getString("gender");

				Customer customer = new Customer(id, name, address, phone, email, dateOfBirth, gender);
				tableView.getItems().add(customer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// method to insert data
	private void insertCustomerData(Customer customer) {
		// Extract data from the Customer object
		String name = customer.getName();
		String address = customer.getAddress();
		String phone = customer.getPhone();
		String email = customer.getEmail();
		LocalDate dateOfBirth = customer.getDateOfBirth();
		String gender = customer.getGender();

		try {
			String query = "INSERT INTO customer (name, address, phone, email, date_of_birth, gender) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, address);
			preparedStatement.setString(3, phone);
			preparedStatement.setString(4, email);
			preparedStatement.setDate(5, java.sql.Date.valueOf(dateOfBirth));
			preparedStatement.setString(6, gender);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					int generatedId = generatedKeys.getInt(1);
					System.out.println("Generated ID: " + generatedId);
				}
				showAlert("Success :)", "Customer inserted: " + name);
				customerNameField.clear();
				refreshTableView();
			}

		} catch (SQLException e) {
			showAlert("Error :(", "Database error.");
			e.printStackTrace();
		}
	}

}
