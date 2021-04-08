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
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;

public class BackgroundChoose extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int ID = 6;
	private static final int BACKGROUND_DELAY = 1;
	private static final int PLAYER_DELAY = -2;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 338;
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 16);
	
	private static final String DIRECTORY_NAME = "backgroundsRescaled";
	
	private File[] imagesNames;
	
	private BufferedImage[] backgrounds;
	
	private Rectangle[] rectangles;
	private Rectangle backRectangle;
	
	private String[] backgroundNames;
	
	private IsekaiBattle isekaiBattle;
	
	public BackgroundChoose(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		
		File dir = new File(DIRECTORY_NAME);
		if(dir.isDirectory()) {
			imagesNames = dir.listFiles();
		}
		
		backgrounds = new BufferedImage[imagesNames.length];
		rectangles = new Rectangle[imagesNames.length];
		backgroundNames = new String[imagesNames.length];
		
		try {
			for(int i = 0; i < backgrounds.length; i++) {
				backgrounds[i] = ImageIO.read(imagesNames[i]);
			}
		}catch(IOException e) {}
		
		for(int i = 0; i < backgroundNames.length; i++) {
			backgroundNames[i] = parseName(imagesNames[i].getName());
		}
		
		int x = 70;
		int y = 30;
		for(int i = 0; i < rectangles.length; i++) {
			if(x + 100 >= WIDTH) {
				x = 100;
				y += 170;
			}
			rectangles[i] = new Rectangle(x,y,backgrounds[i].getWidth(), backgrounds[i].getHeight());
			x += backgrounds[i].getWidth() + 40;
		}
		
		backRectangle = new Rectangle(10,300,100,30);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				int id = chooseRect(point);
				if(id == -2) {
					BackgroundChoose.this.isekaiBattle.receive(ID, PLAYER_DELAY);
				}
				else if(id >= 0){
					BackgroundChoose.this.isekaiBattle.setBackgroundId(id);
					if(!isekaiBattle.isMultiplayer()) {
						BackgroundChoose.this.isekaiBattle.receive(ID, BACKGROUND_DELAY);
					}
					else {
						BackgroundChoose.this.isekaiBattle.receiveBacground(
								backgroundNames[id]);
					}
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
		
		g.setColor(Color.WHITE);
		g.setFont(FONT);
		
		int x = 70;
		int y = 30;
		for(int i = 0; i < backgrounds.length; i++) {
			if(x + 100 >= WIDTH) {
				x = 100;
				y += 170;
			}
			g.drawImage(backgrounds[i], x, y, null);
			g.drawString(backgroundNames[i], x, y + backgrounds[i].getHeight() + 15);
			x += backgrounds[i].getWidth() + 40;
		}
		
		g.drawRect(10,300,100,30);
		g.drawString("BACK", 35, 320);
	}
	
	private String parseName(String name) {
		StringTokenizer st = new StringTokenizer(name,"_" + ".");
		st.nextToken();
		String imageName = st.nextToken();
		return imageName;
	}
	
	private int chooseRect(Point point) {
		if(backRectangle.contains(point)) {
			return -2;
		}
		else {
			for(int i = 0; i < rectangles.length; i++) {
				if(rectangles[i].contains(point)) {
					return i;
				}
			}
			return -1;
		}
	}

}
