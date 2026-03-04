package com.advertising.agent.util;

import com.advertising.entity.BarrierGate;
import com.advertising.entity.Community;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * Excel导出工具类
 */
@Component
public class ExcelExportUtil {

    /**
     * 导出道闸点位数据为Excel文件（返回Base64编码）
     * 
     * @param barriers 道闸列表
     * @param city 城市
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return Base64编码的Excel文件内容
     */
    public String exportBarriersToBase64(List<BarrierGate> barriers, String city, 
                                          LocalDate beginDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // 创建sheet
            Sheet sheet = workbook.createSheet("销控表");
            
            // 创建标题样式
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            // 创建标题行
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(String.format("%s %s 至 %s 销控表", 
                    city, 
                    beginDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            titleCell.setCellStyle(titleStyle);
            
            // 合并标题行单元格
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));
            
            // 创建表头行
            Row headerRow = sheet.createRow(2);
            String[] headers = {"序号", "道闸编号", "设备编号", "门岗位置", "社区名称", 
                               "城市", "详细地址", "开始日期", "结束日期"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            int rowNum = 3;
            for (int i = 0; i < barriers.size(); i++) {
                BarrierGate barrier = barriers.get(i);
                Community community = barrier.getCommunity();
                
                Row row = sheet.createRow(rowNum++);
                
                // 序号
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(i + 1);
                cell0.setCellStyle(dataStyle);
                
                // 道闸编号
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(barrier.getGateNo());
                cell1.setCellStyle(dataStyle);
                
                // 设备编号
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(barrier.getDeviceNo() != null ? barrier.getDeviceNo() : "");
                cell2.setCellStyle(dataStyle);
                
                // 门岗位置
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(barrier.getDoorLocation() != null ? barrier.getDoorLocation() : "");
                cell3.setCellStyle(dataStyle);
                
                // 社区名称
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(community != null && community.getBuildingName() != null 
                        ? community.getBuildingName() : "");
                cell4.setCellStyle(dataStyle);
                
                // 城市
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(community != null && community.getCity() != null 
                        ? community.getCity() : city);
                cell5.setCellStyle(dataStyle);
                
                // 详细地址
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(community != null && community.getBuildingAddress() != null 
                        ? community.getBuildingAddress() : "");
                cell6.setCellStyle(dataStyle);
                
                // 开始日期
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(beginDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                cell7.setCellStyle(dateStyle);
                
                // 结束日期
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                cell8.setCellStyle(dateStyle);
            }
            
            // 设置列宽
            sheet.setColumnWidth(0, 8);    // 序号
            sheet.setColumnWidth(1, 20);   // 道闸编号
            sheet.setColumnWidth(2, 20);   // 设备编号
            sheet.setColumnWidth(3, 25);   // 门岗位置
            sheet.setColumnWidth(4, 30);   // 社区名称
            sheet.setColumnWidth(5, 15);   // 城市
            sheet.setColumnWidth(6, 40);   // 详细地址
            sheet.setColumnWidth(7, 15);   // 开始日期
            sheet.setColumnWidth(8, 15);   // 结束日期
            
            // 冻结表头
            sheet.createFreezePane(0, 3);
            
            // 写入输出流
            workbook.write(outputStream);
            
            // 转为Base64
            byte[] excelBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(excelBytes);
        }
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
}
