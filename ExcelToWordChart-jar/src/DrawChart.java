import org.jfree.chart.ChartPanel;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class DrawChart extends ApplicationFrame
{
	//static String filename;
	//static int type;
   public DrawChart( String applicationTitle,String filename,int type)
   {
      super(applicationTitle);
      //fill titles data from excel and get data size
      ArrayList HorizontalList = new ArrayList();
      ArrayList VerticalList = new ArrayList();
      
      boolean FirstCellFilled=FillTitlesAndCheckLegit(HorizontalList,VerticalList,filename,type);
      //save first block
      String FirstBlock="";
      try{
    	  if(FirstCellFilled)
    		  FirstBlock=(String) VerticalList.get(0);
      }
      catch(Exception e){
    	  JOptionPane.showMessageDialog(null, "First block is not a string");
      }
      //remove arrays 1st block
      VerticalList.remove(0);
      
      //read rest exept titles
      double[][] data=new double[VerticalList.size()][HorizontalList.size()];
      FillData(data,filename,type);
      //check lists
      for(int i=0;i<HorizontalList.size();i++)
    	  System.out.print(HorizontalList.get(i)+"\t");  
      System.out.println();
      for(int i=0;i<VerticalList.size();i++)
    	  System.out.print(VerticalList.get(i)+"\t");  
      System.out.println();
      //check data
      for(int i=0;i<data.length;i++){
    	  for(int j=0;j<data[0].length;j++)
    		  System.out.print(data[i][j]+"\t");  
    	  System.out.println();
      }
      JFreeChart lineChart = ChartFactory.createLineChart(
         FirstBlock,
         "","",
         createDataset(HorizontalList,VerticalList,data),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );


 
   }

   private void FillData(double[][] data, String filename, int type) {
	// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FormulaEvaluator formulaeva;
		int i=-1,j=-1;
		if(type==0)
		{
		//create wrokbook xls
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		HSSFSheet sheet=wb.getSheetAt(0);
		
		//that is for evaluate the cell type
		formulaeva=wb.getCreationHelper().createFormulaEvaluator();

		for(Row row : sheet)
		{
			if(i>=0)
			for(Cell cell : row)
			{
				if(j>=0)
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					data[i][j]=cell.getNumericCellValue();
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					//show message 
					JOptionPane.showMessageDialog(null, "Precentage chart not programmed yet");
					System.exit(1);
					break;
				}

				j++;
			}

			i++;
			j=0;
		}

		}
		if(type==1)
		{
		//create wrokbook xlsx
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		XSSFSheet sheet=wb.getSheetAt(0);
		
		//that is for evaluate the cell type
		formulaeva=wb.getCreationHelper().createFormulaEvaluator();

		for(Row row : sheet)
		{
			if(i>=0)
			for(Cell cell : row)
			{
				if(j>=0)
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
						data[i][j]=cell.getNumericCellValue();	
					
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					System.out.println(cell.getStringCellValue());
				
					//show message 
					JOptionPane.showMessageDialog(null, "Precentage chart not programmed yet");
					System.exit(1);
					break;
				}

				j++;
			}

			i++;
			j=-1;
			}

		}
	
}

