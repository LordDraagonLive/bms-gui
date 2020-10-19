package bms.hazardevaluation;

import bms.sensors.HazardSensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the hazard level of a location using weightings for the sensor values.
 * The sum of the weightings of all sensors must equal 100.
 */
public class WeightingBasedHazardEvaluator implements HazardEvaluator{

    /**
     * mapping of sensors to their respective weighting
     */
    Map<HazardSensor,Integer> sensors;

    /**
     * Creates a new weighting-based hazard evaluator with the given sensors and weightings.
     * Each weighting must be between 0 and 100 inclusive, and the total sum of all weightings must equal 100.
     * @param sensors mapping of sensors to their respective weighting
     * @throws IllegalArgumentException if any weighting is below 0 or above 100; or if the sum of all weightings is not equal to 100
     */
    public WeightingBasedHazardEvaluator(Map<HazardSensor,Integer> sensors)
            throws IllegalArgumentException {
        boolean isValidWeigh = false;
        int weightingSum = 0;

        for(Map.Entry m:sensors.entrySet()){

            if ((int)m.getValue() >= 0 && (int)m.getValue() <= 100){
                isValidWeigh = true;
                weightingSum += (int)m.getValue();
            }else {
                throw new IllegalArgumentException();
            }
        }

        if (weightingSum != 100){
            throw new IllegalArgumentException();
        }

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
        List<Integer> weightings = this.getWeightings();
        List<HazardSensor> hazardSensorList = new ArrayList<HazardSensor>(this.sensors.keySet());
        int sumOfSensors = 0;

        for (HazardSensor sensor: hazardSensorList) {
            for (int weights: weightings) {
                sumOfSensors+= (sensor.getHazardLevel() * weights);
            }
        }

        return (int)Math.round((double)sumOfSensors/100);
    }

    /**
     * Returns a list containing the weightings associated with all of the sensors monitored by this hazard evaluator.
     * @return weightings
     */
    public List<Integer> getWeightings(){
        return new ArrayList<Integer>(this.sensors.values());
    }

    /**
     * Returns the string representation of this hazard evaluator.
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
     * @return string representation of this hazard evaluator.
     */
    @Override
    public String toString() {
        return "WeightingBased";
    }
}
