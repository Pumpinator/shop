package com.shop.admin.exporter;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shop.common.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserExporter implements Exporter<User>{
    @Override
    public void exportCSV(List<User> users, HttpServletResponse response) throws IOException {
        setResponseHeader(response, "text/csv", ".csv");
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(
                response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Id", "Frst Name", "Last Name", "Email", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "firstName", "lastName", "email", "roles", "enabled"};
        csvBeanWriter.writeHeader(csvHeader);
        for (User user : users) {
            csvBeanWriter.write(user, fieldMapping);
        }
        csvBeanWriter.close();
    }

    @Override
    public void exportExcel(List<User> users, HttpServletResponse response) throws IOException {
        setResponseHeader(response, "application/octect-stream", ".xlsx");
        XSSFWorkbook workbook = createExcelWorkbook();
        createExcelData(users, workbook);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        workbook.write(servletOutputStream);
        workbook.close();
        servletOutputStream.close();
    }

    @Override
    public void exportPDF(List<User> users, HttpServletResponse response) throws IOException {
        setResponseHeader(response, "application/pdf", ".pdf");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(14);
        Paragraph paragraph = new Paragraph("Users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1.2f, 3.0f, 3.0f, 3.5f, 3.0f, 1.7f});
        createPdfTable(table);
        createPdfData(users, table);
        document.add(table);
        document.close();
    }

    @Override
    public void createPdfData(List<User> users, PdfPTable table) {
        for (User user : users) {
            table.getDefaultCell().setBorderWidth(0);
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getEmail());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.isEnabled()));
        }
    }

    @Override
    public void createPdfTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(0);
        cell.setBackgroundColor(Color.GRAY);
        cell.setPadding(5);
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
        cell.setPhrase(new Phrase("Id", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Roles", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);
    }

    @Override
    public XSSFWorkbook createExcelWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        cellStyle.setFont(font);
        createExcelCell(row, 0, "Id", cellStyle);
        createExcelCell(row, 1, "First Name", cellStyle);
        createExcelCell(row, 2, "Last Name", cellStyle);
        createExcelCell(row, 3, "Email", cellStyle);
        createExcelCell(row, 4, "Roles", cellStyle);
        createExcelCell(row, 5, "Enabled", cellStyle);
        return workbook;
    }

    @Override
    public void createExcelCell(XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.getSheet().autoSizeColumn(columnIndex);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void createExcelData(List<User> users, XSSFWorkbook workbook) {
        int rowIndex = 1;
        XSSFSheet sheet = workbook.getSheet("Users");
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        cellStyle.setFont(font);
        for (User user : users) {
            sheet.createRow(rowIndex);
            int columnIndex = 0;
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.getId(), cellStyle);
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.getFirstName(), cellStyle);
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.getLastName(), cellStyle);
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.getEmail(), cellStyle);
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.getRoles().toString(), cellStyle);
            createExcelCell(sheet.getRow(rowIndex), columnIndex++, user.isEnabled(), cellStyle);
            rowIndex++;
        }
    }

    @Override
    public void setResponseHeader(HttpServletResponse response, String contentType, String extension) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormat.format(new Date());
        String fileName = "users_" + timeStamp + extension;
        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
    }
}
