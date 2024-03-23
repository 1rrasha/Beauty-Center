package application;

public class Product {
	int produtc_id;
	String Product_Name;
	double Product_Price;
	String Product_descreption;
	private static String imagePath;

//The constructor
	public Product(int produtc_id, String product_Name, double product_Price, String product_descreption) {
		super();
		this.produtc_id = produtc_id;
		Product_Name = product_Name;
		Product_Price = product_Price;
		Product_descreption = product_descreption;
	}

	public Product() {

	}

	public int getProdutc_id() {
		return produtc_id;
	}

	public void setProdutc_id(int produtc_id) {
		this.produtc_id = produtc_id;
	}

	public String getProduct_Name() {
		return Product_Name;
	}

	public void setProduct_Name(String product_Name) {
		Product_Name = product_Name;
	}

	public double getProduct_Price() {
		return Product_Price;
	}

	public void setProduct_Price(double product_Price) {
		Product_Price = product_Price;
	}

	public String getProduct_descreption() {
		return Product_descreption;
	}

	public void setProduct_descreption(String product_descreption) {
		Product_descreption = product_descreption;
	}

	public static String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}