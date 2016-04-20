package deviceScan;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alex on 20.04.16.
 */
public class DeviceScanPane extends JPanel {
    
    public DeviceScanPane(){
        
        this.setLayout( new BorderLayout() );

        this.add( new JButton( "North" ), BorderLayout.NORTH );
        this.add( new JButton( "South" ), BorderLayout.SOUTH );
        this.add( new JButton( "East" ), BorderLayout.EAST );
        this.add( new JButton( "West" ), BorderLayout.WEST );
        this.add( new JButton( "Center" ), BorderLayout.CENTER );
    }
}
