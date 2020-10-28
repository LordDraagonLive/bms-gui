package bms.sensors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * A sensor that measures the number of people in a room.
 * @ass1
 */
public class OccupancySensor extends TimedSensor implements HazardSensor, ComfortSensor {
    /**
     * Maximum capacity of the space the sensor is monitoring.
     */
    private int capacity;

    private int[] sensorReadings;

    /**
     * Creates a new occupancy sensor with the given sensor readings, update
     * frequency and capacity.
     * <p>
     * The given capacity must be greater than or equal to zero.
     *
     * @param sensorReadings a non-empty array of sensor readings
     * @param updateFrequency indicates how often the sensor readings update,
     *                        in minutes
     * @param capacity maximum allowable number of people in the room
     * @throws IllegalArgumentException if capacity is less than zero
     * @ass1
     */
    public OccupancySensor(int[] sensorReadings, int updateFrequency,
                           int capacity) {
        super(sensorReadings, updateFrequency);

        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be >= 0");
        }

        this.capacity = capacity;
        this.sensorReadings = sensorReadings;
    }

    /**
     * Returns the capacity of this occupancy sensor.
     *
     * @return capacity
     * @ass1
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the hazard level based on the ratio of the current sensor reading
     * to the maximum capacity.
     * <p>
     * When the current reading is equal to or more than the capacity, the
     * hazard level is equal to 100 percent.
     * <p>
     * For example, a room with a maximum capacity of 21 people and current
     * occupancy of 8 people would have a hazard level of 38.
     * A room with a maximum capacity of 30 people and a current occupancy of
     * 34 people would have a hazard level of 100.
     * <p>
     * Floating point division should be used when performing the calculation,
     * however the resulting floating point number should be <i>rounded to the
     * nearest integer</i> before being returned.
     *
     * @return the current hazard level as an integer between 0 and 100
     * @ass1
     */
    @Override
    public int getHazardLevel() {
        final int currentReading = this.getCurrentReading();

        if (currentReading >= this.capacity) {
            return 100;
        }
        double occupancyRatio = ((double) currentReading) / this.capacity;
        double occupancyPct = 100 * occupancyRatio;
        return (int) Math.round(occupancyPct);
    }

    /**
     * Returns the comfort level in a location as detected by this sensor (as a percentage).
     * A value of 0 indicates demonstrates very low comfort, and a value of 100 indicates very high comfort.
     *
     * @return level of comfort at sensor location, 0 to 100
     */
    @Override
    public int getComfortLevel() {
        if (this.getCurrentReading() < this.getCapacity()) {
            double result = Math.round((double)this.getCurrentReading() / this.getCapacity());
            return (int)(Math.floor((1 - result))) * 100;
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
        int result = Objects.hash(super.hashCode(), capacity);
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
        if (!super.equals(o)) return false;
        OccupancySensor that = (OccupancySensor) o;
        return capacity == that.capacity &&
                getUpdateFrequency() == that.getUpdateFrequency() &&
                Arrays.equals(sensorReadings, that.sensorReadings);
    }


    /**
     * Returns the String representation of the current state of this object.
     *
     * @return encoded String representation
     */
    @Override
    public String encode() {
        return String.format(
                "OccupancySensor:%s:%d:%d",
                super.encode(),
                this.getUpdateFrequency(),
                this.getCapacity());
    }

    /**
     * Returns the human-readable string representation of this occupancy
     * sensor.
     * <p>
     * The format of the string to return is
     * "TimedSensor: freq='updateFrequency', readings='sensorReadings',
     * type=OccupancySensor, capacity='sensorCapacity'"
     * without the single quotes, where 'updateFrequency' is this sensor's
     * update frequency (in minutes), 'sensorReadings' is a comma-separated
     * list of this sensor's readings, and 'sensorCapacity' is this sensor's
     * maximum capacity.
     * <p>
     * For example: "TimedSensor: freq=5, readings=27,28,28,25,3,1,
     * type=OccupancySensor, capacity=30"
     *
     * @return string representation of this sensor
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s, type=OccupancySensor, capacity=%d",
                super.toString(),
                this.capacity);
    }

}
