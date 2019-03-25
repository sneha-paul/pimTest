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
        int parentNameIdx = header.indexOf("Old Parent Name");
        int productPageNameIdx = header.indexOf("Old Product Page Name");
        int collectionIdx = header.indexOf("Old Collection");
        int sizeIdx = header.indexOf("Old Size");
        int sizeCodeIdx = header.indexOf("Old Size Code");
        int metricSizeIdx = header.indexOf("Old Metric Size");
        Map<String, Map<String, Object>> collectionData = attributeOptionsMap.get("COLLECTION");
        Map<String, Map<String, Object>> sizeData = attributeOptionsMap.get("SIZE");
        Map<String, Map<String, Object>> sizeCodeData = attributeOptionsMap.get("SIZE_CODE");
        Map<String, Map<String, Object>> metricSizeData = attributeOptionsMap.get("METRIC_SIZE");
        int numOfHeaderRows = 5;
        for(int i = numOfHeaderRows; i < products.size(); i ++) {
            String oldParentProductName = (String)products.get(i).get(parentNameIdx);
            int status = -1;
            String key = (String)products.get(i).get(parentCodeIdx);
            if(parentProductsMap.containsKey(key)) {

                Map<String, Object> map = parentProductsMap.get(key);
                if(map.get("NEW_VALUE").toString().isEmpty()) {
                    status = 0;
                    products.get(i).set(parentNameIdx + 1, map.get("VALUE"));
                    products.get(i).set(productPageNameIdx + 1, map.get("VALUE"));
                } else {
                    status = 1;
                    products.get(i).set(parentNameIdx + 1, map.get("NEW_VALUE"));
                    products.get(i).set(productPageNameIdx + 1, map.get("NEW_VALUE"));
                }
            } else {
                products.get(i).set(parentNameIdx + 1, oldParentProductName);
                products.get(i).set(productPageNameIdx + 1, oldParentProductName);
            }
            products.get(i).set(parentNameIdx + 2, status);
            products.get(i).set(productPageNameIdx + 2, status);

            //##########################################################################################################

            int idx = collectionIdx;
            String existingValue = (String)products.get(i).get(idx);
            status = -1;
            key = (String)products.get(i).get(idx);
            if(collectionData.containsKey(key)) {
                Map<String, Object> map = collectionData.get(key);
                if(existingValue.equals(map.get("NEW_VALUE").toString())) {
                    status = 0;
                } else {
                    System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                    status = 1;
                }
                products.get(i).set(idx + 1, map.get("NEW_VALUE"));
            } else {
                products.get(i).set(idx + 1, existingValue);
            }
            products.get(i).set(idx + 2, status);

            //##########################################################################################################

            idx = sizeIdx;
            existingValue = (String)products.get(i).get(idx);
            status = -1;
            key = (String)products.get(i).get(idx);
            if(sizeData.containsKey(key)) {
                Map<String, Object> map = sizeData.get(key);
                if(existingValue.equals(map.get("NEW_VALUE").toString())) {
                    status = 0;
                } else {
                    System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                    status = 1;
                }
                products.get(i).set(idx + 1, map.get("NEW_VALUE"));
            } else {
                products.get(i).set(idx + 1, existingValue);
            }
            products.get(i).set(idx + 2, status);

            //##########################################################################################################

            idx = sizeCodeIdx;
            existingValue = (String)products.get(i).get(idx);
            status = -1;
            key = (String)products.get(i).get(idx);
            if(sizeCodeData.containsKey(key)) {
                Map<String, Object> map = sizeCodeData.get(key);
                if(existingValue.equals(map.get("NEW_VALUE").toString())) {
                    status = 0;
                } else {
                    System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                    status = 1;
                }
                products.get(i).set(idx + 1, map.get("NEW_VALUE"));
            } else {
                products.get(i).set(idx + 1, existingValue);
            }
            products.get(i).set(idx + 2, status);

            //##########################################################################################################

            idx = metricSizeIdx;
            existingValue = (String)products.get(i).get(idx);
            status = -1;
            key = (String)products.get(i).get(idx);
            if(metricSizeData.containsKey(key)) {
                Map<String, Object> map = metricSizeData.get(key);
                if(existingValue.equals(map.get("NEW_VALUE").toString())) {
                    status = 0;
                } else {
                    System.out.println(existingValue + "--" + map.get("NEW_VALUE"));
                    status = 1;
                }
                products.get(i).set(idx + 1, map.get("NEW_VALUE"));
            } else {
                products.get(i).set(idx + 1, existingValue);
            }
            products.get(i).set(idx + 2, status);


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
