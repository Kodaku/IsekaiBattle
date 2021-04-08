package axel.mastroianni.isekaibattle2.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class WaitingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 338;
	
	private static final int ID = 2;
	
	private static final String WAITING_MESSAGE = "Waiting for the other player...";
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 30);
	
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
		
		g.drawString(WAITING_MESSAGE, 100, 160);
	}

}
