package axel.mastroianni.isekaibattle2.player;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.constantsclass.Initialization;
import axel.mastroianni.isekaibattle2.menu.GamePanel;
import axel.mastroianni.isekaibattle2.util.AttackParser;
import axel.mastroianni.isekaibattle2.util.ImagesReader;

public class DrawPlayerOld {
	
	private static final String DIRECTORY_NAME = "myGame";
	
	private Map<String, File[]> normalImagesNames = new HashMap<>();
	private Map<String, File[]> mirroredImagesNames = new HashMap<>();;
	private Map<String, ArrayList<BufferedImage[]>> nImages = new HashMap<>();
	private Map<String, ArrayList<BufferedImage[]>> mImages = new HashMap<>();
	private Map<String, DrawAttack> transformationFinalAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationNormalAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationSpecialAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationFinalAttackMirrored = new HashMap<>();
	private Map<String, DrawAttack> transformationNormalAttackMirrored = new HashMap<>();
	private Map<String, DrawAttack> transformationSpecialAttackMirrored = new HashMap<>();
	
	private Map<String, ArrayList<Integer>> transformationStates = new HashMap<>();
	private Map<String, ArrayList<Integer>> transformationStatesOccurrences = new HashMap<>();
	
	private ArrayList<String> transformations = new ArrayList<>();
	protected ArrayList<Integer> states = new ArrayList<>();
	protected ArrayList<Integer> stateOccurrences = new ArrayList<>();
	
	private ArrayList<BufferedImage[]> actualNImages = new ArrayList<>();
	private ArrayList<BufferedImage[]> actualMImages = new ArrayList<>();
	private DrawAttack actualNormalAttackNormal;
	private DrawAttack actualNormalAttackMirrored;
	protected DrawAttack actualSpecialAttackNormal;
	private DrawAttack actualSpecialAttackMirrored;
	protected DrawAttack actualFinalAttackNormal;
	private DrawAttack actualFinalAttackMirrored;
	
	private BufferedImage[] images;
	
	private BufferedImage playerImage;
	private BufferedImage background;
	
	private String name;
	private String actualTransformation;
	
	private int transformationIndex = 0;
	
	private int life;
	protected int mana;
	protected int state;
	private int index = 0;
	protected int imageIndex = 0;
	protected int imagesLength = 0;
	
	protected int x_ref = 200;
	protected int y_ref = 300;
	
	private int vx = 10;
	private double vy = 30.0;
	
	private boolean isMoving = false;
	private boolean isHitted = false;
	protected boolean isMirrored = false;
	private boolean isJumping = false;
	
	private double t0;
	private double t1;
	private double delta;
	private double jumpY;
	
	protected DrawPlayerOld enemy;
	
	private GamePanel gamePanel;
	
//	private AttackParser attackParser = new AttackParser();
	
	public DrawPlayerOld(String name, int id, GamePanel gamePanel) {
		this.name = name;
		this.gamePanel = gamePanel;
		life = Initialization.MAX_LIFE;
		mana = Initialization.MAX_MANA;
		state = Initialization.STATE_INTRO;
		//obtaining the paths of the character's directory
		String pathNormal = DIRECTORY_NAME + "/" + id + "_" + name;
		String pathMirrored = pathNormal + " Mirrored";
		//obtaining the character's directory
		File normalDir = new File(pathNormal);
		File mirroredDir = new File(pathMirrored);
		//listing the possible character's transformation directories
		File[] normalTransformation = normalDir.listFiles();
		File[] mirroredTransformation = mirroredDir.listFiles();
		//looping the transformation array(they have the same length)
		for(int i = 0; i < normalTransformation.length; i++) {
			String transformationName = normalTransformation[i].getName();
			//this is in order to have an order of transformations(normal, ssj, ssj2, ssj3,ecc.)
			transformations.add(transformationName);
			//listing the states of a transformation(all the same for all transformation)
			File[] normalTransformationStates = normalTransformation[i].listFiles();
			File[] mirroredTransformationStates = mirroredTransformation[i].listFiles();
			//initializing some useful tools for charging images
			ArrayList<File[]> normalImagesDir = new ArrayList<>();
			ArrayList<File[]> mirroredImagesDir = new ArrayList<>();
			ArrayList<BufferedImage[]> normalImages = new ArrayList<>();
			ArrayList<BufferedImage[]> mirroredImages = new ArrayList<>();
			//if a state has 3 possible variants they are all listed here(while reading files)
			transformationStates.put(transformationName,
					ImagesReader.organizeImages(normalTransformationStates,normalImagesDir));
			ImagesReader.organizeImages(mirroredTransformationStates, mirroredImagesDir);
			ImagesReader.chargeImages(normalImagesDir, normalImages);
			ImagesReader.chargeImages(mirroredImagesDir, mirroredImages);
			normalImagesNames.put(transformationName, normalTransformationStates);
			mirroredImagesNames.put(transformationName, mirroredTransformationStates);
			nImages.put(transformations.get(i), normalImages);
			mImages.put(transformations.get(i), mirroredImages);
			states = transformationStates.get(transformationName);
			stateOccurrences = (ImagesReader.getStateOccurrences(states));
			transformationStatesOccurrences.put(transformationName, stateOccurrences);
			organizeAttacks(transformationName);
		}
		//counting how many variants of a state are there
		setCharacterDataStructures();
	}
	
