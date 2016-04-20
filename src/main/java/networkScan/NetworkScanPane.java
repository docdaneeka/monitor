package networkScan;

import javax.swing.*;

/**
 * Created by alex on 20.04.16.
 */
public class NetworkScanPane extends JPanel {

    public NetworkScanPane(){
        this.setLayout(null);

        JLabel label1 = new JLabel( "Username:" );
        label1.setBounds( 10, 15, 150, 20 );
        this.add(label1);

        JTextField field = new JTextField();
        field.setBounds( 10, 35, 150, 20 );
        this.add(field);

        JLabel label2 = new JLabel( "Password:" );
        label2.setBounds( 10, 60, 150, 20 );
        this.add(label2);

        JPasswordField fieldPass = new JPasswordField();
        fieldPass.setBounds( 10, 80, 150, 20 );
        this.add( fieldPass );
    }
}
