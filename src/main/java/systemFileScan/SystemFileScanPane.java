package systemFileScan;

import main.ScriptExecutor;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by alex on 20.04.16.
 */
public class SystemFileScanPane extends JPanel{

    private JTextArea authlogTextArea;
    private Thread scanThread;
    public SystemFileScanPane(){
        this.setLayout(null);
        JLabel label1 = new JLabel( "System log scanner:" );
        label1.setBounds( 10, 15, 150, 20 );
        this.add(label1);
        authlogTextArea = new JTextArea();
        authlogTextArea.setBounds( 10, 35, 600, 200 );
//        JScrollPane scroll = new JScrollPane (authlogTextArea,
       //         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  //      scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        this.add(authlogTextArea);



    }

    public void activate(){
        scanThread = new Thread(new Runnable(){

            public void run() {
                while(true) {
                    String failedLoginAttempts = ScriptExecutor.execScript("sudo grep 'authentication failure' /var/log/auth.log");

                    if(failedLoginAttempts.length() != 0){
                        String[] split = failedLoginAttempts.split("\n");
                        for(String s : split){
                            String date = s.substring(0,15);
                            String[] split2 = s.split("user=");
                            String user = split2[1];
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                            authlogTextArea.append("sdf.format(cal.getTime())" + "  ::  " + "Failed password @ user : " + user + ", tried to access at + " + date + ".");
                     //       authlogTextArea.append(date + ", failed password @ user : " + user +".");
                        }
                    }
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
}
