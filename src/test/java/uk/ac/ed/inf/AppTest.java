package uk.ac.ed.inf;

import com.mapbox.geojson.FeatureCollection;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);

    @Test
    public void testIsConfinedTrueA(){
        assertTrue(appletonTower.isConfined());
    }

    @Test
    public void testIsConfinedTrueB(){
        assertTrue(businessSchool.isConfined());
    }

    @Test
    public void testIsConfinedFalse(){
        assertFalse(greyfriarsKirkyard.isConfined());
    }

    private boolean approxEq(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-12;
    }

    @Test
    public void testDistanceTo(){
        double calculatedDistance = 0.0015535481968716011;
        assertTrue(approxEq(appletonTower.distanceTo(businessSchool), calculatedDistance));
    }

    @Test
    public void testCloseToTrue(){
        LongLat alsoAppletonTower = new LongLat(-3.186767933982822, 55.94460006601717);
        assertTrue(appletonTower.closeTo(alsoAppletonTower));
    }


    @Test
    public void testCloseToFalse(){
        assertFalse(appletonTower.closeTo(businessSchool));
    }


    private boolean approxEq(LongLat l1, LongLat l2) {
        return approxEq(l1.longitude, l2.longitude) &&
                approxEq(l1.latitude, l2.latitude);
    }

    @Test
    public void testAngle0(){
        LongLat nextPosition = appletonTower.nextPosition(0);
        LongLat calculatedPosition = new LongLat(-3.186724, 55.944494);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle20(){
        LongLat nextPosition = appletonTower.nextPosition(20);
        LongLat calculatedPosition = new LongLat(-3.186733046106882, 55.9445453030215);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle50(){
        LongLat nextPosition = appletonTower.nextPosition(50);
        LongLat calculatedPosition = new LongLat(-3.186777581858547, 55.94460890666647);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle90(){
        LongLat nextPosition = appletonTower.nextPosition(90);
        LongLat calculatedPosition = new LongLat(-3.186874, 55.944644);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle140(){
        LongLat nextPosition = appletonTower.nextPosition(140);
        LongLat calculatedPosition = new LongLat(-3.1869889066664676, 55.94459041814145);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle190(){
        LongLat nextPosition = appletonTower.nextPosition(190);
        LongLat calculatedPosition = new LongLat(-3.1870217211629517, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle260(){
        LongLat nextPosition = appletonTower.nextPosition(260);
        LongLat calculatedPosition = new LongLat(-3.18690004722665, 55.944346278837045);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle300(){
        LongLat nextPosition = appletonTower.nextPosition(300);
        LongLat calculatedPosition = new LongLat(-3.186799, 55.94436409618943);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle350(){
        LongLat nextPosition = appletonTower.nextPosition(350);
        LongLat calculatedPosition = new LongLat(-3.1867262788370483, 55.94446795277335);
        assertTrue(approxEq(nextPosition, calculatedPosition));
    }

    @Test
    public void testAngle999(){
        // The special junk value -999 means "hover and do not change position"
        LongLat nextPosition = appletonTower.nextPosition(-999);
        assertTrue(approxEq(nextPosition, appletonTower));
    }

    @Test
    public void testMenusOne() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = new Menus("localhost", "9898");

        List<String> items= new ArrayList<>();
        items.add("Ham and mozzarella Italian roll");
        int totalCost = menus.getDeliveryCost(items);
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 50, totalCost);
    }

    @Test
    public void testMenusTwo() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Ham and mozzarella Italian roll");
        items.add("Salami and Swiss Italian roll");
        int totalCost = menus.getDeliveryCost(items);
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 50, totalCost);
    }

    @Test
    public void testMenusThree() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");

        items.add("Ham and mozzarella Italian roll");
        items.add("Salami and Swiss Italian roll");
        int totalCost = menus.getDeliveryCost(items);
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 460 + 50, totalCost);
    }

    @Test
    public void testMenusFourA() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Dirty matcha latte");
        items.add("Ham and mozzarella Italian roll");
        items.add("Salami and Swiss Italian roll");
        int totalCost = menus.getDeliveryCost(items);
        // Don't forget the standard delivery charge of 50p
        assertEquals(230 + 230 + 460 + 460 + 50, totalCost);
    }

    @Test
    public void testMenusFourB() {
        // The webserver must be running on port 9898 to run this test.
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Dirty matcha latte");
        items.add("Fresh taro latte");
        items.add("Salami and Swiss Italian roll");
        int totalCost = menus.getDeliveryCost(items);
        // Don't forget the standard delivery charge of 50p
        assertEquals(1660, totalCost);
    }
    @Test
    public void testOrdersPractice(){
        Orders orders = new Orders("localhost", "9898","1527");
        FeatureCollection response = orders.getRestaurant("less","change","atomic","name buffer");
        System.out.println(response);
    }

    @Test
    public void testDerbyGetOrdersForDay(){
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a =orders.getOrdersForDay("2022-04-11");

        try {
            if( a.next())
            {
                assertTrue(a.getString(3).equals("s2280261"));}

            if( a.next())
            { assertTrue(a.getString(3).equals("s2230282"));}
        }catch(Exception e)
        {System.err.println("testDerbyGetOrdersForDay test failed");}
        }
    @Test
    public void testDerbyGetOrderDetailsForOrder() {
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a = orders.getOrderDetailsForOrder("987526aa");
        System.out.println("hi");
        try{
        if( a.next())
        {
            assertTrue(a.getString(2).equals("Feta, olives, Greek yoghurt and tomato French country roll"));
            assertTrue(a.getString(1).equals("987526aa"));
        }

        if( a.next())
        {
            assertTrue(a.getString(2).equals("Mozzarella, tomato and basil mayo poppy seed roll"));
            assertTrue(a.getString(1).equals("987526aa"));
        }}
        catch (Exception e)
        {System.err.println("test Derby get order details for order failed");}
    }
    @Test
    public void testGetW3WFromOrderDetailsA(){
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a =orders.getOrderDetailsForOrder("987526aa");
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Salami and Swiss Italian roll");
        List<Menus.NameAndW3W> testDetails = menus.getW3W(items);
        System.out.println(testDetails.get(1).name+"\n"+testDetails.get(1).W3W);
        assertTrue(testDetails.get(0).name.equals("Bing Tea"));
        assertTrue(testDetails.get(0).W3W.equals("looks.clouds.daring"));
    }
    @Test
    public void testGetW3WFromOrderDetailsB(){
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a =orders.getOrderDetailsForOrder("987526aa");
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Salami and Swiss Italian roll");
        List<Menus.NameAndW3W> testDetails = menus.getW3W(items);
        assertTrue(testDetails.get(1).name.equals("Rudis"));
        assertTrue(testDetails.get(1).W3W.equals("sketch.spill.puzzle"));
    }
    @Test
    public void testGetFeatureCollectionFromOrderDetailsB(){
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a =orders.getOrderDetailsForOrder("987526aa");
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Salami and Swiss Italian roll");
        List<Menus.NameAndW3W> testDetails = menus.getW3W(items);

        String[] detailsArray=testDetails.get(1).W3W.split("\\.");
        FeatureCollection FC=orders.getRestaurant(detailsArray[0],detailsArray[1],detailsArray[2],testDetails.get(1).name);
        assertTrue(FC.toString().equals("FeatureCollection{type=FeatureCollection, bbox=null, features=[Feature{type=Feature, bbox=null, id=null, geometry=Point{type=Point, bbox=null, coordinates=[-3.191065, 55.945626]}, properties={\"name\":\"Rudis\",\"location\":\"sketch.spill.puzzle\",\"marker-symbol\":\"landmark\",\"marker-color\":\"#0000ff\"}}]}"));
    }
    @Test
    public void testGetFeatureCollectionFromOrderDetailsA(){
        Orders orders = new Orders("localhost", "9898","1527");
        ResultSet a =orders.getOrderDetailsForOrder("987526aa");
        Menus menus = new Menus("localhost", "9898");
        List<String> items= new ArrayList<>();
        items.add("Flaming tiger latte");
        items.add("Salami and Swiss Italian roll");
        List<Menus.NameAndW3W> testDetails = menus.getW3W(items);

        String[] detailsArray=testDetails.get(0).W3W.split("\\.");
        FeatureCollection FC=orders.getRestaurant(detailsArray[0],detailsArray[1],detailsArray[2],testDetails.get(0).name);
        assertTrue(FC.toString().equals("FeatureCollection{type=FeatureCollection, bbox=null, features=[Feature{type=Feature, bbox=null, id=null, geometry=Point{type=Point, bbox=null, coordinates=[-3.185332, 55.944656]}, properties={\"name\":\"Bing Tea\",\"location\":\"looks.clouds.daring\",\"marker-symbol\":\"landmark\",\"marker-color\":\"#0000ff\"}}]}"));
    }
    @Test
    public void testAngleToA(){
        LongLat a= new LongLat(-3,5);
        LongLat b= new LongLat(-2,4);
        System.out.println(a.angleTo(b));
        assertTrue(Double.compare(a.angleTo(b),320)==0);


    }
    @Test
    public void testAngleToB(){
        LongLat a= new LongLat(-3,5);
        LongLat b= new LongLat(-2,4);
        System.out.println(a.angleTo(b));
        assertTrue(Double.compare(b.angleTo(a),140)==0);


    }

    @Test
    public  void testLineLineIntersectfalse(){
        Orders orders = new Orders("localhost", "9898","1527");
        FeatureCollection noFlyZones =orders.getNoFlyZones();
        LongLat a= new LongLat(1,55.945162658358925);
        LongLat b= new LongLat(-3.1838035583496094,55.942759384112584);
        PathfindingTools pathfiner = new PathfindingTools();
        System.out.println(pathfiner.lineLineIntersect(noFlyZones,b));
        assertFalse(pathfiner.lineLineIntersect(noFlyZones,b));


    }

    @Test
    public void testGetAllW3W(){
        Menus menus = new Menus("localhost", "9898");
        List<Menus.NameAndW3W> list =menus.getAllRestaurantW3W();
        Iterator<Menus.NameAndW3W> iter = list.iterator(); iter.hasNext();
        Menus.NameAndW3W current=iter.next();
        assertTrue(current.W3W.equals("sketch.spill.puzzle"));
        current=iter.next();
        assertTrue(current.W3W.equals("milky.hers.focus"));
        current=iter.next();
        assertTrue(current.W3W.equals("pest.round.peanut"));
        current=iter.next();
        assertTrue(current.W3W.equals("fund.dreams.years"));
        current=iter.next();
        assertTrue(current.W3W.equals("looks.clouds.daring"));

    }
    @Test
    public void testGetUniqueDropOffPoints(){
        Orders orders = new Orders("localhost", "9898","1527");
        List<String> rs =orders.getUniqueDropOffPoints();
        System.out.println(rs.get(0));
        assertTrue(rs.get(0).equals("linked.pads.cigar"));
        assertTrue(rs.get(1).equals("truck.hits.early"));
        assertTrue(rs.get(2).equals("eager.them.agenda"));
        assertTrue(rs.get(3).equals("surely.native.foal"));
        assertTrue(rs.get(4).equals("spell.stick.scale"));
        assertTrue(rs.get(5).equals("less.change.atomic"));
        assertTrue(rs.size()==6);


    }

    @Test
    public void testRoute(){
        Orders orders = new Orders("localhost", "9898","1527");
        Pathfinding pathfinding2 = new Pathfinding("localhost", "9898","1527");
        PathfindingTools pfTools=new PathfindingTools();
        Double a[][]=pathfinding2.Edges("localhost", "9898");
        LongLat b= new LongLat(-3.18933,55.943389);
        LongLat c= new LongLat(-3.191065,55.945626);
        LongLat d= new LongLat(-3.191594,55.943658);
        LongLat e= new LongLat(-3.186199,55.945734);
        List<LongLat> destinations = new ArrayList<>();
        destinations.add(b);
        destinations.add(c);
        destinations.add(e);
        destinations.add(b);
        destinations.add(d);
        destinations.add(c);
        FeatureCollection route2AsFC =pfTools.longLatListToFC(pathfinding2.route(a,destinations).lineCollection);
        System.out.println(route2AsFC.toString());
        assertTrue(route2AsFC.toString().equals("FeatureCollection{type=FeatureCollection, bbox=null, features=[Feature{type=Feature, bbox=null, id=null, geometry=LineString{type=LineString, bbox=null, coordinates=[Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18948, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18963, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1897800000000003, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1899300000000004, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1900800000000005, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.190227721162952, 55.94341504722665]}, Point{type=Point, bbox=null, coordinates=[-3.190375442325904, 55.9434410944533]}, Point{type=Point, bbox=null, coordinates=[-3.1905231634888556, 55.943467141679946]}, Point{type=Point, bbox=null, coordinates=[-3.1906708846518073, 55.943493188906594]}, Point{type=Point, bbox=null, coordinates=[-3.190818605814759, 55.94351923613324]}, Point{type=Point, bbox=null, coordinates=[-3.1909663269777107, 55.94354528335989]}, Point{type=Point, bbox=null, coordinates=[-3.1911140481406624, 55.943571330586536]}, Point{type=Point, bbox=null, coordinates=[-3.191261769303614, 55.943597377813184]}, Point{type=Point, bbox=null, coordinates=[-3.1914094904665657, 55.94362342503983]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1915679527733496, 55.94380572116295]}, Point{type=Point, bbox=null, coordinates=[-3.1915419055466994, 55.943953442325906]}, Point{type=Point, bbox=null, coordinates=[-3.191515858320049, 55.94410116348886]}, Point{type=Point, bbox=null, coordinates=[-3.191489811093399, 55.94424888465181]}, Point{type=Point, bbox=null, coordinates=[-3.1914637638667487, 55.94439660581477]}, Point{type=Point, bbox=null, coordinates=[-3.1914377166400985, 55.94454432697772]}, Point{type=Point, bbox=null, coordinates=[-3.1914116694134482, 55.94469204814067]}, Point{type=Point, bbox=null, coordinates=[-3.191360366391949, 55.94483300203379]}, Point{type=Point, bbox=null, coordinates=[-3.19130906337045, 55.944973955926905]}, Point{type=Point, bbox=null, coordinates=[-3.191257760348951, 55.94511490982002]}, Point{type=Point, bbox=null, coordinates=[-3.191206457327452, 55.945255863713136]}, Point{type=Point, bbox=null, coordinates=[-3.191155154305953, 55.94539681760625]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}, Point{type=Point, bbox=null, coordinates=[-3.191065, 55.945626]}, Point{type=Point, bbox=null, coordinates=[-3.191065, 55.945626]}, Point{type=Point, bbox=null, coordinates=[-3.1909172788370483, 55.945652047226645]}, Point{type=Point, bbox=null, coordinates=[-3.1907695576740966, 55.94567809445329]}, Point{type=Point, bbox=null, coordinates=[-3.190621836511145, 55.94570414167994]}, Point{type=Point, bbox=null, coordinates=[-3.1904741153481933, 55.94573018890659]}, Point{type=Point, bbox=null, coordinates=[-3.1903263941852416, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1901763941852415, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1900263941852414, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1898763941852413, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.189726394185241, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.189576394185241, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.189426394185241, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.189276394185241, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.189126394185241, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1889763941852407, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1888263941852406, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1886763941852405, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1885263941852404, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1883763941852403, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1882263941852402, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.18807639418524, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.18792639418524, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.18777639418524, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.18762639418524, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1874763941852398, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1873263941852397, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1871763941852396, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1870263941852395, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1868763941852394, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.1867263941852393, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.186576394185239, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.186426394185239, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.186276394185239, 55.945756236133235]}, Point{type=Point, bbox=null, coordinates=[-3.186199, 55.945734]}, Point{type=Point, bbox=null, coordinates=[-3.186199, 55.945734]}, Point{type=Point, bbox=null, coordinates=[-3.1863139066664674, 55.94563758185855]}, Point{type=Point, bbox=null, coordinates=[-3.186428813332935, 55.945541163717095]}, Point{type=Point, bbox=null, coordinates=[-3.1865437199994027, 55.94544474557564]}, Point{type=Point, bbox=null, coordinates=[-3.1866586266658703, 55.94534832743419]}, Point{type=Point, bbox=null, coordinates=[-3.186773533332338, 55.945251909292736]}, Point{type=Point, bbox=null, coordinates=[-3.1868884399988056, 55.94515549115128]}, Point{type=Point, bbox=null, coordinates=[-3.187003346665273, 55.94505907300983]}, Point{type=Point, bbox=null, coordinates=[-3.187118253331741, 55.94496265486838]}, Point{type=Point, bbox=null, coordinates=[-3.1872331599982084, 55.944866236726924]}, Point{type=Point, bbox=null, coordinates=[-3.187348066664676, 55.94476981858547]}, Point{type=Point, bbox=null, coordinates=[-3.1874629733311437, 55.94467340044402]}, Point{type=Point, bbox=null, coordinates=[-3.1875778799976113, 55.944576982302564]}, Point{type=Point, bbox=null, coordinates=[-3.187692786664079, 55.94448056416111]}, Point{type=Point, bbox=null, coordinates=[-3.1878076933305466, 55.94438414601966]}, Point{type=Point, bbox=null, coordinates=[-3.187922599997014, 55.944287727878205]}, Point{type=Point, bbox=null, coordinates=[-3.188037506663482, 55.94419130973675]}, Point{type=Point, bbox=null, coordinates=[-3.1881524133299495, 55.9440948915953]}, Point{type=Point, bbox=null, coordinates=[-3.188267319996417, 55.943998473453846]}, Point{type=Point, bbox=null, coordinates=[-3.1883972238069846, 55.94392347345384]}, Point{type=Point, bbox=null, coordinates=[-3.188527127617552, 55.94384847345384]}, Point{type=Point, bbox=null, coordinates=[-3.1886570314281197, 55.94377347345384]}, Point{type=Point, bbox=null, coordinates=[-3.1887869352386873, 55.943698473453836]}, Point{type=Point, bbox=null, coordinates=[-3.188916839049255, 55.94362347345383]}, Point{type=Point, bbox=null, coordinates=[-3.1890467428598224, 55.94354847345383]}, Point{type=Point, bbox=null, coordinates=[-3.18917664667039, 55.94347347345383]}, Point{type=Point, bbox=null, coordinates=[-3.1893065504809575, 55.943398473453826]}, Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18948, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18963, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1897800000000003, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1899300000000004, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1900800000000005, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.190227721162952, 55.94341504722665]}, Point{type=Point, bbox=null, coordinates=[-3.190375442325904, 55.9434410944533]}, Point{type=Point, bbox=null, coordinates=[-3.1905231634888556, 55.943467141679946]}, Point{type=Point, bbox=null, coordinates=[-3.1906708846518073, 55.943493188906594]}, Point{type=Point, bbox=null, coordinates=[-3.190818605814759, 55.94351923613324]}, Point{type=Point, bbox=null, coordinates=[-3.1909663269777107, 55.94354528335989]}, Point{type=Point, bbox=null, coordinates=[-3.1911140481406624, 55.943571330586536]}, Point{type=Point, bbox=null, coordinates=[-3.191261769303614, 55.943597377813184]}, Point{type=Point, bbox=null, coordinates=[-3.1914094904665657, 55.94362342503983]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1915679527733496, 55.94380572116295]}, Point{type=Point, bbox=null, coordinates=[-3.1915419055466994, 55.943953442325906]}, Point{type=Point, bbox=null, coordinates=[-3.191515858320049, 55.94410116348886]}, Point{type=Point, bbox=null, coordinates=[-3.191489811093399, 55.94424888465181]}, Point{type=Point, bbox=null, coordinates=[-3.1914637638667487, 55.94439660581477]}, Point{type=Point, bbox=null, coordinates=[-3.1914377166400985, 55.94454432697772]}, Point{type=Point, bbox=null, coordinates=[-3.1914116694134482, 55.94469204814067]}, Point{type=Point, bbox=null, coordinates=[-3.191360366391949, 55.94483300203379]}, Point{type=Point, bbox=null, coordinates=[-3.19130906337045, 55.944973955926905]}, Point{type=Point, bbox=null, coordinates=[-3.191257760348951, 55.94511490982002]}, Point{type=Point, bbox=null, coordinates=[-3.191206457327452, 55.945255863713136]}, Point{type=Point, bbox=null, coordinates=[-3.191155154305953, 55.94539681760625]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}]}, properties={}}]}"));


    }
    @Test
    public void testSimplePathFinder(){
        PathfindingTools pfTools=new PathfindingTools();
        LongLat b= new LongLat(5,5);
        LongLat a= new LongLat(5.01,5.01);
        PathfindingTools.LongLatWithMoves result=pfTools.simplePathfinderWithHover(b,a);

        //checks if first ll is equal to starting point and last ll is equal to destination
        assertTrue(result.lineCollection.get(0).equals(b) &&result.lineCollection.get(result.lineCollection.size()-1).closeTo(a));
    }

    @Test
    public void testSimplePathFindeWithHover(){
        PathfindingTools pfTools=new PathfindingTools();
        LongLat b= new LongLat(5,5);
        LongLat a= new LongLat(5.01,5.01);
        PathfindingTools.LongLatWithMoves result=pfTools.simplePathfinderWithHover(b,a);

        //checks if first ll is equal to starting point and last ll is equal to destination and that the last point is duplicated indicating a hover move
        assertTrue(result.lineCollection.get(0).equals(b) &&result.lineCollection.get(result.lineCollection.size()-1).closeTo(a)
                &&result.lineCollection.get(result.lineCollection.size()-2).closeTo(a));
    }
    @Test
    public void testRouteAvoidConfinementZome(){
        Orders orders = new Orders("localhost", "9898","1527");
        Pathfinding pathfinding2 = new Pathfinding("localhost", "9898","1527");
        PathfindingTools pfTools=new PathfindingTools();
        Double a[][]=pathfinding2.Edges("localhost", "9898");
        LongLat b= new LongLat(-3.18933,55.943389);
        LongLat c= new LongLat(-3.191065,55.945626);
        LongLat d= new LongLat(-3.191594,55.943658);
        LongLat e= new LongLat(-3.186199,55.945734);
        List<LongLat> destinations = new ArrayList<>();
        destinations.add(b);
        destinations.add(c);
        //destinations.add(e);
        destinations.add(b);
        //destinations.add(d);
        destinations.add(c);
        FeatureCollection route2AsFC =pfTools.longLatListToFC(pathfinding2.route(a,destinations).lineCollection);
        assertTrue(route2AsFC.toString().equals("FeatureCollection{type=FeatureCollection, bbox=null, features=[Feature{type=Feature, bbox=null, id=null, geometry=LineString{type=LineString, bbox=null, coordinates=[Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18948, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18963, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1897800000000003, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1899300000000004, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1900800000000005, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.190227721162952, 55.94341504722665]}, Point{type=Point, bbox=null, coordinates=[-3.190375442325904, 55.9434410944533]}, Point{type=Point, bbox=null, coordinates=[-3.1905231634888556, 55.943467141679946]}, Point{type=Point, bbox=null, coordinates=[-3.1906708846518073, 55.943493188906594]}, Point{type=Point, bbox=null, coordinates=[-3.190818605814759, 55.94351923613324]}, Point{type=Point, bbox=null, coordinates=[-3.1909663269777107, 55.94354528335989]}, Point{type=Point, bbox=null, coordinates=[-3.1911140481406624, 55.943571330586536]}, Point{type=Point, bbox=null, coordinates=[-3.191261769303614, 55.943597377813184]}, Point{type=Point, bbox=null, coordinates=[-3.1914094904665657, 55.94362342503983]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1915679527733496, 55.94380572116295]}, Point{type=Point, bbox=null, coordinates=[-3.1915419055466994, 55.943953442325906]}, Point{type=Point, bbox=null, coordinates=[-3.191515858320049, 55.94410116348886]}, Point{type=Point, bbox=null, coordinates=[-3.191489811093399, 55.94424888465181]}, Point{type=Point, bbox=null, coordinates=[-3.1914637638667487, 55.94439660581477]}, Point{type=Point, bbox=null, coordinates=[-3.1914377166400985, 55.94454432697772]}, Point{type=Point, bbox=null, coordinates=[-3.1914116694134482, 55.94469204814067]}, Point{type=Point, bbox=null, coordinates=[-3.191360366391949, 55.94483300203379]}, Point{type=Point, bbox=null, coordinates=[-3.19130906337045, 55.944973955926905]}, Point{type=Point, bbox=null, coordinates=[-3.191257760348951, 55.94511490982002]}, Point{type=Point, bbox=null, coordinates=[-3.191206457327452, 55.945255863713136]}, Point{type=Point, bbox=null, coordinates=[-3.191155154305953, 55.94539681760625]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}, Point{type=Point, bbox=null, coordinates=[-3.191065, 55.945626]}, Point{type=Point, bbox=null, coordinates=[-3.191065, 55.945626]}, Point{type=Point, bbox=null, coordinates=[-3.1910910472266503, 55.945478278837044]}, Point{type=Point, bbox=null, coordinates=[-3.1911170944533005, 55.94533055767409]}, Point{type=Point, bbox=null, coordinates=[-3.1911431416799507, 55.94518283651114]}, Point{type=Point, bbox=null, coordinates=[-3.191169188906601, 55.94503511534818]}, Point{type=Point, bbox=null, coordinates=[-3.191195236133251, 55.94488739418523]}, Point{type=Point, bbox=null, coordinates=[-3.1912212833599014, 55.944739673022276]}, Point{type=Point, bbox=null, coordinates=[-3.1912473305865516, 55.94459195185932]}, Point{type=Point, bbox=null, coordinates=[-3.1912986336080507, 55.94445099796621]}, Point{type=Point, bbox=null, coordinates=[-3.1913499366295497, 55.94431004407309]}, Point{type=Point, bbox=null, coordinates=[-3.1914012396510487, 55.944169090179976]}, Point{type=Point, bbox=null, coordinates=[-3.1914525426725477, 55.94402813628686]}, Point{type=Point, bbox=null, coordinates=[-3.1915038456940468, 55.943887182393745]}, Point{type=Point, bbox=null, coordinates=[-3.191555148715546, 55.94374622850063]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1914439999999997, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1912939999999996, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1911439999999995, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1909939999999994, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1908439999999993, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1906962788370477, 55.94363195277335]}, Point{type=Point, bbox=null, coordinates=[-3.190548557674096, 55.943605905546704]}, Point{type=Point, bbox=null, coordinates=[-3.1904008365111443, 55.94357985832006]}, Point{type=Point, bbox=null, coordinates=[-3.1902531153481926, 55.94355381109341]}, Point{type=Point, bbox=null, coordinates=[-3.190105394185241, 55.94352776386676]}, Point{type=Point, bbox=null, coordinates=[-3.189957673022289, 55.943501716640114]}, Point{type=Point, bbox=null, coordinates=[-3.1898099518593375, 55.943475669413466]}, Point{type=Point, bbox=null, coordinates=[-3.1896622306963858, 55.94344962218682]}, Point{type=Point, bbox=null, coordinates=[-3.189514509533434, 55.94342357496017]}, Point{type=Point, bbox=null, coordinates=[-3.1893667883704824, 55.943397527733524]}, Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18933, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18948, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.18963, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1897800000000003, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1899300000000004, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.1900800000000005, 55.943389]}, Point{type=Point, bbox=null, coordinates=[-3.190227721162952, 55.94341504722665]}, Point{type=Point, bbox=null, coordinates=[-3.190375442325904, 55.9434410944533]}, Point{type=Point, bbox=null, coordinates=[-3.1905231634888556, 55.943467141679946]}, Point{type=Point, bbox=null, coordinates=[-3.1906708846518073, 55.943493188906594]}, Point{type=Point, bbox=null, coordinates=[-3.190818605814759, 55.94351923613324]}, Point{type=Point, bbox=null, coordinates=[-3.1909663269777107, 55.94354528335989]}, Point{type=Point, bbox=null, coordinates=[-3.1911140481406624, 55.943571330586536]}, Point{type=Point, bbox=null, coordinates=[-3.191261769303614, 55.943597377813184]}, Point{type=Point, bbox=null, coordinates=[-3.1914094904665657, 55.94362342503983]}, Point{type=Point, bbox=null, coordinates=[-3.1915572116295174, 55.94364947226648]}, Point{type=Point, bbox=null, coordinates=[-3.191594, 55.943658]}, Point{type=Point, bbox=null, coordinates=[-3.1915679527733496, 55.94380572116295]}, Point{type=Point, bbox=null, coordinates=[-3.1915419055466994, 55.943953442325906]}, Point{type=Point, bbox=null, coordinates=[-3.191515858320049, 55.94410116348886]}, Point{type=Point, bbox=null, coordinates=[-3.191489811093399, 55.94424888465181]}, Point{type=Point, bbox=null, coordinates=[-3.1914637638667487, 55.94439660581477]}, Point{type=Point, bbox=null, coordinates=[-3.1914377166400985, 55.94454432697772]}, Point{type=Point, bbox=null, coordinates=[-3.1914116694134482, 55.94469204814067]}, Point{type=Point, bbox=null, coordinates=[-3.191360366391949, 55.94483300203379]}, Point{type=Point, bbox=null, coordinates=[-3.19130906337045, 55.944973955926905]}, Point{type=Point, bbox=null, coordinates=[-3.191257760348951, 55.94511490982002]}, Point{type=Point, bbox=null, coordinates=[-3.191206457327452, 55.945255863713136]}, Point{type=Point, bbox=null, coordinates=[-3.191155154305953, 55.94539681760625]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}, Point{type=Point, bbox=null, coordinates=[-3.191103851284454, 55.94553777149937]}]}, properties={}}]}"));



    }
    @Test
    public void testGetRouteForOrder(){
        Orders orders = new Orders("localhost", "9898","1527");
        orders.getOrdersForDay("2022-04-11");
        Pathfinding pathfinding2 = new Pathfinding("localhost", "9898","1527");
        PathfindingTools pfTools=new PathfindingTools();
        Double a[][]=pathfinding2.Edges("localhost", "9898");
        assertTrue(a.length==14);
        assertTrue(a[0].length==14);
    }
    @Test
    public void testPopulateOrdersList(){
        Orders orders = new Orders("localhost", "9898","1527");
        orders.populateOrdersList("2022-04-11");
        System.out.println(orders.ordersList.get(0).orderNumber);
        System.out.println(orders.ordersList.get(1).orderItems.get(0));
        Assert.assertEquals(orders.ordersList.get(0).orderNumber,"406d9b98");
        Assert.assertEquals(orders.ordersList.get(1).orderItems.get(0),"Ploughman's sandwich (Large)");
    }
    @Test
    public void  testGetRouteFor2023_12_27(){
        int moves=0;
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        List<LongLat> entireRouteOfDay=new ArrayList<>();
        orders.populateOrdersList("2023-12-27");
        double revenueSum=0;

        //Create table
        entireRouteOfDay.add(appletonTower);
        Pathfinding pf2 = new Pathfinding("localhost", "9898","1527");
            Double a[][]=pf2.Edges("localhost", "9898");
        orders.createOrderDatabase();
        pf2.createFlightPathDatabase();
        for (Iterator<Orders.OrderDetails> iter = orders.ordersList.iterator(); iter.hasNext();) {
            Orders.OrderDetails item = iter.next();

            //temps is used here to connect routes!
            List<LongLat> temps =new ArrayList<>();
            temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
            temps.addAll(item.orderLocationsLongLats);
            PathfindingTools.LongLatWithMoves currentSet=pf2.route(a,temps);

            List<LongLat> ATcheck =new ArrayList<>();
            ATcheck.add(appletonTower);
            ATcheck.add(currentSet.lineCollection.get(currentSet.lineCollection.size()-1));
            PathfindingTools.LongLatWithMoves DistanceToAt=pf2.route(a,ATcheck);
            if(moves+currentSet.moves+DistanceToAt.moves<1500)
            {
                //add hover for drop off ie add duplicate last point and increase moves by one
                //add order to table

                moves=moves+currentSet.moves;
                entireRouteOfDay.addAll(currentSet.lineCollection);
                revenueSum=revenueSum+item.orderprice;
                orders.addOrderToDatabase(item.orderNumber,item.deliverToW3W,(int) item.orderprice);
                //For each entry in currentSet.lineCollection add it to flightpath database alongside angle between the two using angleTo
                for(int i=0;i<currentSet.lineCollection.size()-1;i++)
                {
                    pf2.addToFlightPathDatabase(currentSet.lineCollection.get(i),currentSet.lineCollection.get(i+1),item.orderNumber);
                }
            }
            if(moves+currentSet.moves+DistanceToAt.moves>1500)
            {
             //   break;
            }
        }
        List<LongLat> temps =new ArrayList<>();
        temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
        temps.add(appletonTower);
        entireRouteOfDay.addAll((pf2.route(a,temps).lineCollection));

        FeatureCollection entireRouteAsFC=pft.longLatListToFC(entireRouteOfDay);
        System.out.println("moves: " +moves);
        System.out.println("Revenue for day : "+ revenueSum);
        System.out.println(entireRouteAsFC.toJson());
    }
    @Test
    public void  testGetRouteFor2022_01_14(){
        int moves=0;
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        List<LongLat> entireRouteOfDay=new ArrayList<>();
        orders.populateOrdersList("2022-01-14");
        double revenueSum=0;

        //Create table
        entireRouteOfDay.add(appletonTower);
        Pathfinding pf2 = new Pathfinding("localhost", "9898","1527");
        Double a[][]=pf2.Edges("localhost", "9898");
        orders.createOrderDatabase();
        pf2.createFlightPathDatabase();
        for (Iterator<Orders.OrderDetails> iter = orders.ordersList.iterator(); iter.hasNext();) {
            Orders.OrderDetails item = iter.next();

            //temps is used here to connect routes!
            List<LongLat> temps =new ArrayList<>();
            temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
            temps.addAll(item.orderLocationsLongLats);
            PathfindingTools.LongLatWithMoves currentSet=pf2.route(a,temps);

            List<LongLat> ATcheck =new ArrayList<>();
            ATcheck.add(appletonTower);
            ATcheck.add(currentSet.lineCollection.get(currentSet.lineCollection.size()-1));
            PathfindingTools.LongLatWithMoves DistanceToAt=pf2.route(a,ATcheck);
            if(moves+currentSet.moves+DistanceToAt.moves<1500)
            {
                //add hover for drop off ie add duplicate last point and increase moves by one
                //add order to table

                moves=moves+currentSet.moves;
                entireRouteOfDay.addAll(currentSet.lineCollection);
                revenueSum=revenueSum+item.orderprice;
                orders.addOrderToDatabase(item.orderNumber,item.deliverToW3W,(int) item.orderprice);
                //For each entry in currentSet.lineCollection add it to flightpath database alongside angle between the two using angleTo
                for(int i=0;i<currentSet.lineCollection.size()-1;i++)
                {
                    pf2.addToFlightPathDatabase(currentSet.lineCollection.get(i),currentSet.lineCollection.get(i+1),item.orderNumber);
                }
            }
            if(moves+currentSet.moves+DistanceToAt.moves>1500)
            {
                //   break;
            }
        }
        List<LongLat> temps =new ArrayList<>();
        temps.add(entireRouteOfDay.get(entireRouteOfDay.size()-1));
        temps.add(appletonTower);
        entireRouteOfDay.addAll((pf2.route(a,temps).lineCollection));

        FeatureCollection entireRouteAsFC=pft.longLatListToFC(entireRouteOfDay);
        System.out.println("moves: " +moves);
        System.out.println("Revenue for day : "+ revenueSum);
        System.out.println(entireRouteAsFC.toJson());
    }
    @Test
    public void testLineLineIntersectTrueA(){
        Orders orders = new Orders("localhost", "9898","1527");
        String request = "http://localhost:9898/buildings/no-fly-zones.geojson";
        String response = HTTPRequests.doGETRequest(request);
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response);
        LongLat inflyZoneA= new LongLat(-3.190128207206726,55.94497941394988);
        PathfindingTools pft = new PathfindingTools();
        assertTrue(pft.lineLineIntersect(noFlyZone,inflyZoneA));
    }
    @Test
    public void testLineLineIntersectTrueB(){
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        String request = "http://"+host+":" + port + "/buildings/no-fly-zones.geojson";
        String response = HTTPRequests.doGETRequest(request);
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response);
        LongLat inflyZoneB=new LongLat(
                -3.1900236010551453,
        55.94501396009331
          );
        assertTrue(pft.lineLineIntersect(noFlyZone,inflyZoneB));
    }
    @Test
    public void testLineLineIntersectTrueC(){
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        String request = "http://"+host+":" + port + "/buildings/no-fly-zones.geojson";
        String response = HTTPRequests.doGETRequest(request);
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response);
        LongLat inflyZoneC=new LongLat(
                -3.189935088157654,
                55.94484122906794
        );
        assertTrue(pft.lineLineIntersect(noFlyZone,inflyZoneC));
    }
    @Test
    public void testLineLineIntersectFalse(){
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        String request = "http://"+host+":" + port + "/buildings/no-fly-zones.geojson";
        String response = HTTPRequests.doGETRequest(request);
        FeatureCollection noFlyZone = FeatureCollection.fromJson(response);
        LongLat notinflyZone=new LongLat(
                -50,
                55.94484122906794
        );
        assertFalse(pft.lineLineIntersect(noFlyZone,notinflyZone));
    }
    @Test
    public void testlongLatListToFC(){

        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        PathfindingTools pft = new PathfindingTools();
        LongLat inflyZoneA=new LongLat(
                -3.1900236010551453,
                55.94501396009331
        );
        LongLat inflyZoneB=new LongLat(
                -3.189935088157654,
                55.94484122906794
        );
        LongLat inflyZoneC= new LongLat(-3.190128207206726,55.94497941394988);
        List<LongLat> LLlist = new ArrayList<>();
        LLlist.add(inflyZoneA);
        LLlist.add(inflyZoneB);
        LLlist.add(inflyZoneC);
        FeatureCollection result = pft.longLatListToFC(LLlist);
        System.out.println(result);
    }
    @Test
    public  void testTotalMonetaryValueForDay(){
        String host= "localhost";
        String port = "9898";
        Orders orders = new Orders(host,port,"1527");
        System.out.println(orders.totalMonetaryValueForDay("2023-12-27"));
        assertTrue(orders.totalMonetaryValueForDay("2023-12-27")==21385.0);

    }
}