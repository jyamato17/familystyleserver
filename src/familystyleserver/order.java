package familystyleserver;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// order is an actual order from a customer

public class order {
	private int quantity;
	private String email;
	private String time;
	private boolean gotDiscount;
	private double totalCost;
	private menuItem item;
	private boolean paid;
	private String timeFilled;
	private boolean orderCompleted;
	
	
	public order(int quantity, String email, String time, menuItem item)
	{
		this.quantity = quantity;
		this.email = email;
		this.time = time;
		this.gotDiscount = false;
		this.totalCost = item.getRegularCost() * quantity;
		this.paid = false;
		this.timeFilled = "0";
		this.orderCompleted = false;
		this.item = item;
	}
	
	// converts database style to java object
	public Map<String, Object> returnMap()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("quantity", quantity);
		map.put("email", email);
		map.put("time", time);
		map.put("gotDiscount", gotDiscount);
		map.put("totalCost", totalCost);
		map.put("menuItem", this.item.getMap());
		map.put("paid", paid);
		map.put("timeFilled", timeFilled);
		map.put("orderCompleted", orderCompleted);
		return map;
	}
	
	public void paid()
	{
		this.paid = true;
	}
	
	public void timeFilled()
	{
		this.timeFilled = String.valueOf(Instant.now().getEpochSecond());
	}
	
	public void completed()
	{
		this.orderCompleted = true;
	}
	
	public void applyDiscount()
	{
		this.gotDiscount = true;
		this.totalCost = item.getDiscountedCost() * quantity; 
	}
	
	public double getTotalCost()
	{
		return this.totalCost;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	
	public String getRestaurant()
	{
		return item.getRestaurant();
	}
}
