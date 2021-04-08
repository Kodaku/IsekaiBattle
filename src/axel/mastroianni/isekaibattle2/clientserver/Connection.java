package axel.mastroianni.isekaibattle2.clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.constantsclass.MenuStates;
import axel.mastroianni.isekaibattle2.util.SuperDatabase;

public class Connection implements Runnable{
	
	private static final String DEFAULT_NAME = "(Default Name)";
	
	private IsekaiServer server;
	
	private Socket socket;
	
	private String name = DEFAULT_NAME;
	
	private BufferedReader in;
	
	private PrintWriter out;
	
	private int id = 0; //count the number of connection received
	private int countLength = 0;
	private int playerID = 0;
	
	private UserPlayer player;
	
	private Game game;
	
	public Connection(IsekaiServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		
		new Thread(this).start();
	}
	
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			Packet packet = new Packet(ActionCode.SUBMIT);
			sendToClient(packet);
			boolean keepRunning = true;
			while(keepRunning) {
				String line = in.readLine();
				if(line == null) {
					keepRunning = false;
				}
				else {
					packet = new Packet(line);
					String actionCode = packet.getActionCode();
					ArrayList<String> parameters = packet.getParameters();
					server.log(line + " was received from " + name);
					switch(actionCode) {
					case ActionCode.NAME:{
						String testName = parameters.get(1);//the index of the name in the packet
						boolean added = server.addedName(testName);
						if(added && server.getNames().size() <= 2) {
							packet = new Packet(ActionCode.ACCEPTED);
							name = testName;
							sendToClient(packet);
							server.log(name + " join the game " + server.getNames().size());
						}
						else {
							packet = new Packet(ActionCode.REJECTED);
							sendToClient(packet);
						}
						break;
					}
					case ActionCode.QUIT:{
						if(server.getNames().size() > 1) {
							packet = new Packet(ActionCode.OPPONENT_QUIT);
							broadcast(name,packet);
						}
						quit();
						keepRunning = false;
						break;
					}
					case ActionCode.READY:{
						name = parameters.get(1);
						server.log(name + " is ready");
						break;
					}
					case ActionCode.CHANGE_STATE:{
						int currentState = Integer.parseInt(parameters.get(1));
						int delay = Integer.parseInt(parameters.get(2));
						packet = new Packet(ActionCode.STATE_CHANGED);
						currentState++;
						currentState += delay;
						packet.add(currentState);
						sendToClient(packet);
						break;
					}
					case ActionCode.COUNT_PLAYER:{
						ArrayList<String> names = server.getNames();
						if(names.size() == 1) {
							packet = new Packet(ActionCode.WAIT);
							sendToClient(packet);
						}
						else if(names.size() == 2) {
							int currentState = MenuStates.STATE_MENU;
							packet = new Packet(ActionCode.STATE_CHANGED);
							packet.add(currentState);
							names.clear();
							server.setNames(names);
							sendToClient(packet);
							broadcast(name,packet);
						}
						break;
					}
					case ActionCode.WAIT_CHOOSE:{
						server.addName(name);
						ArrayList<String> names = server.getNames();
						if(names.size() == 1) {
							packet = new Packet(ActionCode.WAIT);
							sendToClient(packet);
						}
						else if(names.size() == 2) {
							int currentState = MenuStates.STATE_BACKGROUND_CHOOSE;
							packet = new Packet(ActionCode.STATE_CHANGED);
							packet.add(currentState);
							sendToClient(packet);
							broadcast(name,packet);
						}
						break;
					}
					case ActionCode.WAIT_CONFIRM:{
						int otherCurrentState = MenuStates.STATE_CONFIRM;
						packet = new Packet(ActionCode.STATE_CHANGED);
						packet.add(otherCurrentState);
						packet.add(name);
						broadcast(name,packet);
						
						int myCurrentState = MenuStates.STATE_WAITING;
						Packet packet2 = new Packet(ActionCode.STATE_CHANGED);
						packet2.add(myCurrentState);
						sendToClient(packet2);
						break;
					}
					case ActionCode.ANSWER_RECEIVED:{
						String answer = parameters.get(1);
						//asking parameters to create the database with all the images
						if(answer.equals("YES")) {
							packet = new Packet(ActionCode.ASK_PARAMETERS);
							sendToClient(packet);
							broadcast(name,packet);
						}
						else {
							packet = new Packet(ActionCode.STATE_CHANGED);
							int currentState = MenuStates.STATE_BACKGROUND_CHOOSE;
							packet.add(currentState);
							sendToClient(packet);
							broadcast(name, packet);
						}
						break;
					}
					case ActionCode.SEND_PARAMETERS:{
						ArrayList<String> names = server.getNames();
						int characterID = Integer.parseInt(parameters.get(1));
						String characterName = parameters.get(2);
						System.out.println(characterID + ": " + characterName);
						playerID = names.indexOf(name);
						playerID++;
						player = new UserPlayer(playerID, this);
						SuperDatabase.initializeDatabase(characterID, playerID,
								characterName, player);
						player.setTransformation();
						player.setAttacks();
						server.addPlayer(player);
						packet = new Packet(ActionCode.FINISHED_PLAYER);
						sendToClient(packet);
						broadcast(name,packet);
//						//are both two players ready?
//						if(server.getPlayers().size() < 2) {
//							packet = new Packet(ActionCode.WAIT);
//							sendToClient(packet);
//						}
//						else {
//							packet = new Packet(ActionCode.NOTIFY_START_SC); 
//							sendToClient(packet);
//							broadcast(name,packet);
//						}
						break;
					}
					case ActionCode.BOTH_READY:{
						packet = new Packet(ActionCode.NOTIFY_START_SC);
						sendToClient(packet);
						break;
					}
					case ActionCode.NOTIFY_START_CS:{
						packet = new Packet(ActionCode.START_PLAY);
						packet.add(playerID);
						server.setEnemys();
						//only to activate the timer
						game = new Game(player, player.getEnemyMove(), this);
						sendToClient(packet);
//						broadcast(name,packet);
						break;
					}
					case ActionCode.INITIALIZE:{
						player.setState(Player.STATE_INTRO);
						break;
					}
					case ActionCode.START:{
						game.start();
						break;
					}
					case ActionCode.KEY_PRESSED:{
						int keyCode = Integer.parseInt(parameters.get(1));
						player.convertInState(keyCode);
						break;
					}
					case ActionCode.IMAGES_LENGTH:{
						int imagesLength = Integer.parseInt(parameters.get(1));
						int bgWidth = Integer.parseInt(parameters.get(2));
						int bgHeight = Integer.parseInt(parameters.get(3));
						player.setImagesLength(imagesLength);
						player.setBgWidth(bgWidth);
						player.setBgHeight(bgHeight);
						//only to start the game
						if(countLength == 0) {
							countLength++;
							game.start();
						}
						break;
					}
					case ActionCode.ATTACK_IMAGES_LENGTH:{
						player.setAttackLength(parameters);
						break;
					}
					case ActionCode.PLAYER_BOUNDS:{
						int boundsX = Integer.parseInt(parameters.get(1));
						int boundsY = Integer.parseInt(parameters.get(2));
						int boundsWidth = Integer.parseInt(parameters.get(3));
						int boundsHeight = Integer.parseInt(parameters.get(4));
						player.setBoundsX(boundsX);
						player.setBoundsY(boundsY);
						player.setBoundsWidth(boundsWidth);
						player.setBoundsHeight(boundsHeight);
						break;
					}
					case ActionCode.ATTACK_BOUNDS:{
						player.setAttackBounds(parameters);
						break;
					}
					case ActionCode.EMPTY_ATTACK_BOUNDS:{
						player.setAttackBounds(parameters);
						break;
					}
					case ActionCode.NO_ATTACK_BOUNDS:{
						player.setAttackBounds(parameters);
						break;
					}
					}
				}
			}
		}catch(IOException e) {
			
		}
	}
	
	public void sendToClient(Packet packet) {
		String packetString = packet.toString();
		out.println(packetString);
		server.log(packetString + " sent to " + name);
	}
	
	public void broadcast(String sender, Packet packet) {
		server.broadcast(name,packet);
	}
	
	public void quit() {
		remove(name);
		server.log("Connection ended for " + name);
		try {
			socket.close();
		}catch(IOException e) {
			
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public void remove(String name) {
		server.remove(name);
	}

}
