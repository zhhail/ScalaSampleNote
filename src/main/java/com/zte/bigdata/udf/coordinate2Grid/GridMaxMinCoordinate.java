package com.zte.bigdata.udf.coordinate2Grid;

public class GridMaxMinCoordinate
{
    public double maxLonCoordinate;
    public double minLonCoordinate;
    public double maxLatCoordinate;
    public double minLatCoordinate;

    public void increase(double incre)
    {
        this.maxLatCoordinate += incre;
        this.maxLonCoordinate += incre;
        this.minLatCoordinate -= incre;
        this.minLonCoordinate -= incre;
    }
}
