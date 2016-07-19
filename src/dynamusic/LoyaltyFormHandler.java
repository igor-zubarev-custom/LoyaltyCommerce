package dynamusic;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class LoyaltyFormHandler extends RepositoryFormHandler {
	private LoyaltyManager loyaltyManager;	
	private String adminId;
		
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}
	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}	

	@Override
	protected void preCreateItem(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {	
		
		if (isLoggingDebug()){ 
	           logDebug("Call preCreateItem");
		}
				
		try {
			getTransactionDemarcation().begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
			validateInputData();			
			setValueProperty("date", new Date());			
			if (getFormError()) {
				try {
					if (isLoggingDebug()){ 
				           logDebug("Trying to rollback transaction");
					}
					getTransactionManager().setRollbackOnly();
				} catch (Exception e) {					
					if (isLoggingDebug()){ 
				           logDebug("Fail to rollback transaction", e);
					}
					if (isLoggingError()) {
	                    logError("Unable to set rollback for transaction", e);					
					}
				}
			}			
		} catch (TransactionDemarcationException e1) {
			if (isLoggingDebug()){ 
		           logDebug("Creating transaction demarcation failed", e1);
			}
			if (isLoggingError()) {
	             logError("Creating transaction demarcation failed", e1);
			}  
		}			
	}	
	
	protected boolean validateInputData() {
		if (isLoggingDebug()){ 
	           logDebug("Validation input data strat");
		}
		
		boolean validate = true;
				
		if (getLoyaltyManager() == null) {
			addFormException(new DropletException("Loyalty manger is not set"));
		}
		
		String inputUserId = getValueProperty("profileId").toString();		
		try {
			RepositoryItem user = getLoyaltyManager().getUserRepositroy().getItem(inputUserId, "user");
			if (user == null) {
				addFormException(new DropletException("User not exist"));
				validate = false;
			}
		} catch (RepositoryException e) {
			if (isLoggingDebug()){ 
		           logDebug("Exception trying to validate user " + inputUserId, e);
			}
			if (isLoggingError()) {
             logError("Exception trying to validate user " + inputUserId, e);					
			}
			addFormException(new DropletException("Unable to validate user"));
			validate = false;
		}		
		
		Object inputAmount = getValueProperty("amount");
		if (!(inputAmount instanceof Number)) {
			addFormException(new DropletException("Amount must be a Number"));
			validate = false;
		}
		
		String inputDescription = getValueProperty("description").toString();
		if (inputDescription.length() > 1000) {
			addFormException(new DropletException("Description must be less 1000 symbols"));
			validate = false;
		}
		
		if (isLoggingDebug()){ 
	           logDebug("Validation result " + validate);
		}
		
		return validate;
	}
	
	@Override
	protected void postCreateItem(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {		
		if (isLoggingDebug()){ 
	           logDebug("Trying to add created loyaltyTransaction to user");
		}
		try {
			if (!getFormError()) {			
				LoyaltyManager loyaltyManager = getLoyaltyManager();	
				System.out.println("postCreateItem loyaltyManager " + loyaltyManager);
				loyaltyManager.addLoyaltyToUser(getValueProperty("profileId").toString(), getRepositoryId());
			}
		} catch (Exception e) {
			if (isLoggingDebug()){ 
		           logDebug("Exception occured, adding FormException and try to rollback transaction", e);
			}
			if (isLoggingError()) {
                logError("Trying to add created loyaltyTransaction to user", e);					
			}			
			addFormException(new DropletException(e.getMessage()));
			
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
		}finally{
			if (isLoggingDebug()){ 
		           logDebug("Trying to end transaction demarcation");
			}
			try {
				getTransactionDemarcation().end();
			} catch (TransactionDemarcationException e) {
				if (isLoggingDebug()){ 
			           logDebug("Ending transaction demarcation failed", e);
				}
				if (isLoggingError()) {
		             logError("Ending transaction demarcation failed", e);
				}
			}
		}
	}
	
	@Override
	public boolean handleCancel(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()){ 
	           logDebug("Canceling input data");
		}
		setValueProperty("amount", null);
		setValueProperty("profileId", null);
		setValueProperty("date", null);
		setValueProperty("description", null);
		return super.handleCancel(pRequest, pResponse);
	}
	
	
	
	
	

}
