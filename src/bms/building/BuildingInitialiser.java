package bms.building;

import bms.exceptions.FileFormatException;
import bms.floor.Floor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

        String fileDir = "saves/"+filename;
        ArrayList numberOfBuildingsInList = new ArrayList();
        ArrayList allBuildingInformation = new ArrayList();
        ArrayList indexOfBuildingNames = new ArrayList();
        List<List<String>> buildingInformation = new ArrayList<>();

        try {
            File buildingInfo = new File(fileDir);
            Scanner infoReader = new Scanner(buildingInfo);

            /*
             * Reading File Information
             * */
            while (infoReader.hasNextLine()) {
                String data = infoReader.nextLine();
                allBuildingInformation.add(data);
                /*
                 * Check if data contains a number to find the building name and add them into an arraylist
                 * */
                if (!data.matches(".*\\d.*")) {
                    numberOfBuildingsInList.add(data);
                }
            }

            int i = 0;
            for (Object data : allBuildingInformation) {
                if (i <= 2) {
                    if (data.toString().equals(numberOfBuildingsInList.get(i))) {
                        indexOfBuildingNames.add(allBuildingInformation.indexOf(data.toString()));
                        i++;
                    }
                } else {
                    i = 2;
                }
            }

            boolean untilEnd = true;

            int numberCounter = 0;
            boolean lastLoop = false;

            while (untilEnd) {

                List<String> individualInformation = new ArrayList<>();
                int startindex = 0;
                int endindex = 0;

                if (lastLoop) {
                    startindex = (int) indexOfBuildingNames.get(numberCounter + 1);
                    endindex = allBuildingInformation.size();
                } else {
                    startindex = (int) indexOfBuildingNames.get(numberCounter);
                    endindex = (int) indexOfBuildingNames.get(numberCounter + 1);
                }

                firstLoop:
                for (int j = startindex + 1; j < endindex; j++) {
                    individualInformation.add((String) allBuildingInformation.get(j));
                    if (endindex != allBuildingInformation.size()) {
                        if (numberOfBuildingsInList.get(numberCounter + 1).equals(allBuildingInformation.get(j + 1))) {
                            individualInformation.add(0, (String) numberOfBuildingsInList.get(numberCounter));
                            if (numberCounter + 1 == numberOfBuildingsInList.size() - 1) {
                                lastLoop = true;
                            } else {
                                if (numberCounter + 1 < numberOfBuildingsInList.size() - 1) {
                                    numberCounter++;
                                    break firstLoop;
                                } else {
                                    untilEnd = false;
                                }
                            }
                        }
                    } else {
                        if (j == endindex - 1) {
                            individualInformation.add(0, (String) numberOfBuildingsInList.get(numberCounter + 1));
                        }
                        untilEnd = false;
                    }
                }

                buildingInformation.add(individualInformation);

            }

            System.out.println(buildingInformation);

            infoReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        List<Building> buildingList = new ArrayList<>();
        Building building = new Building("");
        buildingList.add(building);
        return buildingList;


//        InputStream in = BuildingInitialiser.class.getResourceAsStream("/lol.txt"); // or /some/resource/path/lol.txt for some other path starting at root of classpath

//
//        String fileName = "saves/"+filename;
//        List<String> fileLines = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));) {
//
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                fileLines.add(line);
//                // System.out.println(line);
//            }
//        } catch (Exception e) {
//            throw new IOException("File path invalid!");
//        }
//
//        // first building
//        Building building;
//        List<Building> buildingList = new ArrayList<>();
//        int numOfFloors = 0;
//        int floorNum = 0;
//        double floorWidth = 0.0, floorLength = 0.0;
//        Floor floor;
//        List<Floor> floorList;
//
//
//        String buildingName = fileLines.get(0);
//        for (int i = 0; i < fileLines.size(); i++) {
//            if (i == 0){
//                buildingName = fileLines.get(i);
//                building = new Building(buildingName);
//                buildingList.add(building);
//                fileLines.set(i,"");
//            }
//            if (i == 1){
//                numOfFloors = Integer.parseInt(fileLines.get(i));
//            }
//
//            if (i == 2 && numOfFloors >= 1){
//                numOfFloors--;
//                String[] floorLine = fileLines.get(i).split(":");
//                floorNum = Integer.parseInt(floorLine[0]);
//                floorWidth = Integer.parseInt(floorLine[1]);
//                floorLength = Integer.parseInt(floorLine[2]);
//
//                floor = new Floor(floorNum,floorWidth,floorLength);
//
//            }
//
////            values.removeAll(Arrays.asList("")); //remove all blank String
//
//        }
//
//        return buildingList;


    }
}
