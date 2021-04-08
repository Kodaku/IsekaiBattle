package axel.mastroianni.isekaibattle2.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import axel.mastroianni.isekaibattle2.menu.GamePanel;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class HUD {
	
	private static final int TOTAL_LIFE = 200;
	private static final int TOTAL_MANA = 200;
	private static final int LIFE_HEIGHT = 10;
	private static final int MANA_HEIGHT = 5;
	private static final int LIFE_WIDTH = TOTAL_LIFE;
	private static final int MANA_WIDTH = TOTAL_MANA;
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 20);
	
	private GamePanel gamePanel;
	
	private DrawPlayerOld player1;
	private DrawPlayerOld player2;
	
//	private DrawPlayer player1Multi;
//	private DrawPlayer player2Multi;
	
	public HUD(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		
		player1 = gamePanel.getPlayer1();
		player2 = gamePanel.getPlayer2();
	}
	
	public void draw(Graphics g) {
		//player
		g.drawRect(10, 10, LIFE_WIDTH, LIFE_HEIGHT);
		g.setColor(Color.GREEN);
		g.fillRect(10, 10, player1.getLife(), LIFE_HEIGHT);
		
		g.setColor(Color.BLACK);
		
		g.drawRect(10, 10 + LIFE_HEIGHT + 5, MANA_WIDTH, MANA_HEIGHT);
		g.setColor(Color.BLUE);
		g.fillRect(10, 10 + LIFE_HEIGHT + 5, player1.getMana(), MANA_HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		g.drawString(gamePanel.getPlayer1().getName(), 10, 10 + LIFE_HEIGHT * 2 + 2* MANA_HEIGHT
				+ 10);
		
		//enemy
		g.drawRect(gamePanel.getGameBackground().getWidth() - 10 - LIFE_WIDTH, 10, LIFE_WIDTH,
				LIFE_HEIGHT);
		g.drawRect(gamePanel.getGameBackground().getWidth() - 10 - MANA_WIDTH, 10 + 
				LIFE_HEIGHT + 5, MANA_WIDTH, MANA_HEIGHT);
		g.setColor(Color.GREEN);
		g.fillRect(gamePanel.getGameBackground().getWidth() - 10 - LIFE_WIDTH, 10, player2.getLife(),
				LIFE_HEIGHT);
		g.setColor(Color.BLUE);
		g.fillRect(gamePanel.getGameBackground().getWidth() - 10 - MANA_WIDTH, 10 + 
				LIFE_HEIGHT + 5, player2.getMana(), MANA_HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString(gamePanel.getPlayer2().getName(), gamePanel.getGameBackground().getWidth() -
				10 - LIFE_WIDTH, 10 + LIFE_HEIGHT * 2 + 2 * MANA_HEIGHT + 10);
	}
	
}
