package loyalty.commerce;

public class LoyaltyPointsInfo {
	
	private String userId = null;
	private double amount;
	private double orderAmount;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String pUserId) {
		userId = pUserId;
	}  	
	public double getAmount() {
		return this.amount;
	}
	public double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public void setAmount(double pAmount) {
		this.amount = pAmount;
	}
}