private boolean FillTitlesAndCheckLegit(ArrayList horizontalList, ArrayList verticalList, String filename, int type) {
	// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FormulaEvaluator formulaeva;
		int i=0,j=0,countWidth=0;
		boolean legit=true,isCellFilled=false;
		if(type==0)
		{
		//create wrokbook xls
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		HSSFSheet sheet=wb.getSheetAt(0);
		
		//that is for evaluate the cell type
		formulaeva=wb.getCreationHelper().createFormulaEvaluator();

		for(Row row : sheet)
		{
			for(Cell cell : row)
			{
				
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					if(j==0)
						verticalList.add(cell.getNumericCellValue());
					if(i==0)
						horizontalList.add(cell.getNumericCellValue());
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					if(j==0)
						verticalList.add(cell.getStringCellValue());
					if(i==0)
						horizontalList.add(cell.getStringCellValue());
					break;
				}

				j++;
			}

			if(i==1){
				countWidth=j;
				//check 1st row only
				if(horizontalList.size()!=countWidth && horizontalList.size()!=countWidth-1)
					legit=false;
			}

			//check all table exept 1st row
			if(i>1 && countWidth!=j)
				legit=false;
			//table not filled
			if(!legit){
				//show message 
				JOptionPane.showMessageDialog(null, verticalList.size()+horizontalList.size()+"Table no filled in the Excel file");
				System.exit(1);
			}
			i++;
			j=0;
			}
		if(horizontalList.size()!=countWidth-1){
			horizontalList.remove(0);
			isCellFilled=true;
		}
		}
		if(type==1)
		{
		//create wrokbook xlsx
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		XSSFSheet sheet=wb.getSheetAt(0);
		
		//that is for evaluate the cell type
		formulaeva=wb.getCreationHelper().createFormulaEvaluator();

		for(Row row : sheet)
		{
			for(Cell cell : row)
			{
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					if(j==0)
						verticalList.add(cell.getNumericCellValue());
					if(i==0)
						horizontalList.add(cell.getNumericCellValue());
					
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					if(j==0)
						verticalList.add(cell.getStringCellValue());
					if(i==0)
						horizontalList.add(cell.getStringCellValue());
					
					break;
				}

				j++;
			}

			if(i==1){
				countWidth=j;			
				//check 1st row only
				if(horizontalList.size()!=countWidth && horizontalList.size()!=countWidth-1)
					legit=false;
			}
			//check all table exept 1st row
			if(i>1 && countWidth!=j)
				legit=false;
			//table not filled
			if(!legit){
				//show message 
				JOptionPane.showMessageDialog(null, verticalList.size()+" "+countWidth+" "+horizontalList.size()+"Table no filled in the Excel file");
				System.exit(1);
			}
			i++;
			j=0;
			}
		if(horizontalList.size()!=countWidth-1){
			horizontalList.remove(0);
			isCellFilled=true;
		}
		}
		
		return isCellFilled;

}

private DefaultCategoryDataset createDataset(ArrayList horizontalList, ArrayList verticalList, double[][] data )
   {
	   //get data from Excel file
	   
	   //create data
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      for(int i=0;i<data.length;i++){
    	  String vertical = null,horizontal = null;
    	  for(int j=0;j<data[0].length;j++){
    		  try{
    		  vertical=(String) verticalList.get(i);
    		  }catch(Exception e){
    			  vertical=Double.toString((double) verticalList.get(i));
    		  }
    		  try{
    		  horizontal=(String) horizontalList.get(j);
    		  }catch(Exception e){
    			  horizontal=Double.toString((double) horizontalList.get(i)); 
    		  }
    		  dataset.addValue( data[i][j] ,vertical ,horizontal );  
    	  }
      }
      return dataset;
   }
   public static void main( String[ ] args ) 
   {
	    String filename = null;
	    int type = 0;
		boolean success=false;
		//show message before chooser
		JOptionPane.showMessageDialog(null, "Select your Excel file");
		//select file
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Microsoft Excel Documents","xls","xlsx");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		final JPanel panel = new JPanel();
		int result = fileChooser.showOpenDialog(panel);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    filename=selectedFile.getAbsolutePath();
		    String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		    System.out.println(filename);
		    
		    if(extension.equals("xls")){
		    	success=true;
		    	type=0;
		    }
		    else if(extension.equals("xlsx")){
		    	success=true;
		    	type=1;
		    }
		    else{
		    	JOptionPane.showMessageDialog(null, "Cant open this file","Error",JOptionPane.ERROR_MESSAGE);
		    	System.exit(1);
		    }
		    
		}
		else
			System.exit(0);
	   //draw the chart
      DrawChart chart = new DrawChart("Excel extracted Line chart",filename,type);
      chart.setExtendedState(JFrame.MAXIMIZED_BOTH); 
      
      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
      //get user input
      JPanel p = new JPanel();
      JTextField FileNameEdit = new JTextField();
      JTextField SizeXEdit = new JTextField();
      JTextField SizeYEdit = new JTextField();
      //cut filename extension of the string
      int index=filename.lastIndexOf(".");
      filename=filename.substring(0,index);//cut
      filename=filename+".doc";//add doc
      FileNameEdit.setText(filename);
      SizeXEdit.setText("500");
      SizeYEdit.setText("300");
      p.add(new JLabel("Save as :"));
      p.add(FileNameEdit);
      p.add(new JLabel("Size :"));
      p.add(SizeXEdit);
      p.add(new JLabel("x"));
      p.add(SizeYEdit);
      int IsOk=JOptionPane.showConfirmDialog(null, p, "Edit save location+type(doc/docx) and chart size inside the file", JOptionPane.OK_CANCEL_OPTION);
      //user canceled save file so exit
      if(IsOk!=0)
    	  System.exit(1);
      //save frame as image
      filename=FileNameEdit.getText();
      filename=MakeTwoBackspace(filename);// add backspaces
      SaveTempImage(chart);//dont forget delete temp image after
      ImportImageIntoWord(SizeXEdit.getText(),SizeYEdit.getText(),filename);

   }

