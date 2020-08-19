package auctionsniper;

import auctionsniper.AuctionEventListener.PriceSource;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static auctionsniper.SniperState.*;
import static org.hamcrest.Matchers.equalTo;

public class AuctionSniperTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);
    // for keeping track of the Sniper's current state
    private final States sniperState = context.states("sniper");

    private static final String ITEM_ID = "item-id";

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations(){{
            ignoring(auction);
            // this is a supporting part of the test, not the part we really care about
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                                    then(sniperState.is("bidding"));
            // the expectation that we want to assert:
            // if the Sniper isn't bidding when it makes this call, the test will fail
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
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
        final int bid = price + increment;
        context.checking(new Expectations(){{
            // bid should be sent exactly once
            oneOf(auction).bid(bid);
            // we don't actually care if listener is notified more than once
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations(){{
            ignoring(auction);

            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                                    then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
                                    when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations(){{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                                    then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
                                    when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
