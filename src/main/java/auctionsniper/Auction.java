package auctionsniper;

/**
 * Auction accepts bids for items in the market.
 *
 * Auction is a dependency of AuctionSniper.
 * AuctionSniper requires it to perform its responsibilities.
 */
public interface Auction {
    void bid(int amount);
    void join();
    void addAuctionEventListener(AuctionEventListener listener);
}
