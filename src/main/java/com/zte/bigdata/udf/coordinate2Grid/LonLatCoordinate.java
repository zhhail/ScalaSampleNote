package com.zte.bigdata.udf.coordinate2Grid;


import java.util.List;

public class LonLatCoordinate
{
    public double lonCoord;
    public double latCoord;

    public static final double MIN_LON = -180.0;
    public static final double MIN_LAT = -90.0;
    public static final double MAX_LON = 180.0;
    public static final double MAX_LAT = 90.0;

    public LonLatCoordinate()
    {
    }

    public LonLatCoordinate(double lon, double lat)
    {
        this.lonCoord = lon;
        this.latCoord = lat;
    }

    public boolean isRightLonLat(boolean isWriteLog)
    {
        if (MIN_LON < lonCoord && lonCoord < MAX_LON && MIN_LAT < latCoord && latCoord < MAX_LAT)
        {
            return true;
        }

        return false;
    }

    public boolean isRightLonLat()
    {
        if (MIN_LON < lonCoord && lonCoord < MAX_LON && MIN_LAT < latCoord && latCoord < MAX_LAT)
        {
            return true;
        }

        return false;
    }

    public static boolean isRightLonLat(String lonStr, String latStr)
    {
        double lon = 0.0;
        double lat = 0.0;
        try
        {
            lon = Double.parseDouble(lonStr);
            lat = Double.parseDouble(latStr);
        }
        catch (NumberFormatException nfe)
        {

            // log.error(LogOutPut.getStackMsg(nfe));
            return false;
        }

        if (new LonLatCoordinate(lon, lat).isRightLonLat())
        {
            return true;
        }
        // log.error("lon: " + lonStr + "lat: " + latStr +
        // " are not right lon and lat, please check");
        return false;
    }

    public static boolean isRightLonLat(List<LonLatCoordinate> lonlats)
    {
        for (LonLatCoordinate lonlat : lonlats)
        {
            if (!lonlat.isRightLonLat())
            {
                return false;
            }
        }
        return true;
    }
}