public class config {
	public static int NO_SLOTS = 200;  //one slot - one unit Time
	public static int X_RANGE = 700; 
	public static int Y_RANGE = 700;
	public static int NO_CLUSTERS = 9;
	public static int NO_TYPE1_NODES = 30;
	public static int NO_TYPE2_NODES = 40;
	public static int NO_NODES = NO_TYPE2_NODES + NO_TYPE1_NODES;
	public static int TRANS_RANGE = 100;
	public static int MIN_POWER = 60;  //Min Energy Unit
	public static int MAX_POWER = 200;  //Max Energy Unit
	public static int WITH_IN_DIST = 650;
	public static int TOTAL_POWER = NO_TYPE1_NODES * MAX_POWER + NO_TYPE2_NODES * MIN_POWER;
}
