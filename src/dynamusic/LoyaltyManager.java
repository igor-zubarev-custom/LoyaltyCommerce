package dynamusic;

import java.util.Collection;
import java.util.Date;

import javax.transaction.TransactionManager;

import loyalty.commerce.LoyaltyConfiguration;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

public class LoyaltyManager extends GenericService {
	private TransactionManager transactionManager;
	private Repository userRepositroy;
	private Repository loyaltyRepository;
	private LoyaltyConfiguration loyaltyConfiguration;
		
	public LoyaltyConfiguration getLoyaltyConfiguration() {
		return loyaltyConfiguration;
	}
	public void setLoyaltyConfiguration(LoyaltyConfiguration loyaltyConfiguration) {
		this.loyaltyConfiguration = loyaltyConfiguration;
	}
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	public Repository getUserRepositroy() {
		return userRepositroy;
	}
	public void setUserRepositroy(Repository userRepositroy) {
		this.userRepositroy = userRepositroy;
	}
	public Repository getLoyaltyRepository() {
		return loyaltyRepository;
	}
	public void setLoyaltyRepository(Repository loyaltyRepository) {
		this.loyaltyRepository = loyaltyRepository;
	}
	
	public void addLoyaltyToUser(String pUserId, String pLoyaltyId) throws RepositoryException, TransactionDemarcationException {
		System.out.println("addLoyaltyToUser userId " + pUserId + " loyaltyId " + pLoyaltyId);		
		if (isLoggingDebug()){ 
	           logDebug("Adding loyaltyTransaction " + pLoyaltyId + " to user " + pUserId);
		}
		try{
			TransactionDemarcation td = new TransactionDemarcation();
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			try {
				MutableRepository mUserRepository = (MutableRepository)getUserRepositroy();
				MutableRepositoryItem mUser = mUserRepository.getItemForUpdate(pUserId, "user");
				Collection userLoyaltyTransactions = (Collection)mUser.getPropertyValue("loyaltyTransactions");
				RepositoryItem loyaltyTransactionForAdd = getLoyaltyRepository().getItem(pLoyaltyId, "loyaltyTransaction");
				if (userLoyaltyTransactions != null && loyaltyTransactionForAdd != null) {
					userLoyaltyTransactions.add(loyaltyTransactionForAdd);				
				}
			} catch (Exception e) {	
				if (isLoggingDebug()){ 
			           logDebug("Exception occured, try to rollback transaction", e);
				}
				if (isLoggingError()) {
                    logError("Exception occured trying to add loyaltyTransaction " + pLoyaltyId + " to user " + pUserId, e);					
				}
				try {
					getTransactionManager().setRollbackOnly();
				} catch (Exception e1) {
					if (isLoggingDebug()){ 
				           logDebug("Fail to rollback transaction", e1);
					}
					if (isLoggingError()) {
	                     logError("Unable to set rollback for transaction", e1);					
					}     
				}
				throw new RepositoryException("Unable to add loyalty transaction " + pLoyaltyId + " to user " + pUserId);
			} finally{
				if (isLoggingDebug()){ 
			           logDebug("End of transaction demarcation reached");
				}
				td.end();
			}
		}catch (TransactionDemarcationException e) {
			if (isLoggingDebug()){ 
		           logDebug("Creating transaction demarcation failed", e);
			}
			if (isLoggingError()) {
	             logError("Creating transaction demarcation failed", e);
			}     
			throw new TransactionDemarcationException("Creating transaction demarcation failed", e);
		}
	}
	
	public boolean authorizeLoyaltyPoints(String pUserId, double pAmount) throws Exception{	
		Integer userLoualtyPoints = (Integer)getUserRepositroy().getItem(pUserId, "user").getPropertyValue("loyaltyAmount");		
		if (pAmount * getLoyaltyConfiguration().getCurrencyRate() > userLoualtyPoints) {			
			return false;
		}		
		return true;
	}
	public void debitLoyaltyPoints(String pUserId, double pAmount) throws Exception{		
		String description = "Purchase order for amount " + pAmount;
		int pointsForCredit = (int)Math.round(pAmount * getLoyaltyConfiguration().getCurrencyRate());
		if (pointsForCredit < 1) {
			pointsForCredit = 1;
		}
		String debitLoualtyTransactionId = this.createLoyaltyTransaction(pUserId, -pointsForCredit, description);		
		this.addLoyaltyToUser(pUserId, debitLoualtyTransactionId);
	}
	public void creditLoyaltyPoints(String pUserId, double pAmount) throws Exception{
		int creditPointsAmount = (int)(pAmount * getLoyaltyConfiguration().getPointsForPayment());
		if (creditPointsAmount >= 1) {
			String description = "Points for payment in the amount of " + pAmount;
			String creditLoyaltyTransactionId = this.createLoyaltyTransaction(pUserId, creditPointsAmount, description);
			this.addLoyaltyToUser(pUserId, creditLoyaltyTransactionId);
		}
	}
	
	public String createLoyaltyTransaction(String pUserId, int pAmount, String pDescription) throws RepositoryException{
		MutableRepository mLoyaltyRepository = (MutableRepository)getLoyaltyRepository();
		MutableRepositoryItem loyaltyTransaction = mLoyaltyRepository.createItem("loyaltyTransaction");
		loyaltyTransaction.setPropertyValue("amount", pAmount);
		loyaltyTransaction.setPropertyValue("date", new Date());
		loyaltyTransaction.setPropertyValue("profileId", pUserId);		
		loyaltyTransaction.setPropertyValue("description", pDescription);
		mLoyaltyRepository.addItem(loyaltyTransaction);
		return loyaltyTransaction.getRepositoryId();
	}

}
