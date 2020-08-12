package auctionsniper;

import auctionsniper.AuctionEventListener.PriceSource;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionSniperTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
    // for keeping track of the Sniper's current state
    private final States sniperState = context.states("sniper");

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations(){{
            ignoring(auction);
            // this is a supporting part of the test, not the part we really care about
            allowing(sniperListener).sniperBidding();
                                    then(sniperState.is("bidding"));
            // the expectation that we want to assert:
            // if the Sniper isn't bidding when it makes this call, the test will fail
            atLeast(1).of(sniperListener).sniperLost();
                                    when(sniperState.is("bidding"));
        }});

        // we are using a sequence of events to get the Sniper
        // into the state we want to test
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPricesArrives() {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations(){{
            // bid should be sent exactly once
            oneOf(auction).bid(price + increment);
            // we don't actually care if listener is notified more than once
            atLeast(1).of(sniperListener).sniperBidding();
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperWinning();
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }
}
