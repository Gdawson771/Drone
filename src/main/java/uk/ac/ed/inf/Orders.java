package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.sql.*;
//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Orders {

    String machineName;
    String webPort;
    String dbPort;
    List<Feature> featureCollection = new ArrayList<Feature>();
    Menus menu;


    List<OrderDetails> ordersList=new ArrayList<>();
    /**
     * OrderDetails - Stores all of the relevant information for an individual order including all restaurants it must visit and the value of that order
     */
    public class OrderDetails{
        String orderNumber;
        List<String> orderItems=new ArrayList<>();
        double orderprice;
        Boolean orderCompleted=false;
        List<Menus.NameAndW3W> locations=new ArrayList<>();
        List<LongLat> orderLocationsLongLats=new ArrayList<>();
        double distanceFromAT;
        String deliverToW3W;
    }

    /**
     * NameAndLongLat - stores the name of a location and its LongLat
     */
    public class NameAndLongLat{
        String name;
        LongLat longlat;
    }
    /**
     * ThisLongLat - Stores a string for longitude and latitude used when parsing with gson
     */
    final class ThisLongLat {
        String lng;
        String lat;
    }
    /**
     * Square - stores two LongLats as String, this is used to parse with gson
     */
    final class Square
    {
        ThisLongLat dir1;
        ThisLongLat dir2;
    }
    /**
     * RestaurantFeatures - class with all of the information which we retrieve we when fetch information about the restaurants
     */
    final class RestaurantFeatures {

        String country;
        Square square;
        String nearestPlace;
        ThisLongLat coordinates;
        String words;
        String language;
        String map;

    }
    /**
     * Orders - instantiates this orders instance with the webport, database port, and machine name
     */
    public Orders( String name, String webPort,String dbPort){
        this.webPort=webPort;
        this.machineName=name;
        this.dbPort=dbPort;
        menu = new Menus(this.machineName,this.webPort);


    }

    /**
     * SortOrderList - sorts the list of orders which is created from populateOrdersList by some highest value orders with least items
     */
    public void sortOrderList(){
        Collections.sort(ordersList, new Comparator<OrderDetails>() {
            @Override
            public int compare(OrderDetails lhs, OrderDetails rhs) {

                return lhs.orderprice > rhs.orderprice ? -1 : (lhs.orderprice  < rhs.orderprice) ? 1 : 0;
            }
        });
    }
    /**
     * getRestaurant - will get the details json of a restaurant given its what three words
     * @param a - String,first of the what three words
     * @param b - String,second of the what three words
     * @param c - String, third of the what three words
     * @param name - String, Name of the restaurant
     * @returns restaurantFC -FeatureCollection, Feature collection of the restaurant in question
     * @throws  Exception - Exception,  thrown if there is any issues contacting the server
     */
    public FeatureCollection getRestaurant(String a, String b, String c,String name){
        try{
        Orders.RestaurantFeatures restaurants =null;
        String request = "http://localhost:"+this.webPort +"/words/"+a+"/"+b+"/"+c+"/"+"details.json";

        String response = HTTPRequests.doGETRequest(request);

        /*
        *  Type listType = new TypeToken<List<Restaurant>>() {} .getType();
            this.restaurants= new Gson().fromJson(response,listType);
        * */
        RestaurantFeatures model = new Gson().fromJson(response, RestaurantFeatures.class);

        String feature="{" +
                "\"type\": \"Feature\"," +
                "\"geometry\": {" +
                "\"type\": \"Point\"," +
                "\"coordinates\": [" +
                model.coordinates.lng + ","+
                model.coordinates.lat +
                "]" +
                "}," +
                "\"properties\": {" +
                "\"name\": \""+name+"\"," +
                "\"location\": \""+a+"."+b+"."+c+"\"," +
                "\"marker-symbol\": \"landmark\"," +
                "\"marker-color\": \"#0000ff\"" +
                "}" +
                "}";
        //System.out.println("{\"type\":\"FeatureCollection\",\"features\":[" +feature+"]}");
        FeatureCollection restaurantFC =FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":["+ feature+"]}");
        //Feature restaurant= Feature.fromJson(response);

        // this.restaurants= new Gson().fromJson(response,listType);
     //   System.out.println("response is" +response);

        return restaurantFC;
    }
    catch (Exception e){
        e.printStackTrace();
        return null;
    }
    }
    /**
     * getRestaurantLongLat - Retrieves a list of names and what three word strings where we can buy the given items
     * @param a - String,first of the what three words
     * @param b - String,second of the what three words
     * @param c - String, third of the what three words
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy the given items
     * @throws  Exception - Exception, thrown if there is any issues contacting the server
     */
    public LongLat getRestaurantLongLat(String a, String b, String c){
            try{
                Orders.RestaurantFeatures restaurants =null;
                String request = "http://localhost:"+this.webPort +"/words/"+a+"/"+b+"/"+c+"/"+"details.json";

                String response = HTTPRequests.doGETRequest(request);
               // System.out.println(response);
                RestaurantFeatures model = new Gson().fromJson(response, RestaurantFeatures.class);
                LongLat x=new LongLat(Integer.parseInt(model.coordinates.lng), Integer.parseInt(model.coordinates.lat));
                return x;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
    }
    /**
     * getLongLatFromW3W - gets the latitude and longitude from a what three words string
     * @param a - String, what three words of a location
     * @returns x - LongLat, the LongLat of the given location
     * @throws  Exception - Exception, thrown if there is any issues contacting the server
     */
    public LongLat getLongLatFromW3W(String a){
        try{
            Orders.RestaurantFeatures restaurants =null;
            String[] detailsArray=a.split("\\.");
            String request = "http://"+this.machineName+":"+this.webPort +"/words/"+detailsArray[0]+"/"+detailsArray[1]+"/"+detailsArray[2]+"/"+"details.json";

            String response = HTTPRequests.doGETRequest(request);

            RestaurantFeatures model = new Gson().fromJson(response, RestaurantFeatures.class);
            LongLat x=new LongLat(Double.parseDouble(model.coordinates.lng), Double.parseDouble(model.coordinates.lat));
            return x;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * getRestaurantInfo - fetches the information of a given restaurant from the server
     * @param a - String,first of the what three words
     * @param b - String,second of the what three words
     * @param c - String, third of the what three words
     * @returns y - NameAndLongLat,name and longitude and latitude of the restaurant
     * @throws  Exception - Exception, thrown if there is any issues contacting the server
     */
    public NameAndLongLat getRestaurantInfo(String a, String b, String c,String name){
        try{
            Orders.RestaurantFeatures restaurants =null;
            String request = "http://localhost:"+this.webPort +"/words/"+a+"/"+b+"/"+c+"/"+"details.json";

            String response = HTTPRequests.doGETRequest(request);

            RestaurantFeatures model = new Gson().fromJson(response, RestaurantFeatures.class);
            LongLat x=new LongLat(Integer.parseInt(model.coordinates.lng), Integer.parseInt(model.coordinates.lat));
            NameAndLongLat y= new NameAndLongLat();
            y.longlat=x;
            y.name= name;
            return y;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //public List<String> getW3WForFoo

    /**
     * getOrderDetailsForOrder - Retrieves the details of a specified order including what items were ordered
     * @param orderNumber - String, a order number
     * @returns rs - ResultSet,  a result set containing order number and item name
     * @throws  Exception - Exception,thrown if there is any issues contacting the server
     */
    public ResultSet getOrderDetailsForOrder(String orderNumber)
    {
        try {
            String jdbcString="jdbc:derby://"+this.machineName+":"+this.dbPort+"/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            final String orderNumberQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(orderNumberQuery);
            psCourseQuery.setString(1,orderNumber);

            ResultSet rs = psCourseQuery.executeQuery();
            return  rs;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("error fetching derbyDB");
        }

        return null;
    }

    /**
     * getUniqueDropOffPoints - Retrieves all drop off points for all orders in our database
     * @returns uniquePoints -List<String>,a list of what three words where each one is a different drop off point
     * @throws  Exception - Exception, thrown if there is any issues contacting the server
     */
    public List<String> getUniqueDropOffPoints(){
        try {
            String jdbcString="jdbc:derby://"+this.machineName+":"+this.dbPort+"/derbyDB";
            List<String> uniquePoints= new ArrayList<>();
            Connection conn = DriverManager.getConnection(jdbcString);
            final String deliveryDateQuery = "select deliverTo from orders Group by deliverTo";
            PreparedStatement psCourseQuery = conn.prepareStatement(deliveryDateQuery);
            ResultSet rs = psCourseQuery.executeQuery();
            while (rs.next()) {

                uniquePoints.add(rs.getString(1));

            }

            return  uniquePoints;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("error fetching derbyDB");
            return  null;
        }
    }
    /**
     * getOrdersForDay -gets all of the order numbers for a given day
     * @param date_string - String, String value of the selected day
     * @returns rs - ResultSet, a result set containing all order numbers their drop off locations, and date of delivery
     * @throws  Exception - Exception, thrown if there is any issues contacting the server
     */
    public ResultSet getOrdersForDay(String date_string){
        try {
            String jdbcString="jdbc:derby://"+this.machineName+":"+this.dbPort+"/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            final String deliveryDateQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(deliveryDateQuery);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(date_string);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            psCourseQuery.setDate(1,sqlDate);
            ResultSet rs = psCourseQuery.executeQuery();
            return  rs;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("error fetching derbyDB");
        }


        return null;
    }

    /**
     * getDerbyOrders - Retrieves a list of names and what three word strings where we can buy the given items
     * @param items - List<String>, list of strings one for each item in the customer's order
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy the given items
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
   /* public void getDerbyOrders(){
        String jdbcString="jdbc:derby://localhost:1527/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            // Create a statement object that we can use for running various
            // SQL statement commands against the database.
            //Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "ORDERS", null);
            final String deliveryDateQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(deliveryDateQuery);
            String date_string = "2022-01-01";
            //Instantiating the SimpleDateFormat class
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            //Parsing the given String to Date object
            //Just added (Date to here and to 6 lines below here, this m
            Date date = formatter.parse(date_string);
            java.util.Date utilDate = new java.util.Date();

            java.sql.Date sqlDate = new java.sql.Date(date.getTime());


            psCourseQuery.setDate(1,sqlDate);
            ResultSet rs = psCourseQuery.executeQuery();

            }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("error fetching derbyDB");
        }
    }*/
    /**
     * getNoFlyZones - Gets all of the no fly zones from our server
     * @returns map - FeatureCollection, a Feature  collection of all no fly zone polygons
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public  FeatureCollection getNoFlyZones(){
        try {


            String request = "http://localhost:" + this.webPort + "/buildings/no-fly-zones.geojson";
            String response = HTTPRequests.doGETRequest(request);

            FeatureCollection map = FeatureCollection.fromJson(response);
            return  map;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    /**
     * getW3W - Retrieves a list of names and what three word strings where we can buy the given items
     * @param items - List<String>, list of strings one for each item in the customer's order
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy the given items
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
   /*public String instantiateGeojson(String name, String port) {
        this.machineName = name;
        this.webPort = port;

        try{


            String request = "http://localhost:"+port +"/buildings/no-fly-zones.geojson";
            String response = HTTPRequests.doGETRequest(request);

            FeatureCollection map = FeatureCollection.fromJson(response);
            List<Feature> features=map.features();

            request = "http://localhost:"+port +"/buildings/landmarks.geojson";
            response = HTTPRequests.doGETRequest(request);


            FeatureCollection map2 = FeatureCollection.fromJson(response);
            for (Feature feature:map2.features()) {
                features.add(feature);
            }
            String confinement_Zone="{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.192473,55.946233],[-3.184319,55.946233],[-3.184319,55.942617],[-3.192473,55.942617],[-3.192473,55.946233]]]},\"properties\":{\"name\":\"Drone confinement zone\",\"fill\":\"none\"}}]}";
            FeatureCollection confinementZone =FeatureCollection.fromJson(confinement_Zone);
            for (Feature feature:confinementZone.features()) {
                features.add(feature);
            }


            String appleton_Tower="{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.186874,55.944494]},\"properties\":{\"name\":\"Appleton Tower\",\"location\":\"nests.takes.print\",\"marker-symbol\":\"building\",\"marker-color\":\"#ffff00\"}}]}";
            FeatureCollection appletonTower =FeatureCollection.fromJson(appleton_Tower);
            for (Feature feature:appletonTower.features()) {
                features.add(feature);
            }
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }*/
    /**
     * getW3W - Retrieves a list of names and what three word strings where we can buy the given items
     * @param items - List<String>, list of strings one for each item in the customer's order
     * @returns W3WList - List<NameAndW3W>, a list of names and what three word strings where we can buy the given items
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    /*public String getRestaruantsForDay(String date)//CHANGE OF PLANS USE MENUS FUNCTION TO GET ALL UNIQUE W3W AND LONGLATS
    {
        List<String> orderNumbers=new ArrayList<String>();
        //1. get list of orderNumbers for a day
        List<ResultSet> orderDetailsList=new ArrayList<ResultSet>();
        try{
        ResultSet ordersRS=this.getOrdersForDay(date);

        List<String> w3w=new ArrayList<String>();


        while (ordersRS.next()) {

            orderNumbers.add(ordersRS.getString(0));
            w3w.add(ordersRS.getString(4));

        }}catch(Exception e)
        {System.err.println("error getting orders for a specified day");}
        //2. get list of ordered item for each student's ordernumber from 1

        ResultSet orderDetailsForOrder;
        for (Iterator<String> iter = orderNumbers.iterator(); iter.hasNext(); ) {
            String currentOrderNumber = iter.next();
            orderDetailsForOrder=this.getOrderDetailsForOrder(currentOrderNumber);
            orderDetailsList.add(orderDetailsForOrder);
            //orderDetailsList is a list of result sets each result set contains all ordered items for a particular ordernumber
        }
        //3. for each order item find the w3w for that food item and store it in a set
        Set<String> w3wSet = new HashSet<String>();
        for (Iterator<ResultSet> iter = orderDetailsList.iterator(); iter.hasNext(); ) {


            ResultSet currentOrderDetailsList = iter.next();
            try {
                while (currentOrderDetailsList.next()) {

                    w3wSet.add(currentOrderDetailsList.getString(1));
                }
            }
            catch (Exception e)
            {System.err.println("error parsing finding w3w for each food item");}
        }
        //4.get the name and long lat for each w3w

        //how do I get the name when I only have the what three words?Lock-Based Protocols
        //Two Phase Locking Protocol
        //Timestamp-Based Protocols
        for (Iterator<String> iter = w3wSet.iterator(); iter.hasNext();) {
            String currentw3w=iter.next();
            String[] w3wSplit=currentw3w.split("\\.");
            this.getRestaurantInfo(w3wSplit[0],w3wSplit[1],w3wSplit[2],"");
        }

        //1.:

        return null;

    }*/
    /**
     * populateOrdersList - populates the orderslist with all orders of a given date alongside all of the relevant information for each order
     * @param date - String, we get all orders on this date
     * @throws  Exception - Exception, thrown if there is any issues reading the resultset
     */
    public void populateOrdersList(String date)
    {
        try
        {
            LongLat appletonTowerLongLat = new LongLat(-3.186874,55.944494);
            ResultSet ordersSet =this.getOrdersForDay(date);
            //Menus menu = new Menus(this.machineName,this.webPort);
        while(ordersSet.next())
        {
            //create a OrderDetails object, populate it then add it to orderslist
            //ordersList.add()

            OrderDetails nextOrder= new OrderDetails();
            nextOrder.orderNumber=ordersSet.getString(1);
            ResultSet nextOrdersDetails = getOrderDetailsForOrder(ordersSet.getString(1));
            String deliverTo= ordersSet.getString(4);
            LongLat dropOffPoint =this.getLongLatFromW3W(deliverTo);
            List<String> orderItems=new ArrayList<>();

            List<LongLat> locations=new ArrayList<>();
            while(nextOrdersDetails.next())
            {
                //for each order also save longlats of where  you pick up that order
                orderItems.add(nextOrdersDetails.getString(2));

                //This is all used to get a longlat for the restaurant of every item adn store in a list
                List<String> singleItem=new ArrayList<>();
                singleItem.add(nextOrdersDetails.getString(2));
                List<Menus.NameAndW3W> menusAndW3w= menu.getW3W(singleItem);
                LongLat nextLonglat =this.getLongLatFromW3W(menusAndW3w.get(0).W3W);
                locations.add(nextLonglat);
            }
            //Now go through each item in orderItems and add its cost to total and location to location List
            //use getDeliveryCost and getW3W( both take a list of strings)
            List<Menus.NameAndW3W> menusAndW3w= menu.getW3W(orderItems);
            int price=menu.getDeliveryCost(orderItems);
            nextOrder.orderprice=price;
            nextOrder.locations=menusAndW3w;


            locations.add(dropOffPoint);
            double distanceTOAT=dropOffPoint.distanceTo(appletonTowerLongLat);
            nextOrder.distanceFromAT=distanceTOAT;
            nextOrder.deliverToW3W=deliverTo;
            nextOrder.orderItems=orderItems;
            //
            nextOrder.orderLocationsLongLats=locations;

            //at very end add our nextOrder item to the list of orders
            //https://stackoverflow.com/questions/25311557/how-to-sort-objects-in-an-arraylist-with-an-object-parameter
            //once all orders are added sort them on moves + cost
            ordersList.add(nextOrder);



        }
            sortOrderList();
        }catch(Exception e)
        {
            System.err.println("error populating OrdersList");
        }
    }
    /**
     * addOrderToDatabase - adds the order number, what three words drop off point and cost of an order to the orders derby database
     * @param orderNo - String,order number of given order
     * @param whatThreeWords - String, what three words location of drop off point for the given order
     * @param cost - int, total cost of the given order in pence
     * @throws  Exception - Exception, thrown if there is any issues connecting to the database
     */
    public void addOrderToDatabase(String orderNo,String whatThreeWords,int cost){
        try {
            String jdbcString = "jdbc:derby://" + this.machineName + ":" + this.dbPort + "/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            PreparedStatement psOrder = conn.prepareStatement(
                    "insert into deliveries values (?, ?, ?)");
            psOrder.setString(1,orderNo);
            psOrder.setString(2,whatThreeWords);
            psOrder.setInt(3,cost);
            psOrder.execute();

        }
        catch(Exception e)
        {System.err.println("Failed to add order to the database");}


    }
    /**
     * createOrderDatabase - deletes then creates the deliveries table on teh derby database server
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public void createOrderDatabase(){
        try {
            String jdbcString = "jdbc:derby://" + this.machineName + ":" + this.dbPort + "/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute(
                    "create table deliveries(" +
                            "orderNo char(8), " +
                            "deliveredTo varchar(19), " +
                            "costInPence int )");
        }
        catch(Exception e)
        {System.err.println("Failed to create the order database");}
    }
    /*
    * totalMonetaryValueForDay - gets the total potential money that can be earned in one day if every order is completed
    * @param date_string - String, date which we calculate total monetary value for
    * @return totalOrderValue - double, total monetary value for the date in question (including delivery cost)
    * @throws e - Exception, thrown if there is an error with the database
    * */
    public double totalMonetaryValueForDay(String date_string){
        try {
            String jdbcString="jdbc:derby://"+this.machineName+":"+this.dbPort+"/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            String deliveryDateQuery = "select item from orders inner join orderDetails on orderDetails.orderNo=orders.orderNo where deliveryDate=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(deliveryDateQuery);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(date_string);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            psCourseQuery.setDate(1,sqlDate);
            ResultSet rs = psCourseQuery.executeQuery();
            double sum=0;
            List<String> orderItems=new ArrayList<>();
            while(rs.next())
            {
                orderItems.add(rs.getString(1));
            }
            double  totalOrderValue =(double) this.menu.getDeliveryCost(orderItems)-50;


            deliveryDateQuery = "select Count(orderNo) from orders where deliveryDate=(?)";
            psCourseQuery = conn.prepareStatement(deliveryDateQuery);
            psCourseQuery.setDate(1,sqlDate);
            rs = psCourseQuery.executeQuery();

            int potentialDeliveryProfits=0;
            if(rs.next())
            {potentialDeliveryProfits=Integer.parseInt(rs.getString(1));}
            totalOrderValue=totalOrderValue+50*potentialDeliveryProfits;
            return  totalOrderValue;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("error fetching derbyDB");
        }



        return  0;}

}
