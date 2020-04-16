package xyz.fluxinc.noxmonitoring.valuefactories;

import javafx.scene.control.TableCell;
import xyz.fluxinc.noxmonitoring.Alarm;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;

public class ValueFormatCell extends TableCell<Object, LogEntry> {

    public ValueFormatCell() {
    }

    @Override
    protected void updateItem(LogEntry entry, boolean isEmpty) {
        if (entry != null) {
            double value = entry.value;
            super.updateItem(entry, isEmpty);

            String text = "NULL";

            if (!isEmpty) {
                text = String.valueOf(value);
            }
            setText(text);

            if (entry.value >= Alarm.getRedAlarm(entry.type)) {
                setStyle("-fx-background-color: darkred");
            } else if (entry.value >= Alarm.getAmberAlarm(entry.type)) {
                setStyle("-fx-background-color: yellow");
            }
        }
    }
}
