package stemrps;

import java.sql.Time;
import java.time.Instant;
import java.util.Random;

/**
 *
 * @author director
 */
public class GameLogic {

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
}
