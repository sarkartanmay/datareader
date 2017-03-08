package org.openqa.selevance.data.action.reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ActionFile {
	public static Object[][] xlsxActionReader(
			String filename,String testCaseName,String steps,
			String index,String testdata,String format){
		try
        {
            FileInputStream file = new FileInputStream(new File(filename));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet testCaseSheet = workbook.getSheet(testCaseName);
            Iterator<Row> rowIterator = testCaseSheet.iterator(); 
            Iterator<Row> rowIterator2 = testCaseSheet.iterator();                        
            int colEx1 = 0;
            ArrayList<String> rowTitle = new ArrayList<String>();
            while (rowIterator2.hasNext()){
            	Row row = rowIterator2.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                if(format.trim().replace(" ", "").equalsIgnoreCase("FIRSTYES")){
                	boolean isDataRow = false;
                	int i=0;
                	while (cellIterator.hasNext())
                    {
                		Cell cell =cellIterator.next(); 
                    	if(i==0 && cell.toString().trim().equalsIgnoreCase("YES")){
                   			isDataRow = true;
                   			break;
                   		}
                    }
                	if(isDataRow){
                		colEx1++;
                	} 
                }else{
                	while (cellIterator.hasNext())
                    {
                    	cellIterator.next();   
                    }
                	colEx1++;
                }                
            } 
            if(format.trim().replace(" ", "").equalsIgnoreCase("FIRSTYES")){
            	colEx1++;
            }
            Object[][] data = new Object[colEx1-1][3];   
            
            int colEx = 0;
            int colExYes = 0;
            while (rowIterator.hasNext())
            {
                String tcID ="";
            	Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
               	HashMap<String, String> hm = new HashMap<String, String>();
               	ArrayList<ArrayList<String>> allAction = new ArrayList<ArrayList<String>>();
               	HashMap<String, String> tdata = new HashMap<String, String>();
               	int i =0;
               	if(format.trim().replace(" ", "").equalsIgnoreCase("FIRSTYES")){
               		boolean isDataRow = false;
               		while (cellIterator.hasNext())
                    {
                       	Cell cell = cellIterator.next();
                       	if(colEx==0){
                       		rowTitle.add(cell.toString());
                       	}else if(colEx!=0){
                       		if(i==0 && cell.toString().trim().equalsIgnoreCase("YES")){
                       			isDataRow = true;
                       		}if(isDataRow){
                       			hm.put(rowTitle.get(i), cell.toString());
                       		}
                       		// hm.put(rowTitle.get(i), cell.toString());
                       	}
                       	i++; 
                   }
                   if(colEx!=0 && isDataRow){
                   		data[colExYes][0] =hm;
                   		colExYes++;
                   }               
                   colEx ++; 
               	}else{
               		while (cellIterator.hasNext())
                    {
                       	Cell cell = cellIterator.next();
                       	
                       	if(colEx==0){
                       		rowTitle.add(cell.toString());
                       	}else{
                       		hm.put(rowTitle.get(i), cell.toString());
                       		tcID = cell.toString() ;
                       		//-------- STEPS based on TCID --------
                       		//System.out.println("--------TEST STEP -----------");
                            XSSFSheet testStepsheet = workbook.getSheet(steps);
                            Iterator<Row> stepsrowIterator = testStepsheet.iterator(); 
                            int rowPos=0;
                            while(stepsrowIterator.hasNext()){
                            	ArrayList<String> innerAction = new ArrayList<String>();
                            	if(rowPos!=0){
                            		Row stepsRow = stepsrowIterator.next();
                                	Iterator<Cell> stepsCellIterator = stepsRow.cellIterator();
                                	int pos = 0; // 0 = TestCaseID, 1 = Page , 2 = Action
                                	String caseID ="";
                                	String page ="";
                                	String action ="";
                                	while (stepsCellIterator.hasNext())
                                    {
                                		Cell stepsCell = stepsCellIterator.next();
                                		//System.out.println(stepsCell);
                                		switch(pos){
	                                		case 0 :caseID = stepsCell.toString();
	                                			break;
	                                		case 1 : page = stepsCell.toString();
	                                			break;
	                                		case 2 :action = stepsCell.toString();
	                                			break;
                                		}
                                		pos++;
                                    }
                                	if(caseID.toLowerCase().contains(tcID.toLowerCase())){
                                		//System.out.print(page + " : " + action);
                                		
                                		XSSFSheet indexheet = workbook.getSheet(index);
                                        Iterator<Row> indexrowIterator = indexheet.iterator();                                        
                                        
                                        while(indexrowIterator.hasNext()){
                                        	Row indexRow = indexrowIterator.next();
                                        	Iterator<Cell> indexCellIterator = indexRow.cellIterator();
                                        	int ind = 0;
                                        	String name="";
                                            String location="";
                                            String act="";
                                        	while (indexCellIterator.hasNext())
                                            {
                                        		Cell indexCell = indexCellIterator.next();
                                        		//System.out.println(indexCell);
                                        		switch(ind){
    	                                		case 0 :name = indexCell.toString();
    	                                			break;
    	                                		case 1 : location = indexCell.toString();
    	                                			break;
    	                                		case 2 :act = indexCell.toString();
    	                                			break;
                                        		}
                                        		ind++;
                                            }
                                        	if(page.toLowerCase().contains(name.toLowerCase()) &&
                                        			action.toLowerCase().contains(act.toLowerCase()) ){
                                        		//System.out.print(" : "+location +"\n");
                                        		innerAction.add(tcID);
                                        		innerAction.add(page);
                                        		innerAction.add(location);
                                        		innerAction.add(act);
                                        		
                                        		/*for(int q=0;q<innerAction.size();q++){
                                        			System.out.print(innerAction.get(q) + "  ");
                                        		}*/
                                        		//System.out.println();
                                        		allAction.add(innerAction);
                                        	}
                                        }
                                        
                                	}
                            	}            	
                            	rowPos++;
                            }
                       		//-------- END STEPS based on TCID --------
                            //---------------- READ Test Data START-------------------
                            //System.out.println("--------TEST DATA -----------");
                            XSSFSheet testDataheet = workbook.getSheet(testdata);
                            Iterator<Row> testDataRowIterator = testDataheet.iterator();
                            ArrayList<String> tDataTitle = new ArrayList<String>();
                            
                           	int col =0;
                            while(testDataRowIterator.hasNext()){
                            	Row testDataRow = testDataRowIterator.next();
                            	Iterator<Cell> testDataCellIterator = testDataRow.cellIterator();
                            	int m =0;
                            	boolean correctTC = false;
                            	while (testDataCellIterator.hasNext())
                                {
                            		Cell testDataCell = testDataCellIterator.next();
                            		if(col==0){
                            			tDataTitle.add(testDataCell.toString());
                            		}
                            		if(m==0 && col !=0 && testDataCell.toString().toLowerCase().contains(tcID.toLowerCase())){
                            			correctTC = true;
                            		}
                            		if(correctTC){
                            			tdata.put(tDataTitle.get(m), testDataCell.toString());
                            		}
                            		/*if(col==0){
                            			tDataTitle.add(testDataCell.toString());
                            		}else{
                            			tdata.put(tDataTitle.get(m), testDataCell.toString());
                            		}*/
                            		m++;
                                }
                            	col++;
                            }
                            
                            //---------------- READ Test Data END-------------------                            
                       	}
                       	i++; 
                   }
                   if(colEx!=0){
                   		data[colEx-1][0] =hm;
                   		data[colEx-1][1] =allAction;
                   		data[colEx-1][2] =tdata;
                   		//System.out.println(tdata);
                   }               
                   colEx ++; 
               	}
               	            
            }
            file.close();
            workbook.close();
            return data;            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		return null;
	}	
}
