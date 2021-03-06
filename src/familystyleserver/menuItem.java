package familystyleserver;

import java.util.HashMap;
import java.util.Map;

// menuItem is an item a restaurant is offering for the day.
// ADD OPEN FOR THE DAY
// ADD CHECK SO NO TWO MENU ITEMS ARE SAME FOR RESTAURANT
public class menuItem {
	private String itemName;
	private double regularCost;
	private double discountedCost;
	private String restaurantID;
	private String timeEnd;
	private int numOrdersForDiscount;
	
	public menuItem(String itemName, double regularCost, double discountedCost, String restaurant, String timeEnd, int numOrders)
	{
		this.itemName = itemName;
		this.regularCost = regularCost;
		this.discountedCost = discountedCost;
		this.restaurantID = restaurant;
		this.timeEnd = timeEnd;
		this.numOrdersForDiscount = numOrders;
	}
	
	// constructor to go from database format back to java object
	public menuItem(Map<String, Object> inputs)
	{
		this.itemName = (String) inputs.get("itemName");
		this.regularCost = (Double) inputs.get("regularCost");
		this.discountedCost = (Double) inputs.get("disountedCost");
		this.restaurantID = (String) inputs.get("restaurantID");
		this.timeEnd = (String) inputs.get("timeEnd");
		this.numOrdersForDiscount = (int) inputs.get("numOrdersForDiscount");
	}
	
	// puts object back to database format
	public Map<String, Object> getMap()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("itemName", itemName);
		map.put("regularCost", regularCost);
		map.put("discountedCost", discountedCost);
		map.put("restaurantID", restaurantID);
		map.put("timeEnd", timeEnd);
		map.put("numOrdersForDiscount", numOrdersForDiscount);
		return map;
	}
	
	public double getRegularCost()
	{
		return this.regularCost;
	}
	
	public double getDiscountedCost()
	{
		return this.discountedCost;
	}
	
	public String getTimeEnd()
	{
		return this.timeEnd;
	}
	
	public int getNumOrdersForDiscount()
	{
		return this.numOrdersForDiscount;
	}
	
	public String getItemName()
	{
		return this.itemName;
	}
	
	public String getRestaurant()
	{
		return this.restaurantID;
	}
}
