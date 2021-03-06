package familystyleserver;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.*;


@RestController
public class AppController {
	private dynamoHandler dynamoHandler;
	private int transactionNum;
	public AppController()
	{
		dynamoHandler = new dynamoHandler();
		transactionNum = 0;
	}
	
	// http://localhost:8080/register?email="email"&password="password"&firstName="firstName"&lastName="lastName"
    @GetMapping("/register")
    @ResponseBody
    public int addUser(@RequestParam(name="email") String email, @RequestParam(name="password") String password, @RequestParam(name="firstName")
    String firstName, @RequestParam(name="lastName") String lastName) {
    	if(validEmail(email))
    	{
    		dynamoHandler.addUser(email, password, firstName, lastName);
    		return 0;
    	}
        return 1;
    }
    
    // http://localhost:8080/login?email="email"&password="password"
    @GetMapping("/login")
    @ResponseBody
    public int login(@RequestParam(name="email") String email, @RequestParam(name="password") String password)
    {
    	if(dynamoHandler.validLogin(email, password))
    	{
    		return 0;
    	}
    	return 1;
    }
    
    // http://localhost:8080/order?quantity=quantity&email=email&time=time&restaurant=restID&orderName=orderName
    @GetMapping("/order")
    @ResponseBody
    public int order(int quantity, String email, String time, String restaurant, String orderName)
    {
    	
    	menuItem item = dynamoHandler.getMenuItemFromRestaurantDB("restaurant", "orderName");
    	order order = new order(quantity, email, time, item);
    	
		if(dynamoHandler.addOrder(order))
		{
			return 0;
		}
    	
		return 1;
    	
    }
    
    // http://localhost:8080/test
    @GetMapping("/test")
    @ResponseBody
    public void connectionsTest()
    {
    	int num = transactionNum;
    	this.transactionNum++;
    	System.out.println("Starting transaction " + num + " " + LocalTime.now());
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	System.out.println("Ended transaction " + num + " " + LocalTime.now());
    }
    
    @GetMapping("/healthCheck")
    @ResponseBody
    public int connections()
    {
    	return 0;
    }
    
    // http://localhost:8080/addRestaurant?name=name
    @GetMapping("/addRestaurant")
    @ResponseBody
    public int addRestaurant(@RequestParam(name="name") String name)
    {
    	restaurant restaurant = new restaurant("1", name);
    	dynamoHandler.addRestaurant(restaurant);
    	return 0; 
    }
    
    // checks email format and if it exists in database
	private boolean validEmail(String email)
    {
    	return (dynamoHandler.validEmail(email) && isValidEmailAddress(email));
    }
    
	//checks email format
    private static boolean isValidEmailAddress(String email) {
    	String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(email);
    	System.out.println(email);
    	System.out.println(matcher.matches());
    	return matcher.matches();
	}
}
