package br.org.roger.files.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * 
 * @author user
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Path path = Paths.get("E:/Projects/Tail/files/xlsx");
		Files.list(path)
			.filter(Files::isRegularFile)
			.map(Path::toFile)
			.map(Main::covertToJson)
			.map(Main::closeJsonArray)
			.forEach(Main::postJson);
//			.forEach(file -> {
//				XSSFWorkbook workbook;
//				try {
//					workbook = new XSSFWorkbook(file);
//					XSSFSheet spreadsheet = workbook.getSheetAt(0);
//					String json = StreamSupport.stream(spreadsheet.spliterator(), false)
//					.map(Main::mapToDomain)
//					.map(Main::convertRowToJson)
//					.collect(Collectors.joining(","));
//					
//					System.out.println(json);
//				} catch (InvalidFormatException | IOException e) {
//					System.out.println("Erro!!!");
//				}
//			});
		
	}
	
	private static Optional<String> covertToJson(final File file) {
		
		try(XSSFWorkbook workbook = new XSSFWorkbook(file)) {
			XSSFSheet spreadsheet = workbook.getSheetAt(0);
			String json = StreamSupport.stream(spreadsheet.spliterator(), false)
			.map(Main::mapToDomain)
			.map(Main::convertRowToJson)
			.collect(Collectors.joining(","));
			
			return Optional.of(json);
		} catch (InvalidFormatException | IOException e) {
			System.out.println("Erro!!!");
			return Optional.empty();
		}
	}
	
	private static String closeJsonArray(Optional<String> openJsonArray) {
		return openJsonArray.isPresent() ? "[" + openJsonArray.get() + "]" : "[]";
	}
	
	private static void postJson(String json) {
		// http://www.baeldung.com/httpclient-post-http-request
		System.out.println("Json: " + json);
	}

	private static AnotherObject mapToDomain(final Row row) {
		return new AnotherObject(
			row.getCell(0).getStringCellValue(),
			row.getCell(1).getNumericCellValue(),
			row.getCell(2).getNumericCellValue(),
			row.getCell(3).getNumericCellValue(),
			row.getCell(4).getNumericCellValue());
	}
	
	private static String convertRowToJson(final AnotherObject anotherObject) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
	
		return gson.toJson(anotherObject);
	}

}
