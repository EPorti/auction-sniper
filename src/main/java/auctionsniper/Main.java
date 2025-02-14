package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuction;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main has one job which is to create the various components
 * and introduce them to each other.
 */
public class Main {
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;

    /**
     * If the chat is garbage-collected, the Smack runtime will hand the message to a new Chat which
     * it will create for the purpose. In an interactive application, we would listen for and show
     * these new chats, but our needs are different, so we add this quirk to stop it from happening.
     * <p>
     * This reference is made clumsy on purpose - to highlight in the code why we're doing it.
     * We also know that we're likely to come up with a better solution in a while.
     */
    private List<Auction> notToBeGCd = new ArrayList<>();

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private void addRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(itemId -> {
            snipers.addSniper(SniperSnapshot.joining(itemId));

            Auction auction = new XMPPAuction(connection, itemId);
            notToBeGCd.add(auction);
            auction.addAuctionEventListener(
                    new AuctionSniper(
                            auction,
                            new SwingThreadSniperListener(snipers),
                            itemId
                    )
            );

            auction.join();
        });
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();

        try {
            XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
            main.disconnectWhenUICloses(connection);
            main.addRequestListenerFor(connection);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("One or more arguments are missing!");
        }
    }

    /**
     * SwingThreadSniperListener pushes updates onto the Swing event thread.
     */
    public class SwingThreadSniperListener implements SniperListener {
        private final SniperListener sniperListener;

        public SwingThreadSniperListener(SniperListener sniperListener) {
            this.sniperListener = sniperListener;
        }

        @Override
        public void sniperStateChanged(final SniperSnapshot snapshot) {
            SwingUtilities.invokeLater(() -> sniperListener.sniperStateChanged(snapshot));
        }

        @Override
        public void addSniper(SniperSnapshot snapshot) {
            throw new UnsupportedOperationException();
        }
    }
}
