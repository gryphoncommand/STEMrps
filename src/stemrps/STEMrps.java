package stemrps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.Instant;
import java.util.Scanner;

/**
 * Rock paper scissors!
 *
 * @author director
 */
public class STEMrps {

    //our directory we store everything in
    public static String dir = "C:/STEMrps/";

    //Our char for pressing keys down
    static String rock = "r";
    static String paper = "p";
    static String scissors = "s";

    //Help text
    static String helpText
            = "Enter 'play' to play the game!";

    //We log games in here
    public static PrintWriter logger;

    //How we read input
    public static Scanner reader = new Scanner(System.in);

    /**
     * @param args $directory
     */
    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            dir = args[0];
        }
        if (!isSetup()) {
            boolean created = new File(dir).mkdir();
            if (!created) {
                System.out.println("Error creating storage file!");
                System.exit(-1);
            }
        }
        logger = new PrintWriter(new FileWriter(dir + "games.txt", true));
        writeToLogger("~");
        writeToLogger("Starting new session at " + Time.from(Instant.now()).toString());
        writeToLogger("");
        System.out.println("For rock: " + rock);
        System.out.println("For paper: " + paper);
        System.out.println("For scissors: " + scissors);
        playGame();
        shutdown();
    }

    //recursively take user input
    public static void playGame() throws IOException {
        System.out.println("~");
        System.out.println("input: ");
        String input = getInput();
        RPS computer = GameLogic.rand(); //Needs actual logic
        RPS player = RPS.ROCK; //Will terminate if user doesnt enter  correct vals
        if (input.equalsIgnoreCase("exit")) {
            System.exit(0);
        } else if (input.equalsIgnoreCase("help")) {
            System.out.println(helpText);
            playGame();
        } else if (input.equalsIgnoreCase(rock)) {
            player = RPS.ROCK;
        } else if (input.equalsIgnoreCase(paper)) {
            player = RPS.PAPER;
        } else if (input.equalsIgnoreCase(scissors)) {
            player = RPS.SCISSORS;
        } else {
            System.out.println("Something went wrong, please try again.");
            playGame();
        }
        Status match = GameLogic.beats(player, computer);
        if (null != match) {
            switch (match) {
                case W:
                    System.out.println("You won!");
                    writeToLogger("You (" + player.name() + ") won against computer (" + computer.name() + ")");
                    break;
                case T:
                    System.out.println("You tied");
                    writeToLogger("You (" + player.name() + ") tied against computer (" + computer.name() + ")");
                    break;
                case L:
                    System.out.println("You lost :(");
                    writeToLogger("You (" + player.name() + ") lost against computer (" + computer.name() + ")");
                    break;
                default:
                    break;
            }
        }
        playGame();
        System.out.println("~");
    }

    //get user input
    public static String getInput() {
        return reader.nextLine();
    }

    //See if we are setup
    private static boolean isSetup() {
        Path dirPath = Paths.get(dir);
        return Files.exists(dirPath, LinkOption.NOFOLLOW_LINKS);
    }

    //Prints a line to the log file
    public static void writeToLogger(String s) throws IOException {
        logger.print(s);
        logger.println();
        logger.flush();
    }

    //closes everything
    public static void shutdown() throws IOException {
        logger.close();
    }

}
