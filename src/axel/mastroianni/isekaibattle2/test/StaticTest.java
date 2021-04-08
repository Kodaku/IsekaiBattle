package axel.mastroianni.isekaibattle2.test;

import java.util.ArrayList;

public class StaticTest {
	
	private static ArrayList<Integer> integers = new ArrayList<>();
	
	public static void add(int num) {
		integers.add(num);
	}
	
	public static void print() {
		for (Integer integer : integers) {
			System.out.println(integer.getClass().getSimpleName());
		}
	}

}
