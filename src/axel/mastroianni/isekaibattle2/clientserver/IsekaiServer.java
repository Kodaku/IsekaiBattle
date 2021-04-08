package axel.mastroianni.isekaibattle2.clientserver;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

public class IsekaiServer extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private static final int PORT_NUMBER = 44444;
	
	private static final String FILE_NAME = "LogFile.txt";
	
	private JTextArea textArea = new JTextArea(20, 30);
	
	private JButton startStopButton = new JButton("Start");
	
	private ServerSocket server;
	
	private boolean listening = false;
	
	private ArrayList<String> names = new ArrayList<String>();
	
	private ArrayList<Connection> connections = new ArrayList<>();
	
	private ArrayList<Player> players = new ArrayList<>();
	
	public IsekaiServer() {
		initGUI();
		
		setTitle("Isekai Server");
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		
	}
	
	private void initGUI() {
		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel, BorderLayout.CENTER);
		
		//text area
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		mainPanel.add(scrollPane);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//button panel
		JPanel buttonPanel = new JPanel();
		startStopButton.addActionListener(e -> startServer());
		buttonPanel.add(startStopButton);
		add(buttonPanel, BorderLayout.PAGE_END);
		getRootPane().setDefaultButton(startStopButton);
		
		//listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopServer();
				System.exit(0);
			}
		});
	}
	
	public void run() {
		log("The server is running");
		try {
			server = new ServerSocket(PORT_NUMBER);
			while(listening) {
				Socket socket = server.accept();
				Connection connection = new Connection(this, socket);
				connections.add(connection);
			}
		}catch(IOException e) {
			if(listening) {
				log("Exception caught while listening to port " + PORT_NUMBER);
				log(e.getMessage());
				stopServer();
			}
		}
	}
	
	private void startServer() {
		if(!listening) {
			startStopButton.setText("Stop");
			listening = true;
			new Thread(this).start();
		}
		else {
			stopServer();
		}
	}
	
	private void stopServer() {
		listening = false;
		startStopButton.setText("Start");
		names.clear();
		
		log("Stopping the server");
		if(server != null && !server.isClosed()) {
			try {
				server.close();
				writeLogFile();//after the end of connection write all the text in the text area
				//on a log file
			}catch(IOException e) {
				log("Cannot close the server");
				log(e.getMessage());
			}
		}
	}
	
	public void log(String message) {
		Date time = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String timeStamp = dateFormat.format(time);
		textArea.append(timeStamp + " " + message + "\n");
	}
	
	/**
	 * does the name logged in already exist?
	 * @param name
	 * @return
	 */
	public boolean addedName(String name) {
		for(int i = 0; i < names.size(); i++) {
			if(name.equals(names.get(i))) {
				return false;
			}
		}
		names.add(name);
		return true;
	}
	
	private void writeLogFile() {
		try {
			File file = new File(FILE_NAME);
			if(file.exists()) {
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				String line = textArea.getText();
				out.write(line);
				out.close();
			}
			else {
				file.createNewFile();
				writeLogFile();
			}
		}catch(IOException e) {}
	}
	
	public ArrayList<String> getNames(){
		return names;
	}
	
	public void setNames(ArrayList<String> names) {
		this.names = names;
	}
	
	public void addName(String name) {
		names.add(name);
	}
	
	public void broadcast(String sender, Packet packet) {
		for(int i = 0; i < 2; i++) {
			try {
				Connection connection = connections.get(i);
				if(!connection.getName().equals(sender)) {
					connections.get(i).sendToClient(packet);
				}
			}catch(NullPointerException e) {
				System.out.println("There's no one to close...you were alone");
			}
		}
	}
	
	public void quitAll(String name) {
		for(int i = 0; i < 2; i++) {
			if(!connections.get(i).getName().equals(name)) {
				connections.get(i).quit();
			}
		}
	}
	
	public void remove(String name) {
		names.remove(name);
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public ArrayList<Player> getPlayers(){
		return players;
	}
	
	public void setEnemys() {
		players.get(0).setEnemy(players.get(1));
		players.get(1).setEnemy(players.get(0));
	}
	

	public static void main(String[] args) {
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		}catch(Exception e) {}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new IsekaiServer();
			}
		});
	}

}
