package xyz.fluxinc.noxmonitoring.resources;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import xyz.fluxinc.noxmonitoring.RunControlServer;
import xyz.fluxinc.noxmonitoring.corba.LogEntry;
import xyz.fluxinc.noxmonitoring.corba.MonitorType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ControlScreenController {

    public TabPane fxStationTabMenu;
    public Label fxMonitorStationMaxName;
    public ListView<String> fxLocalServerList;
    public ListView<String> fxStationList;
    private ObservableList<String> localServerList;
    private ObservableList<String> stationList;
    private Map<String, List<String>> stationsByServer;
    private Map<String, List<LogEntry>> entriesByStation;

    @FXML
    public void initialize() {
        stationsByServer = new LinkedHashMap<>();
        entriesByStation = new LinkedHashMap<>();
        RunControlServer.setControlScreenController(this);
        localServerList = FXCollections.observableList(new ArrayList<>());
        stationList = FXCollections.observableList(new ArrayList<>());
        fxLocalServerList.setItems(localServerList);
        fxStationList.setItems(stationList);
    }

    @FXML
    public void updateServerList(Map<String, LogEntry[]> logs) {
        stationsByServer = new LinkedHashMap<>();
        entriesByStation = new LinkedHashMap<>();
        localServerList.clear();
        for (String server : logs.keySet()) {
            List<String> stations = new ArrayList<>();
            localServerList.add(server);
            for (LogEntry log : logs.get(server)) {
                if (!stations.contains(log.location)) {
                    stations.add(log.location);
                    entriesByStation.putIfAbsent(log.location, new ArrayList<>());
                }
                entriesByStation.get(log.location).add(log);
            }
            stationsByServer.put(server, stations);
        }
    }

    @FXML
    public void updateStationList() {
        String server = fxLocalServerList.getSelectionModel().getSelectedItem();
        stationList.clear();
        if (stationsByServer.get(server) != null) {
            stationList.addAll(stationsByServer.get(server));
        }
    }

    @FXML
    public void showInformation() {
        String station = fxStationList.getSelectionModel().getSelectedItem();
        fxMonitorStationMaxName.setText(station);
        List<LogEntry> entries = entriesByStation.get(station);
        if (entries != null) {
            entries.sort(Comparator.comparingLong(logEntry -> logEntry.timestamp));
            Map<MonitorType, ObservableList<LogEntry>> entryByType = new LinkedHashMap<>();
            for (LogEntry entry : entries) {
                if (!(entryByType.containsKey(entry.type))) {
                    entryByType.put(entry.type, FXCollections.observableList(new ArrayList<>()));
                }
                entryByType.get(entry.type).add(entry);
            }

            for (MonitorType type : entryByType.keySet()) {
                fxStationTabMenu.getTabs().clear();
                Tab tab = new Tab(type.toString());
                TableView<LogEntry> view = new TableView<>();
                view.setItems(entryByType.get(type));
                TableColumn<LogEntry, String> timeStampCol = new TableColumn<>("Timestamp");
                timeStampCol.setCellValueFactory(cellData -> timeStampToDate(cellData.getValue().timestamp));
                TableColumn<LogEntry, String> valueColumn = new TableColumn<>("Value");
                valueColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().value)));
                view.getColumns().addAll(timeStampCol, valueColumn);

                tab.setContent(view);
                fxStationTabMenu.getTabs().add(tab);
            }
        }
    }

    private ReadOnlyStringWrapper timeStampToDate(long timeStamp) {
        Date date = new Date(timeStamp);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        return new ReadOnlyStringWrapper(format.format(date));
    }
}
