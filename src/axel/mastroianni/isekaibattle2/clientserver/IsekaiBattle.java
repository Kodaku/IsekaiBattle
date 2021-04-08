package axel.mastroianni.isekaibattle2.clientserver;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.MenuStates;
import axel.mastroianni.isekaibattle2.menu.BackgroundChoose;
import axel.mastroianni.isekaibattle2.menu.ConfirmPanel;
import axel.mastroianni.isekaibattle2.menu.GamePanel;
import axel.mastroianni.isekaibattle2.menu.IntroWindow;
import axel.mastroianni.isekaibattle2.menu.Menu;
import axel.mastroianni.isekaibattle2.menu.PlayModePanel;
import axel.mastroianni.isekaibattle2.menu.PlayerChoose;
import axel.mastroianni.isekaibattle2.menu.TutorialPanel;
import axel.mastroianni.isekaibattle2.menu.WaitingPanel;

public class IsekaiBattle extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private static final int PORT_NUMBER = 44444;
	
	private LogInDialog logIn;
	
	private String name;
	private String host;
	
	private String opponentName;
	private String bgName;
	
	private boolean keepRunning;
	
	private Socket socket;
	
	private BufferedReader in;
	
	private PrintWriter out;
	
	private GamePanel gamePanel;
	
	private IntroWindow introWindow;
	
	private PlayModePanel playModePanel;
	
	private WaitingPanel waitingPanel;
	
	private Menu menu;
	
	private TutorialPanel tutorial;
	
	private PlayerChoose playerChoose;
	
	private BackgroundChoose backgroundChoose;
	
	private ConfirmPanel confirmPanel;
	
	private JPanel mainPanel;
	
	private int state = 0;
	
	private int[] IDs = new int[2];
	
	private int backgroundId = 0;
	private int playerID = 0;
	
	private int playerCount;
	
	private String[] names = new String[2];
	
	private boolean isMultiplayer = false;
	
	private KeyListener keyListener;
	
	public IsekaiBattle() {
		logIn = new LogInDialog();
		logIn();
		new Thread(this).start();
	}
	
	private void initGUI(){
		switch(state) {
		case MenuStates.STATE_COVER:{
			introWindow = new IntroWindow(this);
			mainPanel.add(introWindow);
			mainPanel.setFocusable(true);
			break;
		}
		case MenuStates.STATE_PLAY_MODE:{
			playModePanel = new PlayModePanel(this);
			mainPanel.add(playModePanel);
			break;
		}
		case MenuStates.STATE_WAITING:{
			waitingPanel = new WaitingPanel();
			mainPanel.add(waitingPanel);
			break;
		}
		case MenuStates.STATE_MENU:{
			menu = new Menu(this);
			mainPanel.add(menu);
			break;
		}
		case MenuStates.STATE_TUTORIAL:{
			tutorial = new TutorialPanel(this);
			mainPanel.add(tutorial);
			break;
		}
		case MenuStates.STATE_PLAYER_CHOOSE:{
			playerChoose = new PlayerChoose(this);
			mainPanel.add(playerChoose);
			break;
		}
		case MenuStates.STATE_BACKGROUND_CHOOSE:{
			backgroundChoose = new BackgroundChoose(this);
			mainPanel.add(backgroundChoose);
			break;
		}
		case MenuStates.STATE_CONFIRM:{
			confirmPanel = new ConfirmPanel(this, opponentName, bgName);
			mainPanel.add(confirmPanel);
			break;
		}
		case MenuStates.STATE_PLAY:{
			gamePanel = new GamePanel(this, IDs,names, backgroundId, playerID);
			mainPanel.add(gamePanel);
			mainPanel.requestFocusInWindow();
			keyListener = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCode = e.getKeyCode();
//					Packet packet = new Packet(ActionCode.KEY_PRESSED);
//					packet.add(keyCode);
//					out.println(packet);
					gamePanel.convertInState(keyCode);
				}
			};
			mainPanel.addKeyListener(keyListener);
			gamePanel.start();
			break;
		}
		}
		
		mainPanel.revalidate();
		
		//listeners
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
	}

	public void run() {
		keepRunning = true;
		try {
			socket = new Socket(host, PORT_NUMBER);//connecting to the server
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			while(keepRunning) {
				String input = in.readLine();
				if(input == null) {
					keepRunning = false;
				}
				else {
					Packet packet = new Packet(input);
					String actionCode = packet.getActionCode();
					ArrayList<String> parameters = packet.getParameters();
					//also have the action code in position zero
					switch(actionCode) {
					case ActionCode.SUBMIT:{
						packet = new Packet(ActionCode.NAME);
						packet.add(name);
						out.println(packet);
						break;
					}
					case ActionCode.ACCEPTED:{
						openWindow();
						packet = new Packet(ActionCode.READY);
						packet.add(name);
						out.println(packet);
						break;
					}
					case ActionCode.REJECTED:{
						JOptionPane.showMessageDialog(this, "The name " + " already exists "
								+ "or the game has already 2 players");
						logIn();
						packet = new Packet(ActionCode.NAME);
						packet.add(name);
						out.println(packet);
						break;
					}
					case ActionCode.STATE_CHANGED:{
						int newState = Integer.parseInt(parameters.get(1));
//						if(parameters.size() > 2) {
//							opponentName = parameters.get(2);
//						}
						setState(newState);
						break;
					}
					case ActionCode.WAIT:{
						setState(MenuStates.STATE_WAITING);
						break;
					}
					case ActionCode.OPPONENT_QUIT:{
						int option = JOptionPane.showConfirmDialog(this, "The opponent left the game "
								+ "do you want to play solo?", "Quitted", JOptionPane.YES_NO_OPTION);
						if(option == JOptionPane.YES_OPTION) {
							isMultiplayer = false;
							setState(MenuStates.STATE_MENU);
						}
						else {
							close();
						}
						break;
					}
					case ActionCode.ASK_PARAMETERS:{
						packet = new Packet(ActionCode.SEND_PARAMETERS);
						packet.add(IDs[0]);//adding the character's id
						packet.add(names[0]);//adding the character's name
						out.println(packet);
						break;
					}
					case ActionCode.FINISHED_PLAYER:{
						playerCount++;
						if(playerCount >= 2) {
							packet = new Packet(ActionCode.BOTH_READY);
							out.println(packet);
						}
						break;
					}
					case ActionCode.NOTIFY_START_SC:{
						packet = new Packet(ActionCode.NOTIFY_START_CS);
						out.println(packet);
						break;
					}
					case ActionCode.START_PLAY:{
						playerID = Integer.parseInt(parameters.get(1));
						setState(MenuStates.STATE_PLAY);
//						packet = new Packet(ActionCode.INITIALIZE);
//						out.println(packet);
						break;
					}
					case ActionCode.DRAW_ME:{
						break;
					}
					case ActionCode.DRAW_OPPONENT:{
						break;
					}
					case ActionCode.SET_IMAGES:{
						int playerID = Integer.parseInt(parameters.get(1));
						int state = Integer.parseInt(parameters.get(2));
						int life = Integer.parseInt(parameters.get(3));
						int mana = Integer.parseInt(parameters.get(4));
						int index = Integer.parseInt(parameters.get(5));
						boolean isMirrored = Boolean.parseBoolean(parameters.get(6));
						gamePanel.setPlayerImages(playerID, state, life, mana, index, isMirrored);
						break;
					}
					case ActionCode.SET_ATTACK_IMAGES:{
						int playerID = Integer.parseInt(parameters.get(1));
						int state = Integer.parseInt(parameters.get(2));
						gamePanel.setPlayerAttackImages(playerID, state, parameters);
						break;
					}
					case ActionCode.DRAW_GENERAL:{
						int playerID = Integer.parseInt(parameters.get(1));
						int life = Integer.parseInt(parameters.get(2));
						int mana = Integer.parseInt(parameters.get(3));
						int state = Integer.parseInt(parameters.get(4));
						boolean isMirrored = Boolean.parseBoolean(parameters.get(5));
						int paintX = Integer.parseInt(parameters.get(6));
						int paintY = Integer.parseInt(parameters.get(7));
						int imageIndex = Integer.parseInt(parameters.get(8));
						gamePanel.prepareForDraw(playerID, life, mana, state, isMirrored,
								paintX, paintY, imageIndex);
						gamePanel.repaint();
						break;
					}
					case ActionCode.DRAW_JUMP:{
						int playerID = Integer.parseInt(parameters.get(1));
						int life = Integer.parseInt(parameters.get(2));
						int mana = Integer.parseInt(parameters.get(3));
						int state = Integer.parseInt(parameters.get(4));
						boolean isMirrored = Boolean.parseBoolean(parameters.get(5));
						int paintX = Integer.parseInt(parameters.get(6));
						int paintJumpY = Integer.parseInt(parameters.get(7));
						int imageIndex = Integer.parseInt(parameters.get(8));
						gamePanel.prepareForDraw(playerID, life, mana, state, isMirrored,
								paintX, paintJumpY, imageIndex);
						gamePanel.repaint();
						break;
					}
					case ActionCode.DRAW_ATTACK:{
						int playerID = Integer.parseInt(parameters.get(1));
						int state = Integer.parseInt(parameters.get(2));
						gamePanel.prepareForDrawAttack(playerID, state, parameters);
						gamePanel.repaint();
						break;
					}
					case ActionCode.REQUEST_PLAYER_BOUNDS:{
						int playerID = Integer.parseInt(parameters.get(1));
						gamePanel.getPlayerBounds(playerID);
						break;
					}
					case ActionCode.REQUEST_ATTACK_BOUNDS:{
						int playerID = Integer.parseInt(parameters.get(1));
						int state = Integer.parseInt(parameters.get(2));
						gamePanel.getAttackBounds(playerID, state);
						break;
					}
					}
				}
			}
		}catch(ConnectException e) {
			JOptionPane.showMessageDialog(this, "The server is not running");
			System.exit(0);
		}catch(IOException e) {
			JOptionPane.showMessageDialog(this, "Lost connection to the server");
			System.exit(0);
		}
	}
	
	private void logIn() {
		logIn.setVisible(true);
		if(!logIn.isCanceled()) {
			host = logIn.getHostName();
			name = logIn.getUserName();
		}
		else {
			close();
		}
	}
	
	public void close() {
		keepRunning = false;
		try {
			if(out != null) {
				Packet packet = new Packet(ActionCode.QUIT);
				out.println(packet);
				out.close();
			}
			if(socket != null) {
				socket.close();
			}
		}catch(IOException e) {}
		
		System.exit(0);
	}
	
	private void openWindow() {
		state = MenuStates.STATE_COVER;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel,BorderLayout.CENTER);
		initGUI();
		
		setTitle("Isekai Battle " + name);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public void setState(int state) {
		this.state = state;
		mainPanel.removeAll();
		mainPanel.invalidate();
		initGUI();
	}
	
	public void setIDs(int id1, int id2) {
		IDs[0] = id1;
		IDs[1] = id2;
	}
	
	public void setNames(String name1, String name2) {
		names[0] = name1;
		names[1] = name2;
	}
	
	public void setBackgroundId(int id) {
		backgroundId = id;
	}
	
	public void receive(int caller, int delay) {
		Packet packet = new Packet(ActionCode.CHANGE_STATE);
		packet.add(caller);
		packet.add(delay);
		out.println(packet);
	}
	
	public void receive(int caller, int delay, boolean resetAll) {
		gamePanel.removeKeyListener(keyListener);
		mainPanel.remove(gamePanel);
		Packet packet = new Packet(ActionCode.CHANGE_STATE);
		packet.add(caller);
		packet.add(delay);
		out.println(packet);
	}
	
	public void receiveMultiplayer() {
		if(!isMultiplayer) {
			isMultiplayer = true;
			Packet packet = new Packet(ActionCode.COUNT_PLAYER);
			out.println(packet);
		}
		else {
			Packet packet = new Packet(ActionCode.WAIT_CHOOSE);
			out.println(packet);
		}
	}
	
	public void receiveBacground(String bgName) {
		this.bgName = bgName;
		Packet packet = new Packet(ActionCode.WAIT_CONFIRM);
		out.println(packet);
	}
	
	public void receiveAnswer(String answer) {
		Packet packet = new Packet(ActionCode.ANSWER_RECEIVED);
		packet.add(answer);
		out.println(packet);
	}
	
	public void setIsMultiplayer(boolean isMultiplayer) {
		this.isMultiplayer = isMultiplayer;
	}
	
	public boolean isMultiplayer() {
		return isMultiplayer;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void send(Packet packet) {
		String packetString = packet.toString();
		out.println(packetString);
	}
	
	public static void main(String[] args) {
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		}catch(Exception e) {}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new IsekaiBattle();
			}
		});
	}

}
