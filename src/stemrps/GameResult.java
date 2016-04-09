package stemrps;

/**
 * DS for a match played
 * @author cade
 */
public class GameResult {
    
    public Status outcome;
    public RPS player, comp;
    
    public GameResult(Status _outcome, RPS _player, RPS _comp) {
        outcome = _outcome;
        player = _player;
        comp = _comp;
    }
    
}
