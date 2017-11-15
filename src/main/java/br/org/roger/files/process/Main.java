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
 * This class is responsible for 
 * 
 * @author user
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
	private String endpointUrl = API_ENDPOINT;
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	// TODO parametrizar se o arquivo possui header
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: java -jar process-files.jar"
				+ " filesPath [encryptEnabled(true|false)] [hasHeader(true|false]");
		}
		
		String filesPath = args[0];
		String encryptEnabled = "false";
		String hasHeader = "false";
		if (args.length > 1) {
			encryptEnabled = args[1];
		}
		if (args.length > 2) {
			hasHeader = args[2];
		}
		
		Main main = new Main(filesPath, encryptEnabled, hasHeader);
		main.processFiles();
		
	}
	
	public Main(final String filesPath, final String encryptEnabled, final String hasHeader) {
		this.filesPath = filesPath;
		this.encryptEnabled = Boolean.valueOf(encryptEnabled);
		this.hasHeader = Boolean.valueOf(hasHeader);
	}
	
	private void processFiles() {
		Path inputFilesDir = Paths.get(this.filesPath);
		try {
			Files.list(inputFilesDir)
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.flatMap(this::extractRowsFromFile)
				.map(this::mapToDomain)
				.map(this::convertToJson)
				.map(this::encryptJson)
				.filter(Optional::isPresent)
				.map(Optional::get)
				//.forEach(this::postMessage);
				.forEach(System.out::println);
		} catch (IOException e) {
			LOGGER.error("Error listing files from path.", e);
		}
	}
	
	private Stream<Row> extractRowsFromFile(final File mobileFile) {
		int skipLines = hasHeader ? ONE_LINE : NO_LINE;
		try(XSSFWorkbook workbook = new XSSFWorkbook(mobileFile)) {
			XSSFSheet spreadsheet = workbook.getSheetAt(0);
			return StreamSupport.stream(spreadsheet.spliterator(), false)
				.skip(skipLines);
		} catch (InvalidFormatException | IOException e) {
			LOGGER.error("Error processing file " + mobileFile.getAbsolutePath(), e);
			return Stream.empty();
		}
	}

	private AnotherObject mapToDomain(final Row row) {
		return new AnotherObject(
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
	
	private String convertToJson(final AnotherObject mobileData) {
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
	
	private void postMessage(final String jsonToSend) {
		try {
			URL url = new URL(this.endpointUrl);
			HttpURLConnection urlConnection = null;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setRequestMethod(HTTP_METHOD_POST);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(jsonToSend.getBytes());
            out.close();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }

            //close the reader
            r.close();
            LOGGER.info("Data sent to the endpoint!");
		} catch (Exception e) {
			LOGGER.error("We can't send data to server, something wrong happened :(", e);
		}
	}

}
