package uk.ac.ed.inf;

public class LongLat    {

    double latitude;
    double longitude;
    /**
     * LongLat - Sets the latitude and longitude for this instance
     * @param longitude - double, the given longitude
     * @param latitude - double, the given latitude
     */
    public LongLat(double longitude, double latitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }
    /**
     * isConfined - returns true if the point is within the drone confinement area and false if it is not
     * @returns true/false - Boolean, true or false return depending on calculations
     */
    Boolean isConfined()
    {
        Boolean confined;
        if(this.longitude>-3.192473 &&this.longitude<-3.184319)
        {
            if(this.latitude>55.942617 &&this.latitude<55.946233)
            {

                return true;
            }
        }
        return false;}
    /**
     * distanceTo - takes a LongLat object as a parameter and returns the Pythagorean distance between the two points as a value of type double
     * @param a - Longlat - an object which contains a longitude and latitude
     * @returns hypotenuse - double, the distance in degrees between two latitude and longitude points
     */
    double distanceTo(LongLat a)
    {
        double ac = Math.abs(a.latitude - this.latitude);
        double cb = Math.abs(a.longitude - this.longitude);
        return Math.hypot(ac, cb);
    }
    /**
     * closeTo - takes a LongLat object as a parameter and returns true if the distance between `1 and `2 is strictly less than the distance tolerance of 0.00015 degrees
     * @param a - Longlat - an object which contains a longitude and latitude
     * @returns true/false/null - Boolean, true if distance is consider close and false if the distance is considered not close and null if a is an empty object
     */
    Boolean closeTo(LongLat a)
    {
        if (distanceTo(a)<=0.00015)
        {return  true;}
        else if(distanceTo(a)>0.00015)
        {return  false;}
        else {
            System.err.println("LongLat object error");
            return  null;}

    }
    /**
     * nextPosition - makes a move in the direction of the angle a, following the definition of a move given on page 3 of documentation
     * @param angle - int -the angle which the drone will move at
     * @returns new LongLat - LongLat, returns the new postion of the drone as a LongLat object
     */
    LongLat nextPosition(double angle){
        if(angle==-999)
        {
            //hover
            return new LongLat(this.longitude,latitude);
        }

        if(angle%10==0 && angle>=0 && angle<=350)
        {

            double latChange=Math.sin(Math.toRadians(angle))*0.00015;
            double longChange=Math.cos(Math.toRadians(angle))*0.00015;
            double newLong=this.longitude+longChange;
            double newLat=this.latitude+latChange;

            LongLat output=new LongLat(newLong,newLat);
            return output;


        }
        return null;
    }
    /**
     * angleTo - returns the angle from this LongLat to a given Longlat
     * @param b - LongLat -the Longlat we are calculating the angle to
     * @returns roundedAngle - Double, angle to given LongLat from this LongLat rounded to the nearest 10 degrees
     */
    double angleTo(LongLat b)
    {

        double y = b.latitude-this.latitude;
        double x = b.longitude-this.longitude;
        double angle =Math.atan2(y,x);
        double angleInDegrees =angle*180/Math.PI;
        double roundedAngle =Math.ceil(angleInDegrees / 10) * 10;
        double roundedAngleInRadians=roundedAngle*Math.PI/180;
        while(roundedAngle<0)
        {
            roundedAngle=360+roundedAngle;
        }

        return  roundedAngle;
    }
}
