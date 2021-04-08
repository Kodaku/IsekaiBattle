package axel.mastroianni.isekaibattle2.clientserver;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.constantsclass.ActionCode;
import axel.mastroianni.isekaibattle2.util.SuperDatabase;

public class Player {
	
	public static final int STATE_DEAD = 0;
	public static final int STATE_FINAL_ATTACK = 1;
	public static final int STATE_HIT = 2;
	public static final int STATE_INTRO = 3;
	public static final int STATE_JUMP = 4;
	public static final int STATE_ATTACK = 5;
	public static final int STATE_RUN = 6;
	public static final int STATE_SPECIAL_ATTACK = 7;
	public static final int STATE_STAND = 8;
	public static final int STATE_STOP = 9;
	public static final int STATE_TRANSFORM = 10;
	public static final int STATE_ESCAPE = 11;
	
	private static final int MAX_MANA = 200;
	private static final int MAX_LIFE = 200;
	
	private ArrayList<String> transformations;
	private ArrayList<Integer> states;
	
	private int imageIndex = 0;
	
	private int x_ref = 0;
	private int y_ref = 0;
	
	private int bgWidth;
	private int bgHeight;
	
	private int vx = 10;
	
	private double vy = 30.0;
	private double jumpY;
	private double t0 = 0.0;
	private double t1;
	private double delta;
	
	protected int state;
	
	private int playerID;
	
	private int mana = MAX_MANA;
	private int life = MAX_LIFE;
	
	private int transformationIndex = 0;
	private int index = 0;
	private int imagesLength;
	
	private int boundsX;
	private int boundsY;
	private int boundsWidth;
	private int boundsHeight;
	
	private boolean isMoving = false;
	private boolean isHitted = false;
	private boolean isJumping = false;
	private boolean isMirrored = false;
	
	private String name;
	
	private Attack normalAttack;
	private Attack specialAttack;
	private Attack finalAttack;
	
	private Player enemy;
	private EnemyMove enemyMove;
	
	private Connection connection;
	
	public Player(int playerID, Connection connection) {
		this.playerID = playerID;
		this.connection = connection;
		state = STATE_INTRO;
	}
	
	public void setTransformation() {
		SuperDatabase.getTransformations(playerID);
	}
	
	public void setAttacks() {
		SuperDatabase.setServerDataStructures(transformationIndex, playerID);
		normalAttack = SuperDatabase.getNormalAttack(playerID);
		specialAttack = SuperDatabase.getSpecialAttack(playerID);
		finalAttack = SuperDatabase.getFinalAttack(playerID);
	}
	
	public void convertInState(int keyCode) {
		if(!isMoving) {
			if(!isHitted) {
				switch(keyCode) {
				case KeyEvent.VK_UP:{
					state = STATE_JUMP;
					isJumping = true;
					break;
				}
				case KeyEvent.VK_RIGHT:{
					isMirrored = false;
					state = STATE_RUN;
					break;
				}
				case KeyEvent.VK_LEFT:{
					isMirrored = true;
					state = STATE_RUN;
					break;
				}
				case KeyEvent.VK_N:{
					mana += 5;
					checkMana();
					state = STATE_ATTACK;
					break;
				}
				case KeyEvent.VK_S:{
					mana -= MAX_MANA / 2;
					if(mana <= 0) {
						mana += MAX_MANA / 2;
						state = STATE_STAND;
					}
					else {
						state = STATE_SPECIAL_ATTACK;
					}
					break;
				}
				case KeyEvent.VK_F:{
					if(mana == MAX_MANA) {
						mana = 0;
						state = STATE_FINAL_ATTACK;
					}
					else {
						state = STATE_STAND;
					}
					break;
				}
				case KeyEvent.VK_T:{
					if(transformationIndex < transformations.size() - 1) {
						mana = MAX_MANA;
						state = STATE_INTRO;
						isMoving  = true;
						setAttacks();
						transformationIndex++;
					}
					else {
						state = STATE_STAND;
					}
					break;
				}
				case KeyEvent.VK_E:{
					mana -= 10;
					if(mana <= 0) {
						mana += 10;
						state = STATE_STAND;
					}
					else {
						state = STATE_ESCAPE;
					}
					break;
				}
				default:{
					state = STATE_STAND;
					break;
				}
				}
				setState(state);
			}
		}
	}
	
