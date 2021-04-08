package axel.mastroianni.isekaibattle2.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;

public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int ID = 3;
	
	private static final int PLAY_DELAY = 1;
	private static final int TUTORIAL_DELAY = 0;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 340;
	
	private static final String LOGO_NAME = "introImage/gameLogo.png";
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 32);
	
	private Rectangle playRectangle;
	private Rectangle tutorialRectangle;
	
	private BufferedImage image;
	
	private IsekaiBattle isekaiBattle;
	
	public Menu(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		
		File file = new File(LOGO_NAME);
		
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		playRectangle = new Rectangle(225,160,170,50);
		tutorialRectangle = new Rectangle(225,220,170,50);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				if(playRectangle.contains(point)) {
					Menu.this.isekaiBattle.receive(ID, PLAY_DELAY);
				}
				else if(tutorialRectangle.contains(point)) {
					Menu.this.isekaiBattle.receive(ID, TUTORIAL_DELAY);
				}
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH,HEIGHT);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.drawImage(image, 30, 50, null);
		
		g.setColor(Color.WHITE);
		g.drawRect(225, 160, 170, 50);
		
		int stringX = playRectangle.x + 42;
		int stringY = playRectangle.y + 35;
		
		g.setFont(FONT);
		
		g.drawString("PLAY", stringX, stringY);
		
		g.drawRect(225, 220, 170, 50);
		
		stringX = tutorialRectangle.x + 8;
		stringY = tutorialRectangle.y + 38;
		
		g.drawString("TUTORIAL", stringX, stringY);
		
	}

}
