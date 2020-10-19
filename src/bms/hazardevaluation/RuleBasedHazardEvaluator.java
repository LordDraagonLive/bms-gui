package bms.hazardevaluation;

import bms.sensors.HazardSensor;

import java.util.List;

public class RuleBasedHazardEvaluator implements HazardEvaluator {

    /**
     * List of sensors to be used in the hazard level calculation
     */
    List<HazardSensor> sensors;

    /**
     * Creates a new rule-based hazard evaluator with the given list of sensors.
     * @param sensors sensors to be used in the hazard level calculation
     */
    public RuleBasedHazardEvaluator(List<HazardSensor> sensors) {
        this.sensors = sensors;
    }

    /**
     * Calculates a hazard level between 0 and 100.
     * Indicates the hazard level in a location based on information available from multiple hazard sensors at the location.
     *
     * @return the hazard level, between 0 and 100 (inclusive)
     */
    @Override
    public int evaluateHazardLevel() {
        int otherSensorsHazardLvl = 0;
        int otherSensorsCount = 0;
        HazardSensor occupancySensor = null;
        double avgHazardLvl;

        if(this.sensors.isEmpty()){
            return 0;
        }else if(this.sensors.size() == 1){
            return this.sensors.get(0).getHazardLevel();
        }

        for (HazardSensor sensor : this.sensors) {
            if (!sensor.getClass().getSimpleName().equals("OccupancySensor")) {
                if (sensor.getHazardLevel() == 100){
                    return 100;
                }
                otherSensorsHazardLvl += sensor.getHazardLevel();
                otherSensorsCount++;
            }else{
                occupancySensor = sensor;
            }
        }

        avgHazardLvl = Math.floor((double)otherSensorsHazardLvl/ otherSensorsCount);

        if (occupancySensor != null) {
            return (int) (avgHazardLvl * Math.floor((double) occupancySensor.getHazardLevel()/100));
        }else {
            return (int) avgHazardLvl;
        }

    }

    /**
     * Returns the string representation of this hazard evaluator.
     * The format of the string to return is simply "RuleBased" without double quotes.
     *
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return string representation of this room
     */
    @Override
    public String toString() {
        return "RuleBased";
    }
}
