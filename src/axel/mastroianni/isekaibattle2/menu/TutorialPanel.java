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

public class TutorialPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 340;
	
	private static final int ID = 4;
	private static final int DELAY = -2;
	
	private static final String RUN_COMMAND = "Press LEFT or RIGTH to run";
	private static final String JUMP_COMMAND = "Press UP to jump";
	private static final String ESCAPE_COMMAND = "Press E to teltransport yourself...10 mana"
			+ "units reqeusted";
	private static final String NORMAL_ATTACK_COMMAND = "Press N to do a basic attack...no mana"
			+ "requested";
	private static final String SPECIAL_ATTACK_COMMAND = "Press S to do a special attack...you'll"
			+ "have to have at least half blu bar";
	private static final String FINAL_ATTACK_COMMAND = "Press F to do the final attack...you'll "
			+ "have to have full blue bar";
	private static final String TRANSFORMATION_COMMAND_1 = "Press T to transform...you won't return "
			+ "to the previous form. ";
	private static final String TRANSFORMATION_COMMAND_2 = "If the character has no transformation "
			+ "or this is the last one,";
	private static final String TRANSFORMATION_COMMAND_3 = "this command won't work";
	
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 16);
	
	private IsekaiBattle isekaiBattle;
	
	private Rectangle backRectangle;
	
	public TutorialPanel(IsekaiBattle isekaiBattle) {
		this.isekaiBattle = isekaiBattle;
		backRectangle = new Rectangle(10,300,100,30);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				if(backRectangle.contains(p)) {
					TutorialPanel.this.isekaiBattle.receive(ID, DELAY);
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
		int y = 30;
		g.drawString(RUN_COMMAND, 20, y += 30);
		g.drawString(JUMP_COMMAND, 20, y += 30);
		g.drawString(ESCAPE_COMMAND, 20, y += 30);
		g.drawString(NORMAL_ATTACK_COMMAND, 20, y += 30);
		g.drawString(SPECIAL_ATTACK_COMMAND, 20, y += 30);
		g.drawString(FINAL_ATTACK_COMMAND, 20, y += 30);
		g.drawString(TRANSFORMATION_COMMAND_1, 20, y += 30);
		g.drawString(TRANSFORMATION_COMMAND_2, 20, y += 20);
		g.drawString(TRANSFORMATION_COMMAND_3, 20, y += 20);
		g.drawRect(10,300,100,30);
		g.drawString("BACK", 35, 320);
	}

}
