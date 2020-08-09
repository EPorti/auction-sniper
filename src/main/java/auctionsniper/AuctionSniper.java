package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;

    public AuctionSniper(SniperListener listener) {
        this.listener = listener;
    }

    @Override
    public void auctionClosed() {
        listener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment) {
    }
}
