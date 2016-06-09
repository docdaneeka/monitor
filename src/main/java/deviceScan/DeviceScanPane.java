package deviceScan;

import main.ScriptExecutor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by alex on 20.04.16.
 */
public class DeviceScanPane extends JPanel {

    private JTextArea usbDevTextArea;
    private JTextArea webcamDevTextArea;
    private JTextArea soundDevTextArea;
    private JTable usbDevTable;
    private JTable webcamDevTable;
    private JTable soundDevTable;
    JScrollPane usbDevScrollPane;
    JScrollPane webcamDevScrollPane;
    JScrollPane soundDevScrollPane;
    private Thread scanThread;
    
    public DeviceScanPane(){

        JLabel tabTitle = new JLabel( "Device usage scanner:" );
        JLabel usbLabel = new JLabel( "List of USB devices:" );
        JLabel webcamLabel = new JLabel( "List of webcam devices:" );
        JLabel soundDevLabel = new JLabel( "List of sound devices:" );

        usbDevTextArea = new JTextArea(15,1);
        usbDevTextArea.setLineWrap(true);
        usbDevTextArea.setWrapStyleWord(true);
        usbDevTable = new JTable(0,0);
        usbDevScrollPane = new JScrollPane(
                usbDevTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        webcamDevTextArea = new JTextArea(15,1);
        webcamDevTextArea.setLineWrap(true);
        webcamDevTextArea.setWrapStyleWord(true);
        webcamDevTable = new JTable(0,0);
        webcamDevScrollPane = new JScrollPane(
                webcamDevTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        soundDevTextArea = new JTextArea(15,1);
        soundDevTextArea.setLineWrap(true);
        soundDevTextArea.setWrapStyleWord(true);
        soundDevTable = new JTable(0,0);
        soundDevScrollPane = new JScrollPane(
                soundDevTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(tabTitle)
                        .addComponent(usbLabel)
                        .addComponent(usbDevScrollPane)
                        .addComponent(webcamLabel)
                        .addComponent(webcamDevScrollPane)
                        .addComponent(soundDevLabel)
                        .addComponent(soundDevScrollPane)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(tabTitle)
                        .addComponent(usbLabel)
                        .addComponent(usbDevScrollPane)
                        .addComponent(webcamLabel)
                        .addComponent(webcamDevScrollPane)
                        .addComponent(soundDevLabel)
                        .addComponent(soundDevScrollPane)
        );
    }

    public void activate(){
        scanThread = new Thread(new Runnable(){

            public void run() {
                while(true) {

                    getUSBDevices();
                    getWebcamDevices();
                    getSoundDevices();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        usbDevTextArea.setText("Scan started on : [" + sdf.format(cal.getTime()) + "].");
        scanThread.start();
    }

    private void getUSBDevices(){

        String command = "lsusb";

        String lines[] = ScriptExecutor.execScript(command).split("\n");

        Object columnNames[] = {"BUS", "DEVICE", "NAME", "IN USAGE", "DEV_FILE", "PID", "PROCESS", "USER"};
        ArrayList<String[]> data = new ArrayList<>();

        for(String line : lines) {

            String out[] = line.split("\\s+");

            String bus = out[1];
            String dev = out[3].substring(0,out[3].length()-1);
            String name = line.split(out[5])[1].trim();

            String devFile = "/dev/bus/usb/" + bus + "/" + dev;

            ArrayList<String[]> processes = getDeviceUsage(devFile);

            for(String[] process : processes){
                String device[] = {bus, dev, name, "YES", devFile, process[1], process[0], process[2]};
                data.add(device);
            }

            if(processes.size() == 0){
                String device[] = {bus, dev, name, "NO", devFile, null, null, null};
                data.add(device);
            }
        }

        Object[][] dataTable = new String[data.size()][];
        dataTable = data.toArray(dataTable);
        TableModel model = new DefaultTableModel(dataTable, columnNames);
        usbDevTable.setModel(model);
        usbDevTable.getColumnModel().getColumn(0).setMaxWidth(35);
        usbDevTable.getColumnModel().getColumn(1).setMaxWidth(50);
        usbDevTable.getColumnModel().getColumn(2).setMinWidth(400);
        usbDevTable.getColumnModel().getColumn(3).setMaxWidth(62);
        usbDevTable.getColumnModel().getColumn(4).setMinWidth(150);
        usbDevTable.getColumnModel().getColumn(5).setMaxWidth(60);
    }

    private void getWebcamDevices(){

        String command = "ls -l /sys/class/video4linux | grep /";

        String lines[] = ScriptExecutor.execScript(command).trim().split("\n");

        Object columnNames[] = {"BUS", "DEVICE", "NAME", "IN USAGE", "DEV_FILE", "PID", "PROCESS", "USER"};
        ArrayList<String[]> data = new ArrayList<>();

        for(String line : lines) {

            if(line.trim().equals("")) continue;

            String out[] = line.split("\\s+");

            String bus = "-";
            String dev = "-";

            String driverPath = out[out.length-1];
            driverPath = driverPath.replace("../..", "/sys");
            driverPath = driverPath.replaceAll(":", "\\:");
            command = "cat " + driverPath + "/device/interface";
            String name = ScriptExecutor.execScript(command);

            try{
                bus = driverPath.split("usb")[1].substring(0,1);
                while(bus.length()<3){
                    bus = "0"+bus;
                }
                dev = driverPath.split("usb")[1].substring(2,3);
                while(dev.length()<3){
                    dev = "0"+dev;
                }
            } catch (Exception e){}

            String devFile = null;
            try {
                devFile = "/dev/" + out[out.length - 3];
            } catch (Exception e){}

            ArrayList<String[]> processes = getDeviceUsage(devFile);

            for(String[] process : processes){
                String device[] = {bus, dev, name, "YES", devFile, process[1], process[0], process[2]};
                data.add(device);
            }

            if(processes.size() == 0){
                String device[] = {bus, dev, name, "NO", devFile, null, null, null};
                data.add(device);
            }
        }

        Object[][] dataTable = new String[data.size()][];
        dataTable = data.toArray(dataTable);
        TableModel model = new DefaultTableModel(dataTable, columnNames);
        webcamDevTable.setModel(model);
        webcamDevTable.getColumnModel().getColumn(0).setMaxWidth(35);
        webcamDevTable.getColumnModel().getColumn(1).setMaxWidth(50);
        webcamDevTable.getColumnModel().getColumn(2).setMinWidth(400);
        webcamDevTable.getColumnModel().getColumn(3).setMaxWidth(62);
        webcamDevTable.getColumnModel().getColumn(4).setMinWidth(150);
        webcamDevTable.getColumnModel().getColumn(5).setMaxWidth(60);
    }

    private void getSoundDevices(){

        String command = "ls -l /sys/class/sound/ | grep /";

        String lines[] = ScriptExecutor.execScript(command).split("\n");

        command = "cat /proc/asound/devices";

        String sndDevs[] = ScriptExecutor.execScript(command).split("\n");

        Object columnNames[] = {"BUS", "DEVICE", "NAME", "IN USAGE", "DEV_FILE", "PID", "PROCESS", "USER"};
        ArrayList<String[]> data = new ArrayList<>();

        for(String line : lines) {

            if(line.endsWith("card0")) continue;

            String out[] = line.split("\\s+");

            String bus = "-";
            String dev = "-";
            String name = "";

            String devTemp = out[out.length - 3];
            if(devTemp.equals("seq")){
                name = "sequencer";
            } else if(devTemp.equals("timer")){
                name = "timer";
            } else if(devTemp.endsWith("c")){
                name = "digital audio capture";
            } else if(devTemp.endsWith("p")){
                name = "digital audio playback";
            } else if(devTemp.contains("control")){
                name = "control";
            }

            String devFile = "/dev/snd/" + out[out.length - 3];

            ArrayList<String[]> processes = getDeviceUsage(devFile);

            for (String[] process : processes) {
                String device[] = {bus, dev, name, "YES", devFile, process[1], process[0], process[2]};
                data.add(device);
            }

            if (processes.size() == 0) {
                String device[] = {bus, dev, name, "NO", devFile, null, null, null};
                data.add(device);
            }
        }

        Object[][] dataTable = new String[data.size()][];
        dataTable = data.toArray(dataTable);
        TableModel model = new DefaultTableModel(dataTable, columnNames);
        soundDevTable.setModel(model);
        soundDevTable.getColumnModel().getColumn(0).setMaxWidth(35);
        soundDevTable.getColumnModel().getColumn(1).setMaxWidth(50);
        soundDevTable.getColumnModel().getColumn(2).setMinWidth(400);
        soundDevTable.getColumnModel().getColumn(3).setMaxWidth(62);
        soundDevTable.getColumnModel().getColumn(4).setMinWidth(150);
        soundDevTable.getColumnModel().getColumn(5).setMaxWidth(60);
    }

    private ArrayList<String[]> getDeviceUsage(String devFilePath){

        String command = null;

        command = "lsof " + devFilePath + " | grep " + devFilePath;

        String lines[] = ScriptExecutor.execScript(command).split("\n");

        ArrayList<String[]> output = new ArrayList<>();

        String[] temp = new String[3];
        for(String line : lines) {

            try {

                String out[] = line.split("\\s+");

                String cmd = out[0];
                String pid = out[1];
                String user = out[2];

                String[] process = {cmd, pid, user};
                if(!process[0].equals(temp[0]) && !process[1].equals(temp[1]) && !process[2].equals(temp[2])){
                    output.add(process);
                }
                temp[0] = process[0];
                temp[1] = process[1];
                temp[2] = process[2];

            } catch (Exception e){}
        }

        return output;
    }
}
