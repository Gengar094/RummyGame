package game;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Config {

    public static final int GAME_SERVER_PORT_NUMBER = 3010;

    public static final String[] GAME_TILES =
            {"R1", "B1", "G1", "O1",
                    "R1", "B1", "G1", "O1",
                    "R2", "B2", "G2", "O2",
                    "R2", "B2", "G2", "O2",
                    "R3", "B3", "G3", "O3",
                    "R3", "B3", "G3", "O3",
                    "R4", "B4", "G4", "O4",
                    "R4", "B4", "G4", "O4",
                    "R5", "B5", "G5", "O5",
                    "R5", "B5", "G5", "O5",
                    "R6", "B6", "G6", "O6",
                    "R6", "B6", "G6", "O6",
                    "R7", "B7", "G7", "O7",
                    "R7", "B7", "G7", "O7",
                    "R8", "B8", "G8", "O8",
                    "R8", "B8", "G8", "O8",
                    "R9", "B9", "G9", "O9",
                    "R9", "B9", "G9", "O9",
                    "R10", "B10", "G10", "O10",
                    "R10", "B10", "G10", "O10",
                    "R11", "B11", "G11", "O11",
                    "R11", "B11", "G11", "O11",
                    "R12", "B12", "G12", "O12",
                    "R12", "B12", "G12", "O12",
                    "R13", "B13", "G13", "O13",
                    "R13", "B13", "G13", "O13", "*", "*"};

    public static List<String> tiles = new LinkedList<String>(Arrays.asList(GAME_TILES));

}