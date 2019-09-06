package com.zte.bigdata.common

import scala.util.Try

object LatLonOf10km{
  def isLessThan10km(lat1: Double, lon1: Double, lat2: Double, lon2: Double):Boolean = Try{
    val latDiff=Math.abs(lat1-lat2)
    val lonDiff=math.abs(lon1-lon2)
    val lat =  Math.min(Math.abs(lat1),Math.abs(lat2))
    latDiff < 0.08983151432766477 && lonDiff < mapOf10km(Math.ceil(lat).toInt)
  }.getOrElse(false)

  val mapOf10km: Map[Int, Double] = Vector(
    (0,0.08983151432766477),
    (1,0.08984519818652356),
    (2,0.08988627061507774),
    (3,0.08995479424683955),
    (4,0.0900508737301303),
    (5,0.09017465611866934),
    (6,0.09032633142219561),
    (7,0.0905061333209696),
    (8,0.0907143400491772),
    (9,0.0909512754534859),
    (10,0.09121731023430642),
    (11,0.09151286337870064),
    (12,0.09183840379537121),
    (13,0.09219445216378329),
    (14,0.09258158301123195),
    (15,0.09300042703359844),
    (16,0.0934516736776631),
    (17,0.0939360740051962),
    (18,0.0944544438616582),
    (19,0.095007667375255),
    (20,0.09559670081535229),
    (21,0.09622257684291119),
    (22,0.09688640918972558),
    (23,0.09758939780789119),
    (24,0.09833283453619761),
    (25,0.09911810933610805),
    (26,0.09994671715678458),
    (27,0.10082026549636519),
    (28,0.10174048273555646),
    (29,0.10270922732975599),
    (30,0.10372849795757796),
    (31,0.1048004447370812),
    (32,0.10592738163649507),
    (33,0.10711180022417398),
    (34,0.10835638492331502),
    (35,0.10966402996117383),
    (36,0.11103785823073999),
    (37,0.11248124231584096),
    (38,0.11399782796935996),
    (39,0.11559156037979178),
    (40,0.11726671361509064),
    (41,0.11902792369635573),
    (42,0.12088022582940987),
    (43,0.12282909641228798),
    (44,0.1248805005442127),
    (45,0.12704094589069653),
    (46,0.12931754391487194),
    (47,0.13171807967314264),
    (48,0.13425109160152163),
    (49,0.13692596299740065),
    (50,0.1397530272425006),
    (51,0.14274368923245456),
    (52,0.1459105659975684),
    (53,0.14926765014466728),
    (54,0.1528305005566318),
    (55,0.15661646580035793),
    (56,0.1606449469764492),
    (57,0.16493770837631244),
    (58,0.16951924640379193),
    (59,0.1744172299171335),
    (60,0.17966302865532952),
    (61,0.1852923510099086),
    (62,0.19134601847787255),
    (63,0.19787091223098474),
    (64,0.20492113813979473),
    (65,0.2125594714191068),
    (66,0.22085916245053833),
    (67,0.22990621370503572),
    (68,0.23980227766020162),
    (69,0.25066838271012687),
    (70,0.26264977686453966),
    (71,0.2759223010538219),
    (72,0.29070088688606954),
    (73,0.307251053630016),
    (74,0.3259047165845895),
    (75,0.3470823188149916),
    (76,0.37132444793801506),
    (77,0.39933804529027206),
    (78,0.4320657197021435),
    (79,0.4707928688078365),
    (80,0.5173190731662501),
    (81,0.5742437531560736),
    (82,0.6454660685719795),
    (83,0.7371133036224311),
    (84,0.8593976369636664),
    (85,1.0307010358199091),
    (86,1.2877874913398983),
    (87,1.7164397247404082),
    (88,2.5740060119900527),
    (89,5.147227956813953),
    (90,360d)).toMap
}