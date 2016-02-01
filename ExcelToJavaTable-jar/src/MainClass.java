
// Imports
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


class MainClass extends JFrame
 {
	 // Instance attributes used in this example
	static String filename;
	static int type;
	private	JPanel		topPanel;
	private	JTable		table;
	private	JScrollPane scrollPane;

	// Constructor of main frame
	public MainClass()
	{
		// Set the frame characteristics
		setTitle( "Excel Table In Java" );
		setSize( 300, 200 );
		setBackground( Color.gray );

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		//func to find length and get coulomn data
		ArrayList list = new ArrayList();
		int columnslength=FuncFillColumnList(list,0);
		int rowslength=list.size();
		// Create columns array and fill from the list
		String columnNames[] = new String[rowslength];
		for(int i=0;i<rowslength;i++){
			try{
			columnNames[i]=(String) list.get(i);
			}catch(Exception e){
				columnNames[i]=String.valueOf(list.get(i));
			}
		}


		// fill body
		String bodyValues[][] =new String[columnslength][rowslength];
		FuncFillbody(bodyValues,0,rowslength);

		// Create a new table instance
		table = new JTable( bodyValues, columnNames );
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		//center all cells
		for(int i=0;i<rowslength;i++){
		table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
		}
		// Add the table to a scrolling pane
		scrollPane = new JScrollPane( table );
		topPanel.add( scrollPane, BorderLayout.CENTER );
		this.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	System.exit(0);
	        }

	    });

	}

	private void FuncFillbody(String[][] bodyValues, int sheetinput,int inputrowsize) {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		HSSFSheet sheet=wb.getSheetAt(sheetinput);
		
		//that is for evaluate the cell type
		FormulaEvaluator formulaeva=wb.getCreationHelper().createFormulaEvaluator();
		int i=-1;
		int j=0;
		for(Row row : sheet)
		{
			if(i>=0){
			for(Cell cell : row)
			{
				try{
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					bodyValues[i][j]=String.valueOf(cell.getNumericCellValue());
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					bodyValues[i][j]=cell.getStringCellValue();
					break;
				}
				}catch(Exception e){
					//if array out of bounds closes next anyway
				}
				j++;
			}
			System.out.println(j);
			if(j!=inputrowsize){
				final JPanel panel = new JPanel();
				JOptionPane.showMessageDialog(panel, "Could not display(table not filled)", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
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
		XSSFSheet sheet=wb.getSheetAt(sheetinput);
		
		//that is for evaluate the cell type
		FormulaEvaluator formulaeva=wb.getCreationHelper().createFormulaEvaluator();
		int i=-1;
		int j=0;
		for(Row row : sheet)
		{
			if(i>=0){
			for(Cell cell : row)
			{
				try{
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					bodyValues[i][j]=String.valueOf(cell.getNumericCellValue());
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					bodyValues[i][j]=cell.getStringCellValue();
					break;
				}
				}catch(Exception e){
					//if array out of bounds closes next anyway
				}
				j++;
			}
			System.out.println(j);
			if(j!=inputrowsize){
				final JPanel panel = new JPanel();
				JOptionPane.showMessageDialog(panel, "Could not display(table not filled)", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			}
			i++;
			j=0;
		}
		}
	}

	private int FuncFillColumnList(ArrayList list,int sheetinput) {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int columns=0;
		if(type==0){
		//create wrokbook xls
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		HSSFSheet sheet=wb.getSheetAt(sheetinput);
		//that is for evaluate the cell type
		FormulaEvaluator formulaeva=wb.getCreationHelper().createFormulaEvaluator();
		
		boolean donerow=false;
		for(Row row : sheet)
		{
			if(!donerow)
			for(Cell cell : row)
			{
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					list.add(cell.getNumericCellValue());
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					list.add(cell.getStringCellValue());
					break;
				}
			}
			columns++;
			donerow=true;

		}
		}
		if(type==1){
		//create wrokbook xlsx
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create sheet object to retrieve the sheet
		XSSFSheet sheet=wb.getSheetAt(sheetinput);
		//that is for evaluate the cell type
		FormulaEvaluator formulaeva=wb.getCreationHelper().createFormulaEvaluator();
		boolean donerow=false;
		for(Row row : sheet)
		{
			if(!donerow)
			for(Cell cell : row)
			{
				switch(formulaeva.evaluateInCell(cell).getCellType())
				{
				//if cell is numeric format
				case Cell.CELL_TYPE_NUMERIC:
					list.add(cell.getNumericCellValue());
					break;
				//if cell is numeric format
				case Cell.CELL_TYPE_STRING:
					list.add(cell.getStringCellValue());
					break;
				}
			}
			columns++;
			donerow=true;

		}
		}
		return columns-1;
	}

	// Main 
	public static void main( String args[] )
	{
		boolean success=false;
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
		    System.out.println("Selected file: " + filename);
		    
		    if(extension.equals("xls")){
		    	success=true;
		    	type=0;
		    }
		    if(extension.equals("xlsx")){
		    	success=true;
		    	type=1;
		    }
		    
		}
		else
			System.exit(0);
		// Create an instance of the test application
		if(success){
		MainClass mainFrame	= new MainClass();
		mainFrame.setVisible( true );
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		}
		else{
			final JPanel panelwrongfile = new JPanel();
			JOptionPane.showMessageDialog(panel, "Not readable file", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

	}


}
