package br.org.roger.files.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * 
 * @author marcos
 *
 */
public class Main {
	
	private final String filesPath;
	private boolean encryptEnabled;
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	private Main(final MainBuilder builder) {
		this.filesPath = builder.getFilesPath();
		this.encryptEnabled = builder.encryptEnabled();
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: java -jar process-files.jar"
				+ " filesPath [encryptEnabled(true|false)] [hasHeader(true|false]");
		}
		
		Main main = new Main.MainBuilder(args)
			.withFilesPath()
			.withEncryptEnabled()
			.build();
		main.processFiles();
		
	}
	
	private void processFiles() {
		long start = System.currentTimeMillis();
		
		Path inputFilesDir = Paths.get(this.filesPath);
		long quant = 0;
		try (Stream<Path> files = Files.list(inputFilesDir)) {
			quant = files
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.flatMap(this::convertToMobileData)
				.collect(Collectors.groupingBy(DomainObject::getHash, Collectors.toList())).values().stream()
				.flatMap(this::calculateLastCoordinates)
				.map(this::convertToJson)
				.map(this::encryptJson)
				.filter(this::isNonEmptyData)
				.map(this::getNonEmptyData)
				.count();
			
			long now = System.currentTimeMillis();
			LOGGER.info((now - start) / 1000.0 + " - Quant: " + quant);
			
			System.exit(0);
		} catch (Exception e) {
			LOGGER.error("Error listing files from path.", e);
			System.exit(1);
		}
	}
	
	private Stream<DomainObject> convertToMobileData(final File mobileFile) {
		try (InputStream inputStream = new FileInputStream(mobileFile);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
			return bufferedReader
				.lines()
				.map(this::toArray)
				.map(this::mapToDomain)
				//.peek(t -> { if (t.getLatitude().equals("-19.4752309")) { throw new RuntimeException("Erro na linha"); } })
				.collect(Collectors.toList()).stream();
		} catch (Exception e) {
			LOGGER.error("Error processing file " + mobileFile.getAbsolutePath(), e);
			return Stream.empty();
		}
	}

	private String[] toArray(final String line) {
		return line.split("\t");
	}
	
	private DomainObject mapToDomain(final String[] fields) {
		return new DomainObject(
			fields[1],
			fields[0],
			fields[3],
			fields[4],
			fields[6]);
	}
	
	private Stream<DomainObject> calculateLastCoordinates(final List<DomainObject> domainValue) {
		for (int i = 0; i < domainValue.size(); i++) {
			DomainObject domain = domainValue.get(i);
			if (isFirstElement(i)) {
				domain.copyLastLatitudeFromLatitude();
				domain.copyLastLongitudeFromLongitude();
			} else {
				domain.setLastLatitude(domainValue.get(i - 1).getLatitude());
				domain.setLastLongitude(domainValue.get(i - 1).getLongitude());
			}
		}
		return domainValue.stream();
	}

	private boolean isFirstElement(int i) {
		return i == 0;
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
	
	private static class MainBuilder {
		private String[] args;
		private String filesPath;
		private boolean encryptEnabled = false;
		
		public MainBuilder(String[] args) {
			this.args = args;
		}
		
		public MainBuilder withFilesPath() {
			this.filesPath = args[0];
			return this;
		}
		
		public MainBuilder withEncryptEnabled() {
			if (args.length > 1) {
				this.encryptEnabled = Boolean.valueOf(args[1]);
			}
			return this;
		}
		
		public Main build() {
			return new Main(this);
		}

		public String getFilesPath() {
			return filesPath;
		}

		public boolean encryptEnabled() {
			return encryptEnabled;
		}
	}

}
