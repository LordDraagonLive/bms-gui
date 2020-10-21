package bms.sensors;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A sensor that measures levels of carbon dioxide (CO2) in the air, in parts
 * per million (ppm).
 * @ass1
 */
public class CarbonDioxideSensor extends TimedSensor implements HazardSensor, ComfortSensor {

    /**
     * The ideal value for this sensor, where the comfort level is highest.
     */
    private int idealValue;

    /**
     * The maximum variation that is allowed from the ideal value. The comfort
     * level will be 0 when the value is this far (or further) away from the
     * ideal value.
     */
    private int variationLimit;

    /**
     * Creates a new carbon dioxide sensor with the given sensor readings,
     * update frequency, ideal CO2 value and acceptable variation limit.
     * <p>
     * Different rooms and environments may naturally have different "normal"
     * CO2 concentrations, for example, a large room with many windows may
     * have lower typical CO2 concentrations than a small room with poor
     * airflow.
     * <p>
     * To allow for these discrepancies, each CO2 sensor has an "ideal" CO2
     * concentration and a maximum acceptable variation from this value.
     * Both the ideal value and variation limit must be greater than zero.
     * These two values must be such that (idealValue - variationLimit) &gt;= 0.
     *
     * @param sensorReadings array of CO2 sensor readings <b>in ppm</b>
     * @param updateFrequency indicates how often the sensor readings update,
     *                        in minutes
     * @param idealValue ideal CO2 value in ppm
     * @param variationLimit acceptable range above and below ideal value in ppm
     * @throws IllegalArgumentException if idealValue &lt;= 0;
     * or if variationLimit &lt;= 0; or if (idealValue - variationLimit) &lt; 0
     * @ass1
     */
    public CarbonDioxideSensor(int[] sensorReadings, int updateFrequency,
                               int idealValue, int variationLimit)
            throws IllegalArgumentException {
        super(sensorReadings, updateFrequency);

        if (idealValue <= 0) {
            throw new IllegalArgumentException("Ideal CO2 value must be > 0");
        }
        if (variationLimit <= 0) {
            throw new IllegalArgumentException(
                    "CO2 variation limit must be > 0");
        }

        if (idealValue - variationLimit < 0) {
            throw new IllegalArgumentException("Ideal CO2 value - variation "
                    + "limit must be >= 0");
        }

        this.idealValue = idealValue;
        this.variationLimit = variationLimit;
    }

    /**
     * Returns the sensor's CO2 variation limit.
     *
     * @return variation limit in ppm
     * @ass1
     */
    public int getVariationLimit() {
        return variationLimit;
    }

    /**
     * Returns the sensor's ideal CO2 value.
     *
     * @return ideal value in ppm
     * @ass1
     */
    public int getIdealValue() {
        return idealValue;
    }

    /**
     * Returns the hazard level as detected by this sensor.
     * <p>
     * The returned hazard level is determined by the following table, and is
     * based on the current sensor reading.
     * <table border="1">
     * <caption>CO2 hazard level table</caption>
     * <tr>
     * <th>Current sensor reading</th>
     * <th>Hazard level</th>
     * <th>Associated effect</th>
     * </tr>
     * <tr><td>0-999</td><td>0</td><td>No effects</td></tr>
     * <tr><td>1000-1999</td><td>25</td><td>Drowsiness</td></tr>
     * <tr><td>2000-4999</td><td>50</td>
     * <td>Headaches, sleepiness, loss of concentration</td></tr>
     * <tr><td>5000+</td><td>100</td><td>Oxygen deprivation</td></tr>
     * </table>
     *
     * @return the current hazard level as an integer between 0 and 100
     * @ass1
     */
    @Override
    public int getHazardLevel() {
        final int currentReading = this.getCurrentReading();
        if (currentReading < 1000) {
            return 0;
        }
        if (currentReading < 2000) {
            return 25;
        }
        if (currentReading < 5000) {
            return 50;
        }
        return 100;
    }

    /**
     * Returns the comfort level in a location as detected by this sensor (as a percentage).
     * A value of 0 indicates demonstrates very low comfort, and a value of 100 indicates very high comfort.
     *
     * @return level of comfort at sensor location, 0 to 100
     */
    @Override
    public int getComfortLevel() {
        double absDifference = Math.abs(this.getCurrentReading() - this.getIdealValue());

        if (absDifference < this.getVariationLimit()) {
            double result = Math.round(absDifference/this.getVariationLimit());
            return ((int)Math.floor(1 - result)) * 100;
        }
        return 0;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link Object#equals(Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     *
     * @return a hash code value for this object.
     * @implSpec As far as is reasonably practical, the {@code hashCode} method defined
     * by class {@code Object} returns distinct integers for distinct objects.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the String representation of the current state of this object.
     *
     * @return encoded String representation
     */
    @Override
    public String encode() {
        return String.format(
                "CarbonDioxideSensor:%s:%d:%d:%d",
                super.encode(),
                this.getUpdateFrequency(),
                this.idealValue,
                this.variationLimit);
    }

    /**
     * Returns the human-readable string representation of this CO2 sensor.
     * <p>
     * The format of the string to return is
     * "TimedSensor: freq='updateFrequency', readings='sensorReadings',
     * type=CarbonDioxideSensor, idealPPM='idealValue',
     * varLimit='variationLimit'"
     * without the single quotes, where 'updateFrequency' is this sensor's
     * update frequency (in minutes), 'sensorReadings' is a comma-separated
     * list of this sensor's readings, 'idealValue' is this sensor's ideal CO2
     * concentration, and 'variationLimit' is this sensor's variation limit.
     * <p>
     * For example: "TimedSensor: freq=5, readings=702,694,655,680,711,
     * type=CarbonDioxideSensor, idealPPM=600, varLimit=250"
     *
     * @return string representation of this sensor
     * @ass1
     */
    @Override
    public String toString() {
        return String.format(
                "%s, type=CarbonDioxideSensor, idealPPM=%d, varLimit=%d",
                super.toString(),
                this.idealValue,
                this.variationLimit);
    }


}