	public void setState(int state) {
		index = states.indexOf(state);
		this.state = state;
		switch(state) {
		case STATE_DEAD:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case STATE_FINAL_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case STATE_HIT:{
			isJumping = false;
			isMoving = true;
			isHitted = true;
			break;
		}
		case STATE_INTRO:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case STATE_JUMP:{
			isJumping = true;
			isMoving = true;
			t0 = System.currentTimeMillis();//getting the time when the player jumps
			jumpY = y_ref;//setting a y to repaint the player
			break;
		}
		case STATE_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case STATE_RUN:{
			isJumping = false;
			isMoving = false;
			break;
		}
		case STATE_SPECIAL_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case STATE_STAND:{
			isJumping = false;
			isMoving = false;
			isHitted = false;
			break;
		}
		case STATE_TRANSFORM:{
			isJumping = false;
			isMoving = true;
			break;
		}
		}
		if(state == STATE_ATTACK || state == STATE_SPECIAL_ATTACK || state == STATE_FINAL_ATTACK) {
			preparePacketForImages();
		}
		else {
			sendToClient(ActionCode.SET_IMAGES);
		}
	}
	
	public void move() {
		
	}
	
	public void dead() {
		sendGeneralPacket();
		imageIndex++;
		if(imageIndex == imagesLength) {
			endGame();
		}
	}
	
	private void endGame() {
		
	}

	public void finalAttack() {
		finalAttack.preparePacketForRepaint(playerID, state);
		finalAttack.sendPacket();
		finalAttack.move();
	}
	
	public void hit() {
		sendGeneralPacket();
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
		mana++;
		checkMana();
	}
	
	public void intro() {
		sendGeneralPacket();
		imageIndex++;
		if(imageIndex == imagesLength) {
			setState(STATE_STAND);
		}
	}
	
	public void jump() {
		sendJumpPacket();
		mana++;
		checkMana();
		t1 = System.currentTimeMillis();
		delta = t1 - t0;
		delta /= 1000;
		vy -= 9.8 * delta;
		jumpY -= vy;
		if(y_ref - jumpY < 0.001) {
			jumpY = y_ref;
			vy = 30.0;
			setState(STATE_STAND);
		}
	}
	
	public void normalAttack() {
		normalAttack.preparePacketForRepaint(playerID, state);
		normalAttack.sendPacket();
		normalAttack.move();
	}
	
	public void run() {
		sendGeneralPacket();
		mana++;
		checkMana();
		if(!isMirrored) {
			x_ref += vx;
		}
		else {
			x_ref -= vx;
		}
		
		if(x_ref >= bgWidth) {
			isMirrored = !isMirrored;
			x_ref = bgWidth;
			setState(STATE_STAND);
		}
		else if(x_ref <= 20) {
			x_ref = 20;
			isMirrored = !isMirrored;
			setState(STATE_STAND);
		}
		
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
	}
	
	public void specialAttack() {
		specialAttack.preparePacketForRepaint(playerID, state);
		specialAttack.sendPacket();
		specialAttack.move();
	}
	
	public void stand() {
		sendGeneralPacket();
		mana++;
		checkMana();
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
	}
	
	public void escape() {
		if(!isMirrored) {
			x_ref += 200;
			if(x_ref > bgWidth - 20) {
				x_ref = bgWidth - 20;
			}
		}
		else {
			x_ref -= 200;
			if(x_ref <= 20) {
				x_ref = 20;
			}
		}
		sendGeneralPacket();
		setState(STATE_STAND);
	}
	
	private void checkMana() {
		if(mana >= MAX_MANA) {
			mana = MAX_MANA;
		}
		else if(mana <= 0) {
			mana = 0;
		}
	}
	
	public void checkLife() {
		if(life <= 0) {
			life = 0;
			setState(STATE_DEAD);
		}
	}
	
