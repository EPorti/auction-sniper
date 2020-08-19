package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener listener;
    private boolean isWinning = false;
    private final String itemId;
    private SniperSnapshot snapshot;

    public AuctionSniper(Auction auction, SniperListener listener, String itemId) {
        this.auction = auction;
        this.listener = listener;
        this.itemId = itemId;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }

        listener.sniperStateChanged(snapshot);
    }
}
