package com.zte.bigdata.udf.coordinate2Grid;

import java.util.ArrayList;
import java.util.HashMap;

public class GridCoordinateGetter {

    private static HashMap<Integer, EarthDivisionData> gridDivisionMap = null;

    private static HashMap<Integer, EarthDivisionData> getGridDivisionMap() {
        if (gridDivisionMap != null) {
            return gridDivisionMap;
        }

        gridDivisionMap = initGridDivisionMap();
        return gridDivisionMap;
    }


    public static GridOffSetData getGridId(double longitude, double latitude, double gSize) {
        return getEarthIDXOffSetYOffSet(longitude, latitude, gSize, getGridDivisionMap());
    }
    public enum ellipsoidType
    {
        etKrasovsky40E,
        etWGS84E;
    }
    public enum projectionType
    {
        ptAMGE, ptCassiniE, ptNewZealandE, ptUTME, ptUKE, ptLambertIIE, ptSwedenE, ptBonneE, ptStereographicE, ptGaussKrugerE, ptVGISSAE, ptEOVE, ptLambertConformalE, ptMalayanRectifiedSkewE;
    }

        // 计算earthID,xoffset和yoffset
    public static GridOffSetData getEarthIDXOffSetYOffSet(double longitude, double latitude, double gSize,
                                                          HashMap<Integer, EarthDivisionData> earthData) {

        GridOffSetData offSetData = new GridOffSetData();
        offSetData.earthID = 0;
        offSetData.xOffSet = 0;
        offSetData.yOffSet = 0;

        // 当经纬度不符合要求时直接返回
        if (longitude < -180.0 ||longitude>180|| latitude < -90.0 || latitude>90) {
            return offSetData;
        }

        // 计算earthID,其中earthID = lonZoneNum + latZoneNum
        int lonZoneNum = ((int) ((longitude + 180) / 6 + 1)) * 100;
        int latZoneNum = (int) ((latitude + 90) / 3) + 1;


        // 如果经度小于当前earthID的realLeftLon, 应该改算作左边的earthID
        if (longitude < earthData.get((lonZoneNum + latZoneNum)).realLeftLon) {
            lonZoneNum = (lonZoneNum == 100) ? 6000 : (lonZoneNum - 100);
        }

        offSetData.earthID = lonZoneNum + latZoneNum;

        double meridian = earthData.get(offSetData.earthID).centerLon;

        // 计算XOffSet
        double gaussX = lonLat2X(longitude, latitude, meridian);
        double disCurPointtocenterX = gaussX - earthData.get(offSetData.earthID).centerX;

        // gSize : 10*10m,20*20m,100*100m
        offSetData.xOffSet = (int) (disCurPointtocenterX / gSize);
        offSetData.xOffSet = (disCurPointtocenterX >= 0) ? offSetData.xOffSet + 1 : offSetData.xOffSet - 1; // 现在的写法有判断，效率可能比较低

        // 计算YOffSet
        double gaussY = lonLat2Y(longitude, latitude, meridian);
        double disCurPointtocenterY = gaussY - earthData.get(offSetData.earthID).centerY;

        offSetData.yOffSet = (int) (disCurPointtocenterY / gSize);
        offSetData.yOffSet = (disCurPointtocenterY >= 0) ? offSetData.yOffSet + 1 : offSetData.yOffSet - 1; // 现在的写法有判断，效率可能比较低

        return offSetData;
    }

    public static double getBfWhenProjectToLonLat(double longAxis, double shortAxis, double X, double Y)
    {
        double Bf;
        double e;
        e = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (longAxis * longAxis));

        double e1_2, e1_4, e1_6, e1_8, e1_10;
        e1_2 = e * e;
        e1_4 = e1_2 * e1_2;
        e1_6 = e1_4 * e1_2;
        e1_8 = e1_4 * e1_4;
        e1_10 = e1_2 * e1_8;

