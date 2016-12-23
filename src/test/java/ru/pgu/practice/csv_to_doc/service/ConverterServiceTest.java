package ru.pgu.practice.csv_to_doc.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ConverterServiceTest {

    /**
     * test for empty file
     * checks whether there is a file in result folder
     */
    @Test
    public void emptyFileTest() throws Exception {
        URL csvLocation = this.getClass().getClassLoader().getResource("emptyFile.csv");
        Assert.assertNotNull(csvLocation);

        File csvFile = new File(csvLocation.toURI());
        byte[] content = Files.readAllBytes(csvFile.toPath());

        MultipartFile multipartFile = new MockMultipartFile(
            csvFile.getName(),
            csvFile.getName(),
            "text/plain",
            content
        );

        Assert.assertNotNull(multipartFile);

        ConverterService convertRun = new ConverterService();
        convertRun.start(multipartFile);


        File resultDir = Paths.get(
            privateStringFieldValue(convertRun, "ROOT_DIR"),
            privateStringFieldValue(convertRun, "RESULT_DIR")
        ).toFile();

        File[] resultFiles = resultDir.listFiles();
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(0, resultFiles.length);
    }

    /**
     * test for file with single line
     * checks whether there is a file in result folder
     * checks the file itself
     */
    @Test
    public void oneLineFileTest() throws Exception {
        URL csvLocation = this.getClass().getClassLoader().getResource("oneLine.csv");
        Assert.assertNotNull(csvLocation);

        File csvFile = new File(csvLocation.toURI());
        byte[] content = Files.readAllBytes(csvFile.toPath());

        MultipartFile multipartFile = new MockMultipartFile(
                csvFile.getName(),
                csvFile.getName(),
                "text/plain",
                content
        );

        Assert.assertNotNull(multipartFile);

        ConverterService convertRun = new ConverterService();
        convertRun.start(multipartFile);

        File resultDir = Paths.get(
                privateStringFieldValue(convertRun, "ROOT_DIR"),
                privateStringFieldValue(convertRun, "RESULT_DIR")
        ).toFile();

        File[] resultFiles = resultDir.listFiles();
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(1, resultFiles.length);

        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(resultFiles[0]));
        HSSFSheet myExcelSheet = myExcelBook.createSheet("CSV Convert");
        HSSFRow row = myExcelSheet.getRow(0);
        Assert.assertEquals("Иван Пупкин", row.getCell(0).getStringCellValue());

        Assert.assertEquals("23", row.getCell(1).getStringCellValue());

        Assert.assertEquals("m", row.getCell(2).getStringCellValue());

        Assert.assertEquals("12000", row.getCell(3).getStringCellValue());
        HSSFRow rowTwo = myExcelSheet.getRow(1);

        Assert.assertEquals("Всего:", rowTwo.getCell(1).getStringCellValue());
        Assert.assertEquals("12000", rowTwo.getCell(3).getStringCellValue());

        resultFiles[0].delete();
    }

    /**
     * test for file with ten lines
     * checks whether there is a file in result folder
     * checks first row, last row and "Total" row in file
     */
    @Test
    public void tenLinesFileTest() throws Exception {
        URL csvLocation = this.getClass().getClassLoader().getResource("tenLines.csv");
        Assert.assertNotNull(csvLocation);

        File csvFile = new File(csvLocation.toURI());
        byte[] content = Files.readAllBytes(csvFile.toPath());

        MultipartFile multipartFile = new MockMultipartFile(
                csvFile.getName(),
                csvFile.getName(),
                "text/plain",
                content
        );

        Assert.assertNotNull(multipartFile);

        ConverterService convertRun = new ConverterService();
        convertRun.start(multipartFile);

        File resultDir = Paths.get(
                privateStringFieldValue(convertRun, "ROOT_DIR"),
                privateStringFieldValue(convertRun, "RESULT_DIR")
        ).toFile();

        File[] resultFiles = resultDir.listFiles();
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(1, resultFiles.length);

        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(resultFiles[0]));
        HSSFSheet myExcelSheet = myExcelBook.createSheet("CSV Convert");
        HSSFRow row = myExcelSheet.getRow(0);
        Assert.assertEquals("Иван Иванович", row.getCell(0).getStringCellValue());

        Assert.assertEquals("18", row.getCell(1).getStringCellValue());

        Assert.assertEquals("m", row.getCell(2).getStringCellValue());

        Assert.assertEquals("12800", row.getCell(3).getStringCellValue());

        HSSFRow tenthRow = myExcelSheet.getRow(9);
        Assert.assertEquals("Надежда Суркова", tenthRow.getCell(0).getStringCellValue());

        Assert.assertEquals("21", tenthRow.getCell(1).getStringCellValue());

        Assert.assertEquals("f", tenthRow.getCell(2).getStringCellValue());

        Assert.assertEquals("15000", tenthRow.getCell(3).getStringCellValue());

        HSSFRow totalRow = myExcelSheet.getRow(10);

        Assert.assertEquals("Всего:", totalRow.getCell(1).getStringCellValue());
        Assert.assertEquals("203000", totalRow.getCell(3).getStringCellValue());

        resultFiles[0].delete();
    }

    /**
     * test for file with eleven lines
     * checks whether two files are creating in result folder
     * checks first row, last row and "Total" row in firs file
     * and checks first row and "Total" row in second file
     */
    @Test
    public void elevenLinesFileTest() throws Exception {
        URL csvLocation = this.getClass().getClassLoader().getResource("elevenLines.csv");
        Assert.assertNotNull(csvLocation);

        File csvFile = new File(csvLocation.toURI());
        byte[] content = Files.readAllBytes(csvFile.toPath());

        MultipartFile multipartFile = new MockMultipartFile(
                csvFile.getName(),
                csvFile.getName(),
                "text/plain",
                content
        );

        Assert.assertNotNull(multipartFile);

        ConverterService convertRun = new ConverterService();
        convertRun.start(multipartFile);

        File resultDir = Paths.get(
                privateStringFieldValue(convertRun, "ROOT_DIR"),
                privateStringFieldValue(convertRun, "RESULT_DIR")
        ).toFile();

        File[] resultFiles = resultDir.listFiles();
        Assert.assertNotNull(resultFiles);
        Assert.assertEquals(2, resultFiles.length);

        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(resultFiles[0]));
        HSSFSheet myExcelSheet = myExcelBook.createSheet("CSV Convert");
        HSSFRow row = myExcelSheet.getRow(0);
        Assert.assertEquals("Иван Иванович", row.getCell(0).getStringCellValue());

        Assert.assertEquals("18", row.getCell(1).getStringCellValue());

        Assert.assertEquals("m", row.getCell(2).getStringCellValue());

        Assert.assertEquals("12800", row.getCell(3).getStringCellValue());

        HSSFRow tenthRow = myExcelSheet.getRow(9);
        Assert.assertEquals("Надежда Суркова", tenthRow.getCell(0).getStringCellValue());

        Assert.assertEquals("21", tenthRow.getCell(1).getStringCellValue());

        Assert.assertEquals("f", tenthRow.getCell(2).getStringCellValue());

        Assert.assertEquals("15000", tenthRow.getCell(3).getStringCellValue());

        HSSFRow totalRow = myExcelSheet.getRow(10);
        Assert.assertEquals("Всего:", totalRow.getCell(1).getStringCellValue());

        Assert.assertEquals("216000", totalRow.getCell(3).getStringCellValue());

        HSSFWorkbook secExcelBook = new HSSFWorkbook(new FileInputStream(resultFiles[1]));
        HSSFSheet secExcelSheet = secExcelBook.createSheet("CSV Convert");
        HSSFRow secFileRow = secExcelSheet.getRow(0);
        Assert.assertEquals("Иван Моржов", secFileRow.getCell(0).getStringCellValue());

        Assert.assertEquals("25", secFileRow.getCell(1).getStringCellValue());

        Assert.assertEquals("m", secFileRow.getCell(2).getStringCellValue());

        Assert.assertEquals("13000", secFileRow.getCell(3).getStringCellValue());

        HSSFRow secTotalRow = secExcelSheet.getRow(10);
        Assert.assertEquals("Всего:", secTotalRow.getCell(1).getStringCellValue());

        Assert.assertEquals("13000", secTotalRow.getCell(3).getStringCellValue());

        resultFiles[0].delete();
        resultFiles[1].delete();
    }

    private String privateStringFieldValue(Object object, String fieldName) {
        try {
            Field rootDirField = ConverterService.class.getDeclaredField(fieldName);
            rootDirField.setAccessible(true);
            return (String) rootDirField.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
