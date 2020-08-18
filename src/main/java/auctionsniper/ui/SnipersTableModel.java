package auctionsniper.ui;

import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

import static auctionsniper.Main.STATUS_JOINING;

/**
 * SnipersTableModel accepts updates from the Sniper and provides
 * a representation of those values to a JTable.
 */
public class SnipersTableModel extends AbstractTableModel {
    private final static SniperState STARTING_UP = new SniperState("", 0, 0);

    private String statusText = STATUS_JOINING;
    private SniperState sniperState = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // we're not keen on using switch, as it's not object-oriented,
        // so we'll keep an eye on this
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return sniperState.itemId;
            case LAST_PRICE:
                return sniperState.lastPrice;
            case LAST_BID:
                return sniperState.lastBid;
            case SNIPER_STATUS:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
        sniperState = newSniperState;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
