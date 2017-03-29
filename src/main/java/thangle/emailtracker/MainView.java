package thangle.emailtracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.apache.commons.validator.routines.EmailValidator;

import thangle.emailtracker.utils.FileObjectStream;
import thangle.emailtracker.api.*;

public class MainView {
    private static final File DATA_FILE = new File(
            System.getProperty("user.home"), ".credentials/email-tracker/email_list");
    
    // email data
    private HashSet<String> myEmailList;
    private DefaultListModel<File> attachments;
    
    private JFrame frame;
    private JTextArea recipients, body;
    private JTextField subject;
    private JButton sendBtn, plusBtn, minusBtn, attachBtn;
    
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
        else myEmailList = new HashSet<String>();
        
        attachments = new DefaultListModel<>();
        
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // set up elements
        frame = new JFrame();
        frame.setBounds(100, 100, 1080, 800);
        frame.getContentPane().setLayout(new GridLayout(1, 3));
        
        subject = new JTextField();
        subject.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        recipients = makeWrapTextArea(0, 0);
        body = makeWrapTextArea(20, 5);
        
        // initialize necessary buttons
        sendBtn = new JButton("Send");
        attachBtn = new JButton("Attach files");
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
            
            if(checkValidAddress(s)) {
                if (!myEmailList.contains(s)) {
                    myEmailList.add(s);
                    emailListModel.addElement(s);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "The email address is not valid!");
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
        
        /*
         * Send email to an address.
         */
        sendBtn.addActionListener(e -> {
            String from = emailList.getSelectedValue();
            String to = recipients.getText();
            String subjectText = subject.getText();
            String bodyText = body.getText();            
            
            // get the attachments
            File[] files = new File[attachments.size()];
            for (int i = 0; i < attachments.size(); i++) {
                files[i] = attachments.getElementAt(i);
            }
                    
            MessageService.sendMessage(from, to, subjectText, bodyText, files);
        });
        
        /*
         * Attach file to email.
         */
        attachBtn.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(frame);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                attachments.addElement(file);
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
    
    /**
     * Build the email composing form.
     * @return the email composing panel
     */
    private JPanel buildFormPanel() {
        FormLayout comLayout = new FormLayout(
                "pref, 3dlu, pref:grow, 3dlu",
                "pref, 3dlu, pref, 3dlu, default, 3dlu, default, 3dlu, pref");
        PanelBuilder comBuilder = new PanelBuilder(comLayout);
        CellConstraints cc = new CellConstraints();
        
        // add form fields
        comBuilder.addLabel("To:", cc.xy(1, 1, "left, top"));
        comBuilder.add(recipients, cc.xy(3, 1));
        comBuilder.addLabel("Subject:", cc.xy(1, 3, "left, center"));
        comBuilder.add(subject, cc.xy(3, 3));
        comBuilder.addLabel("Body:", cc.xy(1, 5, "left, top"));
        comBuilder.add(body, cc.xy(3, 5));
        
        comBuilder.addLabel("Attachments:", cc.xy(1, 7, "left, top"));
        
        // create the scroll pane for displaying attachments
        JList<File> attachmentList = new JList<>(attachments);
        attachmentList.setLayoutOrientation(JList.VERTICAL);
        attachmentList.setCellRenderer(new FileListRenderer());
        JScrollPane scrollPane = new JScrollPane(attachmentList);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        
        comBuilder.add(scrollPane, cc.xy(3, 7));
        
        // add button 
        JPanel btnPane = new JPanel();
        btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
        btnPane.add(attachBtn);
        btnPane.add(sendBtn);
        
        comBuilder.add(btnPane, cc.xy(3, 9, "right, center"));
        
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
        area.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
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
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }
    
    /**
     * Check if the email address is syntactically correct.
     * Support gmail only.
     * @param address the address to be checked 
     * @return whether the email address is syntactically correct or not
     */
    private boolean checkValidAddress(String address) {
        EmailValidator eValidator = EmailValidator.getInstance(false);
        
        // check if the address has valid format 
        if (!eValidator.isValid(address))
            return false;
        
        // check if the domain name is gmail
        String domain = address.substring(address.lastIndexOf('@')+1);
        if (!domain.equals("gmail.com")) {
            return false;
        }
        
        return true;
    }
    
    private class FileListRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel l = (JLabel)c;
            File f = (File)value;
            l.setText(f.getName());
            l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
            
            return l;
        }
    }
}
