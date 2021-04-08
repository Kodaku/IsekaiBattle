package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.running;

import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Player;

public class RunningTargetAttack extends RunningAttack {
	
	private int targetIndex = 0;
	private int targetLength = 0;
	private int targetArrayIndex = 0;
	
	private boolean drawTarget = false;

	public RunningTargetAttack(Player player) {
		super(player);
		id = 5;
	}
	
	@Override
	public void changeMoving() {
		drawTarget = true;
		moveTarget();
	}
	
	private void moveTarget() {
		targetIndex++;
		if(targetIndex == targetLength) {
			reset();
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		targetIndex = 0;
		drawTarget = false;
		targetLength = 0;
		targetArrayIndex = 0;
		player.setXRef(runX);
	}
	
	@Override
	public void preparePacketForImages(int playerID, int state) {
		super.preparePacketForImages(playerID, state);
		packet.add(targetArrayIndex);//7
		packet.add(targetIndex);//8
	}
	
	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		super.setAttackLength(parameters);
		targetLength = Integer.parseInt(parameters.get(3));
	}
	
	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		super.preparePacketForRepaint(playerID, state);
		packet.add(enemy.getXRef());//6
		packet.add(enemy.getYRef());//7
		packet.add(drawTarget);//8
		packet.add(targetIndex);//9
	}

}
