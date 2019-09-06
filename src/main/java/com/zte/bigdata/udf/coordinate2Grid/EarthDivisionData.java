package com.zte.bigdata.udf.coordinate2Grid;

public class EarthDivisionData {
    public int earthID;
    public double centerLon;
    public double centerLat;
    public double centerX;
    public double centerY;
    public double leftLost;
    public double realLeftLon;
    public double realRightLon;
    public double realTopLat;
    public double realBottomLat;
    public double anglePer100mAlongLon;
    public double anglePer100mAlongLat;

    public EarthDivisionData(int earthID, double centerLon, double centerLat, double centerX, double centerY,
                             double leftLost, double realLeftLon, double realRightLon, double realTopLat,
                             double realBottomLat, double anglePer100mAlongLon, double anglePer100mAlongLat) {
        this.earthID = earthID;
        this.centerLon = centerLon;
        this.centerLat = centerLat;
        this.centerX = centerX;
        this.centerY = centerY;
        this.leftLost = leftLost;
        this.realLeftLon = realLeftLon;
        this.realRightLon = realRightLon;
        this.realTopLat = realTopLat;
        this.realBottomLat = realBottomLat;
        this.anglePer100mAlongLon = anglePer100mAlongLon;
        this.anglePer100mAlongLat = anglePer100mAlongLat;
    }

    public EarthDivisionData() {
    }
}
