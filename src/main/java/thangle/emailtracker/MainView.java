package thangle.emailtracker;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
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

import thangle.emailtracker.utils.FileObjectStream;

public class MainView {
    private static final File DATA_FILE = new File(
            System.getProperty("user.home"), ".credentials/email-tracker/email_list");
    
    // email data
    private HashSet<String> myEmailList;
    
    private JFrame frame;
    private JTextArea recipients, body;
    private JTextField subject;
    private JButton sendBtn, plusBtn, minusBtn;
    
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
        // load store email list
        if (DATA_FILE.exists()) {
            @SuppressWarnings("unchecked")
            HashSet<String> tmp = (HashSet<String>)FileObjectStream.readData(DATA_FILE);
            myEmailList = tmp;
        }
        
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
        
        // initialize necessary buttons
        sendBtn = new JButton("Send");
        plusBtn = makeSquareButton("+", 30);
        minusBtn = makeSquareButton("-", 30);
        
        // Cell 1: panel for displaying email list
        
        /*
         * Retrieve the email data list and make a DefaultListModel, which can
         * automatically update view when changed.
         */
        DefaultListModel<String> emailListModel = new DefaultListModel<>();
        
        myEmailList.forEach(elt -> {
            emailListModel.addElement(elt);
        });
        
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
        
        JList<String> emailList = new JList<String>(emailListModel);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(emailList);
        
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPane.add(plusBtn);
        btnPane.add(minusBtn);
        
        listPane.add(scrollPane);
        listPane.add(btnPane);
        
        // Cell 2: create compose email form
        JPanel comPane = buildFormPanel();
        
        // event handlers
        
        /*
         * When plus button is clicked, ask user for a new email address
         * they want to add. Then, store that new email address in the data file.
         */
        plusBtn.addActionListener(event -> {
            String s = (String)JOptionPane.showInputDialog(
                                    frame,
                                    "Enter an email address:",
                                    "Add email address",
                                    JOptionPane.PLAIN_MESSAGE);
            
            if (!myEmailList.contains(s)) {
                myEmailList.add(s);
                emailListModel.addElement(s);
            }
        });
        
        /*
         * Remove the email that is selected.
         */
        minusBtn.addActionListener(e -> {
            String selectedEmail = emailList.getSelectedValue();
            int selectedIndex = emailList.getSelectedIndex();
            
            if(selectedEmail != null) {
                emailListModel.remove(selectedIndex);
                myEmailList.remove(selectedEmail);
            }
        });
        
        // add components to frame
        frame.getContentPane().add(listPane);
        frame.getContentPane().add(comPane);
        
        // save data when frame is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                FileObjectStream.writeData(DATA_FILE, myEmailList);
            }
        });
    }
    
    private JPanel buildFormPanel() {
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
        
        return comPane;
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
     * @param dim width of the button
     * @return a square JButton
     */
    private JButton makeSquareButton(String text, int dim) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(dim, dim));
        return btn;
    }
}
