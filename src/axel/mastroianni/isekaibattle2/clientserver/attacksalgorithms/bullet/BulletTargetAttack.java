package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet;

import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Player;

public class BulletTargetAttack extends BulletAttack {
	
	protected int targetIndex = 0;
	protected int targetLength = 0;
	protected int targetArrayIndex = 0;
	
	protected boolean drawTarget = false;

	public BulletTargetAttack(Player player) {
		super(player);
		id = 4;
	}
	
	@Override
	public void changeMoving() {
		drawTarget = true;
		moveBullet = false;
		moveTarget();
	}
	
	public void moveTarget() {
		basicMove();
		if(targetIndex == targetLength) {
			reset();
		}
	}
	
	public void basicMove() {
//		targetImage = targetImages[targetIndex];//left to the client
		targetIndex++;
	}
	
	 @Override
	 public void reset() {
		super.reset();
		drawTarget = false;
		targetIndex = 0;
		targetLength = 0;
		targetArrayIndex = 0;
			
	 }
	 
	 @Override
	 public void preparePacketForImages(int playerID, int state) {
		 super.preparePacketForImages(playerID, state);
		 packet.add(targetArrayIndex);//6
		 packet.add(targetIndex);//7
	 }
	 
	 @Override
	 public void setAttackLength(ArrayList<String> parameters) {
		 super.setAttackLength(parameters);
		 targetLength = Integer.parseInt(parameters.get(4));
	 }
	 
	 @Override
	 public void preparePacketForRepaint(int playerID, int state) {
		 super.preparePacketForRepaint(playerID, state);
		 packet.add(drawTarget);//7
		 packet.add(targetIndex);//8
		 packet.add(enemy.getXRef());//9
		 packet.add(enemy.getYRef());//10
	 }

}