        double aa, bb, cc, dd;
        aa = 1.0 + 3.0 * e1_2 / 4.0 + 45.0 * e1_4 / 64.0 + 175.0 * e1_6 / 256.0 + 11025.0 * e1_8 / 16384.0 + 43659.0
                * e1_10 / 65536.0;
        bb = 3.0 * e1_2 / 4.0 + 15.0 * e1_4 / 16.0 + 525.0 * e1_6 / 512.0 + 2205.0 * e1_8 / 2048.0 + 72765.0 * e1_10
                / 65536.0;
        cc = 15.0 * e1_4 / 64.0 + 105.0 * e1_6 / 256.0 + 2205.0 * e1_8 / 4096.0 + 10395.0 * e1_10 / 16384.0;
        dd = 35.0 * e1_6 / 512.0 + 315.0 * e1_8 / 2048.0 + 31185.0 * e1_10 / 13072.0;

        double a1, a2, a3, a4;
        a1 = aa * longAxis * (1 - e * e);
        a2 = -bb * longAxis * (1 - e * e) / 2.0;
        a3 = cc * longAxis * (1 - e * e) / 4.0;
        a4 = -dd * longAxis * (1 - e * e) / 6.0;

        double[] b11 = new double[5];
        double[] r11 = new double[5];
        double[] d11 = new double[5];

        b11[0] = -a2 / a1;
        r11[0] = -a3 / a1;
        d11[0] = -a4 / a1;

        for (int i = 0; i < 4; i++)
        {
            b11[i + 1] = b11[0] + b11[0] * r11[i] - 2.0 * r11[0] * b11[i] - 3.0 * b11[0] * b11[i] * b11[i] / 2.0;
            r11[i + 1] = r11[0] + b11[0] * b11[i];
            d11[i + 1] = d11[0] + b11[0] * r11[i] + 2.0 * r11[0] * b11[i] + b11[0] * b11[i] * b11[i] / 2.0;
        }
        double K1, K2, K3;
        K1 = 2.0 * b11[4] + 4.0 * r11[4] + 6.0 * d11[4];
        K2 = -8.0 * r11[4] - 32.0 * d11[4];
        K3 = 32.0 * d11[4];
        Bf = Y / a1;
        Bf = Bf
                + Math.cos(Bf)
                * (Math.sin(Bf) * K1 + Math.sin(Bf) * Math.sin(Bf) * Math.sin(Bf) * K2 + Math.sin(Bf) * Math.sin(Bf)
                * Math.sin(Bf) * Math.sin(Bf) * Math.sin(Bf) * K3);

