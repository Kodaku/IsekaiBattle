package axel.mastroianni.isekaibattle2.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;

public class Database {
	
	private static final String DIRECTORY_NAME = "myGame";
	
	private String characterName;
	
	private ArrayList<String> transformations = new ArrayList<>();
	
	private Map<String, ArrayList<Integer>> transformationStates = new HashMap<>();
	private Map<String, ArrayList<Integer>> transformationStatesOccurrences = new HashMap<>();
	
	private Map<String, File[]> normalImagesNames = new HashMap<>();
	private Map<String, File[]> mirroredImagesNames = new HashMap<>();
	
	private Map<String, ArrayList<BufferedImage[]>> nImages = new HashMap<>();
	private Map<String, ArrayList<BufferedImage[]>> mImages = new HashMap<>();
	
	private ArrayList<Integer> states = new ArrayList<>();
	private ArrayList<Integer> stateOccurrences = new ArrayList<>();
	
	private Map<String, DrawAttack> transformationNormalAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationNormalAttackMirrored = new HashMap<>();
	private Map<String, DrawAttack> transformationSpecialAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationSpecialAttackMirrored = new HashMap<>();
	private Map<String, DrawAttack> transformationFinalAttackNormal = new HashMap<>();
	private Map<String, DrawAttack> transformationFinalAttackMirrored = new HashMap<>();
	
	private ArrayList<BufferedImage[]> actualNImages = new ArrayList<>();
	private ArrayList<BufferedImage[]> actualMImages = new ArrayList<>();
	private DrawAttack actualNormalAttackNormal;
	private DrawAttack actualNormalAttackMirrored;
	private DrawAttack actualSpecialAttackNormal;
	private DrawAttack actualSpecialAttackMirrored;
	private DrawAttack actualFinalAttackNormal;
	private DrawAttack actualFinalAttackMirrored;
	
	private Map<String, Attack> transformationNormalAttack = new HashMap<>();
	private Map<String, Attack> transformationSpecialAttack = new HashMap<>();
	private Map<String, Attack> transformationFinalAttack = new HashMap<>();
	
	private Attack actualNormalAttack;
	private Attack actualSpecialAttack;
	private Attack actualFinalAttack;

	public void initialize(int characterID, String characterName, Player player) {
		this.characterName = characterName;
		
		//obtaining the paths of the character's directory
		String pathNormal = DIRECTORY_NAME + "/" + characterID + "_" + characterName;
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
			organizeAttacks(transformationName, player);
		}
	}
	
	private void organizeAttacks(String transformationName, Player player) {
		organizeClientAttack(Player.STATE_ATTACK, transformationNormalAttackNormal,
				transformationNormalAttackMirrored, transformationName);
		organizeClientAttack(Player.STATE_SPECIAL_ATTACK, transformationSpecialAttackNormal,
				transformationSpecialAttackMirrored, transformationName);
		organizeClientAttack(Player.STATE_FINAL_ATTACK, transformationFinalAttackNormal,
				transformationFinalAttackMirrored, transformationName);
		
		organizeServerAttack(Player.STATE_ATTACK, transformationNormalAttack,
				transformationName, player);
		organizeServerAttack(Player.STATE_SPECIAL_ATTACK, transformationSpecialAttack,
				transformationName, player);
		organizeServerAttack(Player.STATE_FINAL_ATTACK, transformationFinalAttack,
				transformationName, player);
	}

	private void organizeClientAttack(int state, Map<String, DrawAttack> attackNormal, 
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
//		DrawAttack[] attacks = attackParser.selectClientAttack(normalImages, mirroredImages,
//				attackTypeAndNumber, imagesStartIndex, attacksNumber);
//		normalAttack = attacks[0];
//		mirroredAttack = attacks[1];
//		attackNormal.put(transformationName, normalAttack);
//		attackMirrored.put(transformationName, mirroredAttack);
	}
	
	private void organizeServerAttack(int state, Map<String, Attack> mapAttack,
			String transformationName, Player player) {
		Attack attack;
		AttackParser attackParser = new AttackParser();
		//getting the states the character has
		File[] normalStates = normalImagesNames.get(transformationName);
		//get the directory of the specific attack we're interested in
		String directoryName = normalStates[state].getName();
		//see the method Javadoc
		int[] attackTypeAndNumber = parseAttackType(directoryName);
		attack = attackParser.setServerAttack(player, attackTypeAndNumber);
		mapAttack.put(transformationName, attack);
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
	
	public void setClientDataStructures(int transformationIndex) {
		String actualTransformation = transformations.get(transformationIndex);
		states = transformationStates.get(actualTransformation);
		actualNImages = nImages.get(actualTransformation);
		actualMImages = mImages.get(actualTransformation);
		actualNormalAttackNormal = transformationNormalAttackNormal.get(actualTransformation);
		actualNormalAttackMirrored = transformationNormalAttackMirrored.get(
				actualTransformation);
		actualSpecialAttackNormal = transformationSpecialAttackNormal.get(actualTransformation);
		actualSpecialAttackMirrored = transformationSpecialAttackMirrored.get(
				actualTransformation);
		actualFinalAttackNormal = transformationFinalAttackNormal.get(actualTransformation);
		actualFinalAttackMirrored = transformationFinalAttackMirrored.get(actualTransformation);
	}
	
	public void setServerDataStructures(int transformationIndex) {
		String actualTransformation = transformations.get(transformationIndex);
		actualNormalAttack = transformationNormalAttack.get(actualTransformation);
		actualSpecialAttack = transformationSpecialAttack.get(actualTransformation);
		actualFinalAttack = transformationFinalAttack.get(actualTransformation);
	}
	
	public ArrayList<String> getTransformations(){
		return transformations;
	}
	
	public ArrayList<Integer> getStates(){
		return states;
	}
	
	public ArrayList<BufferedImage[]> getActualNImages() {
		return actualNImages;
	}

	public ArrayList<BufferedImage[]> getActualMImages() {
		return actualMImages;
	}

	public DrawAttack getActualNormalAttackNormal() {
		return actualNormalAttackNormal;
	}

	public DrawAttack getActualNormalAttackMirrored() {
		return actualNormalAttackMirrored;
	}

	public DrawAttack getActualSpecialAttackNormal() {
		return actualSpecialAttackNormal;
	}

	public DrawAttack getActualSpecialAttackMirrored() {
		return actualSpecialAttackMirrored;
	}

	public DrawAttack getActualFinalAttackNormal() {
		return actualFinalAttackNormal;
	}

	public DrawAttack getActualFinalAttackMirrored() {
		return actualFinalAttackMirrored;
	}

	public Attack getActualNormalAttack() {
		return actualNormalAttack;
	}

	public Attack getActualSpecialAttack() {
		return actualSpecialAttack;
	}

	public Attack getActualFinalAttack() {
		return actualFinalAttack;
	}
	
	public String getCharacterName() {
		return characterName;
	}
	
}
