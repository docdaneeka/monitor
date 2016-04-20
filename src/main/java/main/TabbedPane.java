package main;

import deviceScan.DeviceScanPane;
import networkScan.NetworkScanPane;
import systemFileScan.SystemFileScanPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alex on 20.04.16.
 */
class TabbedPane extends JFrame
{
    private		JTabbedPane tabbedPane;
    private		JPanel		panel1;
    private		JPanel		panel2;
    private		JPanel		panel3;

    public TabbedPane()
    {
        setTitle( "Monitoring cyber-zagrozen" );
        setSize( 600, 500 );
        setBackground( Color.gray );

        JPanel topPanel = new JPanel();
        topPanel.setLayout( new BorderLayout() );
        getContentPane().add( topPanel );

        // Create the tab pages
        panel1 = new NetworkScanPane();
        panel2 = new DeviceScanPane();
        panel3 = new SystemFileScanPane();

        // Create a tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Page 1", panel1 );
        tabbedPane.addTab( "Page 2", panel2 );
        tabbedPane.addTab( "Page 3", panel3 );
        topPanel.add( tabbedPane, BorderLayout.CENTER );
    }
}