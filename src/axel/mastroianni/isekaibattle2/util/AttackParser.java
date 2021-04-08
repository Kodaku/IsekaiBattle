package axel.mastroianni.isekaibattle2.util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import axel.mastroianni.isekaibattle2.attack.DrawAttack;
import axel.mastroianni.isekaibattle2.attack.bullet.DrawBulletAttack;
import axel.mastroianni.isekaibattle2.attack.bullet.DrawBulletTargetAttack;
import axel.mastroianni.isekaibattle2.attack.bullet.DrawTransformingAndNotReturningAttack;
import axel.mastroianni.isekaibattle2.attack.bullet.DrawTransformingAndReturningAttack;
import axel.mastroianni.isekaibattle2.attack.physical.DrawPhysicalAttack;
import axel.mastroianni.isekaibattle2.attack.running.DrawRunningTargetAttack;
import axel.mastroianni.isekaibattle2.attack.running.DrawRunningTeletransportTargetAttack;
import axel.mastroianni.isekaibattle2.attack.target.DrawMovingTargetAttack;
import axel.mastroianni.isekaibattle2.attack.target.DrawTargetAttack;
import axel.mastroianni.isekaibattle2.clientserver.Player;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.Attack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet.BulletAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet.BulletTargetAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet.TransformingAndNotReturningAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.bullet.TransformingAndReturningAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.physical.PhysicalAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.running.RunningTargetAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.running.RunningTeletransportTargetAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.target.MovingTargetAttack;
import axel.mastroianni.isekaibattle2.clientserver.attacksalgorithms.target.TargetAttack;
import axel.mastroianni.isekaibattle2.player.DrawPlayerOld;

public class AttackParser {
	
	private static final int PHYSICAL_ATTACK = 0;
	private static final int BULLET_ATTACK = 2;
	private static final int TARGET_ATTACK = 3;
	private static final int BULLET_TARGET_ATTACK = 4;
	private static final int RUNNING_TARGET_ATTACK = 5;
	private static final int RUNNING_TELETRANSPORT_ATTACK = 6;
	private static final int MOVING_TARGET_ATTACK = 7;
	private static final int TRANSFORMING_AND_RETURNING_ATTACK = 8;
	private static final int TRANSFORMING_AND_NOT_RETURNING_ATTACK = 9;
	
	private ArrayList<BufferedImage[]> attacksSequencesNormal = new ArrayList<>();
	private ArrayList<BufferedImage[]> attacksSequencesMirrored = new ArrayList<>();
	
	private ArrayList<ArrayList<BufferedImage[]>> sequencesNormal = new ArrayList<>();
	private ArrayList<ArrayList<BufferedImage[]>> sequencesMirrored = new ArrayList<>();
	
