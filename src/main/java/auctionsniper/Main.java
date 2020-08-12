package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuction;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main has one job which is to create the various components
 * and introduce them to each other.
 */
public class Main {
    public static final String STATUS_JOINING = "Joining";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    private MainWindow ui;

    /**
     * If the chat is garbage-collected, the Smack runtime will hand the message to a new Chat which
     * it will create for the purpose. In an interactive application, we would listen for and show
     * these new chats, but our needs are different, so we add this quirk to stop it from happening.
     * <p>
     * This reference is made clumsy on purpose - to highlight in the code why we're doing it.
     * We also know that we're likely to come up with a better solution in a while.
     */
    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private void joinAuction(XMPPConnection connection, String itemId) {
        disconnectWhenUICloses(connection);

        Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        this.notToBeGCd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(new AuctionMessageTranslator(connection.getUser(),
                new AuctionSniper(auction, new SniperStateDisplayer())));
        auction.join();
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(
                connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]
        );
    }

    /**
     * SniperStateDisplayer translates Sniper events into representation that Swing can display.
     */
    public class SniperStateDisplayer implements SniperListener {
        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        @Override
        public void sniperWinning() {
        }

        private void showStatus(final String status) {
            // invokeLater avoids blocking the Smack library
            SwingUtilities.invokeLater(() -> ui.showStatus(status));
        }
    }
}
