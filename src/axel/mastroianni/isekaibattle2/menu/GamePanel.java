package axel.mastroianni.isekaibattle2.menu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;
import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.draw.DrawPlayer;
import axel.mastroianni.isekaibattle2.player.EnemyPlayer;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;
import axel.mastroianni.isekaibattle2.player.UserPlayer;
import axel.mastroianni.isekaibattle2.util.HUD;
import axel.mastroianni.isekaibattle2.util.SuperDatabase;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int ID = 8;
	private static final int COVER_DELAY = -6;
	
	private static final String DIRECTORY_NAME = "backgrounds";
	
	private IsekaiBattle isekaiBattle;
	
	private String[] backgrounds;
	
	private BufferedImage background;
	
	private DrawPlayerOld player1;
	private DrawPlayerOld player2;
	
	private DrawPlayer drawPlayer1Multi;
	private DrawPlayer drawPlayer2Multi;
	
	private DrawPlayer[] playersMulti = new DrawPlayer[2];
	
	private DrawPlayerOld[] players = new DrawPlayerOld[2];
	
	private int backgroundId;
	private int playerID;
	
	private Timer timer;
	
	private HUD hud;
	
	public GamePanel(IsekaiBattle isekaiBattle, int[] ids,
			String[] names,int backgroundId, int playerID) {
		this.isekaiBattle = isekaiBattle;
		this.backgroundId = backgroundId;
		this.playerID = playerID;
		//painting the background
		File directory = new File(DIRECTORY_NAME);
		if(directory.isDirectory()) {
			backgrounds = directory.list();
		}
		try {
			background = ImageIO.read(new File(DIRECTORY_NAME+"/"+backgrounds[this.backgroundId]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//initializing players
		if(!isekaiBattle.isMultiplayer()) {
			players[0] = new UserPlayer(names[0], ids[0], this);
			players[1] = new EnemyPlayer(names[1], ids[1], this);
			player1 = players[0];
			player2 = players[1];
			player1.setBackground(background);
			player2.setBackground(background);
			player1.setAttacksCoordinates();
			player2.setAttacksCoordinates();
			player1.setEnemy(player2);
			player2.setEnemy(player1);
			player1.setAttacksEnemy();
			player2.setAttacksEnemy();
		}
		else {
			//perspective of the first logged in
			if(playerID == SuperDatabase.ID_1) {
				playersMulti[0] = new DrawPlayer(playerID, background, this);
				playersMulti[1] = new DrawPlayer(playerID + 1, background, this);
			}
			//perspective of the second logged in
			else {
				playersMulti[0] = new DrawPlayer(playerID, background, this);
				playersMulti[1] = new DrawPlayer(playerID - 1, background, this);
			}
			drawPlayer1Multi = playersMulti[0];
			drawPlayer2Multi = playersMulti[1];
			drawPlayer1Multi.setPaintX(Initialization.FIRST_X_1);
			drawPlayer2Multi.setPaintX(Initialization.FIRST_X_2);
			drawPlayer1Multi.setPaintY(Initialization.FIRST_Y);
			drawPlayer2Multi.setPaintY(Initialization.FIRST_Y);
			drawPlayer1Multi.setDataStructures();
			drawPlayer2Multi.setDataStructures();
			drawPlayer1Multi.setAttacksPlayer();
			drawPlayer2Multi.setAttacksPlayer();
		}
		
		hud = new HUD(this);
		
		requestFocusInWindow();
		setFocusable(true);
		
		JOptionPane.showMessageDialog(null,"All Images has been charged");
		
		
		//timer
		timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timedAction();
			}
		});
	}
	
	public void start() {
		timer.start();
	}
	
	public void convertInState(int keyCode) {
		System.out.println("Key Pressed");
		player1.convertInState(keyCode);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(background.getWidth(),background.getHeight());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, null);
		if(!isekaiBattle.isMultiplayer()) {
			player1.draw(g);
			player2.draw(g);
		}
		else {
			if(playerID == SuperDatabase.ID_1) {
				//drawing from the point of view of player1
				drawPlayer1Multi.draw(g);
				drawPlayer2Multi.draw(g);
			}
			else {
				//drawing from the point of view of the player2
				drawPlayer2Multi.draw(g);
				drawPlayer1Multi.draw(g);
			}
		}
		hud.draw(g);
	}
	
	private void timedAction() {
		player1.move();
		player2.move();
		repaint();
	}
	
	public BufferedImage getGameBackground() {
		return background;
	}
	
	public DrawPlayer getPlayer1Multi() {
		return drawPlayer1Multi;
	}
	
	public DrawPlayer getPlayer2Multi() {
		return drawPlayer2Multi;
	}
	
	public DrawPlayerOld getPlayer1() {
		return player1;
	}
	
	public DrawPlayerOld getPlayer2() {
		return player2;
	}
	
	public void endGame() {
		int answer;
		if(player1.getLife() == 0) {
			answer = JOptionPane.showConfirmDialog(null, player2.getName() + " wins. Do you "
					+ "want to play again?", "Play Again?",
					JOptionPane.YES_NO_OPTION);
		}
		else {
			answer = JOptionPane.showConfirmDialog(null, player1.getName() + " wins. "
					+ "Do you want to play again?", "Play Again?",
					JOptionPane.YES_NO_OPTION);
		}
		if(answer == JOptionPane.YES_OPTION) {
			isekaiBattle.receive(ID,COVER_DELAY, true);
			timer.stop();
			player1.reset();
			player2.reset();
		}
		else {
			isekaiBattle.close();
		}
	}
	
	public void setPlayerImages(int playerID, int state, int life, int mana, int index,
			boolean isMirrored) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.setState(state);
			drawPlayer1Multi.setLife(life);
			drawPlayer1Multi.setMana(mana);
			drawPlayer1Multi.setIndex(index);
			drawPlayer1Multi.setMirrored(isMirrored);
			drawPlayer1Multi.setImages();
		}
		else {
			drawPlayer2Multi.setState(state);
			drawPlayer2Multi.setLife(life);
			drawPlayer2Multi.setMana(mana);
			drawPlayer2Multi.setIndex(index);
			drawPlayer2Multi.setMirrored(isMirrored);
			drawPlayer2Multi.setImages();
		}
	}
	
	public void setPlayerAttackImages(int playerID, int state, ArrayList<String> parameters) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.setState(state);
			drawPlayer1Multi.setAttackImages(parameters);
		}
		else {
			drawPlayer2Multi.setState(state);
			drawPlayer2Multi.setAttackImages(parameters);
		}
	}
	
	public void prepareForDraw(int playerID, int life, int mana, int state, boolean isMirrored,
			int paintX, int paintY, int imageIndex) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.setLife(life);
			drawPlayer1Multi.setMana(mana);
			drawPlayer1Multi.setState(state);
			drawPlayer1Multi.setMirrored(isMirrored);
			drawPlayer1Multi.setPaintX(paintX);
			drawPlayer1Multi.setPaintY(paintY);
			drawPlayer1Multi.setImageIndex(imageIndex);
		}
		else {
			drawPlayer2Multi.setLife(life);
			drawPlayer2Multi.setMana(mana);
			drawPlayer2Multi.setState(state);
			drawPlayer2Multi.setMirrored(isMirrored);
			drawPlayer2Multi.setPaintX(paintX);
			drawPlayer2Multi.setPaintY(paintY);
			drawPlayer2Multi.setImageIndex(imageIndex);
		}
	}
	
	public void prepareForDrawAttack(int playerID, int state, ArrayList<String> parameters) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.setAttackImages(parameters);
		}
		else {
			drawPlayer2Multi.setAttackImages(parameters);
		}
	}
	
	public void getPlayerBounds(int playerID) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.getBounds();
		}
		else {
			drawPlayer2Multi.getBounds();
		}
	}
	
	public void getAttackBounds(int playerID, int state) {
		if(playerID == SuperDatabase.ID_1) {
			drawPlayer1Multi.getAttackBounds(state);
		}
		else {
			drawPlayer2Multi.getAttackBounds(state);
		}
	}
	
	public void send(Packet packet) {
		isekaiBattle.send(packet);
	}
	
}
