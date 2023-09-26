package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

class Pathfinding
{

    private Queue<List<Integer>> queue;
    private Set<Integer> visited;
    private  String machineName;
    private  String webPort;
    private String dbPort;
    private static final Double INFINITY = Double.valueOf(999);
    private List<LongLat> allNodes =new ArrayList<>();

    /**
     * Pathfinding2 - instantiates class and assigns machine name, web port, and database port
     */
    public Pathfinding(String machineName, String port, String dbPort)
    {
        this.machineName=machineName;
        this.webPort =port;
        this.dbPort=dbPort;
    }
    /**
     * MyGeometry - used to parse coordinates from featurecollection jsons
     */
    public class MyGeometry {
        String type;
        Double[] coordinates;
    }
    /**
     * Edge - used to store the edges of the graph we make with their weights
     */
    static class Edge {
        int src, dest;
        double weight;

        Edge(int src, int dest, double weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }
    }
    /**
     * getAllNodes - Retrieves the List of LongLats which make up all the nodes
     * @returns this.allNodes - List<LongLat>, a list of LongLat
     */
    public List<LongLat> getAllNodes(){return  this.allNodes;}

    /**
     * Edges - Retrieves a list of names and what three word strings where we can buy the given items
     * @param machineName - String,Name of the machine our server is hosted on
     * @param port - String,port of the machine our server is hosted on
     * @returns adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     */
    public Double[][] Edges(String machineName, String port) {
        allNodes=new ArrayList<>();
        //1. Get long lat list of  restaurants[x] , dropoff points[x], landmarks[x],and appleton tower[x]
        //2. Get no-fly zones[x]
        //3. For each longlat pair in 1. try to go from from a to b without a collision.
        //4. for 3 we should get list of all nodes and check each node against that list
        //   If successful store that pair as an edge

        Menus menu = new Menus(machineName, port);
        Orders orders = new Orders(machineName, port,"1527");
        PathfindingTools pathfindingTools = new PathfindingTools();


        //Get list of Longlats for dropOffPoints
        List<String> dropOffPointsW3W = orders.getUniqueDropOffPoints();
        List<LongLat> dropOffPoints = new ArrayList<>();
        for(String w3w:dropOffPointsW3W)
        {

            dropOffPoints.add(orders.getLongLatFromW3W(w3w));
        }

        //Get list of LongLats for all restaurants
        List<Menus.NameAndW3W> restaurantListW3W = menu.getAllRestaurantW3W();
        List<LongLat> restaurantList = new ArrayList<>();
        for(Menus.NameAndW3W w3w:restaurantListW3W)
        {

            restaurantList.add(orders.getLongLatFromW3W(w3w.W3W));

        }

        //Get list of Landmarks Longlat
        String request = "http://localhost:" + port + "/buildings/landmarks.geojson";
        String response = HTTPRequests.doGETRequest(request);
        FeatureCollection landmarksFeatureCollection = FeatureCollection.fromJson(response);
        List<Feature> landmarkLongFeature= landmarksFeatureCollection.features();
        List<LongLat> landmarks = new ArrayList<>();
        for (Iterator<Feature> iter = landmarkLongFeature.iterator(); iter.hasNext(); ) {
            Feature currentFeature=iter.next();

            //System.out.println("issue here");
            String json = currentFeature.geometry().toJson();
           // String json ="banana";
            Type listType = new TypeToken<MyGeometry>() {} .getType();
            MyGeometry myGeometry= new Gson().fromJson(json,listType);
            LongLat geometryCoordinates =new LongLat(myGeometry.coordinates[0],myGeometry.coordinates[1]);
            landmarks.add(geometryCoordinates);

        }

        //AT LongLat
        final LongLat appletonTowerLongLat = new LongLat(-3.186874,55.944494);



        request = "http://"+this.machineName+":" + this.webPort + "/buildings/no-fly-zones.geojson";
        response = HTTPRequests.doGETRequest(request);
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response);
        //Create a list of all nodes
        allNodes.addAll(dropOffPoints);
        allNodes.addAll(restaurantList);
        allNodes.addAll(landmarks);
        allNodes.add(appletonTowerLongLat);
        List<Edge> edges = new ArrayList<>();


        Double adjacency_matrix[][] = new Double[allNodes.size()][allNodes.size()];

        //change this so that it reads in our list of longlats and uses that

