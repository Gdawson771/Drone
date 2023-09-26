package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;


public class Menus {

    String machineName;
    String webPort;
    List<Restaurant> restaurants = new ArrayList<Restaurant>();
    /**
     * Restaurant - class, a class which stores information the name and what three words of a restaurant
     */
    final class NameAndW3W {
        public void setName(String name)
        {this.name=name;}
        public void setW3W(String W3W)
        {this.W3W=W3W;}
        String name;
        String W3W;
    }
    /**
     * Restaurant - class, a class which stores information about a restaurant
     */
    final class Restaurant {
        List<Option> menu;
        String name;
        String location;
    }
    /**
     * Option - class, a class which stores information about an individual menu item
     */
    final class Option{
        String item;
        int pence;
    }
    /**
     * Menus - Retrieves the menu and stores it in a list of Restaurant
     * @param name - String, the name of the machine
     * @param port - String, the port which our server is running on
     */
    public Menus(String name, String port) {
        this.machineName = name;
        this.webPort = port;

        try{

            String request = "http://localhost:"+port +"/menus/menus.json";
            String response = HTTPRequests.doGETRequest(request);
            Type listType = new TypeToken<List<Restaurant>>() {} .getType();
            this.restaurants= new Gson().fromJson(response,listType);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * getDeliveryCost - Retrieves the menu and stores it in a list of Restaurant
     * @param items - String..., list of strings one for each item in the customer's order
     * @returns totalPrice - int, the total cost of delivering this order
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */

    Integer getDeliveryCost(List<String> items)
    {
        try {
            int totalPrice = 0;
            int numberOfShops = 0;
            Boolean boughtFromRestaurant=false;
            Set<String> visitedRestaurantsSet = new HashSet<>();

            if (items.size() < 1 || items.size() > 4) {
                //do an error in here
                System.err.println("Purchases must be between 1 and 4 items.");

            }

                for (Iterator<String> iter = items.iterator(); iter.hasNext();)
                {
                    String item=iter.next();
                    for (Restaurant restaurant : restaurants) {

                        for (Option option : restaurant.menu) {
                            if (option.item.equals(item)) {
                                //restaurant.location;
                                visitedRestaurantsSet.add(restaurant.name);
                                boughtFromRestaurant=true;
                                totalPrice += option.pence;

                            }
                        }
                        if(boughtFromRestaurant)
                        {
                            numberOfShops+=1;
                            boughtFromRestaurant=false;
                        }
                    }
                }

                    totalPrice += 50;
                    return totalPrice;




        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Error getting delivery cost, returning -1");
            return -1;}
    }
    /**
     * getW3W - Retrieves a list of names and what three word strings where we can buy the given items
     * @param items - List<String>, list of strings one for each item in the customer's order
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy the given items
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public List<NameAndW3W> getW3W(List<String> items)
    {
        try {
            List<NameAndW3W> W3WList = new ArrayList<>();
            if (items.size() < 1 || items.size() > 4) {
                System.err.println("Purchases must be between 1 and 4 items.");

            }
                for (Iterator<String> iter = items.iterator(); iter.hasNext();)
                    {
                        String item=iter.next();
                    for (Restaurant restaurant : restaurants) {

                        for (Option option : restaurant.menu) {
                            NameAndW3W x=new NameAndW3W();
                            if (option.item.equals(item)) {
                              //  System.out.println(restaurant.name);

                                x.setName(restaurant.name);
                                x.setW3W(restaurant.location);
                                W3WList.add(x);
                            }
                        }
                    }
                }
                return W3WList;


        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Error getting delivery cost, returning null");
            return null;}
    }


    /**
     * getW3W - Retrieves all names and what three word strings
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy every item
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public List<NameAndW3W> getAllRestaurantW3W()
    {
        try {
            List<NameAndW3W> W3WList = new ArrayList<>();

                    for (Restaurant restaurant : restaurants) {
                        NameAndW3W x=new NameAndW3W();
                        x.setName(restaurant.name);
                        x.setW3W(restaurant.location);
                        W3WList.add(x);
                    }

                return W3WList;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Error getting getting all what 3 words");
            return null;}
    }
}
