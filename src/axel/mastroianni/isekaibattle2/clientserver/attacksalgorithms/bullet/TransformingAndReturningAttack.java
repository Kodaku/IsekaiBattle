package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet;

import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Player;

public class TransformingAndReturningAttack extends BulletTargetAttack {
	
	private int returningIndex = 0;
	private int returningLength = 0;
	private int returningArrayIndex = 0;
	
	private boolean drawReturning = false;

	public TransformingAndReturningAttack(Player player) {
		super(player);
		id = 8;
	}
	
	@Override
	public void moveTarget() {
		basicMove();
		if(targetIndex == targetLength) {
			drawTarget = false;
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
		returningIndex = 0;
		returningLength = 0;
		returningArrayIndex = 0;
		drawReturning = false;
	}
	
	@Override
	public void preparePacketForImages(int playerID, int state) {
		super.preparePacketForImages(playerID, state);
		packet.add(returningArrayIndex);//8
		packet.add(returningIndex);//9
	}
	
	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		super.setAttackLength(parameters);
		returningLength = Integer.parseInt(parameters.get(5));
	}
	
	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		super.preparePacketForRepaint(playerID, state);
		packet.add(drawReturning);//11
		packet.add(returningIndex);//12
	}

}
