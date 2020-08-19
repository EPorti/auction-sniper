package auctionsniper;

import auctionsniper.util.Defect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SniperStateTest {
    @Test
    public void isWonWhenAuctionClosesWhileWinning() {
        assertEquals(SniperState.WON, SniperState.WINNING.whenAuctionClosed());
    }

    @Test
    public void isLostWhenAuctionClosesWhenJoiningOrBidding() {
        assertEquals(SniperState.LOST, SniperState.JOINING.whenAuctionClosed());
        assertEquals(SniperState.LOST, SniperState.BIDDING.whenAuctionClosed());
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenWon() {
        SniperState.WON.whenAuctionClosed();
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenLost() {
        SniperState.LOST.whenAuctionClosed();
    }
}
