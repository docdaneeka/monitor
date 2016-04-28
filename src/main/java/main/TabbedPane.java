package main;

import deviceScan.DeviceScanPane;
import networkScan.NetworkScanPane;
import systemFileScan.SystemFileScanPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
        setSize( 800, 500 );
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
        tabbedPane.addTab( "Network", panel1 );
        tabbedPane.addTab( "Device usage", panel2 );
        tabbedPane.addTab( "System log", panel3 );
        topPanel.add( tabbedPane, BorderLayout.CENTER );
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == 2){
                	SystemFileScanPane fileScanPane = (SystemFileScanPane)panel3;
                    fileScanPane.activate();
                } else if(tabbedPane.getSelectedIndex() == 1){
                    DeviceScanPane deviceScanPane = (DeviceScanPane) panel2;
                    deviceScanPane.activate();
                } else {
                	if(tabbedPane.getSelectedIndex() == 0){
                		NetworkScanPane networkScanPane = (NetworkScanPane)panel1;
                		networkScanPane.monitorPorts();
                	}
                }
            }
        });
    }
}