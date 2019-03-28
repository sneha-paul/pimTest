package com.bigname.pim.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class POIUtil {

    public static List<List<String>> readData(String filePath) {
        List<List<String>> data = new ArrayList<List<String>>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets() && i < 1; i ++) {
                Sheet datatypeSheet = workbook.getSheetAt(i);
                Iterator<Row> rows = datatypeSheet.iterator();

                while (rows.hasNext()) {
                    List<String> rowData = new ArrayList<String>();
                    Row currentRow = rows.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    int rownum = currentRow.getRowNum();
                    int cellNum = 0;
                    while (cellIterator.hasNext()) {

                        Cell currentCell = cellIterator.next();
                        while(cellNum != currentCell.getColumnIndex()) {
                            rowData.add("");
                            cellNum ++;
                        }
                        cellNum ++;
                        //getCellTypeEnum shown as deprecated for version 3.15
                        //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                        if (currentCell.getCellType() == CellType.STRING) {
                            String cellData = currentCell.getStringCellValue().replaceAll("┬«", "®").replaceAll("Γäó", "™").replaceAll("ΓÇ¥", "”");
                            rowData.add(cellData);
                        } else if (currentCell.getCellType() == CellType.NUMERIC) {
                            String cellData = String.valueOf(currentCell.getNumericCellValue());
                            rowData.add(cellData);
                        } else {
                            rowData.add("");
                        }

                    }
                    if(!data.isEmpty() && rowData.size() < data.get(0).size()){
                        for(int x = rowData.size(); x < data.get(0).size(); x++){
                            rowData.add(x, "");
                        }
                    }
                    data.add(rowData);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Deprecated
    /**
     * use the below method instead
     * @see #writeData(String, String, List)
     */
    public static boolean writeData(String filePath, String sheetName, Map<String, Object[]> data) {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);

        // Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            // this creates a new row in the sheet
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                // this line creates a cell in the next column of that row
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String)obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        for(int i = 0; i < data.size(); i ++) {
            sheet.setColumnWidth(i, 15000);
        }
        try {
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean writeData(String filePath,String sheetName,List<List<Object>> data) {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);

        // Iterate over data and write to sheet

        int rownum = 0;
        for (List<Object> objArr : data) {
            // this creates a new row in the sheet
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (Object obj : objArr) {
                // this line creates a cell in the next column of that row
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String)obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        for(int i = 0; i < data.size(); i ++) {
            sheet.setColumnWidth(i, 15000);
        }        
        try {
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean writeData(String filePath, Map<String, List<List<Object>>> datas) {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        CellStyle style = workbook.createCellStyle();
        //Background color
        style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        //Font color
        font.setColor(IndexedColors.WHITE.getIndex());
        //Bold
        font.setBold(true);
        //Italic
//        font.setItalic(true);
        style.setFont(font);
        datas.forEach((sheetName, data) -> {
            // Create a blank sheet
            XSSFSheet sheet = workbook.createSheet(sheetName);

            // Iterate over data and write to sheet

            int rownum = 0;
            for (List<Object> objArr : data) {
                // this creates a new row in the sheet
                Row row = sheet.createRow(rownum++);
                int cellnum = 0;
                for (Object obj : objArr) {
                    // this line creates a cell in the next column of that row
                    Cell cell = row.createCell(cellnum++);
                    if(rownum == 1) {
                        cell.setCellStyle(style);
                    }
                    if (obj instanceof String)
                        cell.setCellValue((String)obj);
                    else if (obj instanceof Integer)
                        cell.setCellValue((Integer)obj);
                }
            }
            for(int i = 0; i < data.get(0).size(); i ++) {
//                sheet.setColumnWidth(i, 15000);
                sheet.autoSizeColumn(i);
            }
        });

        try {
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean writeJsonData(String filePath, String fileName, String data) {

        try (FileWriter file = new FileWriter(filePath)) {

            file.write(data);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
