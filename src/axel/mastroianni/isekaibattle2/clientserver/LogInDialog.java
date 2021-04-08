package axel.mastroianni.isekaibattle2.clientserver;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LogInDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JLabel hostLabel = new JLabel("Host: ");
	private JLabel nameLabel = new JLabel("UserName: ");
	
	private JTextField hostField = new JTextField(10);
	private JTextField nameField = new JTextField(10);
	
	private boolean canceled = false;
	
	private JButton okButton = new JButton("Ok");
	
	public LogInDialog() {
		setTitle("Log in Isekai");
		
		initGUI();
		
		setTitle("Log in Dialog");
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel,BorderLayout.CENTER);
		
		mainPanel.add(hostLabel);
		hostField.setText("localhost");
		hostField.setEditable(false);
		mainPanel.add(hostField);
		
		mainPanel.add(nameLabel);
		mainPanel.add(nameField);
		
		//button panel
		JPanel buttonPanel = new JPanel();
		okButton.addActionListener(e -> ok());
		buttonPanel.add(okButton);
		mainPanel.add(buttonPanel);
		
		//listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
	}
	
	private void ok() {
		canceled = false;
		String host = hostField.getText().trim();
		String name = nameField.getText().trim();
		if(host.length() == 0) {
			JOptionPane.showMessageDialog(null, "You must enter the host ip address");
		}
		else if(name.length() == 0) {
			JOptionPane.showMessageDialog(null, "You must enter the user name");
		}
		else {
			canceled = false;
			setVisible(false);
		}
	}
	
	private void cancel() {
		canceled = true;
		setVisible(false);
	}
	
	public String getHostName() {
		return hostField.getText().trim();
	}
	
	public String getUserName() {
		return nameField.getText().trim();
	}
	
	public boolean isCanceled() {
		return canceled;
	}

}
