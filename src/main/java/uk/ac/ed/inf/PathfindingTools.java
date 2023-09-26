package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import com.mapbox.turf.TurfJoins;

import java.util.ArrayList;
import java.util.List;

public class PathfindingTools {

    /**
     * LongLatWithMoves - stores a collection of LongLats and the collective costs of travelling through all of them
     */
    public static class LongLatWithMoves{
        List<LongLat>lineCollection=new ArrayList<>();
        int moves=0;

    }

    /**
     * LongLatWithMoves - heads from location to destination in a (close to with 10 degree angles) straight line
     * @param location - LongLat, LongLat of starting destination
     * @param destination - LongLat, LongLat of finishing destination
     * @returns myRoute -LongLatWithMoves,A list of moves showing how our drone could get from location to destination with the cost of the trip
     */
    public static LongLatWithMoves simplePathfinderV2(LongLat location, LongLat destination){
        LongLatWithMoves myRoute = new LongLatWithMoves();
        int localmoves=0;
        LongLat currentLocation=location;
        LongLat nextLocation;
        double angleTo;
        FeatureCollection path;
        FeatureCollection newLine;
        List<LongLat> lineCollection=new ArrayList<>();
       // System.out.println(currentLocation.closeTo(destination));
        if((currentLocation .closeTo(destination))==false)
        {
            while ((currentLocation.closeTo(destination))==false)
            {
                //calculate angle between two points
                lineCollection.add(currentLocation);
                angleTo=currentLocation.angleTo(destination);
                nextLocation=currentLocation.nextPosition(angleTo);
                currentLocation=nextLocation;
                localmoves++;
            }
            //return lineCollection;
        }
        if(currentLocation.closeTo(destination)==true)
        {
            lineCollection.add(currentLocation);
            localmoves++;
           // System.out.println("already at location");
            myRoute.lineCollection=lineCollection;
            myRoute.moves=localmoves;
            return myRoute;
        }
        return  null;
    }
    /**
     * LongLatWithMoves - heads from location to destination in a (close to with 10 degree angles) straight line with the addition that this has an additional hover move at the end
     * @param location - LongLat, LongLat of starting destination
     * @param destination - LongLat, LongLat of finishing destination
     * @returns myRoute -LongLatWithMoves,A list of moves showing how our drone could get from location to destination with the cost of the trip
     */
    public static LongLatWithMoves simplePathfinderWithHover(LongLat location, LongLat destination){
        LongLatWithMoves myRoute = new LongLatWithMoves();
        int localmoves=0;
        LongLat currentLocation=location;
        LongLat nextLocation;
        double angleTo;
        FeatureCollection path;
        FeatureCollection newLine;
        List<LongLat> lineCollection=new ArrayList<>();
        // System.out.println(currentLocation.closeTo(destination));
        if((currentLocation .closeTo(destination))==false)
        {
            while ((currentLocation.closeTo(destination))==false)
            {
                //calculate angle between two points
                lineCollection.add(currentLocation);
                angleTo=currentLocation.angleTo(destination);
                nextLocation=currentLocation.nextPosition(angleTo);
                currentLocation=nextLocation;
                localmoves++;
            }
            //return lineCollection;
        }
        if(currentLocation.closeTo(destination)==true)
        {
            lineCollection.add(currentLocation);
            localmoves++;
            //duplicate here as it is a hover move
            lineCollection.add(currentLocation);
            localmoves++;
            // System.out.println("already at location");
            myRoute.lineCollection=lineCollection;
            myRoute.moves=localmoves;
            return myRoute;
        }
        return  null;
    }

    /*public FeatureCollection simplePathfinder(LongLat location, LongLat destination){
        LongLat currentLocation=location;
        LongLat nextLocation;
        double angleTo;
        FeatureCollection path;
        FeatureCollection newLine;
        List<Feature> lineCollection;
        if((currentLocation .closeTo(destination))==false)
        {
            angleTo=currentLocation.angleTo(destination);
            nextLocation=currentLocation.nextPosition(angleTo);
            path=drawLine(currentLocation,nextLocation);
            lineCollection=path.features();


        while ((currentLocation.closeTo(destination))==false)
        {
            //calculate angle between two points
            angleTo=currentLocation.angleTo(destination);
            nextLocation=currentLocation.nextPosition(angleTo);
            newLine=drawLine(currentLocation,nextLocation);
            currentLocation=nextLocation;

            //add new feature collection to path;
            for (Feature feature:newLine.features()) {
                lineCollection.add(feature);
            }
          //  moves++;
        }
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(lineCollection);
        return featureCollection;
        }
        else if(currentLocation.closeTo(destination)==true)
        {
            //System.out.println("already at location");
            return null;
        }
        return  null;
    }
*/
  /*  public Double[][] simplePathfinderForCoords(LongLat location, LongLat destination){
        LongLat currentLocation=location;
        LongLat nextLocation;
        Double[][] result=new Double[2][];
        double angleTo;
        FeatureCollection path;
        FeatureCollection newLine;
        List<Feature> lineCollection;
        if((currentLocation .closeTo(destination))==false)
        {
            angleTo=currentLocation.angleTo(destination);
            nextLocation=currentLocation.nextPosition(angleTo);
            path=drawLine(currentLocation,nextLocation);
            lineCollection=path.features();


            while ((currentLocation.closeTo(destination))==false)
            {
                //calculate angle between two points
                angleTo=currentLocation.angleTo(destination);
                nextLocation=currentLocation.nextPosition(angleTo);
                newLine=drawLine(currentLocation,nextLocation);
                currentLocation=nextLocation;

                //add new feature collection to path;
                for (Feature feature:newLine.features()) {
                    lineCollection.add(feature);
                }
               // moves++;
            }
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(lineCollection);
            return null;
        }
        else if(currentLocation.closeTo(destination)==true)
        {
           // System.out.println("already at location");
            return null;
        }
        return  null;
    }*/

