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
import java.util.Random;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;

public class PlayerChoose extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int ID = 5;
	private static final int PLAYER_DELAY = 0;
	private static final int MENU_DELAY = -3;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 338;
	private static final int IMAGES_OFFSET = 30;
	private static final int PLAYERS_PER_PAGE = 5;
	
	private static final String DIRECTORY_NAME = "playerStand";
	private static final String ARROW_FW = "arrowFw.png";
	private static final String ARROW_RW = "arrowRw.png";
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 16);
	
	private File[] imagesNames;
	
	private BufferedImage[] images;
	
	private BufferedImage arrowFw;
	private BufferedImage arrowRw;
	
	private Rectangle[] rectangles;
	private Rectangle backRectangle;
	private Rectangle fwRectangle;
	private Rectangle rwRectangle;
	
	private int pages;
	private int currentPage = 1;
	
	private String[] characterNames;
	
	private IsekaiBattle isekaiBattle;
	
	private Random rand = new Random();
	
	public PlayerChoose(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		
		File dir = new File(DIRECTORY_NAME);
		File arrowFwFile = new File(ARROW_FW);
		File arrowRwFile = new File(ARROW_RW);
		if(dir.isDirectory()) {
			imagesNames = dir.listFiles();
		}
		
		images = new BufferedImage[imagesNames.length];
		rectangles = new Rectangle[imagesNames.length];
		characterNames = new String[imagesNames.length];
		
		backRectangle = new Rectangle(10,300,100,30);
		
		pages = (int)(imagesNames.length / PLAYERS_PER_PAGE + 1);
		
		try {
			int counter = 0;
			for(int i = 0; i < pages; i++) {
				int x = 50;
				int y = 30;
				for(int j = 0; j < PLAYERS_PER_PAGE && counter < imagesNames.length;
						j++, x+= 170) {
					images[counter] = ImageIO.read(imagesNames[counter]);
					if(x + 100 >= WIDTH) {
						x = 150;
						y += 150;
					}
					rectangles[counter] = new Rectangle(x, y, 100, 100);
					characterNames[counter] = parseName(imagesNames[counter].getName());
					counter++;
				}
			}
			arrowFw = ImageIO.read(arrowFwFile);
			arrowRw = ImageIO.read(arrowRwFile);
			fwRectangle = new Rectangle(530,HEIGHT / 2 - 20, arrowFw.getWidth(), arrowFw.getHeight());
			rwRectangle = new Rectangle(0, HEIGHT / 2 - 20, arrowRw.getWidth(), arrowRw.getHeight());
		}catch(IOException e) {}
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Point pointClicked = e.getPoint();
				int playerId = evaluateClick(pointClicked);
				int enemyId = chooseEnemyId(playerId);
				if(playerId >= 0) {
					PlayerChoose.this.isekaiBattle.setIDs(playerId, enemyId);
					PlayerChoose.this.isekaiBattle.setNames(characterNames[playerId],
							characterNames[enemyId]);
					if(isekaiBattle.isMultiplayer()) {
						PlayerChoose.this.isekaiBattle.receiveMultiplayer();
					}
					else {
						PlayerChoose.this.isekaiBattle.receive(ID,PLAYER_DELAY);
					}
				}
				else if(playerId == -2){
					PlayerChoose.this.isekaiBattle.receive(ID, MENU_DELAY);
				}
				else if(playerId == -3) {
					currentPage++;
					checkCurrent();
					repaint();
				}
				else if(playerId == -4) {
					currentPage--;
					checkCurrent();
					repaint();
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
		
		int x = 50;
		int y = 30;
		
		g.setFont(FONT);
		
		for(int i = 0; i < PLAYERS_PER_PAGE; i++, x += 170) {
			int imageIndex = i + (currentPage - 1) * PLAYERS_PER_PAGE;
			if(imageIndex == images.length) {
				 break;
			}
			if(x + 100 >= WIDTH) {
				x = 150;
				y += 150;
			}
			
			if(currentPage == 1) {
				g.drawImage(arrowFw, 530, HEIGHT / 2 - 20, null);
			}
			else if(currentPage == pages) {
				g.drawImage(arrowRw, 30, HEIGHT / 2 - 20, null);
			}
			else if(currentPage > 1) {
				g.drawImage(arrowFw, 530, HEIGHT / 2 - 20, null);
				g.drawImage(arrowRw, 0, HEIGHT / 2 - 20, null);
			}
			
			g.drawRect(x, y, 100, 100);
			g.drawImage(images[imageIndex], x + IMAGES_OFFSET, y + IMAGES_OFFSET, null);
			g.drawString(characterNames[imageIndex], x + 30, y 
					+ rectangles[imageIndex].height + 20);
		}
		
		g.drawRect(10,300,100,30);
		g.drawString("BACK", 35, 320);
	}
	
	private String parseName(String name) {
		StringTokenizer st = new StringTokenizer(name, "_" + ".");
		st.nextToken();
		String characterName = st.nextToken();
		return characterName;
	}
	
	private int evaluateClick(Point pointClicked) {
		if(backRectangle.contains(pointClicked)) {
			return -2;
		}
		else if(fwRectangle.contains(pointClicked)) {
			return -3;
		}
		else if(rwRectangle.contains(pointClicked)) {
			return -4;
		}
		else {
			for(int i = 0; i < PLAYERS_PER_PAGE; i++) {
				int index = i + (currentPage - 1) * PLAYERS_PER_PAGE;
				if(index == rectangles.length) {
					break;
				}
				if(rectangles[index].contains(pointClicked)) {
					return index;
				}
			}
		}
		return -1;
	}
	
	private int chooseEnemyId(int playerId) {
		int enemyId = -1;
		do {
			enemyId = rand.nextInt(characterNames.length);
		}while(enemyId == playerId);
		
		return enemyId;
	}
	
	private void checkCurrent() {
		if(currentPage < 0) {
			currentPage = 0;
		}
		else if(currentPage >= pages) {
			currentPage = pages;
		}
	}

}
