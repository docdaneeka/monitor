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
    private static Thread package_monitor;
    static JTextArea packageTextField;
    static JTextArea msgTextField;
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
        
        JLabel label31 = new JLabel( "Ports:" );
        label31.setBounds( 10, 30, 150, 20 );
        this.add(label31);
        JLabel label32 = new JLabel( "State:" );
        label32.setBounds( 97, 30, 150, 20 );
        this.add(label32);
        JLabel label33 = new JLabel( "PID:" );
        label33.setBounds( 185, 30, 150, 20 );
        this.add(label33);
        JLabel label34 = new JLabel( "Program:" );
        label34.setBounds( 275, 30, 150, 20 );
        this.add(label34);
        JLabel label4 = new JLabel( "Messages:" );
        label4.setBounds( 450, 30, 150, 20 );
        this.add(label4);
        JLabel label5 = new JLabel( "Packages:" );
        label5.setBounds( 160, 275, 150, 20 );
        this.add(label5);
        
        portTextField = new JTextArea ();
        portTextField.setBounds( 10, 50, 400, 200 );
        portTextField.setEnabled(false);
		this.add(portTextField);
		
		msgTextField = new JTextArea ();
		msgTextField.setBounds( 450, 50, 600, 200 );
		msgTextField.setEnabled(false);
		this.add(msgTextField);
		
		packageTextField = new JTextArea ();
		packageTextField.setBounds( 160, 300, 600, 150 );
		packageTextField.setEnabled(false);
		this.add(packageTextField);
    }
    
    public void monitorPorts () {
    	
    	final String command = "netstat -lnt -p tcp";
    	port_monitor = new Thread(new Runnable(){

            public void run() {
            	int number_of_ports = 0;
            	boolean flag_inc = false;
            	boolean flag_dec=false;
            	int n_ports;
            	String netstat_return;// = ScriptExecutor.execScript (command);
            	String [] fragmented;// = netstat_return.split("\n");
            	//number_of_ports = fragmented.length; //ilo�� otwartych port�w na pocz�tku
            	String textAreaContent = "";
            	String new_text = "";
            	System.out.println("Dziala");
                while(true) {
                	netstat_return = ScriptExecutor.execScript (command);
                	fragmented = netstat_return.split("\n");
                	n_ports = fragmented.length;
                	if (n_ports != number_of_ports) {
                		if (n_ports > number_of_ports) {
                			System.out.println("Otwarto nowe porty");
                			msgTextField.setText("Otwarto nowe porty");
                			flag_inc = true;
                		} else {
                			System.out.println("Zamknięto porty");
                			msgTextField.setText("Zamknięto porty");
                			flag_dec = true;
                		}               		
                		number_of_ports = n_ports;
                	} //else zamknięto else reszta
                	if (flag_inc || flag_dec) {
                		int i=0;
                		new_text = "";
                    	for (String s : fragmented) {
    						i++;
    						if(i==1 || i==2) continue;
                    		String [] s2 = s.split("\\s+");
                    		String [] s3 = s2[3].split(":");
							String prog = "";
							try{
								prog = s2[6].split("/")[1];
							} catch (Exception e){}
                    		new_text = new_text + s3[s3.length-1]+ "\t" +s2[5] + "\t" + s2[6].split("/")[0] + "\t" + prog +"\n";
                    	}
                    	String [] spl = new_text.split(textAreaContent);
                    	if (textAreaContent == "" || flag_dec) {
                    		textAreaContent = new_text;
                    		portTextField.setText(textAreaContent);
                    	} else {
                    		System.out.println(spl[0].split("\t")[0]);
                    		msgTextField.setText("Otwarto nowe porty\n"+spl[0].split("\t")[0]);
                    		textAreaContent = textAreaContent + spl[0]+"\n";
                    		portTextField.setText(textAreaContent);
                    	}
                    	flag_inc = flag_dec = false;
                	} else {
                		portTextField.setText(textAreaContent);
                	}        	
                	
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
				
		/*final String command = "sudo ettercap -T -i "+network_interface+" | while read input; do \n"
				+ "if [[$input == *\"HTTP\"*]] || [[$input == *\"FTP\"*]] || [[$input == *\"TELNET\"*]]\n"
				+ "then \n"
				+ "echo $input \n"
				+ "fi \n"
				+ "done \n"
				+ "done";*/
		final String command = "ls -l";
		//gksudo albo doda� ettercap to wyj�tk�w
		package_monitor = new Thread(new Runnable(){

            public void run() {
            	String ettercap_return = ScriptExecutor.execScript (command);
                while(true) {
                	ettercap_return = ScriptExecutor.execScript (command);
                	
                	String textAreaContent = ettercap_return;
                	textAreaContent = textAreaContent + "\n";
                	packageTextField.setText(textAreaContent);
                	try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }     
            }
        });
		package_monitor.start();
	}
}
