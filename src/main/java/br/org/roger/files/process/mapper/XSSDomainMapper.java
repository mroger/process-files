package br.org.roger.files.process.mapper;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSDomainMapper {
	
	private static final Logger LOGGER = Logger.getLogger(XSSDomainMapper.class.getName());

	public Stream<DomainObject> streamOfDomainFromFile(final File inputFile) {
		boolean isParallel = false;
		try(XSSFWorkbook workbook = new XSSFWorkbook(inputFile)) {
			XSSFSheet spreadsheet = workbook.getSheetAt(0);
			return StreamSupport
				.stream(spreadsheet.spliterator(), isParallel)
				.map(this::mapToDomain);
		} catch (InvalidFormatException | IOException e) {
			LOGGER.error("Error processing file " + inputFile.getAbsolutePath(), e);
			return Stream.empty();
		}
	}

	private DomainObject mapToDomain(final Row row) {
		return new DomainObject(
			getCheckedValue(row.getCell(0)),
			getCheckedValue(row.getCell(1)),
			getCheckedValue(row.getCell(2)),
			getCheckedValue(row.getCell(3)),
			getCheckedValue(row.getCell(4)));
	}

	private String getCheckedValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch(cell.getCellTypeEnum()) {
			case NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue();
			default:
				return "";
		}
	}

}
