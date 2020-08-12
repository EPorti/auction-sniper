package auctionsniper;

import java.util.EventListener;

/**
 * SniperListener reports changes to the current state of the Sniper (feedback to the application).
 *
 * SniperListener is a notification, not a dependency.
 * Notification is a peer that needs to be kept up to date with the object's activity.
 * The object will notify interested peers whenever it changes state or performs significant action.
 *
 * Notifications are "fire and forget" - the object neither knows nor cares which
 * peers are listening. Notifications decouple objects from each other.
 */
public interface SniperListener extends EventListener {
    void sniperLost();
    void sniperBidding();
    void sniperWinning();
    void sniperWon();
}