        for (int source = 0; source < allNodes.size(); source++)
        {
            for (int destination = 0; destination < allNodes.size(); destination++)
            {
                if (pathfindingTools.checkIfRouteClear(allNodes.get(source),allNodes.get(destination),noFlyZone))
                {
                    adjacency_matrix[source][destination]=allNodes.get(source).distanceTo(allNodes.get(destination));
                }
                if (source == destination)
                {
                    adjacency_matrix[source][destination] = Double.valueOf(0);
                   // System.out.println(source + "hi" + destination);
                  //  System.out.println(adjacency_matrix[source][destination]);
                    continue;
                }
                if (adjacency_matrix[source][destination] == null)
                {
                    adjacency_matrix[source][destination] = INFINITY;
                }
            }
        }
      //  System.out.println("The Transitive Closure of the Graph");
      //  Pathfinding2 allPairShortestPath= new Pathfinding2(allNodes.size(),"localhost", "9898","1527");
      //  allPairShortestPath.allPairShortestPath(adjacency_matrix);
        return  adjacency_matrix;

    }

    /**
     * route - Retrieves a list of names and what three word strings where we can buy the given items
     * @param adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @param destinations - List<LongLat>, a list of destinations which we want to route to
     * @returns route -PathfindingTools.LongLatWithMoves>, a list of LongLats and the amount of moves it took to get from the first LongLat to the Last
     */
    public PathfindingTools.LongLatWithMoves route(Double adjacency_matrix[][],List<LongLat> destinations)
    {
        //Double[][] adjacencyMatrix=this.Edges("localhost", "9898");
        List<Integer> nodesInOrderIndex=new ArrayList<>();
        LongLat a;
        LongLat b;
        for(int i =0;i+1<destinations.size();i++)
        {

            a=this.getNodeGivenLongLat(destinations.get(i));
            b=this.getNodeGivenLongLat(destinations.get(i+1));
            if(a==null || b==null)
            {
                System.err.println("given LongLats are not found to be a node in the graph");
                return  null;
            }

            nodesInOrderIndex.addAll(this.bfs(allNodes.indexOf(a),allNodes.indexOf(b),adjacency_matrix));
        }

        List<LongLat> nodesInOrder=new ArrayList<>();
        for (Iterator<Integer> iter = nodesInOrderIndex.iterator(); iter.hasNext(); )
        {
            int currentNode = iter.next();
          //  allNodes.get(currentNode);
            nodesInOrder.add(allNodes.get(currentNode));
        }
        PathfindingTools pfTools = new PathfindingTools();
        //FeatureCollection route=pfTools.simplePathfinder(nodesInOrder.get(0),nodesInOrder.get(1));
        PathfindingTools.LongLatWithMoves route2=pfTools.simplePathfinderWithHover(nodesInOrder.get(0),nodesInOrder.get(1));
        //new Longlat array system
        for (int i =1;i+1<nodesInOrder.size();i++)
        {
            //If it is the last node in our list then perform an additional hover when we get there
            if(i+2==nodesInOrder.size())
            {
                PathfindingTools.LongLatWithMoves currentLLs=pfTools.simplePathfinderWithHover(nodesInOrder.get(i),nodesInOrder.get(i+1));
                route2.moves=route2.moves+currentLLs.moves;
                for (LongLat longlat:currentLLs.lineCollection)
                {
                    route2.lineCollection.add(longlat);

                }
            }
            else{
            PathfindingTools.LongLatWithMoves currentLLs=pfTools.simplePathfinderV2(nodesInOrder.get(i),nodesInOrder.get(i+1));
            route2.moves=route2.moves+currentLLs.moves;
            for (LongLat longlat:currentLLs.lineCollection)
            {
                    route2.lineCollection.add(longlat);

            }}
        }
        FeatureCollection route2AsFC =pfTools.longLatListToFC(route2.lineCollection);
        return  route2;
    }




    /**
     * bfs - breadth first searches through the adjacency matrix and attempts to reach the destination from the starting node,
     * @param adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @param node - Integer, an int corresponding to a node in our graph
     * @param destination - Integer , an int corresponding to a node in our graph
     * @returns pathToNode -List<Integer>, a list of Integers representing a legal node traversal from node to destination
     */
    public List<Integer> bfs(Integer node,Integer destination, Double adjacency_matrix[][]) {

        if(node == null || destination==null){ System.err.println("returning null because a null node was passed in");return null; }


        queue = new LinkedList<>(); //initialize queue
        visited = new HashSet<>();  //initialize visited log

        //a collection to hold the path through which a node has been reached
        //the node it self is the last element in that collection
        List<Integer> pathToNode = new ArrayList<>();
        pathToNode.add(node);

        queue.add(pathToNode);

        while (! queue.isEmpty()) {

            pathToNode = queue.poll();
            //get node (last element) from queue
            node = pathToNode.get(pathToNode.size()-1);

            if(isSolved(node,destination,adjacency_matrix)) {
                //print path
                pathToNode.add(destination);
               // System.out.println(pathToNode);
                return pathToNode;
            }

            //loop over neighbors
            for(int nextNode : getNeighbors(node,adjacency_matrix)){

                Boolean isVisit=isVisited(nextNode);
                if(!isVisit) {
                    //create a new collection representing the path to nextNode
                    List<Integer> pathToNextNode = new ArrayList<>(pathToNode);
                    pathToNextNode.add(nextNode);
                    queue.add(pathToNextNode); //add collection to the queue
                }
            }
        }

        return null;
    }

    /**
     * getNeighbors - Retrieves all neighboring nodes of the given node
     * @param adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @param node - Integer, an int corresponding to a node in our graph
     * @returns neighbourNodes -List<Integer>, a list of Integers representing all nodes reachable from the given node
     */
    private List<Integer> getNeighbors(Integer node, Double adjacency_matrix[][])
    {
        List<Integer> neighbourNodes = new ArrayList<>();
        for (int i=0;i<adjacency_matrix.length;i++)
        {
            if(adjacency_matrix[node][i]!=INFINITY)
            {
                neighbourNodes.add(i);
            }
        }

       return  neighbourNodes;
    }

    /**
     * isSolved - checks to see if we have found a way to get from node to destination
     * @param adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @param node - Integer, an int corresponding to a node in our graph
     * @param destination - Integer, an int corresponding to a node in our graph
     * @returns True/False ,if how to get from node to destination is known return true else return false
     */
    private boolean isSolved(Integer node, Integer destination, Double adjacency_matrix[][])
    {
        if (adjacency_matrix[node][destination]!=INFINITY){return  true;}
        return false;
    }
    /**
     * getNodeGivenLongLat - checks to see if we have found a way to get from node to destination
     * @param longlat - LongLat, a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @returns LongLat , accesses allNodes and retrieves the LongLat of a given node in our graph
     */
    public LongLat getNodeGivenLongLat(LongLat longlat)
    {
        for(int i =0;i<allNodes.size();i++)
        {
            if(allNodes.get(i).closeTo(longlat))
            {
                return  allNodes.get(i);
            }
        }
        return  null;
    }

    /**
     * isVisited - checks to see a given node has been visited and adds it to the list of visited nodes
     * @param node - Integer, an int corresponding to a node in our graph
     * @returns True/False - Boolean, returns true if it as been visited before and false otherwise
     */
    private boolean isVisited(Integer node) {
        if(visited.contains(node)) { return true;}
        visited.add(node);
        return false;
    }

    /**
     * addToFlightPathDatabase - access the derby database table 'flightpath' and fills in the next row
     * @param a - LongLat, where the drone started
     * @param b - LongLat, where the drone went
     * @param orderNumber - String, the order number the drone is completing
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public void addToFlightPathDatabase(LongLat a, LongLat b, String orderNumber){
    try {
        String jdbcString = "jdbc:derby://" + this.machineName + ":" + this.dbPort + "/derbyDB";
        Connection conn = DriverManager.getConnection(jdbcString);
        Statement statement = conn.createStatement();
        //If both are equal it is a hover move so angle between should be set to -999
        int angleBetween=(int) a.angleTo(b);
        if(Double.compare(a.longitude,b.longitude)==0&&Double.compare(a.latitude,b.latitude)==0)
        {
            angleBetween=-999;
        }
        DatabaseMetaData databaseMetadata = conn.getMetaData();
        PreparedStatement psOrder = conn.prepareStatement(
                "insert into flightpath values (?, ?, ?,?,?,?)");
        psOrder.setString(1,orderNumber);
        psOrder.setDouble(2,a.longitude);
        psOrder.setDouble(3,a.latitude);
        psOrder.setInt(4,angleBetween);
        psOrder.setDouble(5,b.longitude);
        psOrder.setDouble(6,b.latitude);
        psOrder.execute();
    }
    catch(Exception e)
    {System.err.println("Failed to add flightpath to the database");}


}

    /**
     * createFlightPathDatabase - creates the derby database table flightpath and deletes anything by the same name if it already exists
     * @throws  Exception - Exception, thrown if there is any issues exploring the data structures
     */
    public void createFlightPathDatabase(){
        try {
            String jdbcString = "jdbc:derby://" + this.machineName + ":" + this.dbPort + "/derbyDB";
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute(
                    "create table flightpath(" +
                            "orderNo char(8), " +
                            "fromLongitude double, " +
                            "fromLatitude double, " +
                            "angle integer, " +
                            "toLongitude double, " +
                            "toLatitude double)");
        }
        catch(Exception e)
        {System.err.println("Failed to create the flightpath database");}
    }


}