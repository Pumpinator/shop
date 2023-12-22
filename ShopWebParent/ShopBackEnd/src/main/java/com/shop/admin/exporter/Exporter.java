package com.shop.admin.exporter;

import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.shop.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public interface Exporter<T> {
    void exportCSV(List<T> list, HttpServletResponse response) throws IOException;

    void exportExcel(List<T> list, HttpServletResponse response) throws IOException;

    void exportPDF(List<T> list, HttpServletResponse response) throws IOException;

    void createPdfData(List<User> list, PdfPTable table);

    void createPdfTable(PdfPTable table);

    XSSFWorkbook createExcelWorkbook();

    void createExcelCell(XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle);

    void createExcelData(List<T> list, XSSFWorkbook workbook);

    void setResponseHeader(HttpServletResponse response, String contentType, String extension);
}