	private void organizeAttacks(String transformationName) {
		organizeAttack(Initialization.STATE_ATTACK, transformationNormalAttackNormal,
				transformationNormalAttackMirrored, transformationName);
		organizeAttack(Initialization.STATE_SPECIAL_ATTACK, transformationSpecialAttackNormal,
				transformationSpecialAttackMirrored, transformationName);
		organizeAttack(Initialization.STATE_FINAL_ATTACK, transformationFinalAttackNormal,
				transformationFinalAttackMirrored, transformationName);
	}
	
	private void organizeAttack(int state, Map<String, DrawAttack> attackNormal, 
			Map<String, DrawAttack> attackMirrored, String transformationName) {
		AttackParser attackParser = new AttackParser();
		stateOccurrences = transformationStatesOccurrences.get(transformationName);
		//getting attack images from the map
		ArrayList<BufferedImage[]> normalImages = nImages.get(transformationName);
		ArrayList<BufferedImage[]> mirroredImages = mImages.get(transformationName);
		//getting the states the character has
		File[] normalStates = normalImagesNames.get(transformationName);
		DrawAttack normalAttack;
		DrawAttack mirroredAttack;
		//get the directory of the specific attack we're interested in
		String directoryName = normalStates[state].getName();
		//see the method Javadoc
		int[] attackTypeAndNumber = parseAttackType(directoryName);
		//how many folder does the attack have?
		int attacksNumber = stateOccurrences.get(state);
		int imagesStartIndex = states.indexOf(state);//every attack it's reinitialized
		DrawAttack[] attacks = attackParser.selectClientAttack(normalImages, mirroredImages,
				attackTypeAndNumber, imagesStartIndex, attacksNumber, this);
		normalAttack = attacks[0];
		mirroredAttack = attacks[1];
		attackNormal.put(transformationName, normalAttack);
		attackMirrored.put(transformationName, mirroredAttack);
	}
	
	private int[] parseAttackType(String directoryName) {
		StringTokenizer st = new StringTokenizer(directoryName, "_");
		int tokenNumber = st.countTokens() - 1;//skipping the attack name(normal, special or final)
		int[] attackTypeAndNumber = new int[tokenNumber];
		st.nextToken();//passing the first(the name of the character) so skip it
		int index = 0;
		while(st.hasMoreTokens()) {
			String attackType = st.nextToken();//interested in the integer after the _
			attackTypeAndNumber[index] = Integer.parseInt(attackType);
			index++;
		}
		return attackTypeAndNumber;
	}
	
	private void setCharacterDataStructures() {
		actualTransformation = transformations.get(transformationIndex);
		states = transformationStates.get(actualTransformation);
		actualNImages = nImages.get(actualTransformation);
		actualMImages = mImages.get(actualTransformation);
		actualNormalAttackNormal = transformationNormalAttackNormal.get(actualTransformation);
		actualNormalAttackMirrored = transformationNormalAttackMirrored.get(actualTransformation);
		actualSpecialAttackNormal = transformationSpecialAttackNormal.get(actualTransformation);
		actualSpecialAttackMirrored = transformationSpecialAttackMirrored.get(actualTransformation);
		actualFinalAttackNormal = transformationFinalAttackNormal.get(actualTransformation);
		actualFinalAttackMirrored = transformationFinalAttackMirrored.get(actualTransformation);
		setAttacksEnemy();
	}
	
