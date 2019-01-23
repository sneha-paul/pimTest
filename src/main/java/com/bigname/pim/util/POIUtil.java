package com.bigname.pim.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
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
}
