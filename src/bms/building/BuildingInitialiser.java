package bms.building;

import bms.exceptions.FileFormatException;
import bms.floor.Floor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which manages the initialisation and saving of buildings by reading and writing data to a file.
 */
public class BuildingInitialiser {

    /**
     * Loads a list of buildings from a save file with the given filename.
     * @param filename path of the file from which to load a list of buildings
     * @return a list containing all the buildings loaded from the file
     * @throws IOException if an IOException is encountered when calling any IO methods
     * @throws FileFormatException if the file format of the given file is invalid according to the rules above
     */
    public static List<Building> loadBuildings(String filename)
            throws IOException,
            FileFormatException {
//        InputStream in = BuildingInitialiser.class.getResourceAsStream("/lol.txt"); // or /some/resource/path/lol.txt for some other path starting at root of classpath


        String fileName = "saves/"+filename;
        List<String> fileLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));) {

            String line;

            while ((line = br.readLine()) != null) {
                fileLines.add(line);
                // System.out.println(line);
            }
        } catch (Exception e) {
            throw new IOException("File path invalid!");
        }

        // first building
        Building building;
        List<Building> buildingList = new ArrayList<>();
        int numOfFloors = 0;
        int floorNum = 0;
        double floorWidth = 0.0, floorLength = 0.0;
        Floor floor;
        List<Floor> floorList;


        String buildingName = fileLines.get(0);
        for (int i = 0; i < fileLines.size(); i++) {
            if (i == 0){
                buildingName = fileLines.get(i);
                building = new Building(buildingName);
                buildingList.add(building);
                fileLines.set(i,"");
            }
            if (i == 1){
                numOfFloors = Integer.parseInt(fileLines.get(i));
            }

            if (i == 2 && numOfFloors >= 1){
                numOfFloors--;
                String[] floorLine = fileLines.get(i).split(":");
                floorNum = Integer.parseInt(floorLine[0]);
                floorWidth = Integer.parseInt(floorLine[1]);
                floorLength = Integer.parseInt(floorLine[2]);

                floor = new Floor(floorNum,floorWidth,floorLength);

            }

//            values.removeAll(Arrays.asList("")); //remove all blank String

        }


    }
}
