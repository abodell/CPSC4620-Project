package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		String query = "INSERT INTO `order` VALUES(?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, o.getOrderID());
			ps.setString(2, o.getOrderType());
			ps.setDouble(3, o.getBusPrice());
			ps.setDouble(4, o.getCustPrice());
			ps.setString(5, o.getDate());
			ps.setInt(6, o.getCustID());
			ps.setInt(7, o.getIsComplete());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		if (o instanceof DineinOrder) {
			String subQuery = "INSERT INTO dineinorder VALUES (?, ?)";
			DineinOrder dineInOrder = (DineinOrder) o;
			try (PreparedStatement ps = conn.prepareStatement(subQuery)) {
				ps.setInt(1, getNextOrderID());
				ps.setInt(2, dineInOrder.getTableNum());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		} else if (o instanceof PickupOrder) {
			String subQuery = "INSERT INTO pickuporder VALUES (?,?)";
			PickupOrder pickUpOrder = (PickupOrder) o;
			try (PreparedStatement ps = conn.prepareStatement(subQuery)) {
				ps.setInt(1, getNextOrderID());
				ps.setInt(2, pickUpOrder.getIsPickedUp());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		} else {
			String subQuery = "INSERT INTO deliveryorder VALUES (?, ?, ?)";
			DeliveryOrder deliveryOrder = (DeliveryOrder) o;
			try (PreparedStatement ps = conn.prepareStatement(subQuery)) {
				ps.setInt(1, getNextOrderID());
				ps.setString(2, deliveryOrder.getAddress());
				ps.setInt(3, deliveryOrder.getIsComplete());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
		double origPrice = o.getCustPrice();
		double origCost = o.getBusPrice();
		if (o.getPizzaList().size() > 0) {
			for (Pizza pizza : o.getPizzaList()) {
				pizza.setOrderID(getNextOrderID());
				pizza.setPizzaID(getMaxPizzaID());
				origPrice += pizza.getCustPrice();
				origCost += pizza.getBusPrice();
			}
		}
		ArrayList<Discount> discounts = o.getDiscountList();
		if (discounts.size() > 0) {
			for (int i = 0; i < discounts.size(); i++) {
				// userOrderDiscount
				useOrderDiscount(o, discounts.get(i));
				if (discounts.get(i).isPercent()) {
					origPrice *= (1 - (discounts.get(i).getAmount() / 100)); 
				} else {
					origPrice -= discounts.get(i).getAmount();
				}
			}
		}
		connect_to_db();
		String updateQuery = "UPDATE `order` SET OrderCost = ?, OrderPrice = ? WHERE OrderID = ?";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setDouble(1, origCost);
			ps.setDouble(2, origPrice);
			ps.setInt(3, getNextOrderID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}

		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */

		// need to loop through toppings and add them
		p.setOrderID(getNextOrderID());
		p.setPizzaID(getMaxPizzaID() + 1);
		connect_to_db();
		String query = "INSERT INTO pizza VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, p.getPizzaID());
			ps.setString(2, p.getCrustType());
			ps.setString(3, p.getSize());
			ps.setDouble(4, p.getBusPrice());
			ps.setDouble(5, p.getCustPrice());
			ps.setInt(6, Integer.parseInt(p.getPizzaState()));
			ps.setInt(7, p.getOrderID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		ArrayList<Topping> tops = p.getToppings();
		boolean[] doubled = p.getIsDoubleArray();
		double origPrice = p.getCustPrice();
		double origCost = p.getBusPrice();
		if (tops.size() > 0) {
			for (int i = 0; i < tops.size(); i++) {
				if (doubled[i] == true) {
					useTopping(p, tops.get(i), true);
					origPrice += tops.get(i).getCustPrice() * 2;
					origCost += tops.get(i).getBusPrice() * 2;
				} else {
					useTopping(p, tops.get(i), false);
					origPrice += tops.get(i).getCustPrice();
					origCost += tops.get(i).getBusPrice();
				}
			}
		}
		// need to loop through discounts and add them
		ArrayList<Discount> discounts = p.getDiscounts();
		if (discounts.size() > 0) {
			for (int i = 0; i < discounts.size(); i++) {
				usePizzaDiscount(p, discounts.get(i));
				if (discounts.get(i).isPercent()) {
					origPrice *= discounts.get(i).getAmount();
				} else {
					origPrice -= discounts.get(i).getAmount();
				}
			}
		}
		

		p.setBusPrice(origCost);
		p.setCustPrice(origPrice);
		connect_to_db();
		String updateQuery = "UPDATE pizza SET PizzaCost = ?, PizzaPrice = ? WHERE PizzaID = ?";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setDouble(1, origCost);
			ps.setDouble(2, origPrice);
			ps.setInt(3, getMaxPizzaID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static int getMaxPizzaID() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need to implement this function if you didn't forget to do that
		 */
		
		String query = "SELECT MAX(PizzaID) as PizzaID from pizza";
		ResultSet rs;
		int id = 0;
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("PizzaID");
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return id;
	}
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */
		String size = p.getSize();
		int topID = t.getTopID();
		int curInventory = t.getCurINVT();
		double topAmt = 0;
		switch (size) {
			case size_s:
				topAmt = t.getPerAMT();
				break;
			case size_m:
				topAmt = t.getMedAMT();
				break;
			case size_l:
				topAmt = t.getLgAMT();
				break;
			case size_xl:
				topAmt = t.getXLAMT();
				break;
		}
		if (isDoubled) {
			topAmt = topAmt * 2;
		}

		if (curInventory - topAmt <= t.getMinINVT()) {
			System.out.println("We are out of that topping");
			return;
		}

		t.setCurINVT((int)curInventory - (int)topAmt);

		String query = "UPDATE topping SET ToppingInventory = ToppingInventory - ? WHERE ToppingID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setDouble(1, topAmt);
			ps.setInt(2, topID);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		// update bridge table
		String subquery = "INSERT INTO pizzatoppingrelationship VALUES (?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(subquery)) {
			ps.setInt(1, p.getPizzaID());
			ps.setInt(2, t.getTopID());
			ps.setInt(3, isDoubled ? 1 : 0);
			ps.execute();
			ps.close();
		} catch  (SQLException e) {
			throw new SQLException(e);
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		
		
		String query = "INSERT INTO pizzadiscount VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, p.getPizzaID());
			ps.setInt(2, d.getDiscountID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		String query = "INSERT INTO orderdiscount VALUES (?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, getNextOrderID());
			ps.setInt(2, d.getDiscountID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	


	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This should add a customer to the database
		 */
		
		String query = "INSERT INTO customer VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, c.getCustID());
			ps.setString(2, c.getFName());
			ps.setString(3, c.getLName());
			ps.setString(4, c.getPhone());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}


	
	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */
		
		String query = "UPDATE `order` SET OrderIsComplete = ? WHERE OrderID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, 1);
			ps.setInt(2, o.getOrderID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}


	
	
	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */
		
		int curInv = t.getCurINVT();
		String query = "UPDATE topping SET ToppingInventory = ToppingInventory + ? WHERE ToppingID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, (int)toAdd);
			ps.setInt(2, t.getTopID());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			throw new SQLException(e);
		}

		t.setCurINVT(curInv + (int)toAdd);		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		
		/*
		 * I used this function to PRINT (not return) the inventory list.
		 * When you print the inventory (either here or somewhere else)
		 * be sure that you print it in a way that is readable.
		 * 
		 * 
		 * 
		 * The topping list should also print in alphabetical order
		 */

		 ResultSet rs;
		 String query = "SELECT ToppingID, ToppingName, ToppingInventory FROM topping ORDER BY ToppingName";
		 System.out.printf("\n%-5s %-25s %-5s\n", "ID", "Name", "Inventory");
		 try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ToppingID");
				String name = rs.getString("ToppingName");
				int inv = rs.getInt("ToppingInventory");
				System.out.printf("%-5d %-25s %-5d\n", id, name, inv);
			}
		 } catch (SQLException e) {
			throw new SQLException(e);
		 }
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();		
	}
	
	
	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings
		 * should be returned in alphabetical order if you don't
		 * plan on using a printInventory function
		 */
		ArrayList<Topping> toppings = new ArrayList<Topping>();
		ResultSet rs;
		String query = "SELECT * FROM topping ORDER BY ToppingName";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ToppingID");
				String name = rs.getString("ToppingName");
				double price = rs.getDouble("ToppingPrice");
				double cost = rs.getDouble("ToppingCost");
				double pAmt = rs.getInt("ToppingPersonalAmt");
				double medAmt = rs.getInt("ToppingMediumAmt");
				double lgAmt = rs.getInt("ToppingLargeAmt");
				double xlgAmt = rs.getInt("ToppingXLargeAmt");
				int inv = rs.getInt("ToppingInventory");
				Topping newTop = new Topping(id, name, pAmt, medAmt, lgAmt, xlgAmt, price, cost, 0, inv);
				toppings.add(newTop);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return toppings;
	}


	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */

		ArrayList<Order> orders = new ArrayList<Order>();
		ResultSet rs;
		String query = "SELECT * FROM `order` ORDER BY OrderTime DESC";
		String dineinQuery = "SELECT * FROM dineinorder WHERE DineInOrderOrderID = ?";
		String pickupQuery = "SELECT * FROM pickuporder WHERE PickUpOrderOrderID = ?";
		String deliveryQuery = "SELECT * FROM deliveryorder WHERE DeliveryOrderOrderID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int order_id = rs.getInt("OrderID");
				int cust_id = rs.getInt("OrderCustomerID");
				String type = rs.getString("OrderType");
				String date = rs.getString("OrderTime");
				double price = rs.getDouble("OrderPrice");
				double cost = rs.getDouble("OrderCost");
				int complete = rs.getInt("OrderIsComplete");
				if (type.equals("dinein")) {
					ResultSet rs1;
					try (PreparedStatement ps1 = conn.prepareStatement(dineinQuery)) {
						ps1.setInt(1, order_id);
						rs1 = ps1.executeQuery();
						while (rs1.next()) {
							int table = rs1.getInt("DineInOrderTableNum");
							DineinOrder newDineInOrder = new DineinOrder(order_id, cust_id, date, price, cost, complete, table);
							orders.add(newDineInOrder);
						}
					} catch (SQLException ex) {
						throw new SQLException(ex);
					}
				} else if (type.equals("pickup")) {
					ResultSet rs1;
					try (PreparedStatement ps1 = conn.prepareStatement(pickupQuery)) {
						ps1.setInt(1, order_id);
						rs1 = ps1.executeQuery();
						while (rs1.next()) {
							int pickedUp = rs1.getInt("PickUpOrderPickedUp");
							PickupOrder newPickupOrder = new PickupOrder(order_id, cust_id, date, price, cost, pickedUp, complete);
							orders.add(newPickupOrder);
						}
					} catch (SQLException ex) {
						throw new SQLException(ex);
					}
				} else {
					ResultSet rs1;
					try (PreparedStatement ps1 = conn.prepareStatement(deliveryQuery)) {
						ps1.setInt(1, order_id);
						rs1 = ps1.executeQuery();
						while (rs1.next()) {
							String address = rs1.getString("DeliveryOrderAddress");
							DeliveryOrder newDeliveryOrder = new DeliveryOrder(order_id, cust_id, date, price, cost, complete, address);
							orders.add(newDeliveryOrder);
						}
					} catch (SQLException ex) {
						throw new SQLException(ex);
					}
				}
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return orders;
	}
	
	public static ArrayList<Order> sortOrders(ArrayList<Order> list)
	{
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
		
	}
	
	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		//Helper function I used to help sort my dates. You likely wont need these
		
		
		
		
		
		
		
		
		return false;
	}
	
	
	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}



	
	
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion
		
		ResultSet rs;
		String query = "SELECT BasePricePizzaPrice FROM baseprice WHERE BasePricePizzaCrust = ? AND BasePricePizzaSize = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, crust);
			ps.setString(2, size);
			rs = ps.executeQuery();
			while (rs.next()) {
				bp = rs.getDouble("BasePricePizzaPrice");
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return bp;
	}
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerFName, CustomerLName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}
	
	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion
		
		ResultSet rs;
		String query = "SELECT BasePricePizzaCost FROM baseprice WHERE BasePricePizzaCrust = ? AND BasePricePizzaSize = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, crust);
			ps.setString(2, size);
			rs = ps.executeQuery();
			while (rs.next()) {
				bp = rs.getDouble("BasePricePizzaCost");
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return bp;
	}

	
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		//returns a list of all the discounts.
		
		
		
		ArrayList<Discount> discounts = new ArrayList<Discount>();
		ResultSet rs;
		String query = "SELECT * FROM discount";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("DiscountID");
				String name = rs.getString("DiscountName");
				double dollarsOff = rs.getDouble("DiscountDollarsOff");
				double percentOff = rs.getDouble("DiscountPercentOff");
				double amount = dollarsOff == 0 ? percentOff : dollarsOff;
				boolean isPercent = dollarsOff == 0 ? true : false;
				Discount newDiscount = new Discount(id, name, amount, isPercent);
				discounts.add(newDiscount);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return discounts;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/
		ResultSet rs;
		String query = "SELECT * FROM customer ORDER BY CustomerLName, CustomerFName";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("CustomerID");
				String fName = rs.getString("CustomerFName");
				String lName = rs.getString("CustomerLName");
				String phone = rs.getString("CustomerPhoneNumber");
				Customer newCustomer = new Customer(id, fName, lName, phone);
				custs.add(newCustomer);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		System.out.println("\n");
		for (Customer cust : custs) {
			System.out.println("CustID: " + cust.getCustID() + "\t|  Name: " + cust.getFName() + " " + cust.getLName() + ",  Phone: " + cust.getPhone());
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return custs;
	}
	
	public static int getNextOrderID() throws SQLException, IOException
	{
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */
		connect_to_db();
		
		String query = "SELECT MAX(OrderID) as OrderID from `order`";
		ResultSet rs;
		int id = 0;
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("OrderID");
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return id;
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */


		ResultSet rs;
		String query = "SELECT * FROM ToppingPopularity ORDER BY Topping";
		System.out.printf("\n%-25s %-7s\n", "Topping Name", "Count");
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("Topping");
				double count = rs.getDouble("ToppingCount");
				int realCount = (int)count;
				System.out.printf("%-25s %-7d\n", name, realCount);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		
		ResultSet rs;
		String query = "SELECT * FROM ProfitByPizza ORDER BY Profit DESC";
		System.out.printf("\n%-20s %-15s %-10s %-20s\n", "PizzaCrust", "PizzaSize", "Profit", "LastOrderDate");
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				String crust = rs.getString("PizzaCrust");
				String size = rs.getString("PizzaSize");
				double profit = rs.getDouble("Profit");
				String date = rs.getString("LastOrderDate");
				System.out.printf("%-20s %-15s %-10.2f %-20s\n", crust, size, profit, date);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		}

		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		
		
		ResultSet rs;
		String query = "SELECT * FROM ProfitByOrderType GROUP BY OrderMonth, CustomerType ORDER BY Profit DESC";
		System.out.printf("\n%-15s %-15s %-15s %-15s %-15s\n", "OrderType", "OrderMonth", "TotalOrderPrice", "TotalOrderCost", "Profit");
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				String type = rs.getString("CustomerType");
				String month = rs.getString("OrderMonth");
				double price = rs.getDouble("TotalOrderPrice");
				double cost = rs.getDouble("TotalOrderCost");
				double profit = rs.getDouble("Profit");
				System.out.printf("%-15s %-15s %-15.2f %-15.2f %-15.2f\n", type, month, price, cost, profit);
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
		conn.close();
	}
}