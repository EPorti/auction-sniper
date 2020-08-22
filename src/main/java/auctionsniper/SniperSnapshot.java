package auctionsniper;

import java.util.Objects;

/**
 * SniperState is a value type to carry the Sniper's state.
 *
 * "Bundling up"
 *
 * When we notice that a group of values are always used together, we take that as
 * a suggestion that there's a missing construct. A first step might be to create
 * a new type with fixed public fields - just giving the group a name highlights
 * the missing concept. Later we can migrate behavior to the new type, which might
 * eventually allow us to hide its fields behind a clean interface, satisfying the
 * "composite simpler than the sum of its parts" rule.
 */
public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    /**
     * SniperSnapshot decides itself whether it's referring to the same item,
     * instead of having the table model extract and compare identifier.
     * It's a clearer division of responsibilities, with the advantage that we can change
     * its implementation without changing the table model.
     * This avoids the "feature envy" code smell.
     */
    public boolean isForSameItemAs(SniperSnapshot snapshot) {
        return itemId.equals(snapshot.itemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperSnapshot that = (SniperSnapshot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid);
    }

    @Override
    public String toString() {
        return "SniperState{" +
                "itemId='" + itemId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lastBid=" + lastBid +
                '}';
    }
}