private static String MakeTwoBackspace(String thestring) {
	// TODO Auto-generated method stub
	int index=0;
	//count backslashes
	int counter = 0;
	for( int i=0; i<thestring.length(); i++ ) {
	    if( thestring.charAt(i) == '\\' ) {
	        counter++;
	    } 
	}
	String[] StringParts=new String[counter+1];
	//fill parts withought backslashes
	for(int i=0;i<counter;i++)
	{
		index=thestring.indexOf("\\");
		StringParts[i]=thestring.substring(0,index);
		thestring=thestring.substring(index+1);
	}
	StringParts[counter]=thestring;//add whats left if no more backshashes
	//reset string
	thestring="";
	for(int i=0;i<counter;i++)//build with 2 backslashes
	{
			thestring+=StringParts[i]+"\\\\";
	}
	thestring+=StringParts[counter];
	return thestring;
}

private static void ImportImageIntoWord(String sizex, String sizey, String filetosave) {
	// TODO Auto-generated method stub
	String[] IMageargs={"temp.png"};
	try{
    XWPFDocument doc = new XWPFDocument();
    XWPFParagraph p = doc.createParagraph();
    XWPFRun xwpfRun = p.createRun();
    
    for (String imgFile : IMageargs) {
        int format=XWPFDocument.PICTURE_TYPE_JPEG;
       // xwpfRun.setText(imgFile);
        xwpfRun.addBreak();
        xwpfRun.addPicture (new FileInputStream(imgFile), format, imgFile, Units.toEMU(Double.parseDouble(sizex)), Units.toEMU(Double.parseDouble(sizey))); // 200x200 pixels
        //xwpfRun.addBreak(BreakType.PAGE);
    }
    FileOutputStream out = new FileOutputStream(filetosave);
    doc.write(out);
    out.close();
    //close so wont leak
	}
	catch(Exception e){
    	JOptionPane.showMessageDialog(null, "Image import into word failed","Error",JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
	}
	
//open file

		    try {
		      Desktop desktop = null;
		      if (Desktop.isDesktopSupported()) {
		        desktop = Desktop.getDesktop();
		      }
		      System.out.print(filetosave);
		      int index=filetosave.lastIndexOf("\\");
		      filetosave=filetosave.substring(0,index);
		       desktop.open(new File(filetosave));
		    } catch (IOException ioe) {
		      ioe.printStackTrace();
		    }

		  
		
}

private static void SaveTempImage(DrawChart chart) {
	// TODO Auto-generated method stub
    BufferedImage bi = new BufferedImage(chart.getSize().width, chart.getSize().height, BufferedImage.TYPE_INT_ARGB); 
    Graphics g = bi.createGraphics();
    chart.paint(g);  //this == JComponent
    g.dispose();
    try{ImageIO.write(bi,"png",new File("temp.png"));}catch (Exception e) {
    	JOptionPane.showMessageDialog(null, "Image save failed","Error",JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }
}
}