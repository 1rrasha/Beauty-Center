package application;

import java.util.Date;

public class Payment {
	int Payment_id;
	String Payment_Method;
	double Payment_Amount;
	Date Payment_Date;

	public int getPayment_id() {
		return Payment_id;
	}

	public void setPayment_id(int payment_id) {
		Payment_id = payment_id;
	}

	public String getPayment_Method() {
		return Payment_Method;
	}

	public void setPayment_Method(String payment_Method) {
		Payment_Method = payment_Method;
	}

	public double getPayment_Amount() {
		return Payment_Amount;
	}

	public void setPayment_Amount(double payment_Amount) {
		Payment_Amount = payment_Amount;
	}

	public Date getPayment_Date() {
		return Payment_Date;
	}

	public void setPayment_Date(Date payment_Date) {
		Payment_Date = payment_Date;
	}

	public Payment(int payment_id, String payment_Method, double payment_Amount, Date payment_Date) {
		super();
		Payment_id = payment_id;
		Payment_Method = payment_Method;
		Payment_Amount = payment_Amount;
		Payment_Date = payment_Date;
	}

}