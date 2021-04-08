package axel.mastroianni.isekaibattle2.util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;

public class SuperDatabase {
	
	public static final int ID_1 = 1;
	public static final int ID_2 = 2;
	
	private static Database database1 = new Database();
	private static Database database2 = new Database();
	
	
	/**
	 * this method initialize the two players databases.
	 * The initialization is based on the position of the name in the list of server's names,
	 * the the effective id of the character and the character name
	 * @param playerID
	 * @param nameID
	 * @param name
	 */
	public static void initializeDatabase(int characterID,int playerID, String characterName,
			Player player) {
		if(playerID == ID_1) {
			database1.initialize(characterID, characterName, player);
			database1.setClientDataStructures(0);
		}
		else if(playerID == ID_2) {
			database2.initialize(characterID, characterName, player);
			database2.setClientDataStructures(0);
		}
	}
	
//	public static Database getDatabase1() {
//		return database1;
//	}
//	
//	public static Database getDatabase2() {
//		return database2;
//	}
	
	public static ArrayList<String> getTransformations(int playerID){
		if(playerID == ID_1) {
			return database1.getTransformations();
		}
		else {
			return database2.getTransformations();
		}
	}
	
	public static ArrayList<Integer> getStates(int playerID){
		if(playerID == ID_1) {
			return database1.getStates();
		}
		else {
			return database2.getStates();
		}
	}
	
	public static void setClientDataStructures(int transformationIndex, int playerID) {
		if(playerID == ID_1) {
			database1.setClientDataStructures(transformationIndex);
		}
		else {
			database2.setClientDataStructures(transformationIndex);
		}
	}
	
	public static void setServerDataStructures(int transformationIndex, int playerID) {
		if(playerID == ID_1) {
			database1.setServerDataStructures(transformationIndex);
		}
		else {
			database2.setServerDataStructures(transformationIndex);
		}
	}
	
	public static ArrayList<BufferedImage[]> getNImages(int playerID){
		if(playerID == ID_1) {
			return database1.getActualNImages();
		}
		else {
			return database2.getActualNImages();
		}
	}
	
	public static ArrayList<BufferedImage[]> getMImages(int playerID){
		if(playerID == ID_1) {
			return database1.getActualMImages();
		}
		else {
			return database2.getActualMImages();
		}
	}
	
	public static DrawAttack getNormalAttackNormal(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualNormalAttackNormal();
		}
		else {
			return database2.getActualNormalAttackNormal();
		}
	}
	
	public static DrawAttack getNormalAttackMirrored(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualNormalAttackMirrored();
		}
		else {
			return database2.getActualNormalAttackMirrored();
		}
	}
	
	public static DrawAttack getSpecialAttackNormal(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualSpecialAttackNormal();
		}
		else {
			return database2.getActualSpecialAttackNormal();
		}
	}
	
	public static DrawAttack getSpecialAttackMirrored(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualSpecialAttackMirrored();
		}
		else {
			return database2.getActualSpecialAttackMirrored();
		}
	}
	
	public static DrawAttack getFinalAttackNormal(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualFinalAttackNormal();
		}
		else {
			return database2.getActualFinalAttackNormal();
		}
	}
	
	public static DrawAttack getFinalAttackMirrored(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualFinalAttackMirrored();
		}
		else {
			return database2.getActualFinalAttackMirrored();
		}
	}
	
	public static Attack getNormalAttack(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualNormalAttack();
		}
		else {
			return database2.getActualNormalAttack();
		}
	}
	
	public static Attack getSpecialAttack(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualSpecialAttack();
		}
		else {
			return database2.getActualSpecialAttack();
		}
	}
	
	public static Attack getFinalAttack(int playerID) {
		if(playerID == ID_1) {
			return database1.getActualFinalAttack();
		}
		else {
			return database2.getActualFinalAttack();
		}
	}
	
	public static String getName(int playerID) {
		if(playerID == ID_1) {
			return database1.getCharacterName();
		}
		else {
			return database2.getCharacterName();
		}
	}

}
