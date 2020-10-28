package bms.util;

import bms.building.Building;
import bms.floor.Floor;
import bms.room.Room;

/**
 * Utility class that provides a recommendation for a study room in a building.
 */
public class StudyRoomRecommender {
    public StudyRoomRecommender() {
    }

    /**
     * Returns a room in the given building that is most suitable for study purposes.
     *
     * @param building  building in which to search for a study room
     * @return the most suitable study room in the building; null if there are none
     */
    public static Room recommendStudyRoom(Building building){
        // Incomplete




        var floors = building.getFloors();
//        var rooms =floors.forEach(floor -> return floor.getRooms());
        return floors.get(1).getRooms().get(1);
    }
}
