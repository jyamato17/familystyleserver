package familystyleserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class restaurant {
	private String restaurantID;
	private String name;
	private List<Map<String, Object>> orders;
	private List<Map<String, Object>> menuItems;
	
	public restaurant(String restaurantID, String name)
	{
		this.restaurantID = restaurantID;
		this.name = name;
		this.orders = new ArrayList<Map<String, Object>>();
		this.menuItems = new ArrayList<Map<String, Object>>();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getId()
	{
		return restaurantID;
	}
	
	public void addOrder(order order)
	{
		this.orders.add(order.returnMap());
	}
	
	public void addMenuItem(menuItem item)
	{
		this.menuItems.add(item.getMap());
	}
	
	public boolean deleteMenuItem(String name)
	{
		for(int i = 0; i < menuItems.size(); i++)
		{
			if(menuItems.get(i).get("itemName").equals(name))
			{
				menuItems.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public Map<String, Object> getMap()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("restaurantID", restaurantID);
		map.put("name", name);
		map.put("orders", orders);
		map.put("menuItems", menuItems);
		return map;
	}
}
