package loyalty.commerce;

public interface LoyaltyPointsProcessor {
	 public LoyaltyPointsStatus authorize(LoyaltyPointsInfo pLoyaltyPointsInfo);
	  
	 public LoyaltyPointsStatus debit(LoyaltyPointsInfo pLoyaltyPointsInfo, LoyaltyPointsStatus pStatus);
	  
	 public LoyaltyPointsStatus credit(LoyaltyPointsInfo pLoyaltyPointsInfo, LoyaltyPointsStatus pStatus);

	 public LoyaltyPointsStatus credit(LoyaltyPointsInfo pLoyaltyPointsInfo);
}
