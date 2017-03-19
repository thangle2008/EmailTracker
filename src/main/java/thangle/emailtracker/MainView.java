package thangle.emailtracker;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MainView {

    private JFrame frame;
    private JTextArea recipients, body;
    private JTextField subject;
    
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
        // set up elements
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 800);
        frame.getContentPane().setLayout(new GridLayout(1, 3));
        
        subject = new JTextField();
        recipients = makeWrapTextArea(0, 0);
        body = makeWrapTextArea(10, 5);
        
        // create necessary buttons
        JButton sendBtn = new JButton("Send");
        JButton plusBtn = makeSquareButton("+", 30, 30);
        JButton minusBtn = makeSquareButton("-", 30, 30);
        
        // Cell 1: panel for displaying email list
        String[] data = {"Thang", "Dat"};
        
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        
        JList<Object> list = new JList<Object>(data);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(plusBtn);
        btnPane.add(minusBtn);
        
        listPane.add(scrollPane);
        listPane.add(btnPane);
        
        // Cell 2: create compose email form
        FormLayout comLayout = new FormLayout(
            "pref, 3dlu, pref:grow, 3dlu",
            "pref, 3dlu, pref, 3dlu, default, 3dlu, pref");
        PanelBuilder comBuilder = new PanelBuilder(comLayout);
        CellConstraints cc = new CellConstraints();
        
        comBuilder.addLabel("To:", cc.xy(1, 1, "left, top"));
        comBuilder.add(recipients, cc.xy(3, 1));
        comBuilder.addLabel("Subject:", cc.xy(1, 3, "left, center"));
        comBuilder.add(subject, cc.xy(3, 3));
        comBuilder.addLabel("Body:", cc.xy(1, 5, "left, top"));
        comBuilder.add(body, cc.xy(3, 5));
        comBuilder.add(sendBtn, cc.xy(3, 7, "right, center"));
        
        JPanel comPane = comBuilder.getPanel();
        comPane.setBorder(new EmptyBorder(10, 10, 0, 0));
        
        // event handlers
        plusBtn.addActionListener(e -> {
            String s = (String)JOptionPane.showInputDialog(
                                    frame,
                                    "Enter an email address:",
                                    "Add email address",
                                    JOptionPane.PLAIN_MESSAGE);
            System.out.println(s);
        });
        
        // add components to frame
        frame.getContentPane().add(listPane);
        frame.getContentPane().add(comBuilder.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Make a text area that will wrap around words.
     * @param col number of columns
     * @param row number of rows
     * @return a JTextArea that wraps around words
     */
    private JTextArea makeWrapTextArea(int col, int row) {
        JTextArea area;
        if(row == 0) {
            area = new JTextArea();
        } else {
            area = new JTextArea(col, row);
        }
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        
        return area;
    }
    
    /**
     * Make a square button (for ex, add + and remove - buttons).
     * @param text the text displayed on the button
     * @param w width of the button
     * @param h height of the button
     * @return a square JButton
     */
    private JButton makeSquareButton(String text, int w, int h) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }
}
