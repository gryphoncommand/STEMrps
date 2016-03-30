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

    //Whether is a twitter server
    public static boolean isTwitter = false;

    //Our char for pressing keys down
    static String rock = "r";
    static String paper = "p";
    static String scissors = "s";

    //Help text
    static String helpText
            = "Enter 'play' to play the game!";

    //We log games in here
    public static PrintWriter logger;
    public static PrintWriter playerLogger;

    //How we read input
    public static Scanner reader = new Scanner(System.in);

    /**
     * @param args $directory
     */
    public static void main(String[] args) throws IOException {
        if (args.length >= 1) {
            dir = args[0];
        }
        if (args.length >= 2) {
            isTwitter = Boolean.parseBoolean(args[1]);
        }
        if (!isTwitter) {
            if (!isSetup()) {
                boolean created = new File(dir).mkdir();
                boolean createdUsr = new File(dir + "usr/").mkdir();
                if (!created || !createdUsr) {
                    System.out.println("Error creating storage file!");
                    System.exit(-1);
                }
            }
            logger = new PrintWriter(new FileWriter(dir + "games.txt", true));
            playerLogger = new PrintWriter(new FileWriter(dir + "player.txt", true));
            writeToLogger("~");
            writeToLogger("Starting new session at " + Time.from(Instant.now()).toString());
            writeToLogger("");
            System.out.println("For rock: " + rock);
            System.out.println("For paper: " + paper);
            System.out.println("For scissors: " + scissors);
            playGames();
        } else {

        }
        shutdown();
    }

    //Loop for games
    public static void playGames() throws IOException {
        Scanner inp = new Scanner(System.in);
        for (int i = 0; i < 10000; ++i) { //max 10000 games
            Status match = playGame(0, inp.nextLine());
            if (match == Status.W) {
                System.out.println("You won!");
            }
            if (match == Status.T) {
                System.out.println("You tied");
            }
            if (match == Status.L) {
                System.out.println("You lost :(");
            }
        }
    }

    //Plays one game
    public static Status playGame(long usr, String input) throws IOException {
        /*System.out.println("~");
        System.out.println("input: ");*/
        RPS computer = GameLogic.getSmartStrat(usr);
        RPS player = RPS.ROCK; //Will terminate if user doesnt enter  correct vals
        if (input.equalsIgnoreCase("exit")) {
            System.exit(0);
        } else if (input.equalsIgnoreCase("help")) {
            System.out.println(helpText);
            return null;
        } else {
            player = getFromString(input);
        }
        Status match = GameLogic.beats(player, computer);
        formatGameToLogger(player, match, computer, usr);
        return match;
    }

    //Get what they chose
    public static RPS getFromString(String s) {
        if (s.equalsIgnoreCase(rock)) {
            return RPS.ROCK;
        } else if (s.equalsIgnoreCase(paper)) {
            return RPS.PAPER;
        } else if (s.equalsIgnoreCase(scissors)) {
            return RPS.SCISSORS;
        } else {
            System.out.println("Something went wrong, please try again.");
            return null;
        }
    }

    //get user input
    public static String getInput() {
        return reader.nextLine();
    }

    //Log to player logger
    public static void formatGameToLogger(RPS player, Status s, RPS comp, long usr) throws IOException {
        makeSureUsrSetup(usr);
        PrintWriter usrLogger = new PrintWriter(new FileWriter(dir + "usr/" + usr + ".txt", true));
        usrLogger.print(player.name() + "," + s.name() + "," + comp.name());
        usrLogger.println();
        usrLogger.close();
    }

    public static void makeSureUsrSetup(long usr) throws IOException {
        File f = new File(dir + "usr/" + usr + ".txt");
        f.createNewFile();
    }

    //See if we are setup
    private static boolean isSetup() {
        Path dirPath = Paths.get(dir);
        return Files.exists(dirPath, LinkOption.NOFOLLOW_LINKS);
    }

    //See if we are setup
    public static boolean isUsrSetup(long usr) {
        Path dirPath = Paths.get(dir + "usr/" + usr + ".txt");
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
        playerLogger.close();
    }
}
