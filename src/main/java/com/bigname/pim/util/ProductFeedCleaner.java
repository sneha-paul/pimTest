package com.bigname.pim.util;

import com.bigname.core.domain.ValidatableEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ProductFeedCleaner {

    public static final String productFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Active_Products_ENVELOPES.xlsx";
    public static final String updatedProductFeedFilePath = "/usr/local/pim/uploads/data/cleanup/Active_Products_ENVELOPES-" + PimUtil.getTimestamp() + ".xlsx";
    public static final String attributeOptionsFilePath = "/usr/local/pim/uploads/data/cleanup/AttributeOptions_ENVELOPES.xlsx";
    public static final String parentProductsFilePath = "/usr/local/pim/uploads/data/cleanup/ParentProductsData.xlsx";

    private static Map<String, Map<String, Map<String, Object>>> attributeOptionsMap = readAttributesData(attributeOptionsFilePath);
    private static Map<String, Map<String, Object>> parentProductsMap = readParentProductsData(parentProductsFilePath).entrySet().stream().findFirst().get().getValue();
    private static List<List<Object>> products = readData1(productFeedFilePath);

    public static void doCleanup() {
        List<Object> header = products.get(0);

        int parentCodeIdx = header.indexOf("Parent Code");
        int oldParentNameIdx = header.indexOf("Old Parent Name");
        int newParentNameIdx = header.indexOf("Parent Name");
        int parentNameProcessStatusIdx = header.indexOf("Parent Name Process Status");
        int oldProductPageNameIdx = header.indexOf("Old Product Page Name");
        int newProductPageNameIdx = header.indexOf("Product Page Name");
        int productPageNameProcessStatusIdx = header.indexOf("Product Page Name Process Status");
        int numOfHeaderRows = 5;
        for(int i = 5; i < products.size(); i ++) {
            String oldParentProductName = (String)products.get(i).get(oldParentNameIdx);
            int status = -1;
            String key = (String)products.get(i).get(parentCodeIdx);
            if(parentProductsMap.containsKey(key)) {

                Map<String, Object> map = parentProductsMap.get(key);
                if(map.get("NEW_VALUE").toString().isEmpty()) {
                    status = 0;
                    products.get(i).set(newParentNameIdx, map.get("VALUE"));
                    products.get(i).set(newProductPageNameIdx, map.get("VALUE"));
                } else {
                    status = 1;
                    products.get(i).set(newParentNameIdx, map.get("NEW_VALUE"));
                    products.get(i).set(newProductPageNameIdx, map.get("NEW_VALUE"));
                }
            } else {
                products.get(i).set(newParentNameIdx, oldParentProductName);
                products.get(i).set(newProductPageNameIdx, oldParentProductName);
            }
            products.get(i).set(parentNameProcessStatusIdx, status);
            products.get(i).set(productPageNameProcessStatusIdx, status);
        }
        POIUtil.writeData(updatedProductFeedFilePath, "PRODUCTS", products);
    }

    public static Map<String, Map<String, Map<String, Object>>> readAttributesData(String filePath) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                data.put(sheet.getSheetName(), new LinkedHashMap<>());
                Iterator<Row> rows = sheet.iterator();
                List<String> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                while(!headers.isEmpty() && rows.hasNext()) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                rowData.put(headers.get(cellNum), cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.put(headers.get(cellNum), cell.getNumericCellValue());
                                } else {
                                    rowData.put(headers.get(cellNum), Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.put(headers.get(cellNum), "");
                                break;
                            default:
                                rowData.put(headers.get(cellNum), "");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                    }
                    if(rowData.entrySet().stream().anyMatch(e -> !e.getValue().toString().isEmpty())) {
                        data.get(sheet.getSheetName()).put((String)rowData.get("VALUE"), rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Map<String, Map<String, Map<String, Object>>> readParentProductsData(String filePath) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                data.put(sheet.getSheetName(), new LinkedHashMap<>());
                Iterator<Row> rows = sheet.iterator();
                List<String> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                while(!headers.isEmpty() && rows.hasNext()) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                rowData.put(headers.get(cellNum), cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.put(headers.get(cellNum), cell.getNumericCellValue());
                                } else {
                                    rowData.put(headers.get(cellNum), Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.put(headers.get(cellNum), "");
                                break;
                            default:
                                rowData.put(headers.get(cellNum), "");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.put(headers.get(cellNum), "");
                            cellNum ++;
                        }
                    }
                    if(rowData.entrySet().stream().anyMatch(e -> !e.getValue().toString().isEmpty())) {
                        data.get(sheet.getSheetName()).put((String)rowData.get("PARENT PRODUCT ID"), rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<List<Object>> readData1(String filePath) {
        List<List<Object>> data = new ArrayList<>();
        try {

            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rows = sheet.iterator();
                List<Object> headers = new ArrayList<>();
                if(rows.hasNext()) {
                    Row headerRow = rows.next();
                    Iterator<Cell> headerCells = headerRow.cellIterator();
                    while(headerCells.hasNext()) {
                        Cell headerCell = headerCells.next();
                        if(headerCell.getCellType() == CellType.STRING) {
                            headers.add(headerCell.getStringCellValue());
                        }
                    }
                }
                data.add(headers);
                while(!headers.isEmpty() && rows.hasNext()) {
                    List<Object> rowData = new ArrayList<>();
                    int cellNum = 0;
                    Iterator<Cell> cells = rows.next().iterator();
                    while(cells.hasNext() && cellNum < headers.size()) {
                        Cell cell = cells.next();
                        while(cellNum != cell.getColumnIndex()) {
                            rowData.add("");
                            cellNum ++;
                        }
                        switch(cell.getCellType()) {
                            case STRING:
                                rowData.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if("Base Quantity".equals(headers.get(cellNum)) || "Each Weight".equals(headers.get(cellNum))) {
                                    rowData.add(cell.getNumericCellValue());
                                } else {
                                    rowData.add(Integer.toString((int)cell.getNumericCellValue()));
                                }
                                break;
                            case BLANK:
                                rowData.add("");
                                break;
                            default:
                                rowData.add("");

                        }
                        cellNum ++;
                    }
                    if(rowData.size() < headers.size()) {
                        for(int x = rowData.size(); x < headers.size(); x ++) {
                            rowData.add("");
                            cellNum ++;
                        }
                    }
                    if(rowData.stream().anyMatch(e -> !e.toString().isEmpty())) {
                        data.add(rowData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        doCleanup();
    }

}
