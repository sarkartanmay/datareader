package org.openqa.selevance.util;

import java.util.Random;

public class RandomStringGenerator {
	public static enum Mode {
	    ALPHA, ALPHANUMERIC, NUMERIC 
	}
	
	public static String randomString(int length, Mode mode) throws Exception {

		StringBuffer buffer = new StringBuffer();
		String characters = "";

		if(Mode.ALPHA.toString().contentEquals(mode.name()))
		{
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		}
		else if (Mode.ALPHANUMERIC.toString().contentEquals(mode.name()))
		{
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		}
		else if (Mode.NUMERIC.toString().contentEquals(mode.name()))
		{
			characters = "1234567890";
		}
		
		/*switch(mode){
		
		case ALPHA:
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			break;
		
		case ALPHANUMERIC:
			characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			break;
	
		case NUMERIC:
			characters = "1234567890";
		    break;
		}*/
		
		int charactersLength = characters.length();

		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		return buffer.toString();
	}
	
	
	public static int randInt(int min, int max) {
	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt(max);
	    String strNum = Integer.toString(randomNum).replaceFirst("0", "");
	    randomNum = Integer.parseInt(strNum);
	    if(randomNum >=max || randomNum <=0)
	    	randomNum = min;
	    return randomNum;
	}
}
 