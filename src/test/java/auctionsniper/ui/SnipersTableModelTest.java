package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

public class SnipersTableModelTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        // attach a mock implementation of TableModelListener to the model;
        // this is one of the few occasions where we break our rule "Only mock types that you own"
        // because the table model design fits our design approach so well
        model.addTableModelListener(listener);
    }
    
    @Test
    public void hasEnoughColumns() {
        // make sure we're rendering the right number of columns
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        context.checking(new Expectations(){{
            // checks that we notify any attached JTable that the contents have changed
            oneOf(listener).tableChanged(with(aRowChangedEvent()));
        }});

        // event that triggers the behavior we want to test
        model.sniperStatusChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        // asserts that the table model returns the right values in the right columns
        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATE, MainWindow.STATUS_BIDDING);
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();

        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}