	public void convertInState(int keyCode) {
		if(!isMoving) {
			if(!isHitted) {
				switch(keyCode) {
				case KeyEvent.VK_UP:{
					state = Initialization.STATE_JUMP;
					isJumping = true;
					break;
				}
				case KeyEvent.VK_RIGHT:{
					isMirrored = false;
					state = Initialization.STATE_RUN;
					break;
				}
				case KeyEvent.VK_LEFT:{
					isMirrored = true;
					state = Initialization.STATE_RUN;
					break;
				}
				case KeyEvent.VK_N:{
					mana += 5;
					checkMana();
					state = Initialization.STATE_ATTACK;
					break;
				}
				case KeyEvent.VK_S:{
					mana -= Initialization.MAX_MANA / 2;
					if(mana <= 0) {
						mana += Initialization.MAX_MANA / 2;
						state = Initialization.STATE_STAND;
					}
					else {
						state = Initialization.STATE_SPECIAL_ATTACK;
					}
					break;
				}
				case KeyEvent.VK_F:{
					if(mana == Initialization.MAX_MANA) {
						mana = 0;
						state = Initialization.STATE_FINAL_ATTACK;
					}
					else {
						state = Initialization.STATE_STAND;
					}
					break;
				}
				case KeyEvent.VK_T:{
					if(transformationIndex < transformations.size() - 1) {
						mana = Initialization.MAX_MANA;
						state = Initialization.STATE_INTRO;
						isMoving  = true;
						transformationIndex++;
						setCharacterDataStructures();
					}
					else {
						state = Initialization.STATE_STAND;
					}
					break;
				}
				case KeyEvent.VK_E:{
					mana -= 10;
					System.out.println("Escape " + mana);
					if(mana <= 0) {
						mana += 10;
						state = Initialization.STATE_STAND;
					}
					else {
						state = Initialization.STATE_ESCAPE;
					}
					break;
				}
				default:{
					state = Initialization.STATE_STAND;
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
		case Initialization.STATE_DEAD:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case Initialization.STATE_FINAL_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case Initialization.STATE_HIT:{
			isJumping = false;
			isMoving = true;
			isHitted = true;
			break;
		}
		case Initialization.STATE_INTRO:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case Initialization.STATE_JUMP:{
			isJumping = true;
			isMoving = true;
			t0 = System.currentTimeMillis();//getting the time when the player jumps
			jumpY = y_ref;//setting a y to repaint the player
			break;
		}
		case Initialization.STATE_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case Initialization.STATE_RUN:{
			isJumping = false;
			isMoving = false;
			break;
		}
		case Initialization.STATE_SPECIAL_ATTACK:{
			isJumping = false;
			isMoving = true;
			break;
		}
		case Initialization.STATE_STAND:{
			isJumping = false;
			isMoving = false;
			isHitted = false;
			break;
		}
		case Initialization.STATE_TRANSFORM:{
			isJumping = false;
			isMoving = true;
			break;
		}
		}
		if(state == Initialization.STATE_ATTACK || 
				state == Initialization.STATE_SPECIAL_ATTACK ||
				state == Initialization.STATE_FINAL_ATTACK) {
			setAttackImages();
		}
		else {
			setImages();
		}
	}
	
	private void setImages() {
		imageIndex = 0;
		if(index == -1) {
			return;
		}
		if(!isMirrored) {
			images = actualNImages.get(index);
		}
		else {
			images = actualMImages.get(index);
		}
		imagesLength = images.length;
		playerImage = images[0];
	}
	
	private void setAttackImages() {
		if(state == Initialization.STATE_ATTACK) {
			if(!isMirrored) {
				actualNormalAttackNormal.setImages();
			}
			else {
				actualNormalAttackMirrored.setImages();
			}
		}
		else if(state == Initialization.STATE_SPECIAL_ATTACK) {
			if(!isMirrored) {
				actualSpecialAttackNormal.setImages();
			}
			else {
				actualSpecialAttackMirrored.setImages();
			}
		}
		else if(state == Initialization.STATE_FINAL_ATTACK) {
			if(!isMirrored) {
				actualFinalAttackNormal.setImages();
			}
			else {
				actualFinalAttackMirrored.setImages();
			}
		}
	}
	
	public void draw(Graphics g) {
		switch(state) {
		case Initialization.STATE_DEAD:{
			drawGeneral(g);
			break;
		}
		case Initialization.STATE_FINAL_ATTACK:{
			if(isMirrored) {
				actualFinalAttackMirrored.draw(g);
			}
			else {
				actualFinalAttackNormal.draw(g);
			}
			break;
		}
		case Initialization.STATE_HIT:{
			drawGeneral(g);
			break;
		}
		case Initialization.STATE_INTRO:{
			drawIntro(g);
			break;
		}
		case Initialization.STATE_JUMP:{
			drawJump(g);
			break;
		}
		case Initialization.STATE_ATTACK:{
			if(isMirrored) {
				actualNormalAttackMirrored.draw(g);
			}
			else {
				actualNormalAttackNormal.draw(g);
			}
			break;
		}
		case Initialization.STATE_RUN:{
			drawGeneral(g);
			break;
		}
		case Initialization.STATE_SPECIAL_ATTACK:{
			if(isMirrored) {
				actualSpecialAttackMirrored.draw(g);
			}
			else {
				actualSpecialAttackNormal.draw(g);
			}
			break;
		}
		case Initialization.STATE_STAND:{
			drawGeneral(g);
			break;
		}
		}
	}
	
	private void drawIntro(Graphics g) {
		int index = states.indexOf(state);
		BufferedImage[] images;
		int x = 0;
		int y = 0;
		if(!isMirrored) {
			images = actualNImages.get(index);
			x = x_ref - images[imageIndex].getWidth() / 2;
			y = y_ref - images[imageIndex].getHeight();
		}
		else {
			images = actualMImages.get(index);
			x = x_ref - images[imageIndex].getWidth() / 2;
			y = y_ref - images[imageIndex].getHeight();
		}
		imagesLength = images.length;
		playerImage = images[0];
		g.drawImage(images[imageIndex], x, y, null);
	}
	
	public void drawGeneral(Graphics g) {
		int x = 0;
		int y = 0;
		x = x_ref - images[imageIndex].getWidth() / 2;
		y = y_ref - images[imageIndex].getHeight();
		g.drawImage(images[imageIndex], x, (int)y, null);
	}
	
	private void drawJump(Graphics g) {
		int x = 0;
		double y = 0;
		x = x_ref - images[imageIndex].getWidth() / 2;
		y = jumpY - images[imageIndex].getHeight();
		g.drawImage(images[imageIndex], x, (int)y, null);
	}
	
	public void move() {
		
	}
	
	public void dead() {
		imageIndex++;
		if(imageIndex == imagesLength) {
			endGame();
		}
	}
	
	public void finalAttack() {
		if(isMirrored) {
			actualFinalAttackMirrored.move();
		}
		else {
			actualFinalAttackNormal.move();
		}
	}
	
	public void hit() {
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
		mana++;
		checkMana();
	}
	
	public void intro() {
		imageIndex++;
		if(imageIndex == imagesLength) {
			setState(Initialization.STATE_STAND);
		}
	}
	
	public void jump() {
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
			setState(Initialization.STATE_STAND);
		}
	}
	
	public void normalAttack() {
		if(isMirrored) {
			actualNormalAttackMirrored.move();
		}
		else {
			actualNormalAttackNormal.move();
		}
	}
	
	public void run() {
		mana++;
		checkMana();
		if(!isMirrored) {
			x_ref += vx;
		}
		else {
			x_ref -= vx;
		}
		
		if(x_ref >= background.getWidth()) {
			isMirrored = !isMirrored;
			x_ref = background.getWidth();
			setState(Initialization.STATE_STAND);
		}
		else if(x_ref <= images[imageIndex].getWidth()) {
			x_ref = images[imageIndex].getWidth();
			isMirrored = !isMirrored;
			setState(Initialization.STATE_STAND);
		}
		
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
	}
	
	public void specialAttack() {
		if(isMirrored) {
			actualSpecialAttackMirrored.move();
		}
		else {
			actualSpecialAttackNormal.move();
		}
	}
	
	public void stand() {
		mana++;
		checkMana();
		imageIndex++;
		imageIndex = imageIndex % imagesLength;
	}
	
	public void escape() {
		if(!isMirrored) {
			x_ref += 200;
			if(x_ref > background.getWidth() - images[imageIndex].getWidth()) {
				x_ref = background.getWidth() - images[imageIndex].getWidth();
			}
		}
		else {
			x_ref -= 200;
			if(x_ref <= images[imageIndex].getWidth()) {
				x_ref = images[imageIndex].getWidth();
			}
		}
		setState(Initialization.STATE_STAND);
	}
	
	private void checkMana() {
		if(mana >= Initialization.MAX_MANA) {
			mana = Initialization.MAX_MANA;
		}
		else if(mana <= 0) {
			mana = 0;
		}
	}
	
	private void checkLife() {
		if(life <= 0) {
			life = 0;
			setState(Initialization.STATE_DEAD);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getState() {
		return state;
	}
	
	public int getXRef() {
		return x_ref;
	}
	
	public int getYRef() {
		return y_ref;
	}
	
	public void setXRef(int x_ref) {
		this.x_ref = x_ref;
	}
	
	public void setYRef(int y_ref) {
		this.y_ref = y_ref;
	}
	
	public int getLife() {
		return life;
	}
	
	public int getMana() {
		return mana;
	}
	
	public boolean isMirrored() {
		return isMirrored;
	}
	
	public void setBackground(BufferedImage background) {
		this.background = background;
	}
	
	public void setEnemy(DrawPlayerOld enemy) {
		this.enemy = enemy;
	}
	
	public DrawPlayerOld getEnemy() {
		return enemy;
	}
	
	public void setAttacksEnemy() {
		actualNormalAttackNormal.setEnemy(enemy);
		actualNormalAttackMirrored.setEnemy(enemy);
		actualSpecialAttackNormal.setEnemy(enemy);
		actualSpecialAttackMirrored.setEnemy(enemy);
		actualFinalAttackNormal.setEnemy(enemy);
		actualFinalAttackMirrored.setEnemy(enemy);
	}
	
	public void setAttacksCoordinates() {
		actualNormalAttackNormal.setPlayerCoordinates();
		actualNormalAttackMirrored.setPlayerCoordinates();
		actualSpecialAttackNormal.setPlayerCoordinates();
		actualSpecialAttackMirrored.setPlayerCoordinates();
		actualFinalAttackNormal.setPlayerCoordinates();
		actualFinalAttackMirrored.setPlayerCoordinates();
	}
	
	public Rectangle getBounds() {
		if(playerImage != null) {
			int width = playerImage.getWidth();
			int height = playerImage.getHeight();
			Rectangle bounds = new Rectangle(x_ref, y_ref,width, height);
			return bounds;
		}
		return new Rectangle();
	}
	
	public boolean isAttacking() {
		return state == Initialization.STATE_ATTACK ||
				state == Initialization.STATE_SPECIAL_ATTACK
				|| state == Initialization.STATE_FINAL_ATTACK;
	}
	
	public void reduceLife() {
		if(enemy.getState() == Initialization.STATE_ATTACK) {
			life -= 10;
		}
		else if(enemy.getState() == Initialization.STATE_SPECIAL_ATTACK) {
			life -= 30;
		}
		else if(enemy.getState() == Initialization.STATE_FINAL_ATTACK) {
			life -= 70;
		}
		checkLife();
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public boolean isJumping() {
		return isJumping;
	}
	
	public BufferedImage getBackground() {
		return background;
	}
	
	public void setIsMirrored(boolean isMirrored) {
		this.isMirrored = isMirrored;
	}
	
	private void endGame() {
		gamePanel.endGame();
	}
	
	public void reset() {
		normalImagesNames.clear();
		mirroredImagesNames.clear();
		nImages.clear();
		mImages.clear();
		transformationFinalAttackNormal.clear();
		transformationNormalAttackNormal.clear();
		transformationSpecialAttackNormal.clear();
		transformationFinalAttackMirrored.clear();
		transformationNormalAttackMirrored.clear();
		transformationSpecialAttackMirrored.clear();
		
		transformationStates.clear();
		transformationStatesOccurrences.clear();
		
		transformations.clear();
		states.clear();
		stateOccurrences.clear();
		
		actualNImages.clear();
		actualMImages.clear();
	}
	
}
