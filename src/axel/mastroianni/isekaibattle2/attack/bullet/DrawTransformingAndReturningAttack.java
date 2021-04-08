package axel.mastroianni.isekaibattle2.attack.bullet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class DrawTransformingAndReturningAttack extends DrawBulletTargetAttack {
	
	private ArrayList<BufferedImage[]> returningSequence;
	
	private BufferedImage[] returningImages;
	
	private BufferedImage returningImage;
	
	private int returningArrayIndex = 0;
	private int returningIndex = 0;
	private int returningLength = 0;
	
	private boolean drawReturning = false;

	public DrawTransformingAndReturningAttack(ArrayList<BufferedImage[]> attackSequence,
			ArrayList<BufferedImage[]> bulletSequence, ArrayList<BufferedImage[]> targetSequence,
			ArrayList<BufferedImage[]> returningSequence, DrawPlayerOld player) {
		super(attackSequence, bulletSequence, targetSequence, player);
		this.returningSequence = returningSequence;
		id = 8;
	}
	
	@Override
	public void setImages() {
		super.setImages();
		setReturningImages();
	}
	
	private void setReturningImages() {
		returningImages = returningSequence.get(returningArrayIndex);
		returningLength = returningImages.length;
	}
	
//	@Override
//	public void setImages(ArrayList<String> parameters) {
//		super.setImages(parameters);
//		setReturningImages(parameters);
//	}
//	
//	private void setReturningImages(ArrayList<String> parameters) {
//		returningArrayIndex = Integer.parseInt(parameters.get(8));
//		returningIndex = Integer.parseInt(parameters.get(9));
//		returningImages = returningSequence.get(returningArrayIndex);
//		returningLength = returningImages.length;
//		returningImage = returningImages[returningIndex];
//	}
	
	@Override
	public void draw(Graphics g) {
		int x = 0;
		int y = 0;
		if(!drawReturning) {
			super.draw(g);
		}
		else {
			returningImage = returningImages[returningIndex];
			x = player.getXRef() - returningImage.getWidth() / 2;
			y = player.getYRef() - returningImage.getHeight();
			g.drawImage(returningImage, x, y, null);
		}
	}
	
//	public void preparePacketForImageLength() {
//		super.preparePacketForImageLength();
//		packet.add(returningLength);//5
//	}
//	
//	@Override
//	public void setAttackVariables(ArrayList<String> parameters) {
//		super.setAttackVariables(parameters);
//		drawReturning = Boolean.parseBoolean(parameters.get(11));
//		returningIndex = Integer.parseInt(parameters.get(12));
//	}
	
	@Override
	public void moveTarget() {
		basicMove();
		if(targetIndex == targetLength) {
			drawTarget = false;
			drawReturning = true;
			targetIndex--;
			moveReturning();
		}
	}
	
	private void moveReturning() {
		returningImage = returningImages[returningIndex];
		returningIndex++;
		if(returningIndex == returningLength) {
			reset();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		returningArrayIndex = 0;
		returningIndex = 0;
		returningLength = 0;
		drawReturning = false;
	}

}
