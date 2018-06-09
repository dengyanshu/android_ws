package net.deng.poi;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class PoiTest {
	  @Test
	  public  void  testRead() throws  Exception{
               Workbook   wookbook=null;
               String filename="d://1.xlsx";
               if(filename.endsWith(".xls")){
            	   wookbook=new HSSFWorkbook(new  FileInputStream(filename));
               }
               else  if(filename.endsWith(".xlsx")){
            	   wookbook=new XSSFWorkbook(new  FileInputStream(filename));
               }
               else{
            	   throw  new RuntimeException("����֧�ֵ�EXCEL��ʽ");
               }
               Sheet  sheet=wookbook.getSheetAt(0);
               Row row= sheet.getRow(0);
               Cell cell=row.getCell(0);
               System.out.println(cell.getStringCellValue());
	  }
	  
	  /**
	   * �˷����������������
	   * ����web������ ȱ��jar��
	   * 
	   * */
	  
	  @Test
		public void testRead2()throws Exception{
			Workbook  workbook=new XSSFWorkbook(new FileInputStream("D://tomcat7/webapps/rolemenu/upload/c5a1bbbe490848359477e555009925e7.xlsx"));
			Sheet sheet=workbook.getSheetAt(0);
			int len=sheet.getPhysicalNumberOfRows();
			for(int i=2;i<len;i++){
				 Row row=sheet.getRow(i);
				 System.out.println(row.getCell(0).getStringCellValue()+":"+row.getCell(1).getStringCellValue());
			}
		}
	  
	  
	  @Test
	  public  void testWrite()throws Exception{
		  Workbook  workbook=new HSSFWorkbook();
		  CellRangeAddress  cellRangeAddress=new CellRangeAddress(0, 0, 0, 2);
		  //������ʽ
		  CellStyle style=workbook.createCellStyle();
		  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		  Font  font=workbook.createFont();
		  font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		  font.setFontHeightInPoints((short)16);
		  style.setFont(font);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  style.setFillForegroundColor(HSSFColor.YELLOW.index);
		  
		  Sheet sheet=workbook.createSheet("sheet1");
		  sheet.addMergedRegion(cellRangeAddress);
		  
		  Row row=sheet.createRow(0);
		  
		  
		  Cell cell=row.createCell(0);
		  cell.setCellStyle(style);
		  
		  cell.setCellValue("hello world!!");
		  
		  
		  workbook.write(new FileOutputStream("d://1.xls"));
		  workbook.close();
	  }
	  

	  
	  
	  
	  
	  /*
	  
	  Workbook  workbook=new HSSFWorkbook();
	  //�ϲ���Ԫ��
	  CellRangeAddress cellRangeAddress=new CellRangeAddress(0, 0, 0, 3);
	  //��ʽ ��ֱ ˮƽ����
	  CellStyle cellStyle=workbook.createCellStyle();
	  cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	  cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	  //������ʽs
	  HSSFFont  hssfFont=(HSSFFont) workbook.createFont();
	  hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	  hssfFont.setFontHeightInPoints((short)16);
	  cellStyle.setFont(hssfFont);
	  //����
	  cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	  cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
	  
	  
	  Sheet sheet=workbook.createSheet("mysheet1");
	  sheet.addMergedRegion(cellRangeAddress);
	  
	  Row row= sheet.createRow(0);
	  
	  Cell cell= row.createCell(0);
	  cell.setCellValue("hello my baby!!");
	  cell.setCellStyle(cellStyle);
	  
	  workbook.write(new FileOutputStream("d://2.xls"));
	  workbook.close();
	  
	  */
}
