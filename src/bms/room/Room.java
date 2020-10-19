package bms.room;

import bms.exceptions.DuplicateSensorException;
import bms.hazardevaluation.HazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.sensors.Sensor;
import bms.sensors.TemperatureSensor;
import bms.util.Encodable;

import java.util.*;

/**
 * Represents a room on a floor of a building.
 * <p>
 * Each room has a room number (unique for this floor, ie. no two rooms on the
 * same floor can have the same room number), a type to indicate its intended
 * purpose, and a total area occupied by the room in square metres.
 * <p>
 * Rooms also need to record whether a fire drill is currently taking place in
 * the room.
 * <p>
 * Rooms can have one or more sensors to monitor hazard levels
 * in the room.
 * @ass1
 */
public class Room implements Encodable {

    /**
     * Unique room number for this floor.
     */
    private int roomNumber;

    /**
     * The type of room. Different types of rooms can be used for different
     * activities.
     */
    private RoomType type;

    /**
     * List of sensors located in the room. Rooms may only have up to one of
     * each type of sensor. Alphabetically sorted by class name.
     */
    private List<Sensor> sensors;

    /**
     * Area of the room in square metres.
     */
    private double area;

    /**
     * Minimum area of all rooms, in square metres.
     * (Note that dimensions of the room are irrelevant).
     * Defaults to 5.
     */
    private static final int MIN_AREA = 5;

    /**
     * Records whether there is currently a fire drill.
     */
    private boolean fireDrill;

    /**
     * Records whether there is currently a fire drill.
     */
    private boolean maintenance;

    /**
     *  room's hazard evaluator
     */
    private HazardEvaluator hazardEvaluator;

    /**
     * Creates a new room with the given room number.
     *
     * @param roomNumber the unique room number of the room on this floor
     * @param type the type of room
     * @param area the area of the room in square metres
     * @ass1
     */
    public Room(int roomNumber, RoomType type, double area) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.area = area;

