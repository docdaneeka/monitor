package networkScan;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;

import main.ScriptExecutor;

/**
 * Created by alex on 20.04.16.
 */
public class NetworkScanPane extends JPanel {

    private static Thread port_monitor;
    static JTextArea portTextField;
	public NetworkScanPane(){
        this.setLayout(null);

//        JLabel label1 = new JLabel( "Username:" );
//        label1.setBounds( 10, 15, 150, 20 );
//        this.add(label1);
//
//        JTextField field = new JTextField();
//        field.setBounds( 10, 35, 150, 20 );
//        this.add(field);
//
//        JLabel label2 = new JLabel( "Password:" );
//        label2.setBounds( 10, 60, 150, 20 );
//        this.add(label2);
//
//        JPasswordField fieldPass = new JPasswordField();
//        fieldPass.setBounds( 10, 80, 150, 20 );
//        this.add( fieldPass );
        
        JLabel label3 = new JLabel( "Otwarte porty:" );
        label3.setBounds( 10, 15, 150, 20 );
        this.add(label3);
        
        portTextField = portTextField = new JTextArea ();
        portTextField.setBounds( 10, 50, 800, 400 );
        portTextField.setEnabled(false);
		this.add(portTextField);
    }
    
    public void monitorPorts () {
    	
    	final String command = "netstat -lnt -p tcp";
    	port_monitor = new Thread(new Runnable(){

            public void run() {
            	int number_of_ports = 0;
            	int n_ports;
            	String netstat_return = ScriptExecutor.execScript (command);
            	String [] fragmented = netstat_return.split("\n");
            	number_of_ports = fragmented.length; //ilo�� otwartych port�w na pocz�tku
            	System.out.println("Dziala");
                while(true) {
                	netstat_return = ScriptExecutor.execScript (command);
                	fragmented = netstat_return.split("\n");
                	n_ports = fragmented.length;
                	if (n_ports > number_of_ports) {
                		//zosta� otwarty nowy port. Doda� oflagowanie
                		number_of_ports = n_ports;
                	}
                	String textAreaContent = "";

					int i=0;
                	for (String s : fragmented) {
						i++;
						if(i==1 || i==2) continue;
                		String [] s2 = s.split("\\s+");
                		String [] s3 = s2[3].split(".");
//						System.out.println(s);
                		textAreaContent = textAreaContent + s+ "\n ";
                	}
                	portTextField.setText(textAreaContent);
                	
                	try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }     
            }
        });
    	port_monitor.start();
    }
    
    public static void scanHTTP (String network_interface) {
		
		String command = null;
		
		command = "ettercap -T -i "+network_interface+" | while read input; do \n"
				+ "if [[$input == *\"HTTP\"*]] || [[$input == *\"FTP\"*]] || [[$input == *\"TELNET\"*]]\n"
				+ "then \n"
				+ "echo $input \n"
				+ "fi \n"
				+ "done \n"
				+ "done";
		//gksudo albo doda� ettercap to wyj�tk�w
			ScriptExecutor.execScript (command);
	}
}
