package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet;

import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Player;

public class TransformingAndNotReturningAttack extends BulletAttack {
	
	private int secondIndex = 0;
	private int secondLength = 0;
	private int secondArrayIndex = 0;
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private int returningIndex = 0;
	private int returningLength = 0;
	private int returningArrayIndex = 0;
	
	private boolean drawReturning = false;
	private boolean drawTarget = false;
	private boolean drawSecond = false;

	public TransformingAndNotReturningAttack(Player player) {
		super(player);
		id = 9;
	}
	
	@Override
	public void changeMoving() {
		moveBullet = false;
		drawSecond = true;
		moveSecond();
	}
	
	private void moveSecond() {
//		secondImage = secondImages[secondIndex]; left to the client
		secondIndex++;
		if(secondIndex == secondLength) {
			secondIndex--;
			drawTarget = true;
			moveTarget();
		}
	}
	
	private void moveTarget() {
//		targetImage = targetImages[targetIndex]; left to the client
		targetIndex++;
		if(targetIndex == targetLength) {
			drawSecond = false;
			drawTarget = false;
			drawReturning = true;
			targetIndex--;
			moveReturning();
		}
	}
	
	private void moveReturning() {
//		returningImage = returningImages[returningIndex];
		returningIndex++;
		if(returningIndex == returningLength) {
			reset();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		secondIndex = 0;
		secondLength = 0;
		secondArrayIndex = 0;
		targetIndex = 0;
		targetLength = 0;
		targetArrayIndex = 0;
		returningIndex = 0;
		returningLength = 0;
		returningArrayIndex = 0;
		drawSecond = false;
		drawTarget = false;
		drawReturning = false;
	}
	
	@Override
	public void preparePacketForImages(int playerID, int state) {
		super.preparePacketForImages(playerID, state);
		packet.add(secondArrayIndex);//6
		packet.add(secondIndex);//7
		packet.add(targetArrayIndex);//8
		packet.add(targetIndex);//9
		packet.add(returningArrayIndex);//10
		packet.add(returningIndex);//11
	}
	
	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		super.setAttackLength(parameters);
		secondLength = Integer.parseInt(parameters.get(4));
		targetLength = Integer.parseInt(parameters.get(5));
		returningLength = Integer.parseInt(parameters.get(6));
	}
	
	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		super.preparePacketForRepaint(playerID, state);
		packet.add(drawSecond);//7
		packet.add(drawReturning);//8
		packet.add(drawTarget);//9
		packet.add(secondIndex);//10
		packet.add(targetIndex);//11
		packet.add(returningIndex);//12
		packet.add(enemy.getXRef());//13
		packet.add(enemy.getYRef());//14
	}

}
