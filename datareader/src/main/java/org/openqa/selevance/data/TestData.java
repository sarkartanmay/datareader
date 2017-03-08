package org.openqa.selevance.data;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selevance.GlobalExtn;
import org.openqa.selevance.data.reader.BasicFile;
import org.openqa.selevance.data.reader.DBFile;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;


/**
 * Read XLSX/XLS/XML/CSV/JSON File for Test data
 * @author Tanmay Sarkar
 * @since 6th March 2015
 */
public class TestData extends GlobalExtn{
	
	/**
	 * @param String file : Test Data file name (*.xls / *.xlsx / *.csv / *.json) <br/>
	 * @param String sheet : For excel sheet name , use node name for XML	<br/>
	 * 
	 * @author Tanmay Sarkar
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface SelevanceBasic
	{
		String file();
		String sheet() default "test";
		String format() default "BASE";
		// FIRSTYES - If only First column is YES
	}
	
	@DataProvider(name = "STANDARD", parallel = true)
	public static Object[][] BaseDataProvider(Method method) {		
		SelevanceBasic testData = method.getAnnotation(SelevanceBasic.class);	
		String fileName =testData.file().toLowerCase(); 
		String sheetName = testData.sheet();
		String format = testData.format();
		if(fileName.trim().length() == 0 || sheetName.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}
		Matcher matcher;
	    Pattern pattern;
	    
		String regexXLSX ="(.*).xlsx$";		
	    pattern = Pattern.compile(regexXLSX);
	    matcher = pattern.matcher(fileName);	       
		if(matcher.matches()){
			Object[][] obj = BasicFile.xlsxReader(fileName,sheetName,format);
			return obj;
		}
		
		String regexXLS ="(.*).xls$";		
	    pattern = Pattern.compile(regexXLS);
	    matcher = pattern.matcher(fileName);
		if(matcher.matches()){
			Object[][] obj = BasicFile.xlsReader(fileName,sheetName,format);
			return obj;
		}
		
		String regexXML ="(.*).xml$";		
	    pattern = Pattern.compile(regexXML);
	    matcher = pattern.matcher(fileName);
		if(matcher.matches()){
			Object[][] obj = BasicFile.xmlReader(fileName,sheetName,format);
			return obj;
		}
		
		String regexCSV ="(.*).csv$";		
	    pattern = Pattern.compile(regexCSV);
	    matcher = pattern.matcher(fileName);
		if(matcher.matches()){
			Object[][] obj = BasicFile.csvReader(fileName,sheetName,format);
			return obj;
		}
		
		String regexJSON ="(.*).json$";		
	    pattern = Pattern.compile(regexJSON);
	    matcher = pattern.matcher(fileName);
		if(matcher.matches()){
			Object[][] obj = BasicFile.jsonReader(fileName,sheetName,format);
			return obj;
		}else{
			return null;
		}
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface SelevanceDB
	{
		/**
		 * For MySql source = Table name <br/>
		 * For CouchDB source = Database name
		 */
		String source();
	}
	
	@DataProvider(name = "MYSQL", parallel = true)
	public static Object[][] mysqlProvider(Method method) {
		SelevanceDB testData = method.getAnnotation(SelevanceDB.class);	
		String tableName =testData.source().toLowerCase();
		if(tableName.trim().length() == 0 || tableName.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}		
		try {
			InputStream input = new FileInputStream(PROPFILEPATH+PROPFILENAME);
			
			Properties prop = new Properties();
			prop.load(input);
			String host = prop.getProperty("Host") ;
			String user = prop.getProperty("User") ;
			String pwd = prop.getProperty("Password");
			String db = prop.getProperty("Database");
			return DBFile.mysqlReader(host,user,pwd,db,tableName);
			
		} catch (Exception e) {
			System.out.println("Property File not Declared");
			try{
				System.out.println("Loading Data From VM args");
				String host = System.getProperty("Host") ;
				String user = System.getProperty("User") ;
				String pwd = System.getProperty("Password");
				String db = System.getProperty("Database");
				return DBFile.mysqlReader(host,user,pwd,db,tableName);			
			}catch(Exception ex){
				System.out.println("VM Args not specified");
				return null;
			}
		}
	}
	
	@DataProvider(name = "COUCH", parallel = true)
	public static Object[][] couchProvider(Method method) {
		SelevanceDB testData = method.getAnnotation(SelevanceDB.class);	
		String tableName =testData.source().toLowerCase();
		if(tableName.trim().length() == 0 || tableName.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}		
		try {
			InputStream input = new FileInputStream(PROPFILEPATH+PROPFILENAME);
			
			Properties prop = new Properties();
			prop.load(input);
			String host = prop.getProperty("Host") ;
			return DBFile.couchReader(host,tableName);
			
		} catch (Exception e) {
			System.out.println("Property File not Declared");
			try{
				System.out.println("Loading Data From VM args");
				String host = System.getProperty("Host") ;
				return DBFile.couchReader(host,tableName);		
			}catch(Exception ex){
				System.out.println("VM Args not specified");
				return null;
			}
		}
	}
	
	@DataProvider(name = "GOOGLE", parallel = true)
	public static Object[][] googleProvider(Method method) {
		SelevanceDB testData = method.getAnnotation(SelevanceDB.class);	
		String spreadsheet =testData.source();
		if(spreadsheet.trim().length() == 0 || spreadsheet.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}		
		try {
			InputStream input = new FileInputStream(PROPFILEPATH+PROPFILENAME);
			Properties prop = new Properties();
			prop.load(input);
			String gid = prop.getProperty("GID") ;
			String gpd = prop.getProperty("GPW") ;
			return DBFile.gSpreadSheetReader(gid,gpd,spreadsheet);
			
		} catch (Exception e) {
			System.out.println("Property File not Declared");
			try{
				System.out.println("Loading Data From VM args");
				String gid = System.getProperty("GID") ;
				String gpd = System.getProperty("GPW") ;
				return DBFile.gSpreadSheetReader(gid,gpd,spreadsheet);	
			}catch(Exception ex){
				System.out.println("VM Args not specified");
				return null;
			}
		}
	}
	
	@DataProvider(name = "MONGO", parallel = true)
	public static Object[][] mongoProvider(Method method) {
		SelevanceDB testData = method.getAnnotation(SelevanceDB.class);	
		String tableName =testData.source().toLowerCase();
		if(tableName.trim().length() == 0 || tableName.trim().length() == 0){
			throw new SkipException(method.getName() + " : All Parameters are required");
		}		
		try {
			InputStream input = new FileInputStream(PROPFILEPATH+PROPFILENAME);
			
			Properties prop = new Properties();
			prop.load(input);
			String host = prop.getProperty("Host") ;
			String db = prop.getProperty("DataBase") ;
			return DBFile.mongoReader(host,db,tableName);
			
		} catch (Exception e) {
			System.out.println("Property File not Declared");
			try{
				System.out.println("Loading Data From VM args");
				String host = System.getProperty("Host") ;
				String db = System.getProperty("DataBase") ;
				return DBFile.mongoReader(host,db,tableName);		
			}catch(Exception ex){
				System.out.println("VM Args not specified");
				return null;
			}
		}
	}
	
	
	
	@DataProvider(name = "SETPRIORITY", parallel = true)
	public static Object[][] basicNmoreProvider(Method method) {
		String p = System.getProperty("SETPRIORITY") ;
		if(p==null){
			return BaseDataProvider( method);
		}else if (p.toUpperCase().contains("COUCH")){
			return couchProvider(method);
		}else{
			System.out.println(p);
			return null;
		}
	}
}
