package eu.asterics.component.actuator.event_visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * Date: 1/9/11
 * Time: 11:37 AM
 */
public class TestGUI
{
    public static void main(String[] args)
    {
        final VisualizerGUI visualizerGUI = new VisualizerGUI();
        visualizerGUI.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try { Thread.currentThread().sleep(1000); } catch (InterruptedException ie) {}
                    visualizerGUI.addEvent("hi!");
                }
            }
        }).start();
    }
}