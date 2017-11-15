package br.org.roger.files.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

/**
 * 
 * @author marcos
 *
 */
public class Main {
	
	private static final int NO_LINE = 0;
	private static final int ONE_LINE = 1;
	private static final String HTTP_METHOD_POST = "POST";
	private static final String API_ENDPOINT = "https://localhost:8080/inbox";
	private static final int TIMEOUT = 2000;

	private final String filesPath;
	private boolean encryptEnabled;
	private boolean hasHeader;
	private boolean postToEndpoint;
	private String endpointUrl = API_ENDPOINT;
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: java -jar process-files.jar"
				+ " filesPath [encryptEnabled(true|false)] [hasHeader(true|false]");
		}
		
		String filesPath = args[0];
		String encryptEnabled = "false";
		String hasHeader = "true";
		String postToEndpoint = "false";
		if (args.length > 1) {
			encryptEnabled = args[1];
		}
		if (args.length > 2) {
			hasHeader = args[2];
		}
		if (args.length > 3) {
			postToEndpoint = args[3];
		}
		
		Main main = new Main(filesPath, encryptEnabled, hasHeader, postToEndpoint);
		main.processFiles();
		
	}
	
	public Main(final String filesPath, final String encryptEnabled,
			final String hasHeader, final String postToEndpoint) {
		this.filesPath = filesPath;
		this.encryptEnabled = Boolean.valueOf(encryptEnabled);
		this.hasHeader = Boolean.valueOf(hasHeader);
		this.postToEndpoint = Boolean.valueOf(postToEndpoint);
	}
	
	private void processFiles() {
		Path inputFilesDir = Paths.get(this.filesPath);
		try (Stream<Path> files = Files.list(inputFilesDir)) {
			files
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.flatMap(this::extractRowsFromFile)
				.map(this::mapToDomain)
				.map(this::convertToJson)
				.map(this::encryptJson)
				.filter(this::isNonEmptyData)
				.map(this::getNonEmptyData)
				.peek(System.out::println)
				.forEach(this::postMessage);
			System.exit(0);
		} catch (IOException e) {
			LOGGER.error("Error listing files from path.", e);
			System.exit(1);
		}
	}
	
	private Stream<Row> extractRowsFromFile(final File inputFile) {
		int skipLines = hasHeader ? ONE_LINE : NO_LINE;
		boolean isParallel = false;
		try(XSSFWorkbook workbook = new XSSFWorkbook(inputFile)) {
			XSSFSheet spreadsheet = workbook.getSheetAt(0);
			return StreamSupport.stream(spreadsheet.spliterator(), isParallel)
				.skip(skipLines);
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
	
	private String convertToJson(final DomainObject mobileData) {
		return new Gson().toJson(mobileData);
	}
	
	private Optional<String> encryptJson(final String jsonToSend) {
		if (!encryptEnabled) {
			return Optional.of(jsonToSend);
		}
		final String aesKey = "super-secret-key";
		SecretKey secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");

		Cipher aes;
		try {
			aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

			aes.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
			byte[] ciphertext = aes.doFinal(jsonToSend.getBytes());

			String encrypted = Base64.getEncoder().encodeToString(ciphertext);
			return Optional.of(encrypted);
		} catch (Exception e) {
			LOGGER.error("Error encrypting Json payload", e);
			return Optional.empty();
		}
	}
	
	private boolean isNonEmptyData(Optional<String> checkingData) {
		return checkingData.isPresent();
	}
	
	private String getNonEmptyData(Optional<String> nonEmptyData) {
		return nonEmptyData.get();
	}
	
	private void postMessage(final String jsonToSend) {
		if (!postToEndpoint) {
			return;
		}
		try {
			URL url = new URL(this.endpointUrl);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setConnectTimeout(TIMEOUT);
			urlConnection.setRequestMethod(HTTP_METHOD_POST);

			try (OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					BufferedReader r = new BufferedReader(new InputStreamReader(in));) {

				out.write(jsonToSend.getBytes());

				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = r.readLine()) != null) {
					sb.append(line).append('\n');
				}

				LOGGER.info("Data sent to the endpoint!");
			} catch (Exception e) {
				LOGGER.error("Error! It's not possible to send data to the server.", e);
			}
		} catch (Exception e) {
			LOGGER.error("Error! It's not possible to connect to the server.", e);
		}
	}

}
