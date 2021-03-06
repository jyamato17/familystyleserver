package familystyleserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class dynamoHandler {
	private DynamoDB dynamoDB;
	private String loginTable = "familystyleUser";
	private String orderTable = "orders";
	private String restaurantTable = "restaurants";
	
	//logs in with aws credentials... may have to check how long this can be logged in...
	public dynamoHandler()
	{
		config config = new config();
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withCredentials(new AWSStaticCredentialsProvider(
	            		new BasicAWSCredentials(familystyleserver.config.getAccessKey(), familystyleserver.config.getSecretKey())))
	            .withRegion(Regions.US_WEST_2)
	            .build();
		DynamoDB dynamoDB = new DynamoDB(client);
		this.dynamoDB = dynamoDB;
	}
	
	// adds user to dynamoDB database
	public void addUser(String email, String password, String firstName, String lastName)
	{
        Table table = dynamoDB.getTable(loginTable);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                .putItem(new Item().withPrimaryKey("email", email)
                		.withString("password", password)
                		.withString("firstName", firstName)
                		.withString("lastName", lastName));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        }
        catch (Exception e) {
            System.err.println("Unable to add user: " + email);
            System.err.println(e.getMessage());
        }
	}
	
	// checks if the email exists in the database
	public boolean validEmail(String email)
	{
		Table table = dynamoDB.getTable(loginTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email);
		try {
			System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            
            if(outcome == null)
            {
            	System.out.println("returning true");
            	return true;
            }
		}
		catch(Exception e)
		{
			System.err.println("Unable to read item: " + email);
            System.err.println(e.getMessage());
		}

		return false;
	}
	
	// checks if the email and password match
	public boolean validLogin(String email, String password)
	{
        Table table = dynamoDB.getTable(loginTable);

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            
            String passwordInDatabase = (String) outcome.get("password");
            if(password.equals(passwordInDatabase))
    		{
            	return true;
    		}

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + email);
            System.err.println(e.getMessage());
        }
		return false;
	}
	
	private int getLengthOfOrderArray(String restaurantID)
	{
		Table table = dynamoDB.getTable(restaurantTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("restaurantID", restaurantID);
		try {
			 System.out.println("Attempting to read the item...");
	         Item outcome = table.getItem(spec);
	         System.out.println("GetItem succeeded: " + outcome);
	            
	         List<Object> attributes = (List<Object>) outcome.get("orders");
	         return attributes.size();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	private int getLengthOfMenuItemArray(String restaurantID)
	{
		Table table = dynamoDB.getTable(restaurantTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("restaurantID", restaurantID);
		try {
			 System.out.println("Attempting to read the item...");
	         Item outcome = table.getItem(spec);
	         System.out.println("GetItem succeeded: " + outcome);
	            
	         List<Object> attributes = (List<Object>) outcome.get("menuItems");
	         return attributes.size();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	private boolean validMenuItem(String restaurantID, String itemName)
	{
		Table table = dynamoDB.getTable(restaurantTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("restaurantID", restaurantID);
		try {
			 System.out.println("Attempting to read the item...");
	         Item outcome = table.getItem(spec);
	         System.out.println("GetItem succeeded: " + outcome);
	            
	         List<Map<String, Object>> attributes = (List<Map<String, Object>>) outcome.get("menuItems");
	         for(int i = 0; i < attributes.size(); i++)
	         {
	        	 if(attributes.get(i).get("itemName").equals(itemName))
	        	 {
	        		 return false;
	        	 }
	         }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean addMenuItem(menuItem menuItem, String restaurantID)
	{
		if(!validMenuItem(restaurantID, menuItem.getItemName()))
		{
			return false;
		}
		Table table = dynamoDB.getTable(restaurantTable);
        try {
        	int arrayLength = getLengthOfMenuItemArray(restaurantID);
        	if(arrayLength == -1)
        	{
        		System.out.println("Cannot find menuItem array");
        		return false;
        	}
        	
        	String menuItems = "menuItem[" + arrayLength + "]";
        	
            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("restaurantID", restaurantID)
                .withUpdateExpression("set " + menuItems + " = :vals")
//                .withNameMap(new NameMap().with("#ri", "orders"))
                .withValueMap(new ValueMap().withMap(":vals", menuItem.getMap()))
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out.println("Printing item after adding new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());
            return true;

        }
        catch (Exception e) {
            System.err.println("Failed to add new attribute in " + restaurantTable);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
	}
	
	// adds order to the restaurant in the database
	// TO DO
	public boolean addOrder(order order)
	{
		Table table = dynamoDB.getTable(restaurantTable);
        try {
        	int arrayLength = getLengthOfOrderArray(order.getRestaurant());
        	if(arrayLength == -1)
        	{
        		System.out.println("Cannot find order array");
        		return false;
        	}
        	
        	String orders = "orders[" + arrayLength + "]";
        	
            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("restaurantID", order.getRestaurant())
                .withUpdateExpression("set " + orders + " = :vals")
//                .withNameMap(new NameMap().with("#ri", "orders"))
                .withValueMap(new ValueMap().withMap(":vals", order.returnMap()))
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out.println("Printing item after adding new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());
            return true;

        }
        catch (Exception e) {
            System.err.println("Failed to add new attribute in " + restaurantTable);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
	}
	
	// gets the orders from a particular restaurant
	public String[] getOrderFromRestaurant(String restaurant)
	{
		
		return null;
	}
	
	// gets all the information about a particular menuItem
	public menuItem getMenuItemFromRestaurantDB(String restaurantID, String itemName)
	{
		
		Table table = dynamoDB.getTable(restaurantTable);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("restaurantID", restaurantID);
		try {
			 System.out.println("Attempting to read the item...");
	         Item outcome = table.getItem(spec);
	         System.out.println("GetItem succeeded: " + outcome);
	            
	         List<Map<String, Object>> attributes = (List<Map<String, Object>>) outcome.get("menuItems");
	         for(int i = 0; i < attributes.size(); i++)
	         {
	        	 if(attributes.get(i).get("itemName").equals(itemName))
	        	 {
	        		 return(new menuItem(attributes.get(i)));
	        	 }
	         }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	// easy way to add a restaurant to the restaurant table
	public boolean addRestaurant(restaurant restaurant)
	{
		Table table = dynamoDB.getTable(restaurantTable);
		
		final Map<String, Object> infoMap = restaurant.getMap();
		
		try {
            System.out.println("Adding a new item...");
            System.out.println(restaurant.getId());
            PutItemOutcome outcome = table
                .putItem(new Item().withPrimaryKey("restaurantID", restaurant.getId())
                		.withString("name", restaurant.getName())
                		.withList("orders")
                		.withList("menuItems"));
                
                
            

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            return true;

        }
        catch (Exception e) {
            System.err.println("Unable to add user: " + restaurant.getId());
            System.err.println(e.getMessage());
        }
        return false;
	}
	
	// deletes all the orders from a day
	public boolean deleteOrdersFromDay()
	{
		return false;
	}
	
	
}
