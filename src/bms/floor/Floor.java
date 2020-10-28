package bms.floor;

import bms.exceptions.DuplicateRoomException;
import bms.exceptions.FloorTooSmallException;
import bms.exceptions.InsufficientSpaceException;
import bms.room.Room;
import bms.room.RoomType;
import bms.util.Encodable;
import bms.util.FireDrill;

import java.util.*;

/**
 * Represents a floor of a building.
 * <p>
 * All floors have a floor number (ground floor is floor 1), a list of rooms,
 * and a width and length.
 * <p>
 * A floor can be evacuated, which causes all rooms on the floor to be
 * evacuated.
 * @ass1
 */
public class Floor implements FireDrill,Encodable {
    /**
     * Unique floor number for this floor. Corresponds to how many floors above
     * ground floor (inclusive).
     */
    private int floorNumber;

    /**
     * List of rooms on the floor level.
     */
    private List<Room> rooms;

    /**
     * Width of the floor in metres.
     */
    private double width;

    /**
     * Length of the floor in metres.
     */
    private double length;

    /**
     * Minimum width of all floors, in metres.
     */
    private static final int MIN_WIDTH = 5;

    /**
     * Minimum length of all floors, in metres.
     */
    private static final int MIN_LENGTH = 5;

    /**
     * Maintenance Schedule of the floor
     */
    private MaintenanceSchedule maintenanceSchedule;

    /**
     * Creates a new floor with the given floor number.
     *
     * @param floorNumber a unique floor number, corresponds to how many floors
     * above ground floor (inclusive)
     * @param width the width of the floor in metres
     * @param length the length of the floor in metres
     * @ass1
     */
    public Floor(int floorNumber, double width, double length) {
        this.floorNumber = floorNumber;
        this.width = width;
        this.length = length;

        this.rooms = new ArrayList<>();

        // Defaults to Null
        maintenanceSchedule = null;
    }

    /**
     * Returns the floor number of this floor.
     *
     * @return floor number
     * @ass1
     */
    public int getFloorNumber() {
        return this.floorNumber;
    }

    /**
     * Returns the minimum width for all floors.
     *
     * @return 5
     * @ass1
     */
    public static int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * Returns the minimum length for all floors.
     *
     * @return 5
     * @ass1
     */
    public static int getMinLength() {
        return MIN_LENGTH;
    }

    /**
     * Returns a new list containing all the rooms on this floor.
     * <p>
     * Adding or removing rooms from this list should not affect the
     * floor's internal list of rooms.
     *
     * @return new list containing all rooms on the floor
     * @ass1
     */
    public List<Room> getRooms() {
        return new ArrayList<>(this.rooms);
    }

    /**
     * Returns width of the floor.
     *
     * @return floor width
     * @ass1
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Returns length of the floor.
     *
     * @return floor length
     * @ass1
     */
    public double getLength() {
        return this.length;
    }

