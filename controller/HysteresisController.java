package controller;

//Prevents frequent up/down toggling of replication by applying upper and lower thresholds
//"stability" part of our system. It avoids replica churn.
public class HysteresisController {

    //threshold values to control replication factor adjustments based on a file’s calculated "heat"
    private static final double UPPER_HEAT = 70.0; //Tunable thresholds
    private static final double LOWER_HEAT = 30.0; //Tunable thresholds

    //Ensures replication increases only when heat is significantly high, and decreases only when very low
    public int adjustReplication(double heat, int currentRep) {
        if (heat > UPPER_HEAT && currentRep < 4) return currentRep + 1;
        if (heat < LOWER_HEAT && currentRep > 1) return currentRep - 1;
        return currentRep;
    }
} 