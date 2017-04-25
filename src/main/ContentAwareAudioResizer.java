package main;

import java.awt.EventQueue;

import gui.SetupForm;

public class ContentAwareAudioResizer {

    public void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SetupForm frame = new SetupForm();
                    // todo: add listeners
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}
