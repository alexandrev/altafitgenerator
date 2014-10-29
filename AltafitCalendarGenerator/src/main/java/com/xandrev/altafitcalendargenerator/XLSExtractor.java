/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.altafitcalendargenerator;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author alexa_000
 */
public class XLSExtractor {

    public HashMap<Integer, ArrayList<TimeTrack>> importExcelSheet(String fileName) {
        HashMap<Integer, String> tmpHours = new HashMap<Integer, String>();
        HashMap<Integer, ArrayList<TimeTrack>> out = new HashMap<Integer, ArrayList<TimeTrack>>();
        init(out);
        try {
            Workbook workBook = WorkbookFactory.create(new FileInputStream(fileName));
            Sheet sheet = workBook.getSheetAt(0);
            Iterator rowIter = sheet.rowIterator();
            int rowIdx = 0;
            boolean started = false;
            boolean finished = false;
            while (rowIter.hasNext() && !finished) {
                XSSFRow row = (XSSFRow) rowIter.next();
                if (row != null && !started) {
                    XSSFCell cell = row.getCell(0);
                    if (cell != null) {
                        String value = cell.getStringCellValue();
                        if (value == null || value.isEmpty() || !"HORA".equals(value)) {
                            rowIdx++;
                            started = true;
                            continue;
                        }
                    }
                    row = (XSSFRow) rowIter.next();
                }
                
                

                Iterator<Cell> cellIter = row.cellIterator();
                int cellIndex = 0;
                while (cellIter.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIter.next();
                    if (cell != null) {
                        String value = cell.getStringCellValue();
                        installHashMap(tmpHours,out,cellIndex,rowIdx, value);
                    }
                    cellIndex++;
                }
                rowIdx++;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return out;
    }

    private void installHashMap(HashMap<Integer, String> tmpHours, HashMap<Integer, ArrayList<TimeTrack>> out, int cellIndex, int rowIndex, String value) {
        if (out != null) {
            switch (cellIndex) {
                case 0:
                    tmpHours.put(rowIndex, value);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    String valueTime = tmpHours.get(rowIndex);
                    ArrayList<TimeTrack> tmp = out.get(cellIndex);
                    if (tmp != null && value != null && !value.isEmpty()) {
                        TimeTrack tTrack = new TimeTrack();
                        tTrack.setTime(valueTime);
                        tTrack.setName(value);
                        tmp.add(tTrack);
                    }
                    break;
            }
        }
    }

    private void init(HashMap<Integer, ArrayList<TimeTrack>> out) {
       for (int i=0;i<5;i++){
           out.put(i+1, new ArrayList<TimeTrack>());
       }
    }

}
