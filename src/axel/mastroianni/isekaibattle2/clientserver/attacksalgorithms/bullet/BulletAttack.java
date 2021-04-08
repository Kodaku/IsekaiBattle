package axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet;

import java.awt.Rectangle;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.Packet;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;

public class BulletAttack extends Attack {
	
	private int bulletIndex = 0;
	private int bulletLength = 0;
	private int bulletArrayIndex = 0;
	
	//setted by the client
	private int bulletX;
	private int bulletY;
	private int bulletWidth;
	private int bulletHeight;
	
	private int vx = 10;
	
	private boolean hitted = false;
	protected boolean moveBullet = false;
	
	protected Rectangle bulletBounds = new Rectangle();
	
	public BulletAttack(Player player) {
		super(player);
		id = 2;
	}

	@Override
	public void move() {
		//always send a packet when the method is invoked
		imageIndex++;
		if(imageIndex == imagesLength) {
			imageIndex--;
			moveBullet = true;
			moveBullet();
		}
	}
	
	private void moveBullet() {
		bulletIndex++;
		bulletIndex = bulletIndex % bulletLength;
		if(player.isMirrored()) {
			bulletX -= vx;
		}
		else {
			bulletX += vx;
		}
		checkCollision();
		if(hitted) {
			changeMoving();
		}
		else if(bulletX >= x_ref + 300 || bulletX <= x_ref - 300) {
			reset();
		}
	}
	
	//bounds and collision are left to the client
	private void checkCollision() {
		player.requestAttackBounds();
		enemy.requestPlayerBounds();
		if(getBounds().intersects(enemy.getBounds())) {
			changeEnemyState();
		}
	}
	
	protected void changeEnemyState() {
		enemy.setState(Player.STATE_HIT);
		if(!hitted) {
			enemy.reduceLife();
		}
		hitted = true;
	}
	
	protected void changeMoving() {
		reset();
	}
	
	protected void reset() {
		moveBullet = false;
		hitted = false;
		player.setState(Player.STATE_STAND);
		if(enemy.getState() == Player.STATE_HIT) {
			enemy.setState(Player.STATE_STAND);
		}
		imageIndex = 0;
		arrayAttackIndex = 0;
		bulletIndex = 0;
		bulletArrayIndex = 0;
	}
	
	public void setBounds(ArrayList<String> parameters) {
		String action = parameters.get(0);
		if(action.equals(ActionCode.ATTACK_BOUNDS)) {
			bulletX = Integer.parseInt(parameters.get(1));
			bulletY = Integer.parseInt(parameters.get(2));
			bulletWidth = Integer.parseInt(parameters.get(3));
			bulletHeight = Integer.parseInt(parameters.get(4));
			bulletBounds = new Rectangle(bulletX, bulletY, bulletWidth, bulletHeight);
		}
		else if(action.equals(ActionCode.EMPTY_ATTACK_BOUNDS)) {
			bulletBounds = new Rectangle();
		}
	}

	@Override
	public Rectangle getBounds() {
		return bulletBounds;
	}
	
	@Override
	public void preparePacketForImages(int playerID, int state) {
		packet = new Packet(ActionCode.SET_ATTACK_IMAGES);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(arrayAttackIndex);//3
		packet.add(bulletArrayIndex);//4
		packet.add(bulletIndex);//5
	}
	
	@Override
	public void preparePacketForRepaint(int playerID, int state) {
		packet = new Packet(ActionCode.DRAW_ATTACK);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(imageIndex);//3
		packet.add(bulletIndex);//4
		packet.add(bulletX);//5
		packet.add(moveBullet);//6
	}
	
	@Override
	public void setAttackLength(ArrayList<String> parameters) {
		imagesLength = Integer.parseInt(parameters.get(1));
		bulletLength = Integer.parseInt(parameters.get(2));
		bulletX = Integer.parseInt(parameters.get(3));
	}

}
