package org.openqa.selevance.util;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

public class StringManipulator {
	
	public String trim_String_Length(String text, int stringLimit){
		//System.out.println("Given text length was : " +text.length());
		text = StringUtils.left(text, stringLimit);
		//System.out.println("Trim text length was : " +text.length());
		return text;
	}
	
	@Test
	public void test(){
		System.out.println( trim_String_Length ("Ademiban's version is marginally more efficient, "
				+ "but he'd got the 2nd parameter of substring wrong. "
				+ "That's 3 answers that made that mistake", 100 ));
	}
}