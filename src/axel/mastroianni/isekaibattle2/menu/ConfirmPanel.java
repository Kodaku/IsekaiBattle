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

public class ConfirmPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 338;
	
//	private static final int ID = 7;
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 25);
	
	private static final String YES = "YES";
	private static final String NO = "NO";
	
	private IsekaiBattle isekaiBattle;
	
	private Rectangle yesRectangle;
	private Rectangle noRectangle;
	
	private String name;
	private String bgName;
	
	private String message;
	
	public ConfirmPanel(IsekaiBattle isekaiBattle, String name, String bgName) {
		this.isekaiBattle = isekaiBattle;
		this.name = name;
		this.bgName = bgName;
		
		message = name + " has chosen " + bgName + ". Is it ok for you?";
		
		yesRectangle = new Rectangle(100,160,175,40);
		noRectangle = new Rectangle(340,160,175,40);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				if(yesRectangle.contains(p)) {
					ConfirmPanel.this.isekaiBattle.receiveAnswer(YES);
				}
				else if(noRectangle.contains(p)) {
					ConfirmPanel.this.isekaiBattle.receiveAnswer(NO);
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
		g.drawString(message, 130, 140);
		
		g.drawRect(100, 160, 175, 40);
		g.drawRect(340, 160, 175, 40);
		
		g.drawString(YES, 185, 185);
		g.drawString(NO, 390, 185);
	}

}
