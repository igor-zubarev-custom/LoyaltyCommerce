package dynamusic;


import java.util.Iterator;
import java.util.List;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class CalculateLoyaltyAmount extends RepositoryPropertyDescriptor {

	@Override
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		List loyaltyTransactions = (List)pItem.getPropertyValue("loyaltyTransactions");
		Integer totalAmount = new Integer(0);
		if (loyaltyTransactions != null) {
			Iterator i = loyaltyTransactions.iterator();
			while (i.hasNext()) {
				totalAmount +=(Integer)((RepositoryItemImpl)i.next()).getPropertyValue("amount");				
			}
		}
		return totalAmount;
	}
	

}
