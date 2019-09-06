package com.zte.bigdata.udf.coordinate2Grid;

public class GridOffSetData
{
    public int earthID;
    public int xOffSet;
    public int yOffSet;

    public GridOffSetData() {
    }

    public GridOffSetData(int earthID, int xOffSet, int yOffSet) {
        this.earthID = earthID;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
    }

    @Override
    public int hashCode()
    {
        // TODO Auto-generated method stub
        return (earthID + xOffSet + yOffSet + "").hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        // TODO Auto-generated method stub
        GridOffSetData target = (GridOffSetData) obj;
        return this.earthID == target.earthID && this.xOffSet == target.xOffSet && this.yOffSet == target.yOffSet;
    }

    @Override
    public String toString() {
        return earthID + "*" + xOffSet + "*" + yOffSet;
    }
}
