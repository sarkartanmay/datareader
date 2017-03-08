package org.openqa.selevance.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selevance.GlobalExtn;
import org.openqa.selevance.data.action.reader.ActionFile;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

public class ActionData extends GlobalExtn{

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface SelevanceAction
	{
		String file();
		String testcase() default "TestCase";
		String steps() default "Steps";
		String index() default "Index";
		String data() default "Data";
		String format() default "BASE";
		// FIRSTYES - If only First column is YES
	}
	@DataProvider(name = "STANDARD", parallel = true)
	public static Object[][] BaseActionDataProvider(Method method) {		
		SelevanceAction testData = method.getAnnotation(SelevanceAction.class);	
		String fileName =testData.file().toLowerCase(); 
		String testCaseName = testData.testcase();
		String stepsName = testData.steps();
		String indexValue = testData.index();
		String providedData = testData.data();
		
		String format = testData.format();
		if(fileName.trim().length() == 0 || testCaseName.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}
		Matcher matcher;
	    Pattern pattern;
	    
		String regexXLSX ="(.*).xlsx$";		
	    pattern = Pattern.compile(regexXLSX);
	    matcher = pattern.matcher(fileName);	       
		if(matcher.matches()){
			Object[][] obj = ActionFile.xlsxActionReader(
					fileName,
					testCaseName,
					stepsName,
					indexValue,
					providedData,
					format);
			return obj;
		}
		
		return null;
	}
}
