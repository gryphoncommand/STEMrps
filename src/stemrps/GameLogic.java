package stemrps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author director
 */
public class GameLogic {

    //Returns a good tweet for every stats
    public static String getStatsString(long userId) throws FileNotFoundException {
        String s = "win: %" + (int)getWinPercent(userId);
        s += "  tie: %" + (int)getTiePercent(userId);
        s += "  lose: %" + (int)getLossPercent(userId);
        s += "  rock: %" + (int)getRockPercent(userId);
        s += "  paper: %" + (int)getPaperPercent(userId);
        s += "  scissors: %" + (int)getScissorsPercent(userId);
        return s;
    }

    //return if a beats b
    public static Status beats(RPS a, RPS b) {
        if (a == b) {
            return Status.T;
        }
        if (a == RPS.ROCK && b == RPS.SCISSORS) {
            return Status.W;
        }
        if (a == RPS.SCISSORS && b == RPS.PAPER) {
            return Status.W;
        }
        if (a == RPS.PAPER && b == RPS.ROCK) {
            return Status.W;
        }
        return Status.L;
    }

    //Gets the other remaining
    public static RPS getOther(RPS a, RPS b) {
        if (a == RPS.ROCK) {
            if (b == RPS.PAPER) {
                return RPS.SCISSORS;
            } else if (b == RPS.SCISSORS) {
                return RPS.PAPER;
            }
        } else if (a == RPS.PAPER) {
            if (b == RPS.ROCK) {
                return RPS.SCISSORS;
            } else if (b == RPS.SCISSORS) {
                return RPS.ROCK;
            }
        } else if (a == RPS.SCISSORS) {
            if (b == RPS.ROCK) {
                return RPS.PAPER;
            } else if (b == RPS.PAPER) {
                return RPS.ROCK;
            }
        }
        return rand(); //If they chose the same
    }

    //Returns random one
    public static RPS rand() {
        Random r = new Random(Time.from(Instant.now()).getTime());
        int n = r.nextInt(3);
        if (n == 0) {
            return RPS.ROCK;
        }
        if (n == 1) {
            return RPS.PAPER;
        }
        if (n == 2) {
            return RPS.SCISSORS;
        }
        return null;
    }

    //Returns win percentage
    public static double getWinPercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[1];
            if (matchStatus.equalsIgnoreCase("w")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Returns tie percentage
    public static double getTiePercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[1];
            if (matchStatus.equalsIgnoreCase("t")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Returns loss percentage
    public static double getLossPercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[1];
            if (matchStatus.equalsIgnoreCase("l")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Returns win percentage
    public static double getRockPercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[0];
            if (matchStatus.equalsIgnoreCase("rock")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Returns win percentage
    public static double getPaperPercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[0];
            if (matchStatus.equalsIgnoreCase("paper")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Returns win percentage
    public static double getScissorsPercent(long userId) throws FileNotFoundException {
        Scanner s = new Scanner(new File(STEMrps.dir + "usr/" + userId + ".txt"));
        int wins = 0;
        int lines = 0;
        while (s.hasNextLine()) {
            String curLine = s.nextLine();
            String matchStatus = curLine.split(",")[0];
            if (matchStatus.equalsIgnoreCase("scissors")) {
                wins++;
            }
            ++lines;
        }
        return 100 * ((double) (wins)) / lines;
    }

    //Gets the most likely winning strategy
    public static RPS getSmartStrat(long usr) throws FileNotFoundException, IOException {
        RPS suggestedChoice = RPS.ROCK;
        double likelyToChange_loss = 0;
        double likelyToChange_win = 0;
        double likelyToStay_t = 0;
        int wins = 0;
        int ties = 0;
        int losses = 0;
        String file = STEMrps.dir + "/usr/" + usr + ".txt";
        STEMrps.makeSureUsrSetup(usr);
        Scanner s = new Scanner(new File(file));
        List<String> line = new ArrayList<>();
        int lines = 0;
        while (s.hasNextLine()) {
            line.add(s.nextLine());
            ++lines;
        }
        boolean wonLast = false;
        Status playerS_l = null;
        RPS player_l = null;
        RPS comp_l = null;
        int max = Math.min(lines, 18);
        for (int i = 0; i < max; ++i) { //Last 30 games
            RPS player;
            RPS comp;
            Status playerS;
            String match = line.get(lines - max + i);
            String[] arr = match.split(",");
            player = RPS.valueOf(arr[0]);
            comp = RPS.valueOf(arr[2]);
            playerS = Status.valueOf(arr[1]);

            if (playerS == Status.L) {
                if (!wonLast) {
                    if (player != player_l) {
                        likelyToChange_loss++;
                    }
                }
            }
            if (playerS == Status.W) {
                if (wonLast) {
                    if (player != player_l) {
                        likelyToChange_win++;
                    }
                }
            }
            if (playerS == Status.T) {
                if (playerS_l == Status.T) {
                    if (player == player_l) {
                        likelyToStay_t++;
                    }
                }
            }

            if (playerS == Status.W) {
                ++wins;
            }
            if (playerS == Status.T) {
                ++ties;
            }
            if (playerS == Status.L) {
                ++losses;
            }

            if (playerS == Status.W) {
                wonLast = true;
            } else {
                wonLast = false;
            }
            player_l = player;
            playerS_l = playerS;
            comp_l = comp;
        }
        likelyToChange_win /= wins;
        likelyToChange_loss /= losses;
        likelyToStay_t /= ties;
        if (playerS_l == Status.W) {
            if (likelyToChange_win > .5) {
                suggestedChoice = player_l;
            } else {
                suggestedChoice = getOther(player_l, comp_l);
            }
        }
        if (playerS_l == Status.T) {
            if (likelyToStay_t > .5) {
                suggestedChoice = getOther(player_l, comp_l);
            } else {
                suggestedChoice = player_l;
            }
        }
        if (playerS_l == Status.L) {
            if (likelyToChange_loss > .5) {
                suggestedChoice = getOther(player_l, comp_l);
            } else {
                suggestedChoice = comp_l;
            }
        }
        return suggestedChoice;
    }
}
