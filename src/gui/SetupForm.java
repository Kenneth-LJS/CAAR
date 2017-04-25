package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.eventlisteners.SetupFormOnCloseListener;
import gui.eventlisteners.SetupFormOnOkListener;
import utils.GraphUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetupForm extends JFrame {

    private JPanel contentPane;
    private boolean clickedOk = false;
    private List<SetupFormOnOkListener> onOkListeners = new ArrayList<SetupFormOnOkListener>();
    private List<SetupFormOnCloseListener> onCloseListeners = new ArrayList<SetupFormOnCloseListener>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SetupForm frame = new SetupForm();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public SetupForm() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent arg0) {
                if (!clickedOk) {
                    for (SetupFormOnCloseListener listener : onCloseListeners) {
                        listener.update();
                    }
                }
            }
        });
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
    }

}
