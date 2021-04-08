package axel.mastroianni.isekaibattle2.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImagesReader {
	
	public static ArrayList<Integer> organizeImages(File[] imagesFiles, 
			ArrayList<File[]>imagesNames){
		int state = 0;
		int index = 0;
		ArrayList<Integer> states = new ArrayList<Integer>();
		for(int i = 0; i < imagesFiles.length; i++) {
			File dir = imagesFiles[i];
			if(dir.isDirectory()) {
				index = open(dir, imagesNames, state, index, states);
				state++;
			}
		}
		return states;
	}
	
	private static int open(File dir, ArrayList<File[]> imagesNames,
			int state, int index, ArrayList<Integer> states) {
		imagesNames.add(dir.listFiles());
		states.add(state);
		if(hasOtherDirectory(dir)) {
			states.remove(states.size() - 1);
			File[] directories = imagesNames.get(index);
			for(int i = 0; i < directories.length; i++) {
				File directory = directories[i];
				index = open(directory, imagesNames,state, index, states);
			}
		}
		index++;
		return index;
	}
	
	private static boolean hasOtherDirectory(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if(file.isDirectory()) {
				return true;
			}
		}
		return false;
	}
	
	public static void chargeImages(ArrayList<File[]> imagesNames, 
			ArrayList<BufferedImage[]> images) {
		for(int i = 0; i < imagesNames.size(); i++) {
			File[] files = imagesNames.get(i);
			BufferedImage[] imagesBuffered = new BufferedImage[files.length];
			for(int j = 0; j < files.length; j++) {
				try {
					if(!files[j].isDirectory()) {
						imagesBuffered[j] = ImageIO.read(files[j]);
					}
				}catch(IOException e) {
					e.printStackTrace();
					imagesBuffered[j] = null;
				}
			}
			if(imagesBuffered[0] != null) {
				images.add(imagesBuffered);
			}
		}
	}
	
	public static ArrayList<Integer> getStateOccurrences(ArrayList<Integer> states){
		int occurrences = 1;
		ArrayList<Integer> stateOccurrences = new ArrayList<Integer>();
		for(int i = 0; i < states.size() - 1; i++) {
			if(states.get(i) == states.get(i + 1)) {
				occurrences++;
			}
			else {
				stateOccurrences.add(occurrences);
				occurrences = 1;
			}
		}
		stateOccurrences.add(occurrences);//adding the stand state
		return stateOccurrences;
	}

}
