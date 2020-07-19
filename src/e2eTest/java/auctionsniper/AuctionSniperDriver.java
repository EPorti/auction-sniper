package auctionsniper;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.equalTo;

/**
 * AuctionSniperDriver is an extension of a WindowLicker specialized for our tests.
 * <p>
 * It attempts to find a visible top-level window for the Auction Sniper within the given timeout.
 * It can also look for the relevant label in the user interface and confirms that it shows the given status.
 */
public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(this,
                named(MainWindow.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
    }
}
