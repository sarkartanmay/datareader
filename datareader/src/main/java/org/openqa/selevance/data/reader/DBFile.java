package org.openqa.selevance.data.reader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DBFile {
	public static Object[][] mysqlReader(String host,String username,String password,String database,String tablename){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager.
					getConnection("jdbc:mysql://"+host+"/"+database+"?user="+username+"&password="+password);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from "+tablename);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			resultSet.last();
			Object[][] data = new Object[resultSet.getRow()][1];
			resultSet.beforeFirst();			
			int k=0;
			while (resultSet.next()) {
				 Map<String, String> maps = new HashMap<String, String>();
				 for(int i=1;i<=rsmd.getColumnCount();i++){
					maps.put(rsmd.getColumnLabel(i), resultSet.getString(i));
				 }
				 data[k][0]= maps;
				 k++;
			 }
			 connect.close();
			 return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public static Object[][] couchReader(String host,String database){
		try {
			JSONParser parser = new JSONParser();			
			URL baseCouch = new URL(host + "/"+database+"/_all_docs");
			Reader in = new InputStreamReader(baseCouch.openStream());
			
			Object obj  = parser.parse(in);
			JSONArray a = new JSONArray();
			a.add(obj);
			
			for (Object o : a)
			  {
				  JSONObject jobj = (JSONObject) o;
				 
				  JSONArray rows = (JSONArray) jobj.get("rows");
				  int length = rows.size();
				  Object[][] data = new Object[length][1];
				  int i=0;
				  for (Object row : rows){					  
					  JSONObject jobjInside = (JSONObject)row;
					  String eachDoc = (String) jobjInside.get("id");
					  URL eachCouch = new URL(host + "/"+database+"/"+eachDoc);
					  Reader eachReader = new InputStreamReader(eachCouch.openStream());
					  Object eachObj  = parser.parse(eachReader);
					  JSONArray eachArray = new JSONArray();
					  eachArray.add(eachObj);					 
					  for (Object each : eachArray)
					  {
						  JSONObject eachJson = (JSONObject) each;	
						  System.out.println(eachJson);
						  data[i][0]=eachJson;
					  }					  
					  i++;					  
				  }
				 return data;
			  }			
			return null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static Object[][] gSpreadSheetReader(
			final String GOOGLE_ACCOUNT_USERNAME,
			final String GOOGLE_ACCOUNT_PASSWORD,
			final String SPREADSHEET_URL){
		try{
			SpreadsheetService service = new SpreadsheetService("Print Google Spreadsheet For Selevance");
		    service.setUserCredentials(GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);		 
		    // Load sheet
		    String url ="https://spreadsheets.google.com/feeds/spreadsheets/"+SPREADSHEET_URL;
		    URL metafeedUrl = new URL(url);
		    SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
		    URL listFeedUrl = ((WorksheetEntry) spreadsheet.getWorksheets().get(0)).getListFeedUrl();		 
		    ListFeed feedInit = (ListFeed) service.getFeed(listFeedUrl, ListFeed.class);
		    // Count entries
		    int rowCount =0;
		    for(@SuppressWarnings("unused") ListEntry entry : feedInit.getEntries())
		    {		      
		      rowCount++;
		    }
		    Object[][] data = new Object[rowCount][1];
		    // Print entries
		    ListFeed feed = (ListFeed) service.getFeed(listFeedUrl, ListFeed.class);
		    
		    int i=0;
		    for(ListEntry entry : feed.getEntries())
		    {
		    	HashMap<String, String> hm = new HashMap<String, String>();
		    	for(String tag : entry.getCustomElements().getTags())
			    {
			        hm.put(tag, entry.getCustomElements().getValue(tag));
			    }
		    	data[i][0] =hm;
		    	i++;
		    }
		    return data;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}	
	public static Object[][] mongoReader(String host,String database,String collection){
		try {
			String hostDetails[] = host.split(":");
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient(hostDetails[0] , Integer.parseInt(hostDetails[1]) );
			@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB(database);
			DBCollection coll = db.getCollection(collection);
			DBCursor cursor = coll.find();
			int rowCount = cursor.count();
			int i=0;
			Object[][] data = new Object[rowCount][1];
			try {
			   while(cursor.hasNext()) {
				   String row = cursor.next().toString().trim();
				   int lastPos = row.length();
				   row = row.substring(1, lastPos-1);				   
				   String eachSet[] = row.split(",");
				   HashMap<String, String> hm = new HashMap<String, String>();
				   for(String each : eachSet){
					   String keyPair[] = each.split(":");
					   String key = keyPair[0].replace("\"", "").trim();
					   String value_ = keyPair[1].replace("\"", "").trim();
					   String value = value_ + (key.contains("_id") ? keyPair[2] : "");
					   hm.put(key, value);
				   }
				   data[i][0] =hm;
				   i++;
			   }
			} finally {
			   cursor.close();
			}
			return data;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