	public void reduceLife() {
		if(enemyMove.getState() == STATE_ATTACK) {
			life -= 10;
		}
		else if(enemyMove.getState() == STATE_SPECIAL_ATTACK) {
			life -= 30;
		}
		else if(enemyMove.getState() == STATE_FINAL_ATTACK) {
			life -= 70;
		}
		checkLife();
	}
	
	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}
	
	public int getImageIndex() {
		return imageIndex;
	}
	
	public void setXRef(int x_ref) {
		this.x_ref = x_ref;
	}
	
	public int getXRef() {
		return x_ref;
	}
	
	public void setYRef(int y_ref) {
		this.y_ref = y_ref;
	}
	
	public int getYRef() {
		return y_ref;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void setImagesLength(int imagesLength) {
		this.imagesLength = imagesLength;
	}
	
	public void setEnemy(Player enemy) {
		this.enemy = enemy;
		enemyMove = new EnemyMove(playerID, connection, enemy);
	}
	
	public EnemyMove getEnemyMove() {
		return enemyMove;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean isMirrored() {
		return isMirrored;
	}
	
	public void setIsMirrored(boolean isMirrored) {
		this.isMirrored = isMirrored;
	}
	
	public String getName() {
		return name;
	}
	
	public int getBgWidth() {
		return bgWidth;
	}

	public void setBgWidth(int bgWidth) {
		this.bgWidth = bgWidth;
	}

	public int getBgHeight() {
		return bgHeight;
	}

	public void setBgHeight(int bgHeight) {
		this.bgHeight = bgHeight;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setBoundsX(int boundsX) {
		this.boundsX = boundsX;
	}
	
	public int getBoundsX() {
		return boundsX;
	}
	
	public void setBoundsY(int boundsY) {
		this.boundsY = boundsY;
	}
	
	public int getBoundsY() {
		return boundsY;
	}
	
	public void setBoundsWidth(int boundsWidth) {
		this.boundsWidth = boundsWidth;
	}
	
	public int getBoundsWidth() {
		return boundsWidth;
	}
	
	public void setBoundsHeight(int boundsHeight) {
		this.boundsHeight = boundsHeight;
	}
	
	public int getBoundsHeight() {
		return boundsHeight;
	}
	
	public void requestPlayerBounds() {
		Packet packet = new Packet(ActionCode.REQUEST_PLAYER_BOUNDS);
		packet.add(playerID);
		connection.sendToClient(packet);
	}
	
	public void requestAttackBounds() {
		Packet packet = new Packet(ActionCode.REQUEST_ATTACK_BOUNDS);
		packet.add(playerID);
		packet.add(state);
		connection.sendToClient(packet);
	}
	
	public void setAttackBounds(ArrayList<String> parameters) {
		if(state == STATE_ATTACK) {
			normalAttack.setBounds(parameters);
		}
		else if(state == STATE_SPECIAL_ATTACK) {
			specialAttack.setBounds(parameters);
		}
		else if(state == STATE_FINAL_ATTACK) {
			finalAttack.setBounds(parameters);
		}
	}
	
	public Rectangle getBounds() {
		return new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);
	}

	public void sendToClient(String actionCode) {
		Packet packet = new Packet(actionCode);
		packet.add(playerID);//1
		packet.add(state);//2
		packet.add(life);//3
		packet.add(mana);//4
		packet.add(index);//5
		packet.add(isMirrored);//6
		connection.sendToClient(packet);
		connection.broadcast(connection.getName(), packet);
	}
	
	public void preparePacketForImages() {
		if(state == STATE_ATTACK) {
			normalAttack.preparePacketForImages(playerID, state);
			normalAttack.sendPacket();
		}
		else if(state == STATE_SPECIAL_ATTACK) {
			specialAttack.preparePacketForImages(playerID, state);
			specialAttack.sendPacket();
		}
		else if(state == STATE_FINAL_ATTACK) {
			finalAttack.preparePacketForImages(playerID, state);
			finalAttack.sendPacket();
		}
	}
	
	public void sendGeneralPacket() {
		Packet packet = new Packet(ActionCode.DRAW_GENERAL);
		packet.add(playerID);//1
		packet.add(life);//2
		packet.add(mana);//3
		packet.add(state);//4
		packet.add(isMirrored);//5
		packet.add(x_ref);//6
		packet.add(y_ref);//7
		packet.add(imageIndex);//8
		connection.sendToClient(packet);
		connection.broadcast(connection.getName(), packet);
	}
	
	private void sendJumpPacket() {
		Packet packet = new Packet(ActionCode.DRAW_JUMP);
		packet.add(playerID);//1
		packet.add(life);//2
		packet.add(mana);//3
		packet.add(state);//4
		packet.add(isMirrored);//5
		packet.add(x_ref);//6
		packet.add(jumpY);//7
		packet.add(imageIndex);//8
		connection.sendToClient(packet);
		connection.broadcast(connection.getName(), packet);
	}
	
	public void setAttackLength(ArrayList<String> parameters) {
		if(state == STATE_ATTACK) {
			normalAttack.setAttackLength(parameters);
		}
		else if(state == STATE_SPECIAL_ATTACK) {
			specialAttack.setAttackLength(parameters);
		}
		else if(state == STATE_FINAL_ATTACK) {
			specialAttack.setAttackLength(parameters);
		}
	}
}
