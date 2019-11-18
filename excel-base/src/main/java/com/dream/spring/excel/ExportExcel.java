package com.dream.spring.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author DreamJM
 */
public class ExportExcel {

    private SheetStyle sheetStyle;

    private ColumnHeader[] headers;

    private HeaderCategory[] categories;

    private List<Map<Integer, CellData>> dataset;

    private OutputStream out;

    public ExportExcel(SheetStyle sheetStyle, ColumnHeader[] headers, List<Map<Integer, CellData>> dataset, OutputStream out) {
        this(sheetStyle, headers, null, dataset, out);
    }

    public ExportExcel(SheetStyle sheetStyle, ColumnHeader[] headers, HeaderCategory[] categories,
                       List<Map<Integer, CellData>> dataset, OutputStream out) {
        this.sheetStyle = sheetStyle;
        this.headers = headers;
        this.categories = categories;
        this.dataset = dataset;
        this.out = out;
    }

    public void exportExcel() throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet(sheetStyle.getTitle());
        sheet.setDefaultColumnWidth(sheetStyle.getDefaultWidth());

        int contentOffset = prepareHeader(workbook, sheet);
        sheet.createFreezePane(0, contentOffset, 0, contentOffset);

        XSSFCellStyle contentStyle = getContentCellStyle(workbook, CustomStyle.builder().build());
        for (int i = 0; i < dataset.size(); i++) {
            Map<Integer, CellData> vpd = dataset.get(i);
            SXSSFRow sheetRow = sheet.createRow(i + contentOffset);
            if (sheetStyle.getContentRowHeight() != null && sheetStyle.getContentRowHeight() > 0) {
                sheetRow.setHeight((short) (sheetStyle.getContentRowHeight() * 20));
            }
            for (int j = 0; j < headers.length; j++) {
                CellData cellData = vpd.getOrDefault(j, CellData.builder("").build());
                SXSSFCell cell = sheetRow.createCell(j);
                cell.setCellStyle(cellData.getStyle() != null ? getContentCellStyle(workbook, cellData.getStyle()) : contentStyle);
                cell.setCellType(cellData.getType() == null ? CellType.STRING : cellData.getType());
                cell.setCellValue(cellData.getValue());
            }
        }
        workbook.write(out);
    }

    private int prepareHeader(SXSSFWorkbook workbook, SXSSFSheet sheet) {
        XSSFCellStyle headerStyle = getHeaderCellStyle(workbook, CustomStyle.builder().build());
        SXSSFRow headerRow = sheet.createRow(categories == null || categories.length == 0 ? 0 : 1);
        headerRow.setHeight((short) (sheetStyle.getDefaultHeaderHeight() * 20));
        int contentOffset = 1;
        Set<Integer> spanHeaders = new HashSet<>();
        for (int i = 0; i < headers.length; i++) {
            ColumnHeader header = headers[i];
            SXSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(header.getStyle() != null ? getHeaderCellStyle(workbook, header.getStyle()) : headerStyle);
            cell.setCellValue(header.getHeader());
            cell.setCellType(CellType.STRING);
            spanHeaders.add(i);
            if (header.getWidth() != null) {
                sheet.setColumnWidth(i, header.getWidth() * 256);
            }
        }
        if (categories != null && categories.length > 0) {
            contentOffset++;
            SXSSFRow categoryRow = sheet.createRow(0);
            categoryRow.setHeight((short) (sheetStyle.getDefaultHeaderHeight() * 20));
            for (HeaderCategory category : categories) {
                for (int i = category.getStart(); i <= category.getEnd(); i++) {
                    spanHeaders.remove(i);
                    SXSSFCell cell = categoryRow.createCell(i);
                    cell.setCellStyle(category.getStyle() != null ? getHeaderCellStyle(workbook, category.getStyle()) : headerStyle);
                    cell.setCellValue(i == category.getStart() ? category.getName() : "");
                    cell.setCellType(CellType.STRING);
                }
                CellRangeAddress region = new CellRangeAddress(0, 0, category.getStart(), category.getEnd());
                sheet.addMergedRegion(region);
            }
            for (Integer spanIndex : spanHeaders) {
                SXSSFCell cell = headerRow.getCell(spanIndex);
                SXSSFCell targetCell = categoryRow.createCell(spanIndex);
                targetCell.setCellStyle(cell.getCellStyle());
                targetCell.setCellValue(cell.getStringCellValue());
                targetCell.setCellType(cell.getCellType());
                CellRangeAddress region = new CellRangeAddress(0, 1, spanIndex, spanIndex);
                sheet.addMergedRegion(region);
            }
        }
        return contentOffset;
    }

    private XSSFCellStyle getHeaderCellStyle(SXSSFWorkbook workbook, CustomStyle style) {
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setWrapText(true);
        if (style.getBg() != null) {
            headerStyle.setFillForegroundColor(style.getBg());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        setBorder(headerStyle);
        return headerStyle;
    }

    private XSSFCellStyle getContentCellStyle(SXSSFWorkbook workbook, CustomStyle style) {
        XSSFCellStyle contentStyle = (XSSFCellStyle) workbook.createCellStyle();
        if (style.getBg() != null) {
            contentStyle.setFillForegroundColor(style.getBg());
            contentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        XSSFDataFormat format = (XSSFDataFormat) workbook.createDataFormat();
        contentStyle.setDataFormat(format.getFormat("@"));
        contentStyle.setAlignment(style.getHorizontalAlignment());
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setWrapText(true);
        setBorder(contentStyle);
        return contentStyle;
    }

    private void setBorder(XSSFCellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }


}
