package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductsTable extends Application {

	private TableView<Product> productsTableView = new TableView<>();
	private Connection connection;
	private TextField idTextField;
	private String userId;

	public ProductsTable(String userId) {
		this.userId = userId;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeDatabase();
		primaryStage.setTitle("Products Table");

		BorderPane productsBorderPane = new BorderPane();
		HBox productsHbox = new HBox();
		VBox productsVbox = new VBox();

		Label idLabel = new Label("Enter product ID:");
		idLabel.setStyle("-fx-font-weight: bold; -fx-font-family: 'Mono Space'; -fx-font-size: 14px;");
		idTextField = new TextField();
		idTextField.setMaxWidth(100);
		Label titleLabel = new Label("Products");
		titleLabel.setStyle(
				"-fx-font-family: 'Verdana'; -fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

		HBox titleBox = new HBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
		BorderPane borderPane = new BorderPane();
		HBox hbox = new HBox();
		VBox topVBox = new VBox(titleBox);
		productsBorderPane.setTop(topVBox);
		Button searchProductButton = new Button("Search Product");
		Button clearButton = new Button("Clear Data");
		Button sortButton = new Button("Sort");
		Button exportButton = new Button("Export");
		Button refreshButton = new Button("Refresh");
		Button myCartButton = new Button("My Cart");
		myCartButton.setStyle(" -fx-font-weight: bold;");
		myCartButton.setOnAction(event -> showCart());

		searchProductButton.setStyle(" -fx-font-weight: bold;");
		clearButton.setStyle(" -fx-font-weight: bold;");
		sortButton.setStyle(" -fx-font-weight: bold;");
		exportButton.setStyle(" -fx-font-weight: bold;");
		refreshButton.setStyle("-fx-font-weight: bold;");

		HBox buttonsHbox = new HBox(searchProductButton, exportButton, sortButton, clearButton, refreshButton,
				myCartButton);
		buttonsHbox.setSpacing(20);
		buttonsHbox.setAlignment(Pos.CENTER);

		productsVbox.getChildren().addAll(idLabel, idTextField, buttonsHbox);
		productsVbox.setSpacing(15);
		productsVbox.setAlignment(Pos.CENTER);

		productsBorderPane.setBottom(productsVbox);
		productsBorderPane.setCenter(productsTableView);

		productsBorderPane.setBottom(productsVbox);
		productsBorderPane.setCenter(productsTableView);

		TableColumn<Product, Integer> productIdColumn = new TableColumn<>("ID");
		productIdColumn.setCellValueFactory(new PropertyValueFactory<>("produtc_id"));

		TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
		productNameColumn.setCellValueFactory(new PropertyValueFactory<>("Product_Name"));

		TableColumn<Product, Double> productPriceColumn = new TableColumn<>("Price");
		productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Product_Price"));

		TableColumn<Product, String> productDescriptionColumn = new TableColumn<>("Description");
		productDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Product_descreption"));

		productsTableView.getColumns().addAll(productIdColumn, productNameColumn, productPriceColumn,
				productDescriptionColumn);

		productIdColumn.setCellFactory(column -> {
			TableCell<Product, Integer> cell = new TableCell<Product, Integer>() {
				@Override
				protected void updateItem(Integer item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item.toString());
				}
			};

			cell.setOnMouseClicked(event -> {
				if (!cell.isEmpty() && event.getClickCount() == 2) {
					Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
					showAddToCartDialog(selectedProduct);
				}
			});

			return cell;
		});

		searchProductButton.setOnAction(event -> searchProductData());
		clearButton.setOnAction(event -> clearProductData());
		sortButton.setOnAction(event -> sortTable());
		exportButton.setOnAction(event -> exportData());
		refreshButton.setOnAction(event -> refreshTableView());

		Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\product.png").toURI().toString());
		primaryStage.getIcons().add(icon);

		productsBorderPane.setBackground(
				new Background(new BackgroundFill(javafx.scene.paint.Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));

		Scene scene = new Scene(productsBorderPane, 500, 550);

		primaryStage.setScene(scene);
		primaryStage.show();

		loadDataFromProductsTable();
	}

	private void exportData() {
		try (PrintWriter writer = new PrintWriter(new File("products_data.csv"))) {
			StringBuilder sb = new StringBuilder();
			sb.append("ID,Name,Price,Description\n");

			for (Product product : productsTableView.getItems()) {
				sb.append(product.getProdutc_id()).append(",").append(product.getProduct_Name()).append(",")
						.append(product.getProduct_Price()).append(",").append(product.getProduct_descreption())
						.append("\n");
			}

			writer.write(sb.toString());
			showAlert("Export Success", "products data exported to products_data.csv");
		} catch (IOException e) {
			showAlert("Export Error", "Error exporting products data");
			e.printStackTrace();
		}
	}

	private void sortTable() {
		List<TableColumn<Product, ?>> sortOrder = productsTableView.getSortOrder();

		if (!sortOrder.isEmpty()) {
			TableColumn<Product, ?> selectedColumn = sortOrder.get(0);

			String columnName;
			if (selectedColumn.getCellData(new Product()) instanceof String) {
				columnName = selectedColumn.getText();
			} else {
				// Handle other data types if needed
				columnName = ""; // Adjust as per your requirements
			}

			try {
				String query = "SELECT * FROM beauty_center_products ORDER BY " + columnName;
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet resultSet = ps.executeQuery();

				// Create a new list to hold the sorted items
				List<Product> sortedList = new ArrayList<>();

				while (resultSet.next()) {
					int id = resultSet.getInt("product_id");
					String name = resultSet.getString("product_name");
					double price = resultSet.getDouble("product_price");
					String description = resultSet.getString("product_description");

					Product product = new Product(id, name, price, description);
					sortedList.add(product);
				}

				// Set the sorted list to the table
				productsTableView.setItems(FXCollections.observableArrayList(sortedList));

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Sort Error", "Error sorting product data");
			}
		}

		showAlert("Sort", "Data are sorted");
	}

	private void searchProductData() {
		String input = idTextField.getText();

		if (input.isEmpty()) {
			showAlert("Error :(", "Please enter a product ID.");
			return;
		}

		try {
			String query;
			if (input.matches("\\d+")) {
				query = "SELECT * FROM beauty_center_products WHERE product_id = ?"; // Correct table name and column
																						// name
			} else {
				showAlert("Error :(", "Invalid ID format.");
				return;
			}

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, input);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				int id = resultSet.getInt("product_id");
				String name = resultSet.getString("product_name");
				double price = resultSet.getDouble("product_price");
				String description = resultSet.getString("product_description");

				Map<Integer, String> ProductImageMap = new HashMap<>();
				ProductImageMap.put(1, "C:\\Users\\user\\Desktop\\java\\DataBase3\\Shampoo.jpg");
				ProductImageMap.put(2, "C:\\Users\\user\\Desktop\\java\\DataBase3\\conditioner.jpg");
				ProductImageMap.put(3, "C:\\Users\\user\\Desktop\\java\\DataBase3\\moistriser.jpg");
				ProductImageMap.put(4, "C:\\Users\\user\\Desktop\\java\\DataBase3\\cleanser.jpg");
				ProductImageMap.put(5, "C:\\Users\\user\\Desktop\\java\\DataBase3\\Sunscreen.png");
				ProductImageMap.put(6, "C:\\Users\\user\\Desktop\\java\\DataBase3\\body.jpg");
				ProductImageMap.put(7, "C:\\Users\\user\\Desktop\\java\\DataBase3\\mancire.jpg");
				ProductImageMap.put(8, "C:\\Users\\user\\Desktop\\java\\DataBase3\\hair.jpg");
				ProductImageMap.put(9, "C:\\Users\\user\\Desktop\\java\\DataBase3\\perfume.jpg");
				ProductImageMap.put(10, "C:\\Users\\user\\Desktop\\java\\DataBase3\\pallete.jpg");

				String imageIdentifier = Service.getImagePath(); // Get the image identifier based on your mapping
				String imagePath = ProductImageMap.get(id);

				showAlert1("Product Information",
						"ID: " + id + "\nName: " + name + "\nPrice: " + price + "\nDescription: " + description,
						imagePath);
				idTextField.clear();
			} else {
				showAlert("Search Result", "Product not found.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while searching for the product.");
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

	private void clearProductData() {
		idTextField.clear();
		showAlert("Clear Data", "ID cleared successfully.");
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void refreshTableView() {
		productsTableView.getItems().clear();
		loadDataFromProductsTable();
		showAlert("Done", "Table refreshed successfully");
	}

	private void loadDataFromProductsTable() {
		try {
			String query = "SELECT * FROM beauty_center_products"; // Correct table name
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("product_id");
				String name = resultSet.getString("product_name");
				double price = resultSet.getDouble("product_price");
				String description = resultSet.getString("product_description");

				// Create a Product object and add it to your TableView
				Product product = new Product(id, name, price, description);
				productsTableView.getItems().add(product);
			}
		} catch (SQLException e) {
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

			createProductsTable();
			createCartTable(); // Add this line to create the cart table
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createCartTable() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS user_cart (" + "cart_id INTEGER PRIMARY KEY AUTO_INCREMENT, "
					+ "product_id INTEGER, " + "quantity INTEGER, "
					+ "FOREIGN KEY (product_id) REFERENCES beauty_center_products(product_id))";

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addToCart(Product product, int quantity) {
		try {
			String query = "INSERT INTO user_cart (product_id, quantity, customer_id) VALUES (?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, product.getProdutc_id());
			preparedStatement.setInt(2, quantity);
			preparedStatement.setString(3, userId); // Use the stored customer ID
			preparedStatement.executeUpdate();

			showAlert("Add to Cart", "Product added to cart successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while adding the product to the cart.");
		}
	}

	private void showAddToCartDialog(Product product) {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Add to Cart");
		dialog.setHeaderText(null);
		dialog.setContentText("Enter quantity:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(quantity -> addToCart(product, Integer.parseInt(quantity)));
	}

	private void createProductsTable() {
		try {
			// Create beauty_center_products table
			String productsQuery = "CREATE TABLE IF NOT EXISTS beauty_center_products ("
					+ "product_id INTEGER PRIMARY KEY AUTO_INCREMENT, " + "product_name TEXT, "
					+ "product_price DECIMAL(10, 2), " + "product_description TEXT)";

			PreparedStatement productsStatement = connection.prepareStatement(productsQuery);
			productsStatement.executeUpdate();

			// Create user_cart table with customer_id column and foreign key constraint
			String cartQuery = "CREATE TABLE IF NOT EXISTS user_cart (" + "cart_id INTEGER PRIMARY KEY AUTO_INCREMENT, "
					+ "product_id INTEGER, " + "quantity INTEGER, " + "customer_id INT, "
					+ "FOREIGN KEY (product_id) REFERENCES beauty_center_products(product_id), "
					+ "FOREIGN KEY (customer_id) REFERENCES customer(id))";

			PreparedStatement cartStatement = connection.prepareStatement(cartQuery);
			cartStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showCart() {
		try {
			// Fetch cart data from the database based on the logged-in customer
			String query = "SELECT b.product_name, b.product_price, c.quantity FROM user_cart c "
					+ "JOIN beauty_center_products b ON c.product_id = b.product_id " + "WHERE c.customer_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();

			VBox cartVBox = new VBox();
			cartVBox.setStyle("-fx-background-image: url('file:/C:/Users/user/Desktop/java/DataBase3/cartb.png'); "
					+ "-fx-background-size: cover;");

			cartVBox.setSpacing(10);
			cartVBox.setSpacing(10);

			double totalAmount = 0;

			while (resultSet.next()) {
				String productName = resultSet.getString("product_name");
				double productPrice = resultSet.getDouble("product_price");
				int quantity = resultSet.getInt("quantity");

				HBox cartItemHBox = new HBox();
				cartItemHBox.setAlignment(Pos.CENTER_LEFT);

				Label cartItemLabel = new Label(productName + " x" + quantity + " - $" + productPrice * quantity);

				TextField deleteQuantityTextField = new TextField();
				deleteQuantityTextField.setPromptText("Delete Quantity");
				deleteQuantityTextField.setVisible(false);

				Button deleteButton = new Button("Delete");
				deleteButton.setStyle(
						"-fx-background-color: #ff0000; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 10;");
				deleteButton.setPrefWidth(50); // Set the preferred width
				deleteButton.setPrefHeight(20); // Set the preferred height
				deleteButton.setOnAction(event -> {
					deleteQuantityTextField.setVisible(true);
					deleteButton.setOnAction(deleteEvent -> {
						try {
							int deleteQuantity = Integer.parseInt(deleteQuantityTextField.getText());

							if (deleteQuantity > 0 && deleteQuantity <= quantity) {
								deleteFromCart(productName, deleteQuantity);
							} else {
								showAlert("Invalid Quantity", "Please enter a valid quantity to delete.");
							}
						} catch (NumberFormatException e) {
							showAlert("Invalid Input", "Please enter a valid numeric quantity.");
						}
					});
				});

				cartItemHBox.getChildren().addAll(cartItemLabel, deleteButton, deleteQuantityTextField);
				cartItemHBox.setSpacing(20);
				cartVBox.getChildren().add(cartItemHBox);

				totalAmount += productPrice * quantity;
			}

			if (cartVBox.getChildren().isEmpty()) {
				showAlert("My Cart", "Your cart is empty.");
				return;
			}

			// Add total amount at the bottom
			Label totalLabel = new Label("Total Amount: $" + totalAmount);
			totalLabel.setStyle("-fx-font-weight: bold;");
			cartVBox.getChildren().add(totalLabel);

			ScrollPane scrollPane = new ScrollPane(cartVBox);

			Stage cartStage = new Stage();
			cartStage.setTitle("My Cart");
			cartStage.initModality(Modality.APPLICATION_MODAL);

			Scene scene = new Scene(scrollPane, 300, 300);
			Image icon = new Image(new File("C:\\Users\\user\\Desktop\\java\\DataBase3\\cart.png").toURI().toString());
			cartStage.getIcons().add(icon);
			cartStage.setScene(scene);
			cartStage.show();

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while fetching cart data.");
		}
	}

	private void deleteFromCart(String productName, int deleteQuantity) {
		try {
			String query = "UPDATE user_cart SET quantity = GREATEST(quantity - ?, 0) WHERE product_id IN "
					+ "(SELECT product_id FROM beauty_center_products WHERE product_name = ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setInt(1, deleteQuantity);
				preparedStatement.setString(2, productName);
				preparedStatement.executeUpdate();

				showCart();
				showAlert("Delete from Cart", "Item removed from cart successfully.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "An error occurred while deleting from the cart.");
		}
	}

	private void updateCartTable() {
		// Fetch and display the updated cart data
		showCart();
	}

}