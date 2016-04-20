package systemFileScan;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alex on 20.04.16.
 */
public class SystemFileScanPane extends JPanel{
    
    public SystemFileScanPane(){
        this.setLayout( new GridLayout( 3, 2 ) );

        this.add( new JLabel( "Field 1:" ) );
        this.add( new TextArea() );
        this.add( new JLabel( "Field 2:" ) );
        this.add( new TextArea() );
        this.add( new JLabel( "Field 3:" ) );
        this.add( new TextArea() );
    }
}
