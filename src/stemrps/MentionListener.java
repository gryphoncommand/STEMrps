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

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

/**
 *
 * @author director
 */
public class MentionListener implements StatusListener {

    //when a user tells mentions us
    @Override
    public void onStatus(Status status) {
        System.out.println(status.getUser().getScreenName() + ": " + status.getText());
        try {
            STEMrps.makeSureUsrSetup(status.getUser().getId());
            String[] inp = status.getText().split(" ");
            String player = "";
            for (String s : inp) {
                if (STEMrps.getFromString(s) != null) {
                    player = s;
                    break;
                }
            }
            stemrps.Status match = STEMrps.playGame(status.getUser().getId(), player);
            RPS player_rps = STEMrps.getFromString(player);
            String tweet = "@" + status.getUser().getScreenName() + " ";
            if (player_rps == null) {
                tweet += "Something went wrong! pls use r, p, or s to play!";
            } else if (match == stemrps.Status.W) {
                tweet += "You (" + player_rps.name() + ") won!";
            } else if (match == stemrps.Status.T) {
                tweet += "You (" + player_rps.name() + ") tied";
            } else if (match == stemrps.Status.L) {
                tweet += "You (" + player_rps.name() + ") lost :(";
            }
            StatusUpdate rt = new StatusUpdate(tweet);
            STEMrps.t.updateStatus(rt);
        } catch (IOException ex) {
            Logger.getLogger(MentionListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TwitterException ex) {
            Logger.getLogger(MentionListener.class.getName()).log(Level.SEVERE, null, ex);
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
