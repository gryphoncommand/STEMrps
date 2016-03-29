package stemrps;

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
}
