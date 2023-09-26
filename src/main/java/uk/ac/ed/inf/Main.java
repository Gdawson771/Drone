package uk.ac.ed.inf;

import com.mapbox.geojson.FeatureCollection;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;  // Import the IOException class to handle errors

public class Main {
    public static void main(String[] args){
        final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
        int moves=0;
        final String host= "localhost";
        final int movesMax=1500;
        //final String webPort = "9898";
        final String day=args[0];
        final String month= args[1];
        final String year =args[2];
        final String webPort = args[3];
        final String dbport =args[4];
        String date = year + "-" +month +"-"+day;
        Orders orders = new Orders(host,webPort,dbport);
        PathfindingTools pft = new PathfindingTools();
        List<LongLat> entireRouteOfDay=new ArrayList<>();
        orders.populateOrdersList(date);

       // orders.populateOrdersList("2023-12-27");
        double revenueSum=0;

        //start route from appleton tower
        entireRouteOfDay.add(appletonTower);

        //Create graph
        Pathfinding pf = new Pathfinding(host, webPort,dbport);
        Double edgeMatrix[][]=pf.Edges(host, webPort);

        //Create tables
        orders.createOrderDatabase();
        pf.createFlightPathDatabase();
        for (Iterator<Orders.OrderDetails> iter = orders.ordersList.iterator(); iter.hasNext();)
        {
            Orders.OrderDetails item = iter.next();

            List<LongLat> temps =new ArrayList<>();
            temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
            temps.addAll(item.orderLocationsLongLats);
            PathfindingTools.LongLatWithMoves currentSet=pf.route(edgeMatrix,temps);

            List<LongLat> ATcheck =new ArrayList<>();
            ATcheck.add(appletonTower);
            ATcheck.add(currentSet.lineCollection.get(currentSet.lineCollection.size()-1));
            PathfindingTools.LongLatWithMoves DistanceToAt=pf.route(edgeMatrix,ATcheck);
            if(moves+currentSet.moves+DistanceToAt.moves<movesMax)
            {
                moves=moves+currentSet.moves;
                entireRouteOfDay.addAll(currentSet.lineCollection);
                revenueSum=revenueSum+item.orderprice;
                orders.addOrderToDatabase(item.orderNumber,item.deliverToW3W,(int) item.orderprice);
                //For each entry in currentSet.lineCollection add it to flightpath database alongside angle between the two using angleTo
                for(int i=0;i<currentSet.lineCollection.size()-1;i++)
                {
                    pf.addToFlightPathDatabase(currentSet.lineCollection.get(i),currentSet.lineCollection.get(i+1),item.orderNumber);
                }
            }
        }
        List<LongLat> temps =new ArrayList<>();
        temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
        temps.add(appletonTower);
        entireRouteOfDay.addAll((pf.route(edgeMatrix,temps).lineCollection));
        FeatureCollection entireRouteAsFC=pft.longLatListToFC(entireRouteOfDay);
        System.out.println("moves: " +moves);
        System.out.println("Revenue for day : "+ revenueSum);
        System.out.println("percentage monetary value for day: " +revenueSum/orders.totalMonetaryValueForDay(date));
       // System.out.println(entireRouteAsFC.toJson());
        try {
            FileWriter myWriter = new FileWriter("drone-"+date+".geojson");
            myWriter.write(entireRouteAsFC.toJson());
            myWriter.close();
        } catch (IOException e) {
            System.err.println("An error occurred creating the drone file.");
            e.printStackTrace();
        }
    }
}
