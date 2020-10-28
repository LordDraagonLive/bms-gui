package bms.sensors;

import bms.util.Encodable;
import bms.util.TimedItem;
import bms.util.TimedItemManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * An abstract class to represent a sensor that iterates through observed values
 * on a timer.
 */
public abstract class TimedSensor implements TimedItem, Sensor, Encodable {

    /**
     * Data array representing the readings observed by the sensor.
     * <p>
     * Readings taken one minute apart.
     */
    private int[] sensorReadings;

    /**
     * The current sensor reading observed by the sensor.
     */
    private int currentReading;

    /**
     * The amount of time in minutes that the sensor has been running
     * (according to the system, not real life).
     */
    private int timeElapsed;

    /**
     * The number of minutes that must pass before the current sensor
     * reading is updated.
     */
    private int updateFrequency;

    /**
     * Creates a new timed sensor, using the provided list of sensor readings.
     * These represent "raw" data values, and have different meanings depending
     * on the concrete sensor class used.
     * <p>
     * The provided update frequency must be greater than or equal to one (1),
     * and less than or equal to five (5). The provided sensor readings array
     * must not be null, and must have at least one element. All sensor readings
     * must be non-negative.
     * <p>
     * The new timed sensor should be configured such that the first call
     * to {@link TimedSensor#getCurrentReading()} after calling the
     * constructor must return the first element of the given array.
     * <p>
     * The sensor should be registered as a timed item, see
     * {@link TimedItemManager#registerTimedItem(TimedItem)}.
     *
     * @param sensorReadings a non-empty array of sensor readings
     * @param updateFrequency indicates how often the sensor readings updates,
     *                        in minutes
     * @throws IllegalArgumentException if updateFrequency is &lt; 1 or &gt; 5;
     * or if sensorReadings is null; if sensorReadings is empty; or if any
     * value in sensorReadings is less than zero
     * @ass1
     */
    public TimedSensor(int[] sensorReadings, int updateFrequency) throws
            IllegalArgumentException {
        if ((updateFrequency < 1) || (updateFrequency > 5)) {
            throw new IllegalArgumentException("Update frequency must be "
                    + "between 1 and 5 minutes (inclusive)");
        }
        if (sensorReadings == null || sensorReadings.length == 0) {
            throw new IllegalArgumentException("Sensor readings array must "
                    + "not be null and must have at least one element");
        }
        for (int reading : sensorReadings) {
            if (reading < 0) {
                throw new IllegalArgumentException(
                        "All sensor readings must be non-negative");
            }
        }
        this.sensorReadings = sensorReadings;
        this.currentReading = sensorReadings[0];
        this.updateFrequency = updateFrequency;
        this.timeElapsed = 0;
        TimedItemManager.getInstance().registerTimedItem(this);
    }

    /**
     * Returns the current sensor reading observed by the sensor.
     *
     * @return the current sensor reading
     * @ass1
     */
    public int getCurrentReading() {
        return this.currentReading;
    }

    /**
     * Returns the number of minutes that have elapsed since the sensor was
     * instantiated. Should return 0 immediately after the constructor is
     * called.
     *
     * @return the sensor's time elapsed in minutes
     * @ass1
     */
    public int getTimeElapsed() {
        return timeElapsed;
    }

    /**
     * Returns the number of minutes in between updates to the current sensor
     * reading.
     *
     * @return the sensor's update frequency in minutes
     * @ass1
     */
    public int getUpdateFrequency() {
        return updateFrequency;
    }

    /**
     * Increments the time elapsed (in minutes) by one.
     * <p>
     * If {@link #getTimeElapsed()} divided by {@link #getUpdateFrequency()}
     * leaves zero (0) remainder, the sensor reading needs to be updated.
     * In this case, the current sensor reading is updated to the next value
     * in the array.
     * <p>
     * When the end of the sensor readings array is reached, it must start
     * again at the beginning of the array (in other words it wraps around).
     * @ass1
     * */
    public void elapseOneMinute() {
        this.timeElapsed++;

        // calculate the time taken before wrapping around to the starting value
        // again
        int rotationDuration = this.sensorReadings.length
                * this.updateFrequency;

        // calculate the time remaining in the current rotation
        int timeRemainingInRotation =  this.timeElapsed % rotationDuration;

        // index is time remaining in the current rotation divided by the update
        // frequency
        int index = timeRemainingInRotation / this.updateFrequency;

        this.currentReading = this.sensorReadings[index];
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
        int result = Objects.hash(updateFrequency);
        result = 31 * result + Arrays.hashCode(sensorReadings);
        return result;
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
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimedSensor that = (TimedSensor) o;
        return updateFrequency == that.updateFrequency &&
                Arrays.equals(sensorReadings, that.sensorReadings);
    }

    /**
     * Returns the human-readable string representation of this timed sensor.
     * <p>
     * The format of the string to return is
     * "TimedSensor: freq='updateFrequency', readings='sensorReadings'"
     * without the single quotes, where 'updateFrequency' is this sensor's
     * update frequency (in minutes) and 'sensorReadings' is a comma-separated
     * list of this sensor's readings.
     * <p>
     * For example: "TimedSensor: freq=5, readings=24,25,25,23,26"
     *
     * @return string representation of this sensor
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("TimedSensor: freq=%d, readings=%s",
                this.updateFrequency,
                String.join(",", Arrays.stream(this.sensorReadings)
                        .mapToObj(String::valueOf)
                        .toArray(String[]::new)));
    }

    /**
     * Returns the String representation of the current state of this object.
     *
     * @return encoded String representation
     */
    @Override
    public String encode() {
        return String.join(",", Arrays.stream(this.sensorReadings)
                .mapToObj(String::valueOf)
                .toArray(String[]::new));
    }
}