    /**
     * bfs - breadth first searches through the adjacency matrix and attempts to reach the destination from the starting node,
     * @param adjacency_matrix - Double[][], a matrix of every node and every node that node can reach. unreachable nodes set to INFINITY
     * @param node - Integer, an int corresponding to a node in our graph
     * @param destination - Integer , an int corresponding to a node in our graph
     * @returns pathToNode -List<Integer>, a list of Integers representing a legal node traversal from node to destination
     */

  /* public FeatureCollection drawLine(LongLat a, LongLat b)
    {
        List<Point> points = new ArrayList<>();
        points.add(Point.fromLngLat(1.0,2.0));
        points.add(Point.fromLngLat(4.0,8.0));
        MultiPoint multiPoint = MultiPoint.fromLngLats(points);
        LineString lineString = LineString.fromLngLats(multiPoint);

        String newLine4="{\n" +
                "\"type\": \"FeatureCollection\"," +
                "\"features\": [" +
                "{" +
                "\"type\": \"Feature\"," +
                "\"properties\": {}," +
                "\"geometry\": {" +
                "\"type\": \"LineString\"," +
                "\"coordinates\": [" +
                "[" +(a.longitude)+
                "," +
                (a.latitude) +
                "]," +
                "[" +(b.longitude)+
                "," +
                (b.latitude) +
                "]" +
                "]" +
                "}" +
                "}"+
                "]" +
                "}";
        FeatureCollection lineFC =FeatureCollection.fromJson(newLine4);
        return  lineFC;
    }
*/
    /**
     * lineLineIntersect - checks if a LongLat is within the collisionzones set of polygons
     * @param collisionzones - FeatureCollection, a collection of the no-fly-zone polygons
     * @param newPoint - LongLat, the point we are checking the legality of
     * @returns True/False -Boolean, True if within polygons and false if not
     */
    public static Boolean lineLineIntersect(FeatureCollection collisionzones, LongLat newPoint )
    {

        Point point = Point.fromLngLat(newPoint.longitude, newPoint.latitude);
        FeatureCollection pointFC =FeatureCollection.fromFeature(Feature.fromGeometry(point));
        FeatureCollection a =TurfJoins.pointsWithinPolygon(pointFC,collisionzones);
        if(a.features().size()==0)
        {
            //System.out.println("not within polygons!!!");
            return false;
        }
        else{
            //System.out.println(a.features()+"\n"+a.features().size());
            return true;
        }
    }
    /**
     * checkIfRouteClear - checks to see if you can go from a to be without going through a collision zone
     * @param collisionzones - FeatureCollection, a collection of the no-fly-zone polygons
     * @param location - LongLat, the starting LongLat
     * @param destination - LongLat, the endpoint LongLat
     * @returns True/False -Boolean, True if there is no collision zones between two points
     */
    public static Boolean checkIfRouteClear(LongLat location, LongLat destination,FeatureCollection collisionzones)
    {
        LongLat currentLocation=location;
        LongLat nextLocation;
        double angleTo;

        if((currentLocation .closeTo(destination))==false)
        {
            angleTo=currentLocation.angleTo(destination);
            nextLocation=currentLocation.nextPosition(angleTo);


            while ((currentLocation.closeTo(destination))==false)
            {
                //calculate angle between two points
                angleTo=currentLocation.angleTo(destination);
                nextLocation=currentLocation.nextPosition(angleTo);
                if (lineLineIntersect(collisionzones,nextLocation))
                {
                    return false;
                }
                currentLocation=nextLocation;

            }
            return true;
        }
        return  true;
    }

    /**
     * longLatListToFC - Takes a list of LongLats and turns that in to a Featurecollection with one feature which is a linestring with all of the LongLats in it
     * @param lineStringPoints - List<LongLat>, a list of all the points to be converted to a linestring
     * @returns routeFC -FeatureCollection, a single feature which is a Linestring containign all points in lineStringPoints
     */
    public static FeatureCollection longLatListToFC(List<LongLat> lineStringPoints)
    {
        String listOfCoordinates="["+"["+lineStringPoints.get(0).longitude+","+lineStringPoints.get(0).latitude+"]";
        for(int i=1;i<lineStringPoints.size();i++)
        {
            listOfCoordinates=listOfCoordinates+",["+lineStringPoints.get(i).longitude+","+lineStringPoints.get(i).latitude+"]";

        }
        listOfCoordinates=listOfCoordinates+"]";
        String newLine4="{\n" +
                "\"type\": \"FeatureCollection\"," +
                "\"features\": [" +
                "{" +
                "\"type\": \"Feature\"," +
                "\"properties\": {}," +
                "\"geometry\": {" +
                "\"type\": \"LineString\"," +
                "\"coordinates\":" +
                listOfCoordinates
                +
                "}" +
                "}"+
                "]" +
                "}";
        FeatureCollection routeFC =FeatureCollection.fromJson(newLine4);
        return  routeFC;

    }

}
