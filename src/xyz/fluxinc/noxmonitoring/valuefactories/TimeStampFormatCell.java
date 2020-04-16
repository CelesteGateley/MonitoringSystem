package xyz.fluxinc.noxmonitoring.valuefactories;

import javafx.scene.control.TableCell;
import xyz.fluxinc.noxmonitoring.Alarm;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;

public class TimeStampFormatCell extends TableCell<Object, LogEntry> {

    public TimeStampFormatCell() {
    }

    @Override
    protected void updateItem(LogEntry entry, boolean isEmpty) {
        long value = entry.timestamp;
        super.updateItem(entry, isEmpty);

        String text = "NULL";

        if (!isEmpty) {

        }
        setText(text);

        if (entry.value >= Alarm.getRedAlarm(entry.type)) {
            setStyle("-fx-background-color: darkred");
        } else if (entry.value >= Alarm.getAmberAlarm(entry.type)) {
            setStyle("-fx-background-color: yellow");
        }
    }
}