	/**
	 * returning an attack based on:
	 * 1. attackTypeNumber;
	 * 2. attackStartIndex;
	 * 3. attacksNumber;
	 * 4. Two ArrayList<BufferdImage[]>
	 * returning a vector of attack containing the normal and mirrored attack
	 */
	public DrawAttack[] selectClientAttack(ArrayList<BufferedImage[]> normalImages,
			ArrayList<BufferedImage[]> mirroredImages, int[] indexes, int attackStartIndex,
			int attacksNumber, DrawPlayerOld player) {
		fillingTheBigArray(normalImages, mirroredImages, attackStartIndex, attacksNumber);
		DrawAttack[] attacks = new DrawAttack[2];
		DrawAttack normalAttack = null;
		DrawAttack mirroredAttack = null;
		//splitting the big array into the subfolders
		for(int i = 0; i < indexes[1]; i++) {
			ArrayList<BufferedImage[]> seqN = new ArrayList<BufferedImage[]> 
					(attacksSequencesNormal.subList(i,i+1));
			ArrayList<BufferedImage[]> seqM = new ArrayList<BufferedImage[]>
					(attacksSequencesMirrored.subList(i,i+1));
			sequencesNormal.add(seqN);
			sequencesMirrored.add(seqM);
		}
		//switching the attack type
		switch(indexes[0]) {
		case PHYSICAL_ATTACK:{
			//only one folder
			normalAttack = new DrawPhysicalAttack(attacksSequencesNormal, player);
			mirroredAttack = new DrawPhysicalAttack(attacksSequencesMirrored, player);
			break;
		}
		case BULLET_ATTACK:{
			/**
			 * two folders:
			 * 1. Moving
			 * 2. Bullet
			 */
			normalAttack = new DrawBulletAttack(sequencesNormal.get(0), sequencesNormal.get(1), 
					player);
			mirroredAttack = new DrawBulletAttack(sequencesMirrored.get(0), sequencesMirrored.get(1), 
					player);
			break;
		}
		case TARGET_ATTACK:{
			/**
			 * 2 folders:
			 * 1. Moving
			 * 2. Attack
			 */
			normalAttack = new DrawTargetAttack(sequencesNormal.get(0), sequencesNormal.get(1), 
					player);
			mirroredAttack = new DrawTargetAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), player);
			break;
		}
		case BULLET_TARGET_ATTACK:{
			/**
			 * 3 folders:
			 * 1. Moving
			 * 2. Bullet
			 * 3. Target
			 */
			normalAttack = new DrawBulletTargetAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2), player);
			mirroredAttack = new DrawBulletTargetAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2), player);
			break;
		}
		case RUNNING_TARGET_ATTACK:{
			/** 3 folders:
			 * 1. Moving
			 * 2. Running to the enemy
			 * 3. Target(when hitting the enemy)
			 */
			normalAttack = new DrawRunningTargetAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2), player);
			mirroredAttack = new DrawRunningTargetAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2), player);
			break;
		}
		case RUNNING_TELETRANSPORT_ATTACK:{
			/**
			 * 4 folders:
			 * 1. Moving
			 * 2. Running
			 * 3. Teletransport to the enemy
			 * 4. Target(when hitting the enemy)
			 */
			normalAttack = new DrawRunningTeletransportTargetAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2), sequencesNormal.get(3), player);
			mirroredAttack = new DrawRunningTeletransportTargetAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2),
					sequencesMirrored.get(3), player);
			break;
		}
		case MOVING_TARGET_ATTACK:{
			/**
			 * 3 folders:
			 * 1. Moving
			 * 2. Attack far from enemy
			 * 3. Target
			 */
			normalAttack = new DrawMovingTargetAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2), player);
			mirroredAttack = new DrawMovingTargetAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2), player);
			break;
		}
		case TRANSFORMING_AND_RETURNING_ATTACK:{
			/**
			 * 4 Folders:
			 * 1. Transforming and Moving;
			 * 2. Bullet
			 * 3. Target(when hitting the enemy)
			 * 4. Returning to the previous transformation
			 */
			normalAttack = new DrawTransformingAndReturningAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2),
					sequencesNormal.get(3), player);
			mirroredAttack = new DrawTransformingAndReturningAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2), 
					sequencesMirrored.get(3), player);
			break;
		}
		case TRANSFORMING_AND_NOT_RETURNING_ATTACK:{
			/**
			 * 5 folders:
			 * 1. First move
			 * 2. Bullet
			 * 3. When hitted do second move
			 * 4. Target(when hitting the enemy with the second move)
			 * 5. Returning to the previous state not transformation
			 */
			normalAttack = new DrawTransformingAndNotReturningAttack(sequencesNormal.get(0),
					sequencesNormal.get(1), sequencesNormal.get(2), sequencesNormal.get(3),
					sequencesNormal.get(4), player);
			mirroredAttack = new DrawTransformingAndNotReturningAttack(sequencesMirrored.get(0),
					sequencesMirrored.get(1), sequencesMirrored.get(2), sequencesMirrored.get(3),
					sequencesMirrored.get(4), player);
			break;
		}
		}
		
		attacks[0] = normalAttack;
		attacks[1] = mirroredAttack;
		return attacks;
	}
	
	public Attack setServerAttack(Player player, int[] indexes) {
		Attack attack = null;
		switch(indexes[0]) {
		case PHYSICAL_ATTACK:{
			attack = new PhysicalAttack(player);
			break;
		}
		case BULLET_ATTACK:{
			attack = new BulletAttack(player);
			break;
		}
		case TARGET_ATTACK:{
			attack = new TargetAttack(player);
			break;
		}
		case BULLET_TARGET_ATTACK:{
			attack = new BulletTargetAttack(player);
			break;
		}
		case RUNNING_TARGET_ATTACK:{
			attack = new RunningTargetAttack(player);
			break;
		}
		case RUNNING_TELETRANSPORT_ATTACK:{
			attack = new RunningTeletransportTargetAttack(player);
			break;
		}
		case MOVING_TARGET_ATTACK:{
			attack = new MovingTargetAttack(player);
			break;
		}
		case TRANSFORMING_AND_RETURNING_ATTACK:{
			attack = new TransformingAndReturningAttack(player);
			break;
		}
		case TRANSFORMING_AND_NOT_RETURNING_ATTACK:{
			attack = new TransformingAndNotReturningAttack(player);
			break;
		}
		}
		return attack;
	}
	/**
	 * this method fills the big array with all the possible attacks without caring what type
	 * they are
	 * @param normalImages
	 * @param mirroredImages
	 * @param attackStartIndex
	 * @param attacksNumber
	 */
	private void fillingTheBigArray(ArrayList<BufferedImage[]> normalImages,
			ArrayList<BufferedImage[]> mirroredImages, int attackStartIndex,
			int attacksNumber) {
		//here put the various attack sequences
		for(int i = 0; i < attacksNumber; i++) {
			attacksSequencesNormal.add(normalImages.get(i + attackStartIndex));
			attacksSequencesMirrored.add(mirroredImages.get(i + attackStartIndex));
		}
	}
	
	public void clearAll() {
		attacksSequencesNormal.clear();
		attacksSequencesMirrored.clear();
		sequencesNormal.clear();
		sequencesMirrored.clear();
	}

}
