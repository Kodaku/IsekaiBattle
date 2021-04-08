package axel.mastroianni.isekaibattle2.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;

public class PlayModePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 338;
	
	private static final int ID = 1;
	private static final int WAITING_DELAY = 0;
	private static final int MENU_DEALY = 1;
	
	private static final int MESSAGE_X = 130;
	private static final int MESSAGE_Y = 140;
	
	private static final int SOLO_RECTANGLE_X = 100;
	private static final int SOLO_RECTANGLE_Y = 160;
	private static final int MULTI_PLAYER_RECTANGLE_X = 340;
	
	private static final int RECTANGLE_WIDTH = 175;
	private static final int RECTANGLE_HEIGHT = 40;
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 20);
	
	private static final String CHOOSE_MESSAGE = "Choose the mode you want to play";
	private static final String SOLO_BUTTON = "SINGLE PLAYER";
	private static final String MULTI_BUTTON = "1 VS 1";
	
	private IsekaiBattle isekaiBattle;
	
	private Rectangle soloRectangle;
	private Rectangle multiPlayerRectangle;
	
	public PlayModePanel(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		
		soloRectangle = new Rectangle(SOLO_RECTANGLE_X, SOLO_RECTANGLE_Y, RECTANGLE_WIDTH,
				RECTANGLE_HEIGHT);
		multiPlayerRectangle = new Rectangle(MULTI_PLAYER_RECTANGLE_X, SOLO_RECTANGLE_Y,
				RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				if(soloRectangle.contains(p)) {
					PlayModePanel.this.isekaiBattle.receive(ID, MENU_DEALY);
				}
				else if(multiPlayerRectangle.contains(p)) {
					PlayModePanel.this.isekaiBattle.receiveMultiplayer();
				}
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setFont(FONT);
		g.setColor(Color.WHITE);
		
		g.drawString(CHOOSE_MESSAGE, MESSAGE_X, MESSAGE_Y);
		
		g.drawRect(SOLO_RECTANGLE_X, SOLO_RECTANGLE_Y, RECTANGLE_WIDTH,RECTANGLE_HEIGHT);
		g.drawRect(MULTI_PLAYER_RECTANGLE_X, SOLO_RECTANGLE_Y, RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
		
		g.drawString(SOLO_BUTTON, 105, 185);
		g.drawString(MULTI_BUTTON, 395, 185);
	}

}
