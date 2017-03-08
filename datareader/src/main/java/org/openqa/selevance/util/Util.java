package org.openqa.selevance.util;

public class Util {
	public static void sleep(int milisec){
		try {
			Thread.sleep(milisec);
		} catch (InterruptedException e) {
		}
	}
}
