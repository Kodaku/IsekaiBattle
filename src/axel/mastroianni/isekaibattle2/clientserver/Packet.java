package axel.mastroianni.isekaibattle2.clientserver;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Packet {
	
	private ArrayList<String> parameters = new ArrayList<String>();
	
	public Packet() {
		
	}
	
	public Packet(String action) {
		if(action != null && action.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(action, "|");
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				parameters.add(token);
			}
		}
		else {
			parameters.add("");//the action code will be empty
		}
	}
	
	public void add(String param) {
		parameters.add(param);
	}
	
	public void add(int param) {
		parameters.add(param+"");
	}
	
	public void add(boolean param) {
		parameters.add(param+"");
	}
	
	public void add(char param) {
		parameters.add(param+"");
	}
	
	public void add(double param) {
		parameters.add(param+"");
	}
	
	public ArrayList<String> getParameters(){
		return parameters;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < parameters.size(); i++) {
			sb.append(parameters.get(i));
			sb.append("|");
		}
		return sb.toString();
	}
	
	public String getActionCode() {
		return parameters.get(0);
	}

}
