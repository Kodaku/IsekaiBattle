package axel.mastroianni.isekaibattle2.menu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import axel.mastroianni.isekaibattle2.clientserver.IsekaiBattle;

public class IntroWindow extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int ID = 0;
	private static final int DELAY = 0;
	
	private static final String FILE_NAME = "introImage/IsekaiImage.jpg";
	
	private BufferedImage introImage;
	
	private IsekaiBattle isekaiBattle;
	
	public IntroWindow(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		try {
			File file = new File(FILE_NAME);
			introImage = ImageIO.read(file);
		}catch(IOException e) {
			
		}
		
		setFocusable(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IntroWindow.this.isekaiBattle.receive(ID, DELAY);
			}
		});
		
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(introImage.getWidth(), introImage.getHeight());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(introImage, 0, 0, null);
	}

}
