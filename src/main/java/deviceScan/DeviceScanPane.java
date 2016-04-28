package deviceScan;

import main.ScriptExecutor;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by alex on 20.04.16.
 */
public class DeviceScanPane extends JPanel {

    private JTextArea authlogTextArea;
    JScrollPane scroll_bars;
    private Thread scanThread;
    
    public DeviceScanPane(){

        this.setLayout(null);
        JLabel label1 = new JLabel( "Device usage scanner:" );
        label1.setBounds( 10, 15, 150, 20 );
        this.add(label1);
        authlogTextArea = new JTextArea(15,18);
        authlogTextArea.setLineWrap(true);
        authlogTextArea.setWrapStyleWord(true);
        scroll_bars = new JScrollPane(
                authlogTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll_bars.setBounds( 10, 35, 760, 390 );
        this.add(scroll_bars);
    }

    public void activate(){
        scanThread = new Thread(new Runnable(){

            public void run() {
                while(true) {

                    getDeviceUsage();

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
        authlogTextArea.setText("Scan started on : [" + sdf.format(cal.getTime()) + "].");
        scanThread.start();
    }

    private void getDeviceUsage(){

        String command = null;

        command = "lsof | grep /dev/";

        String lines[] = ScriptExecutor.execScript(command).split("\n");

        for(String line : lines) {

//            System.out.println(line);

            String out[] = line.split("\\s+");

            String cmd = out[0];
            String pid = out[1];
            String user = out[2];
            String fd = out[3];
            String type = out[4];
            String device = out[5];
            String size = out[6];
            String node = out[7];
            String name = out[8];

            authlogTextArea.append("\n" + cmd + "\t" + user + "\t" + pid + "\t" + name);
            authlogTextArea.setCaretPosition(authlogTextArea.getDocument().getLength());
        }
    }
}
