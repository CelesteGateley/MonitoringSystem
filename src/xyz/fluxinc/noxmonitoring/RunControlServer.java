package xyz.fluxinc.noxmonitoring;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import xyz.fluxinc.noxmonitoring.adapters.CentralControl;
import xyz.fluxinc.noxmonitoring.orbmanagement.CentralControlOrb;
import xyz.fluxinc.noxmonitoring.orbmanagement.LocalServerOrb;
import xyz.fluxinc.noxmonitoring.resources.ControlScreenController;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class RunControlServer extends Application {

    public static Stage stage;
    private static ControlScreenController controlScreenController;
    private static String[] staticArgs;
    private static Map<String, Object> argsMap;

    public static void main(String[] args) {
        staticArgs = args;
        argsMap = RunMonitorStation.getArgs(args);
        if (!((boolean) argsMap.get("nogui"))) {
            argsMap.put("location", JOptionPane.showInputDialog(null, "Please enter the location of the monitor station", "Enter Location Name", JOptionPane.QUESTION_MESSAGE));
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFXML("ControlScreen"));
        RunControlServer.stage = stage;
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });

        LocalServerOrb serverOrb = new LocalServerOrb(staticArgs);
        CentralControl centralControl = new CentralControl(serverOrb, controlScreenController);
        CentralControlOrb controlOrb = new CentralControlOrb(staticArgs, centralControl);
        RunControlServerThread controlThread = new RunControlServerThread(controlOrb, (String) argsMap.get("location"));
        controlThread.start();

    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RunControlServer.class.getResource("resources/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setControlScreenController(ControlScreenController screenController) {
        controlScreenController = screenController;
    }

    private static class RunControlServerThread extends Thread {

        private final CentralControlOrb centralControlOrb;
        private final String serverName;

        public RunControlServerThread(CentralControlOrb centralControlOrb, String serverName) {
            this.centralControlOrb = centralControlOrb;
            this.serverName = serverName;
        }

        @Override
        public void run() {
            super.run();
            try {
                centralControlOrb.bind(serverName);
                centralControlOrb.runServer();
            } catch (ServantNotActive | WrongPolicy | CannotProceed | InvalidName | NotFound servantNotActive) {
                servantNotActive.printStackTrace();
            }
        }
    }
}
