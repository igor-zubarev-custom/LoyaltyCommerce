package loyalty.commerce;

public class LoyaltyConfiguration {
	private boolean enable;
	private double currencyRate;
	private double maxRateOfPayment;
	private double pointsForPayment;
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public double getCurrencyRate() {
		return currencyRate;
	}
	public void setCurrencyRate(double currencyRate) {
		this.currencyRate = currencyRate;
	}
	public double getMaxRateOfPayment() {
		return maxRateOfPayment;
	}
	public void setMaxRateOfPayment(double maxRateOfPayment) {
		this.maxRateOfPayment = maxRateOfPayment;
	}
	public double getPointsForPayment() {
		return pointsForPayment;
	}
	public void setPointsForPayment(double pointsForPayment) {
		this.pointsForPayment = pointsForPayment;
	}
	

}
