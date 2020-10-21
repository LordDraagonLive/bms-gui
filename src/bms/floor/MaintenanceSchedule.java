package bms.floor;

import bms.room.Room;
import bms.room.RoomState;
import bms.room.RoomType;
import bms.sensors.TimedSensor;
import bms.util.Encodable;
import bms.util.TimedItem;
import bms.util.TimedItemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Carries out maintenance on a list of rooms in a given floor.
 * The maintenance time for each room depends on the type of the room and its area.
 * Maintenance cannot progress whilst an evacuation is in progress.
 */
public class MaintenanceSchedule implements TimedItem, Encodable {

    /**
     * list of rooms on which to perform maintenance, in order
     */
    List<Room> roomOrder;

    /**
     * The amount of time in minutes that the sensor has been running
     * (according to the system, not real life).
     */
    private int timeElapsed;
    /**
     * Creates a new maintenance schedule for a floor's list of rooms.
     * In this constructor, the new maintenance schedule should be registered as a timed item with the timed item manager.
     * @param roomOrder list of rooms on which to perform maintenance, in order
     */
    public MaintenanceSchedule(List<Room> roomOrder){

        if (roomOrder != null && roomOrder.size() > 0){
            this.roomOrder = roomOrder;
            this.timeElapsed = 0;
            this.roomOrder.get(0).setMaintenance(true);
            TimedItemManager.getInstance().registerTimedItem(this);
        }
    }

    /**
     * Returns the time taken to perform maintenance on the given room, in minutes.
     * @param room room on which to perform maintenance
     * @return room's maintenance time in minutes
     */
    public int getMaintenanceTime(Room room){

        double resultArea = room.getArea() - Room.getMinArea();
        if (room.getArea()>= Room.getMinArea()){
            resultArea = room.getArea();
        }
        double baseMaintenanceTime = (resultArea * 0.2) + 5;

        RoomType roomType = room.getType();
        double roomTypeMultiplier = switch (roomType) {
            case STUDY -> 1;
            case OFFICE -> 1.5;
            case LABORATORY -> 2;
        };

        return (int)Math.round(baseMaintenanceTime * roomTypeMultiplier);
    }

    /**
     * Returns the room which is currently in the process of being maintained.
     * @return room currently in maintenance
     */
    public Room getCurrentRoom(){
        for (Room room: this.roomOrder) {
            if (room.maintenanceOngoing()){
                return room;
            }
        }
        return null;
    }

    /**
     * Returns the number of minutes that have elapsed while maintaining the current room (getCurrentRoom()).
     * @return time elapsed maintaining current room
     */
    public int getTimeElapsedCurrentRoom(){
        return this.timeElapsed;
    }

    /**
     * Progresses the maintenance schedule by one minute.
     */
    public void elapseOneMinute() {

        if (this.getCurrentRoom().evaluateRoomState() != RoomState.EVACUATE) {

            // calculate the time taken before wrapping around to the starting value
            // again
            int rotationDuration = getMaintenanceTime(this.getCurrentRoom());

            // If enough time has elapsed
            if (rotationDuration <= timeElapsed){
//                this.getCurrentRoom().setMaintenance(false);
//
//                int currentRoomIndex = this.roomOrder.indexOf(this.getCurrentRoom());
//                if (currentRoomIndex < 0 || currentRoomIndex+1 == this.roomOrder.size()){
//                    this.roomOrder.get(0).setMaintenance(true);
//                }else{
//                    this.roomOrder.get(currentRoomIndex+1).setMaintenance(true);
//                }
                this.skipCurrentMaintenance();
            }else{
                this.timeElapsed++;
            }
        }
    }

    /**
     * Stops the in-progress maintenance of the current room and progresses to the next room.
     */
    public void skipCurrentMaintenance(){

        int currentRoomIndex = this.roomOrder.indexOf(this.getCurrentRoom());
        this.getCurrentRoom().setMaintenance(false);

        if (currentRoomIndex < 0 || currentRoomIndex+1 == this.roomOrder.size()){
            this.roomOrder.get(0).setMaintenance(true);
        }else{
            this.roomOrder.get(currentRoomIndex+1).setMaintenance(true);
        }
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
        //MaintenanceSchedule: currentRoom=#currentRoomNumber, currentElapsed=elapsed
        return String.format(
                "MaintenanceSchedule: currentRoom=#%d, currentElapsed=%d",
                this.getCurrentRoom().getRoomNumber(),
                this.timeElapsed);
    }

    /**
     * Returns the String representation of the current state of this object.
     *
     * @return encoded String representation
     */
    @Override
    public String encode() {
        ArrayList<String> roomEncodeArray = new ArrayList<>();

        this.roomOrder.forEach( (room) -> roomEncodeArray.add(String.valueOf(room.getRoomNumber())) );

        return Arrays.toString(roomEncodeArray.toArray())
                .replace("[","").replace("]", "");
    }
}
