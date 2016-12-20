package ru.pgu.practice.csv_to_doc.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import ru.pgu.practice.csv_to_doc.model.DataRow;
import ru.pgu.practice.csv_to_doc.model.Response;
import ru.pgu.practice.csv_to_doc.model.Sex;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Convert csv file to xls file
 */
public class ConverterService {
    private final Logger log = LoggerFactory.getLogger(ConverterService.class);

    private static final String ROOT_DIR = System.getProperties().getProperty("user.dir");
    private static final String DATA_DIR = "data";
    private static final String RESULT_DIR = "result";
    private static final String RESULT_FILENAME_TEMPLATE = "result_%s.xlsx";

    private ArrayList<DataRow> rows = new ArrayList<>();

    /**
     * start reads the csv file line by line,
     * for each line forms the object "DataRow"
     * and puts them into a collection of ArrayList
     * @param file
     */
    public void start(MultipartFile file) {
        if (file == null) {
            log.error("FILE IS NULL");
            return;
        }

        try {
            File csvFile = toFile(file);
            Files.lines(csvFile.toPath(), StandardCharsets.UTF_8)
                    .filter(line -> !Objects.equals(line, "fio;age;sex;salary"))
                    .map(line -> {
                        String[] parts;
                        parts = line.split(";");
                        int age = Integer.valueOf(parts[1]);
                        Sex sex = Sex.valueOf(parts[2]);
                        BigDecimal salary = new BigDecimal(parts[3]);
                        return new DataRow(parts[0], age, sex, salary);
                    })
                    .forEach((DataRow row) -> {
                        rows.add(row);
                        if (rows.size() == 9){
                            writeXlsFile(Collections.unmodifiableList(rows));
                            rows.clear();
                        }
                    } );
        } catch (Exception e) {
            log.error("FILE CONVERTING ERROR: \n", e);
        }
    }

    /**
     * TODO write javaDoc
     * @return
     */
    public Response checkResult() {
        Response response = new Response();
        return response;
    }

    /**
     * writeXlsFile create xls file
     * and write data from DataRow into it
     * @param rows
     */
    private void writeXlsFile(List<DataRow> rows) {
        String timestamp = String.valueOf(System.nanoTime());
        String resultFilename = String.format(RESULT_FILENAME_TEMPLATE, timestamp);
        File file = Paths.get(ROOT_DIR, RESULT_DIR, resultFilename).toFile();

        try (Workbook book = new HSSFWorkbook();
             OutputStream os = new FileOutputStream(file)) {

            Sheet sheet = book.createSheet("CSV Convert");

            for (DataRow row : rows) {
                String fio = row.getFio();
                int age = row.getAge();
                String sex = row.getSex() == Sex.MALE ? "Мужской" : "Женский";
                BigDecimal salary = row.getSalary();

                Row sheetRow = sheet.createRow(0);
                Cell cellFio = sheetRow.createCell(0);
                cellFio.setCellValue(fio);

                Cell cellAge = sheetRow.createCell(1);
                cellAge.setCellValue(Integer.toString(age));

                Cell cellSex = sheetRow.createCell(2);
                cellSex.setCellValue(sex);

                Cell cellSalary = sheetRow.createCell(3);
                cellSalary.setCellValue(salary.toString());
            }
            BigDecimal sum = rows.stream()
                    .map(DataRow::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Row sumRow = sheet.createRow(11);
            Cell cellResult = sumRow.createCell(0);
            cellResult.setCellValue("Всего:");

            Cell cellSum = sumRow.createCell(3);
            cellSum.setCellValue(sum.toString());

            sheet.autoSizeColumn(0);
            book.write(os);
        }catch (FileNotFoundException e) {

            log.error("File {} not found", file);
        } catch (IOException e) {

            log.error("xlsx File writing error");
        }
    }

    private File toFile(MultipartFile multipartFile) throws IOException {
        Path dataDir = Paths.get(ROOT_DIR, DATA_DIR);
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        File result = Paths.get(ROOT_DIR, DATA_DIR, multipartFile.getOriginalFilename()).toFile();
        multipartFile.transferTo(result);
        return result;
    }
}
