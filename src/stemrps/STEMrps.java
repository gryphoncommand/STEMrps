package stemrps;

import java.io.File;
import java.io.FileNotFoundException;
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
import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Rock paper scissors!
 *
 * @author director
 */
public class STEMrps {

    //Our twitter acc
    public static Twitter t;

    //our directory we store everything in
    public static String dir = "C:/STEMrps/";

    //Whether is a twitter server
    public static boolean isTwitter = false;

    //Our char for pressing keys down aliases
    static String[] rock = new String[]{"r", "rock"};
    static String[] paper = new String[]{"p", "paper"};
    static String[] scissors = new String[]{"s", "scissors"};

    //Help text
    static String helpText
            = "Enter 'play' to play the game!";

    //How we read input
    public static Scanner reader = new Scanner(System.in);

    /**
     * @param args $directory $isTwitter $oath
     */
    public static void main(String[] args) throws IOException {
        String path = "";
        if (args.length >= 1) {
            dir = args[0];
        }
        if (args.length >= 2) {
            isTwitter = Boolean.parseBoolean(args[1]);
            path = args[2];
        }
        if (!isTwitter) {
            if (!isSetup()) {
                boolean created = new File(dir).mkdir();
                boolean createdUsr = new File(dir + "usr/").mkdir();
                File numGames = new File(dir + "count.txt");
                numGames.createNewFile();
                PrintWriter numWrite0 = new PrintWriter(numGames);
                numWrite0.println("0");
                numWrite0.close();
                if (!created || !createdUsr) {
                    System.out.println("Error creating storage file!");
                    System.exit(-1);
                }
            }
            writeToLogger("~");
            writeToLogger("Starting new session at " + Time.from(Instant.now()).toString());
            writeToLogger("");
            System.out.println("For rock: " + rock[0]);
            System.out.println("For paper: " + paper[0]);
            System.out.println("For scissors: " + scissors[0]);
            playGames();
        } else {
            initTwitter(path);
        }
    }

    //init twitter
    public static void initTwitter(String file) throws FileNotFoundException {
        Scanner s = new Scanner(new File(file));
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(s.nextLine())
                .setOAuthConsumerSecret(s.nextLine())
                .setOAuthAccessToken(s.nextLine())
                .setOAuthAccessTokenSecret(s.nextLine());
        TwitterFactory tf = new TwitterFactory(cb.build());
        t = tf.getInstance();
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(t.getConfiguration());
        TwitterStream twitterStream = twitterStreamFactory.getInstance();
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.follow(new long[]{4741197613L});
        twitterStream.addListener(new MentionListener());
        twitterStream.filter(filterQuery);
    }

    //Loop for games
    public static void playGames() throws IOException {
        Scanner inp = new Scanner(System.in);
        for (int i = 0; i < 10000; ++i) { //max 10000 games
            GameResult match = playGame(0, inp.nextLine());
            if (match == null) {
                System.out.println("Something went wrong");
                continue;
            }
            if (match.outcome == Status.W) {
                System.out.println("You won!");
            }
            if (match.outcome == Status.T) {
                System.out.println("You tied");
            }
            if (match.outcome == Status.L) {
                System.out.println("You lost :(");
            }
        }
    }

    //Plays one game
    public static GameResult playGame(long usr, String input) throws IOException {
        /*System.out.println("~");
        System.out.println("input: ");*/
        GameResult r;
        RPS computer = GameLogic.getSmartStrat(usr);
        RPS player = GameLogic.rand(); //Will terminate if user doesnt enter  correct vals
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
        r = new GameResult(match, player, computer);
        return r;
    }

    //Sees if it is non-case sensitive contained in the alias
    public static boolean isCmdMatch(String[] aliasarr, String inp) {
        for (String s : aliasarr) {
            if (s.equalsIgnoreCase(inp)) {
                return true;
            }
        }
        return false;
    }

    //Get what they chose
    public static RPS getFromString(String s) {
        if (isCmdMatch(rock, s)) {
            return RPS.ROCK;
        } else if (isCmdMatch(paper, s)) {
            return RPS.PAPER;
        } else if (isCmdMatch(scissors, s)) {
            return RPS.SCISSORS;
        } else {
            System.out.println("Something went wrong, please try again.");
            return null;
        }
    }

    //Gets number of games played
    public static long gamesPlayed() throws FileNotFoundException {
        Scanner file = new Scanner(new File(dir + "count.txt"));
        return Long.parseLong(file.nextLine());
    }

    //get user input
    public static String getInput() {
        return reader.nextLine();
    }

    //Log to player logger returns games played
    public static long formatGameToLogger(RPS player, Status s, RPS comp, long usr) throws IOException {
        try {
            PrintWriter logger = new PrintWriter(new FileWriter(dir + "games.txt", true));
            PrintWriter usrLogger = new PrintWriter(new FileWriter(dir + "usr/" + usr + ".txt", true));

            Scanner countFile = new Scanner(new File(dir + "count.txt"));
            long games = Long.parseLong(countFile.nextLine());
            countFile.close();
            games++;

            PrintWriter countLogger = new PrintWriter(new FileWriter(dir + "count.txt"));
            countLogger.println(games);
            countLogger.close();

            makeSureUsrSetup(usr);

            usrLogger.println(player.name() + "," + s.name() + "," + comp.name());
            logger.println(player.name() + "," + s.name() + "," + comp.name());

            usrLogger.close();
            logger.close();
            return games;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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
        PrintWriter logger = new PrintWriter(new FileWriter(dir + "games.txt", true));
        logger.print(s);
        logger.println();
        logger.flush();
    }
}
