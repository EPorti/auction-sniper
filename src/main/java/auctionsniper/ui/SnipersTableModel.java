package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * SniperTableModel represents the state of our bidding in the user interface.
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static final String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };

    private List<SniperSnapshot> snapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = rowMatching(newSniperSnapshot);
        snapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + newSnapshot);
    }

    @Override
    public void addSniper(SniperSnapshot snapshot) {
        int row = snapshots.size();
        snapshots.add(snapshot);
        fireTableRowsInserted(row, row);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
}
