package thangle.emailtracker;

import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MainView {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainView window = new MainView();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainView() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 800);
        frame.getContentPane().setLayout(new GridLayout(1, 3));
        
        // display email list
        String[] data = {"Thang", "Dat"};
        JList list = new JList(data);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        
        // create compose email form
        FormLayout comLayout = new FormLayout(
            "pref, 3dlu, default:grow, 3dlu",
            "pref, 3dlu, default");
        PanelBuilder comBuilder = new PanelBuilder(comLayout);
        CellConstraints cc = new CellConstraints();
        
        comBuilder.addLabel("Subject", cc.xy(1, 1, "left, center"));
        comBuilder.add(new JTextField(), cc.xy(3, 1));
        comBuilder.addLabel("Body", cc.xy(1, 3, "left, top"));
        JTextArea area = new JTextArea(10, 5);
        comBuilder.add(area, cc.xy(3, 3));
        
        JPanel comPane = comBuilder.getPanel();
        comPane.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        
        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(comBuilder.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
