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
import java.util.*;

/**
 * @author DreamJM
 */
public class ExportExcel {

    private SheetStyle sheetStyle;

    private Column[] columns;

    private HeaderCategory[] categories;

    private List<Map<Integer, CellData>> dataset;

    private OutputStream out;

    private CustomStyle defaultStyle;

    public ExportExcel(SheetStyle sheetStyle, Column[] columns, List<Map<Integer, CellData>> dataset, OutputStream out) {
        this(sheetStyle, columns, null, dataset, out);
    }

    public ExportExcel(SheetStyle sheetStyle, Column[] columns, HeaderCategory[] categories,
                       List<Map<Integer, CellData>> dataset, OutputStream out) {
        this.sheetStyle = sheetStyle;
        this.columns = columns;
        this.categories = categories;
        this.dataset = dataset;
        this.defaultStyle = sheetStyle.getDefaultStyle() == null ? CustomStyle.builder().build() : sheetStyle.getDefaultStyle();
        this.out = out;
    }

    public void exportExcel() throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet(sheetStyle.getTitle());
        sheet.setDefaultColumnWidth(sheetStyle.getDefaultWidth());

        int contentOffset = prepareHeader(workbook, sheet);
        if (sheetStyle.isFreezeHeader()) {
            sheet.createFreezePane(0, contentOffset, 0, contentOffset);
        }

        XSSFCellStyle contentStyle = getContentCellStyle(workbook, defaultStyle);
        Map<Integer, XSSFCellStyle> styleMap = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            CustomStyle columnStyle = columns[i].getStyle();
            styleMap.put(i, columnStyle != null ? getContentCellStyle(workbook, columnStyle) : contentStyle);
        }
        for (int i = 0; i < dataset.size(); i++) {
            Map<Integer, CellData> vpd = dataset.get(i);
            SXSSFRow sheetRow = sheet.createRow(i + contentOffset);
            if (sheetStyle.getContentRowHeight() != null && sheetStyle.getContentRowHeight() > 0) {
                sheetRow.setHeight((short) (sheetStyle.getContentRowHeight() * 20));
            }
            for (int j = 0; j < columns.length; j++) {
                CellData cellData = vpd.getOrDefault(j, CellData.builder("").build());
                SXSSFCell cell = sheetRow.createCell(j + sheetStyle.getXOffset());
                cell.setCellStyle(cellData.getStyle() != null ? getContentCellStyle(workbook, cellData.getStyle()) : styleMap.get(j));
                cell.setCellType(cellData.getType() == null ? CellType.STRING : cellData.getType());
                cell.setCellValue(cellData.getValue());
            }
        }
        workbook.write(out);
    }

    private int prepareHeader(SXSSFWorkbook workbook, SXSSFSheet sheet) {
        XSSFCellStyle headerStyle = getHeaderCellStyle(workbook,
                sheetStyle.getDefaultHeaderStyle() == null ? CustomStyle.builder().build() : sheetStyle.getDefaultHeaderStyle());
        SXSSFRow headerRow = sheet.createRow(sheetStyle.getYOffset() + (categories == null || categories.length == 0 ? 0 : 1));
        headerRow.setHeight((short) (sheetStyle.getHeaderHeight() * 20));
        int contentOffset = 1 + sheetStyle.getYOffset();
        Set<Integer> spanHeaders = new HashSet<>();
        for (int i = 0; i < columns.length; i++) {
            Column header = columns[i];
            SXSSFCell cell = headerRow.createCell(i + sheetStyle.getXOffset());
            cell.setCellStyle(header.getHeaderStyle() != null ? getHeaderCellStyle(workbook, header.getHeaderStyle()) : headerStyle);
            cell.setCellValue(header.getHeader());
            cell.setCellType(CellType.STRING);
            spanHeaders.add(i);
            if (header.getWidth() != null) {
                sheet.setColumnWidth(i, header.getWidth() * 256);
            }
        }
        if (categories != null && categories.length > 0) {
            contentOffset++;
            SXSSFRow categoryRow = sheet.createRow(sheetStyle.getYOffset());
            if (sheetStyle.getCategoryHeight() > 0) {
                categoryRow.setHeight((short) (sheetStyle.getHeaderHeight() * 20));
            }
            XSSFCellStyle categoryStyle = getHeaderCellStyle(workbook,
                    sheetStyle.getDefaultCategoryStyle() == null ? CustomStyle.builder().build() : sheetStyle.getDefaultCategoryStyle());
            for (HeaderCategory category : categories) {
                for (int i = category.getStart(); i <= category.getEnd(); i++) {
                    spanHeaders.remove(i);
                    SXSSFCell cell = categoryRow.createCell(i + sheetStyle.getXOffset());
                    cell.setCellStyle(category.getStyle() != null ? getHeaderCellStyle(workbook, category.getStyle()) : categoryStyle);
                    cell.setCellValue(i == category.getStart() ? category.getName() : "");
                    cell.setCellType(CellType.STRING);
                }
                if (category.getStart() != category.getEnd()) {
                    CellRangeAddress region = new CellRangeAddress(sheetStyle.getYOffset(), sheetStyle.getYOffset(),
                            category.getStart() + sheetStyle.getXOffset(),
                            category.getEnd() + sheetStyle.getXOffset());
                    sheet.addMergedRegion(region);
                }
            }
            for (Integer spanIndex : spanHeaders) {
                SXSSFCell cell = headerRow.getCell(spanIndex + sheetStyle.getXOffset());
                SXSSFCell targetCell = categoryRow.createCell(spanIndex + sheetStyle.getXOffset());
                targetCell.setCellStyle(cell.getCellStyle());
                targetCell.setCellValue(cell.getStringCellValue());
                targetCell.setCellType(cell.getCellType());
                CellRangeAddress region =
                        new CellRangeAddress(sheetStyle.getYOffset(), sheetStyle.getYOffset() + 1, spanIndex + sheetStyle.getXOffset(),
                                spanIndex + sheetStyle.getXOffset());
                sheet.addMergedRegion(region);
            }
        }
        return contentOffset;
    }

    private XSSFCellStyle getHeaderCellStyle(SXSSFWorkbook workbook, CustomStyle style) {
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = prepareFont(workbook, style);
        headerFont.setBold(true);
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
        contentStyle.setFont(prepareFont(workbook, style));
        XSSFDataFormat format = (XSSFDataFormat) workbook.createDataFormat();
        contentStyle.setDataFormat(format.getFormat("@"));
        contentStyle.setAlignment(style.getHorizontalAlignment());
        contentStyle.setVerticalAlignment(style.getVerticalAlignment());
        contentStyle.setWrapText(true);
        setBorder(contentStyle);
        return contentStyle;
    }

    private XSSFFont prepareFont(SXSSFWorkbook workbook, CustomStyle style) {
        XSSFFont font = (XSSFFont) workbook.createFont();
        if (style.getFontColor() != null) {
            font.setColor(style.getFontColor());
        } else if (defaultStyle.getFontColor() != null) {
            font.setColor(defaultStyle.getFontColor());
        }
        if (style.getFontName() != null && !"".equals(style.getFontName())) {
            font.setFontName(style.getFontName());
        } else if (defaultStyle.getFontName() != null && !"".equals(defaultStyle.getFontName())) {
            font.setFontName(defaultStyle.getFontName());
        }
        if (style.getFontSize() > 0) {
            font.setFontHeightInPoints((short) style.getFontSize());
        } else if (defaultStyle.getFontSize() > 0) {
            font.setFontHeightInPoints((short) style.getFontSize());
        }
        return font;
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