        this.sensors = new ArrayList<>();
        this.fireDrill = false;
        this.maintenance = false;
        this.hazardEvaluator = null;
    }

    /**
     * Returns room number of the room.
     *
     * @return the room number on the floor
     * @ass1
     */
    public int getRoomNumber() {
        return this.roomNumber;
    }

    /**
     * Returns area of the room.
     *
     * @return the room area in square metres
     * @ass1
     */
    public double getArea() {
        return this.area;
    }

    /**
     * Returns the minimum area for all rooms.
     * <p>
     * Rooms must be at least 5 square metres in area.
     *
     * @return the minimum room area in square metres
     * @ass1
     */
    public static int getMinArea() {
        return MIN_AREA;
    }

    /**
     * Returns the type of the room.
     *
     * @return the room type
     * @ass1
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Returns whether there is currently a fire drill in progress.
     *
     * @return current status of fire drill
     * @ass1
     */
    public boolean fireDrillOngoing() {
        return this.fireDrill;
    }

    /**
     * Returns whether there is currently maintenance in progress.
     * @return current status of maintenance
     */
    public boolean maintenanceOngoing(){
        return this.maintenance;
    }

    /**
     * Returns the list of sensors in the room.
     * <p>
     * The list of sensors stored by the room should always be in alphabetical
     * order, by the sensor's class name.
     * <p>
     * Adding or removing sensors from this list should not affect the room's
     * internal list of sensors.
     *
     * @return list of all sensors in alphabetical order of class name
     * @ass1
     */
    public List<Sensor> getSensors() {
        return new ArrayList<>(this.sensors);
    }

    /**
     * Change the status of the fire drill to the given value.
     *
     * @param fireDrill whether there is a fire drill ongoing
     * @ass1
     */
    public void setFireDrill(boolean fireDrill) {
        this.fireDrill = fireDrill;
    }

    /**
     * Return the given type of sensor if there is one in the list of sensors;
     * return null otherwise.
     *
     * @param sensorType the type of sensor which matches the class name
     *                   returned by the getSimpleName() method,
     *                   e.g. "NoiseSensor" (no quotes)
     * @return the sensor in this room of the given type; null if none found
     * @ass1
     */
    public Sensor getSensor(String sensorType) {
        for (Sensor s : this.getSensors()) {
            if (s.getClass().getSimpleName().equals(sensorType)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Change the status of maintenance to the given value.
     * @param maintenance whether there is maintenance ongoing
     */
    public void setMaintenance(boolean maintenance){
        this.maintenance = maintenance;
    }

    /**
     * Returns this room's hazard evaluator, or null if none exists.
     * @return room's hazard evaluator
     */
    public HazardEvaluator getHazardEvaluator(){
        return this.hazardEvaluator;
    }

    /**
     * Sets the room's hazard evaluator to a new hazard evaluator.
     * @param hazardEvaluator new hazard evaluator for the room to use
     */
    public void setHazardEvaluator(HazardEvaluator hazardEvaluator){
        this.hazardEvaluator = hazardEvaluator;
    }


    /**
     * Adds a sensor to the room if a sensor of the same type is not
     * already in the room.
     * <p>
     * The list of sensors should be sorted after adding the new sensor, in
     * alphabetical order by simple class name ({@link Class#getSimpleName()}).
     *
     * @param sensor the sensor to add to the room
     * @throws DuplicateSensorException if the sensor to add is of the
     * same type as a sensor already in this room
     * @ass1
     */
    public void addSensor(Sensor sensor)
            throws DuplicateSensorException {
        for (Sensor s : sensors) {
            if (s.getClass().equals(sensor.getClass())) {
                throw new DuplicateSensorException(
                        "Duplicate sensor of type: "
                                + s.getClass().getSimpleName());
            }
        }
        sensors.add(sensor);
        setHazardEvaluator(null);
        sensors.sort(Comparator.comparing(s -> s.getClass().getSimpleName()));
    }

    /**
     * Evaluates the room status based upon current information.
     * @return current room status
     */
    public RoomState evaluateRoomState(){

        if(fireDrillOngoing()){
            return RoomState.EVACUATE;
        }

        for (Sensor sensor:this.getSensors()) {
            if (sensor.getClass().getSimpleName().equals("TemperatureSensor")) {
                TemperatureSensor tempSensor = (TemperatureSensor) sensor;
                if (tempSensor.getHazardLevel() == 100){
                    return RoomState.EVACUATE;
                }
            }
        }

        if(maintenanceOngoing() && !fireDrillOngoing()){
            return RoomState.MAINTENANCE;
        }
        return RoomState.OPEN;
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
     * Returns the human-readable string representation of this room.
     * <p>
     * The format of the string to return is
     * "Room #'roomNumber': type='roomType', area='roomArea'm^2,
     * sensors='numSensors'"
     * without the single quotes, where 'roomNumber' is the room's unique
     * number, 'roomType' is the room's type, 'area' is the room's type,
     * 'numSensors' is the number of sensors in the room.
     * <p>
     * The room's area should be formatted to two (2) decimal places.
     * <p>
     * For example:
     * "Room #42: type=STUDY, area=22.50m^2, sensors=2"
     *
     * @return string representation of this room
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("Room #%d: type=%s, area=%.2fm^2, sensors=%d",
                this.roomNumber,
                this.type,
                this.area,
                this.sensors.size());
    }

    /**
     * Returns the machine-readable string representation of this room and all of its sensors.
     *
     * @return encoded string representation of this room
     */
    @Override
    public String encode() {

        ArrayList<String> sensorEncodeArray = new ArrayList<>();
        String roomAreaInDecimal = String.format("%.2f", this.getArea());

        this.getSensors().forEach( (sensor) -> sensorEncodeArray.add(sensor.encode()) );
        String sensorEncodeArrayStr = Arrays.toString(sensorEncodeArray.toArray())
                .replace("[","").replace("]", "");

        if (this.getHazardEvaluator() == null){
            return this.getRoomNumber()+":"+this.getType()+":"+roomAreaInDecimal+":"+this.getSensors().size()
                    +System.lineSeparator()+sensorEncodeArrayStr;
        }

        if (this.getHazardEvaluator().toString().equals("RuleBased")){
            return this.getRoomNumber()+":"+this.getType()+":"+roomAreaInDecimal+":"+this.getSensors().size()+":"+this.getHazardEvaluator().toString()
                    +System.lineSeparator()+sensorEncodeArrayStr;
        }
        List<Integer> weightingsList = ((WeightingBasedHazardEvaluator)this.getHazardEvaluator()).getWeightings();
        List<String> weightingBasedEvalList = new ArrayList<>();

        for (int i = 0; i < weightingsList.size(); i++) {
            weightingBasedEvalList.add(sensorEncodeArray.get(i)+"@"+weightingsList.get(i));
        }
        String weightingBasedEvalListStr = Arrays.toString(weightingBasedEvalList.toArray())
                .replace("[","").replace("]", "");

        return this.getRoomNumber()+":"+this.getType()+":"+roomAreaInDecimal+":"+this.getSensors().size()
                +":"+this.getHazardEvaluator().toString()
                +System.lineSeparator()+weightingBasedEvalListStr;
    }
}