    /**
     * Search for the room with the specified room number.
     * <p>
     * Returns the corresponding Room object, or null if the room was not
     * found.
     *
     * @param roomNumber room number of room to search for
     * @return room with the given number if found; null if not found
     * @ass1
     */
    public Room getRoomByNumber(int roomNumber) {
        for (Room room : this.rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    /**
     * Calculates the area of the floor in square metres.
     * <p>
     * The area should be calculated as {@code getWidth()} multiplied by
     * {@code getLength()}.
     * <p>
     * For example, a floor with a length of 20.5 and width of 35.2, would be
     * 721.6 square metres.
     *
     * @return area of the floor in square metres
     * @ass1
     */
    public double calculateArea() {
        return this.getWidth() * this.getLength();
    }

    /**
     * Calculates the area of the floor which is currently occupied by all the
     * rooms on the floor.
     *
     * @return area of the floor that is currently occupied, in square metres
     * @ass1
     */
    public float occupiedArea() {
        float area = 0;
        for (Room room : rooms) {
            area += room.getArea();
        }
        return area;
    }

    /**
     * Adds a room to the floor.
     * <p>
     * The dimensions of the room are managed automatically. The length and
     * width of the room do not need to be specified, only the required space.
     *
     * @param newRoom object representing the new room
     * @throws IllegalArgumentException if area is less than Room.getMinArea()
     * @throws DuplicateRoomException if the room number on this floor is
     * already taken
     * @throws InsufficientSpaceException if there is insufficient space
     * available on the floor to be able to add the room
     * @ass1
     */
    // check that there is enough space available left on the floor
    public void addRoom(Room newRoom)
            throws DuplicateRoomException, InsufficientSpaceException {
        if (newRoom.getArea() < Room.getMinArea()) {
            throw new IllegalArgumentException(
                    "Area cannot be less than " + Room.getMinArea());
        }

        if (this.getRoomByNumber(newRoom.getRoomNumber()) != null) {
            throw new DuplicateRoomException(
                    "The room number " + newRoom.getRoomNumber()
                            + " is already taken on this floor.");
        }

        if ((this.occupiedArea() + newRoom.getArea()) > this.calculateArea()) {
            throw new InsufficientSpaceException("Insufficient space to add "
                    + "room. Floor area:" + this.calculateArea()
                    + "m^2, Occupied area: " + this.occupiedArea()
                    + "m^2, This room: " + newRoom.getArea() + "m^2");
        }

        // No problems, so add room to the list of rooms
        rooms.add(newRoom);
    }

    /**
     * Starts a fire drill in all rooms of the given type on the floor.
     * <p>
     * Only rooms of the given type must start a fire drill.
     * Rooms other than the given type must not start a fire drill.
     * <p>
     * If the room type given is null, then <b>all</b> rooms on the floor
     * must start a fire drill.
     *
     * @param roomType the type of room to carry out fire drills on; null if
     *                 fire drills are to be carried out in all rooms
     * @ass1
     */
    public void fireDrill(RoomType roomType) {
        for (Room r : this.rooms) {
            if (roomType == null || roomType == r.getType()) {
                r.setFireDrill(true);
            }
        }
    }

    /**
     * Cancels any ongoing fire drill in rooms on the floor.
     * <p>
     * All rooms must have their fire alarm cancelled regardless of room type.
     *
     * @ass1
     */
    public void cancelFireDrill() {
        for (Room r : this.rooms) {
            r.setFireDrill(false);
        }
    }

    /**
     * Returns the floor's maintenance schedule, or null if it does not exist.
     * @return maintenance schedule
     */
    public MaintenanceSchedule getMaintenanceSchedule(){
        return maintenanceSchedule;
    }


    /**
     * Changes the width and length of this floor.
     *
     * @param newWidth  new width dimension for the floor
     * @param newLength new length dimension for the floor
     * @throws IllegalArgumentException if newWidth < Floor.getMinWidth(); or newLength < Floor.getMinLength()
     * @throws FloorTooSmallException if the total size of the current rooms could not be supported
     *                                  by decreased dimensions
     */
    public void changeDimensions(double newWidth,
                                  double newLength)
            throws IllegalArgumentException,
            FloorTooSmallException {
        if(newWidth < Floor.getMinWidth() || newLength < Floor.getMinLength()){
            throw new IllegalArgumentException("The new dimensions must be greater than or equal to the minimum width and length for all floors!");
        }
        double result = newWidth * newLength;
        if (result < this.occupiedArea()){
            throw new FloorTooSmallException("The total size of the current rooms could not be supported by decreased dimensions");
        }

        this.width = newWidth;
        this.length = newLength;
    }

    /**
     * Adds a maintenance schedule to this floor with the given room order.
     * Maintenance will be undertaken on rooms on the floor in the given order,
     * with maintenance wrapping back to the start of the order once all the rooms
     * in the order have been visited.
     *
     * @param roomOrder rooms on which to perform maintenance, in order
     * @throws IllegalArgumentException if the given order is null or empty, if a room
     * in the order is not on this floor, or if a room appears twice or more consecutively
     * (the start of the list and the end of the list are also considered consecutive)
     * in an order with at least two rooms.
     */
    public void createMaintenanceSchedule(List<Room> roomOrder)
            throws IllegalArgumentException{

        if (roomOrder == null || roomOrder.isEmpty()){
            throw new IllegalArgumentException("The given room order must not be null and must contain at least one room.");
        }
//        if (!this.getRooms().containsAll(roomOrder)){
//            throw new IllegalArgumentException("All rooms in the given order must be rooms on this floor");
//        }

        for (Room room:roomOrder) {
            if (!this.getRooms().contains(room)){
                throw new IllegalArgumentException("All rooms in the given order must be rooms on this floor");
            }
        }


        for (int i = 0; i < roomOrder.size(); i++) {
            Room prevRoom;
            if (i+1 < roomOrder.size()){
                if (roomOrder.get(i).equals(roomOrder.get(i+1))){
                    throw new IllegalArgumentException("Cannot contain the same room twice or more consecutively");
                }else {
                    prevRoom = roomOrder.get(i);
                }
            }else{
                prevRoom = roomOrder.get(i);
                if (roomOrder.get(0).equals(prevRoom)){
                    throw new IllegalArgumentException("Cannot contain the same room twice or more consecutively");
                }
            }

        }

        this.maintenanceSchedule = new MaintenanceSchedule(roomOrder);

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
        return Objects.hash(floorNumber, rooms, width, length);
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
        // self check
        if (this == obj)
            return true;
        // null check
        if (obj == null)
            return false;
        // type check and cast
        if (getClass() != obj.getClass())
            return false;
        Floor floor = (Floor) obj;
        // field comparison
        return Objects.equals(this.floorNumber, floor.floorNumber)
                && Objects.equals(this.length, floor.getLength())
                && Objects.equals(this.width, floor.getWidth())
                && Objects.equals(this.getRooms().size(), floor.getRooms().size())
                && Objects.equals(this.getRooms(), floor.getRooms());
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Floor floor = (Floor) o;
//        return floorNumber == floor.floorNumber &&
//                Double.compare(floor.width, width) == 0 &&
//                Double.compare(floor.length, length) == 0 &&
//                Objects.equals(rooms, floor.rooms);
//    }

    /**
     * Returns the human-readable string representation of this floor.
     * <p>
     * The format of the string to return is
     * "Floor #'floorNumber': width='floorWidth'm, length='floorLength'm,
     * rooms='numRooms'"
     * without the single quotes, where 'floorNumber' is the floor's unique
     * number in the building, 'floorWidth' is the floor's width, 'floorLength'
     * is the floor's length, 'numRooms' is the number of rooms in the floor.
     * <p>
     * The floor's length and width should be formatted to two (2)
     * decimal places.
     * <p>
     * For example:
     * "Floor #6: width=12.80m, length=10.25m, rooms=15"
     *
     * @return string representation of this floor
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("Floor #%d: width=%.2fm, length=%.2fm, rooms=%d",
                this.floorNumber,
                this.width,
                this.length,
                this.rooms.size());
    }

    /**
     * Returns the String representation of the current state of this object.
     *
     * @return encoded String representation
     */
    @Override
    public String encode() {
        // need more formatting

        ArrayList<String> roomEncodeArray = new ArrayList<>();

        this.rooms.forEach( (room) -> roomEncodeArray.add(room.encode()) );
        String roomEncodeArrayStr = Arrays.toString(roomEncodeArray.toArray())
                .replace("[","").replace("]", "");

        return this.getFloorNumber()+":"+this.getWidth()+":"+this.getWidth()+":"+this.getRooms().size()
                +System.lineSeparator()+roomEncodeArrayStr;
    }
}
