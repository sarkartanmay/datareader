package org.openqa.selevance.datareader;

import java.util.HashMap;

import org.openqa.selevance.data.TestData;
import org.openqa.selevance.data.TestData.SelevanceBasic;
import org.testng.annotations.Test;

public class Sample {

	@Test(description = "Sample Test case with XLSX Test data", 
			dataProviderClass= TestData.class,
			dataProvider = "STANDARD" )
	@SelevanceBasic(file = "src/test/resources/data/data1.xlsx", 
			sheet ="Sheet2" , format ="FIRSTYES")
	public void doTest1(HashMap<String, String> testdata){	
		String name = testdata.get("Fname") + " " +testdata.get("Lname") ;
		System.out.println(name);
}
}