        return Bf;
    }

    public static LonLatCoordinate projectToLonLat(double X, double Y, double meridian)
    {
        LonLatCoordinate lonLat = new LonLatCoordinate();
        double lat;
        double lon;

        ellipsoidType ellType = ellipsoidType.etWGS84E;
        projectionType proType = projectionType.ptGaussKrugerE;

        double Bf;
        double longAxis, shortAxis;

        if (ellType == ellipsoidType.etWGS84E)
        {
            longAxis = 6378137.0;
            shortAxis = 6356752.314245;
        }
        else if (ellType == ellipsoidType.etKrasovsky40E)
        {
            longAxis = 6378245.0;
            shortAxis = 6356863.01877305;
        }
        else
        {
            longAxis = 6378245.0;
            shortAxis = 6356863.01877305;
        }

        double e1, e2;
        e1 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (longAxis * longAxis));
        e2 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (shortAxis * shortAxis));

        if (proType == projectionType.ptUTME)
        {
            X = X - 500000.0;
            X = X / 0.9996 + 500000.0;
            Y = Y / 0.9996;
        }

        Bf = getBfWhenProjectToLonLat(longAxis, shortAxis, X, Y);

        X = X - 500000.0;
        lat = Bf
                - Math.tan(Bf)
                * Math.pow(X, 2.0)
                / (2.0 * longAxis * (1 - e1 * e1) / Math.pow(Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 3.0)
                * longAxis / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)))
                + Math.tan(Bf)
                * (5.0 + 3.0 * Math.pow(Math.tan(Bf), 2.0) + Math.pow(e2 * Math.cos(Bf), 2.0) - 9.0 * Math.pow(
                Math.tan(Bf) * e2 * Math.cos(Bf), 2.0))
                * Math.pow(X, 4.0)
                / (24.0 * 6378137.0 * (1 - e1 * e1)
                / Math.pow(Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 3.0) * Math.pow(longAxis
                / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 3.0))
                - Math.tan(Bf)
                * (61.0 + 90.0 * Math.tan(Bf) * Math.tan(Bf) + 45.0 * Math.pow(Math.tan(Bf), 4.0))
                * Math.pow(X, 6)
                / (720.0 * longAxis * (1 - e1 * e1)
                / Math.pow(Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 3.0) * Math.pow(longAxis
                / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 5.0));

        lon = X
                / (longAxis / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)))
                / Math.cos(Bf)
                - (1.0 + 2.0 * Math.pow(Math.tan(Bf), 2.0) + Math.pow(e2 * Math.cos(Bf), 2.0))
                * Math.pow(X, 3.0)
                / (6.0 * Math.pow(longAxis / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 3.0) * Math.cos(Bf))
                + (5.0 + 28.0 * Math.pow(Math.tan(Bf), 2.0) + 24 * Math.pow(Math.tan(Bf), 4) + 6.0
                * Math.pow(e2 * Math.cos(Bf), 2.0) + 8.0 * Math.pow(Math.tan(Bf) * e2 * Math.cos(Bf), 2.0))
                * Math.pow(X, 5) / 120.0
                / Math.pow(longAxis / Math.sqrt(1 - e1 * e1 * Math.sin(Bf) * Math.sin(Bf)), 5.0) / Math.cos(Bf);

        lat = lat * 180.0 / Math.PI;
        lon = lon * 180.0 / Math.PI + meridian;

        lonLat.latCoord = lat;
        lonLat.lonCoord = lon;

        return lonLat;
    }
    public static GridCenterCoordinate getGridCenterCoord(int earthid,int x,int y,int gridSize)
    {
        GridOffSetData grid = new GridOffSetData(earthid,x,y);
        GridMaxMinCoordinate coordinate =  GridCoordinateGetter.getGridCoordinate(grid,gridSize,getGridDivisionMap());
        return new GridCenterCoordinate((coordinate.maxLonCoordinate+coordinate.minLonCoordinate)/2, (coordinate.maxLatCoordinate+coordinate.minLatCoordinate)/2);
    }
    // 计算栅格的最大最小经纬度
    public static GridMaxMinCoordinate getGridCoordinate(GridOffSetData offSetData, double gSize,
                                                         HashMap<Integer, EarthDivisionData> divisionMap)
    {
        GridMaxMinCoordinate maxMinCoord = new GridMaxMinCoordinate();

        if (!divisionMap.containsKey(offSetData.earthID))
        {
            return maxMinCoord;
        }

        // double num = (divisionMap.get(offSetData.earthID).leftLost -
        // divisionMap.get(offSetData.earthID).centerLon)
        // / divisionMap.get(offSetData.earthID).anglePer100mAlongLon;
        // divisionMap.get(offSetData.earthID).minX = (int)num;
        // int minX = divisionMap.get(offSetData.earthID).minX ;

        double X = 0;
        double Y = 0;
        double changeGridSizeX = gSize;
        double changeGridSizeY = gSize;

        // if (minX < 0)
        // {
        // divisionMap.get(offSetData.earthID).minX--;
        // }
        // else
        // {
        // divisionMap.get(offSetData.earthID).minX++;
        // }

        if (offSetData.xOffSet > 0)
        {
            changeGridSizeX = -gSize;
        }
        if (offSetData.yOffSet > 0)
        {
            changeGridSizeY = -gSize;
        }

        X = offSetData.xOffSet * gSize + 500000;
        Y = offSetData.yOffSet * gSize + divisionMap.get(offSetData.earthID).centerY;

        LonLatCoordinate lonLat1;
        lonLat1 = projectToLonLat(X, Y, divisionMap.get(offSetData.earthID).centerLon);

        LonLatCoordinate lonLat2;
        lonLat2 = projectToLonLat(X + changeGridSizeX, Y + changeGridSizeY,
                divisionMap.get(offSetData.earthID).centerLon);

        maxMinCoord.maxLonCoordinate = Math.max(lonLat1.lonCoord, lonLat2.lonCoord);
        maxMinCoord.minLonCoordinate = Math.min(lonLat1.lonCoord, lonLat2.lonCoord);

        maxMinCoord.maxLatCoordinate = Math.max(lonLat1.latCoord, lonLat2.latCoord);
        maxMinCoord.minLatCoordinate = Math.min(lonLat1.latCoord, lonLat2.latCoord);

        return maxMinCoord;
    }

    // 6度带法计算X坐标，正算高斯投影，地球经纬度转换为高斯平面坐标
    public static double lonLat2X(double lon, double lat, double merid) {

        double gaussX;
        double lonSecond;
        double latSecond;
        double meridianSecond = 0;
        double e1;
        double e2;

        // WGS84
        double longAxis = 6378137.0;
        double shortAxis = 6356752.314245;

        // 中央经线，以及经纬度单位换算成秒
        meridianSecond = merid * 3600;
        lonSecond = lon * 3600;
        latSecond = lat * 3600;

        // 计算第一和第二偏心率 sqrt返回数字的平方根
        e1 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (longAxis * longAxis));

        e2 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (shortAxis * shortAxis));

        gaussX = longAxis / Math.pow((1 - e1 * e1 * Math.pow(Math.sin(latSecond / 206264.808), 2.0)), 0.5) / 206264.808
                * Math.cos(
                latSecond / 206264.808)
                * (lonSecond - meridianSecond)
                + longAxis / Math.pow((1 - e1 * e1 * Math.pow(Math.sin(latSecond / 206264.808), 2.0)), 0.5) / 6.0
                / Math.pow(206264.808, 3.0) * Math.pow(Math.cos(latSecond / 206264.808), 3.0)
                * (1.0 - Math.pow(Math.tan(latSecond / 206264.808), 2.0)
                + e2 * e2 * Math.pow(Math.cos(latSecond / 206264.808), 2.0))
                * Math.pow(lonSecond - meridianSecond, 3.0)
                + longAxis
                / Math.pow(
                (1 - e1 * e1
                        * Math.pow(Math.sin(latSecond / 206264.808),
                        2.0)),
                0.5)
                / 120.0 / Math.pow(206264.808, 5.0) * Math.pow(Math.cos(latSecond / 206264.808), 5.0)
                * (5.0 - 18 * Math.pow(Math.tan(latSecond / 206264.808), 2.0)
                + Math.pow(Math.tan(latSecond / 206264.808), 4.0)
                + 14.0 * e2 * e2 * Math.pow(Math.cos(latSecond / 206264.808), 2.0)
                - 58.0 * e2 * e2 * Math.pow(Math.cos(latSecond / 206264.808), 2.0)
                * Math.pow(Math.tan(latSecond / 206264.808), 2.0))
                * Math.pow(lonSecond - meridianSecond, 5.0)
                + 500000.0;

        return gaussX;
    }

    // 6度带法计算Y坐标，正算高斯投影，经纬度转换为高斯平面坐标
    public static double lonLat2Y(double lon, double lat, double merid) {
        double gaussY;
        double lonSecond;
        double latSecond;

        double meridianSecond = 0;
        double lengthFromLat0;
        double e1;
        double e2;

        // WGS84
        double longAxis = 6378137.0;
        double shortAxis = 6356752.314245;

        meridianSecond = merid * 3600.0;
        lonSecond = lon * 3600;
        latSecond = lat * 3600;

        // calc ziwu length
        lengthFromLat0 = getLengthFromLatO(longAxis, shortAxis, lat);

        // 计算第一和第二偏心率
        e1 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (longAxis * longAxis));
        e2 = Math.sqrt((longAxis * longAxis - shortAxis * shortAxis) / (shortAxis * shortAxis));
        gaussY = lengthFromLat0
                + longAxis / Math.pow((1 - e1 * e1 * Math.pow(Math.sin(latSecond / 206264.808), 2.0)), 0.5) / 2.0
                / Math.pow(206264.808,
                2.0)
                * Math.sin(latSecond / 206264.808) * Math.cos(latSecond / 206264.808)
                * Math.pow(lonSecond - meridianSecond,
                2.0)
                + longAxis
                / Math.pow((1 - e1 * e1 * Math.pow(Math.sin(latSecond / 206264.808), 2.0)),
                0.5)
                / 24.0 / Math.pow(206264.808, 4.0)
                * Math.sin(
                latSecond
                        / 206264.808)
                * Math.pow(
                Math.cos(
                        latSecond
                                / 206264.808),
                3.0)
                * (5.0 - Math.pow(Math.tan(latSecond / 206264.808), 2.0)
                + 9.0 * e2 * e2
                * Math.pow(Math.cos(latSecond / 206264.808), 2.0)
                + 4 * Math
                .pow(e2 * e2
                                * Math.pow(
                        Math.cos(
                                latSecond / 206264.808),
                        2.0),
                        2.0))
                * Math.pow(lonSecond - meridianSecond, 4.0)
                + longAxis
                / Math.pow((1 - e1 * e1 * Math.pow(Math.sin(latSecond / 206264.808), 2.0)),
                0.5)
                / 720.0 / Math.pow(206264.808, 6.0) * Math.sin(latSecond / 206264.808)
                * Math.pow(Math.cos(latSecond / 206264.808), 5.0)
                * (61.0 - 58.0 * Math.pow(Math.tan(latSecond / 206264.808), 2.0)
                + Math.pow(Math.tan(latSecond / 206264.808), 4.0))
                * Math.pow(lonSecond - meridianSecond, 6.0);

        return gaussY;
    }


    public static double getLengthFromLatO(double lAxis, double sAxis, double lat) {

        // 计算赤道子午曲率半径
        double d;
        d = (sAxis * sAxis) / lAxis;


        double e;
        e = Math.sqrt((lAxis * lAxis - sAxis * sAxis) / (lAxis * lAxis));

        double a0, b0, c0, d0, e0;
        a0 = d * (1 + 3.0 / 4.0 * e * e + 45.0 / 64.0 * e * e * e * e + 175.0 / 256.0 * e * e * e * e * e * e
                + 11025.0 / 16384.0 * e * e * e * e * e * e * e * e);
        b0 = d * (3.0 / 4.0 * e * e + 45.0 / 64.0 * e * e * e * e + 175.0 / 256.0 * e * e * e * e * e * e
                + 11025.0 / 16384.0 * e * e * e * e * e * e * e * e);
        c0 = d * (15.0 / 32.0 * e * e * e * e + 175.0 / 368.0 * e * e * e * e * e * e
                + 3675.0 / 8192.0 * e * e * e * e * e * e * e * e);
        d0 = d * (35.0 / 96.0 * e * e * e * e * e * e + 735.0 / 2048.0 * e * e * e * e * e * e * e * e);
        e0 = d * (315.0 / 1024.0 * e * e * e * e * e * e * e * e);

        double length;

        double latRad = lat * Math.PI / 180;

        // 计算赤道到该点的经线弧长
        length = a0 * (lat) * Math.PI / 180 - b0 * Math.sin(latRad) * Math.cos(latRad)
                - c0 * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad) * Math.cos(latRad)
                - d0 * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad)
                * Math.cos(latRad)
                - e0 * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad) * Math.sin(latRad)
                * Math.sin(latRad) * Math.sin(latRad) * Math.cos(latRad);

        return length;
    }

    public static HashMap<Integer, EarthDivisionData> initGridDivisionMap() {
        HashMap<Integer, EarthDivisionData> divDataMap = new HashMap<Integer, EarthDivisionData>();
        ArrayList<double[][]> earthIdCode = new ArrayList<double[][]>();
        earthIdCode.add(EarthIdCode_1.earthIdCode_1);
        earthIdCode.add(EarthIdCode_2.earthIdCode_2);
        earthIdCode.add(EarthIdCode_3.earthIdCode_3);
        earthIdCode.add(EarthIdCode_4.earthIdCode_4);
        earthIdCode.add(EarthIdCode_5.earthIdCode_5);
        earthIdCode.add(EarthIdCode_6.earthIdCode_6);


        for (int i = 0; i < earthIdCode.size(); i++) {
            double[][] earthIdCode_i = earthIdCode.get(i);
            for (int x = 0; x < earthIdCode_i.length; x++) {
                divDataMap.put((int) earthIdCode_i[x][0], new EarthDivisionData((int) earthIdCode_i[x][0], earthIdCode_i[x][1], earthIdCode_i[x][2], earthIdCode_i[x][3],
                        earthIdCode_i[x][4], earthIdCode_i[x][5], earthIdCode_i[x][6], earthIdCode_i[x][7], earthIdCode_i[x][8], earthIdCode_i[x][9], earthIdCode_i[x][10], earthIdCode_i[x][11]));
            }
        }
        return divDataMap;
    }


}
