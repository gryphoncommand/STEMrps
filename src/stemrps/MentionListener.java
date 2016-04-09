/* 
 * Copyright (C) 2016 ChemicalDevelopment
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package stemrps;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 *
 * @author director
 */
public class MentionListener implements StatusListener {

    //when a user tells mentions us
    @Override
    public void onStatus(Status status) {
        System.out.println(status.getUser().getScreenName() + ": " + status.getText());
        String[] inp = status.getText().split(" ");
        String player = "";
        if (inp[1].equalsIgnoreCase("stats")) {
            try {
                long user = status.getUser().getId();
                String whoStats = "";
                if (inp.length >= 3) {
                    if (inp[2] != null && !inp[2].equalsIgnoreCase("me")) {
                        User acc = STEMrps.t.showUser(inp[2]);
                        user = acc.getId();
                        whoStats = acc.getScreenName() + " ";
                    }
                }
                String tweet = "@" + status.getUser().getScreenName() + " " + whoStats + GameLogic.getStatsString(user);
                STEMrps.t.updateStatus(tweet);

            } catch (Exception ex) {
                Logger.getLogger(MentionListener.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            try {
                if (status.getUser().getId() == 4741197613L) { //If it is our's?
                    return;
                }
                STEMrps.makeSureUsrSetup(status.getUser().getId());
                for (String s : inp) {
                    if (STEMrps.getFromString(s) != null) {
                        player = s;
                        break;
                    }
                }
                stemrps.GameResult match = STEMrps.playGame(status.getUser().getId(), player);
                String tweet = "@" + status.getUser().getScreenName() + " ";
                if (match == null) {
                    tweet += "Something went wrong! Please use r, rock, p, paper, s, or scissors to play!";
                } else if (match.outcome == stemrps.Status.W) {
                    tweet += "You (" + match.player.name() + ") won against me (" + match.comp.name() + ")";
                } else if (match.outcome == stemrps.Status.T) {
                    tweet += "You (" + match.player.name() + ") tied against me (" + match.comp.name() + ")";
                } else if (match.outcome == stemrps.Status.L) {
                    tweet += "You (" + match.player.name() + ") lost against me (" + match.comp.name() + ")";
                }
                tweet += " (Game #" + STEMrps.gamesPlayed() + ")";
                StatusUpdate rt = new StatusUpdate(tweet);
                STEMrps.t.updateStatus(rt);
            } catch (Exception ex) {
                Logger.getLogger(MentionListener.class.getName()).log(Level.SEVERE, null, ex);
                String tweet = "@" + status.getUser().getScreenName() + " Oops, did't catch that. " + LocalDateTime.now();
                StatusUpdate rt = new StatusUpdate(tweet);
                try {
                    STEMrps.t.updateStatus(rt);
                } catch (TwitterException ex1) {
                    Logger.getLogger(MentionListener.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice sdn) {
        System.out.println(sdn.getUserId() + "'s status deleted!");
    }

    @Override
    public void onTrackLimitationNotice(int i) {

    }

    @Override
    public void onScrubGeo(long l, long l1) {

    }

    @Override
    public void onStallWarning(StallWarning sw) {
        System.out.println("Stall warning encountered!");
    }

    @Override
    public void onException(Exception excptn) {
        System.out.println(Arrays.toString(excptn.getStackTrace()));
    }

}
