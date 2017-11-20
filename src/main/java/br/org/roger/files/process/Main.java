package br.org.roger.files.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import br.org.roger.files.process.crypto.Crypto;
import br.org.roger.files.process.mapper.DomainObject;
import br.org.roger.files.process.mapper.XSSDomainMapper;
import br.org.roger.files.process.parameter.AppParameter;
import br.org.roger.files.process.parameter.AppParameterException;
import br.org.roger.files.process.parameter.AppParameterMissingException;

/**
 * 
 * @author marcos
 *
 */
public class Main {
	
	private AppParameter appParameter;
	private XSSDomainMapper xssDomainMapper;
	private Crypto crypto;
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private Main(final String[] args) {
		this.appParameter = new AppParameter(args);
		this.xssDomainMapper = new XSSDomainMapper();
		this.crypto = new Crypto(appParameter.encryptEnabled());
	}

	public static void main(String[] args) throws IOException {
		try {
			Main main = new Main(args);
			main.processFiles();
		} catch (AppParameterException ap) {
			LOGGER.error("An error ocurred parsing command line parameters", ap);
			System.exit(1);
		} catch (AppParameterMissingException apm) {
			LOGGER.error(apm.getMessage());
			System.exit(1);
		}
	}
	
	private void processFiles() {
		Path inputFilesDir = Paths.get(this.appParameter.getInputPath());
		try (Stream<Path> files = Files.list(inputFilesDir)) {
			Stream<String> outputLines = files
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.flatMap(xssDomainMapper::streamOfDomainFromFile)
				.collect(Collectors.groupingBy(DomainObject::getHash, Collectors.toList()))
					.values()
					.stream()
				.flatMap(this::calculateLastCoordinates)
				.map(this::convertToJson)
				.map(this::encryptJson)
				.filter(this::isNonEmptyData)
				.map(this::getNonEmptyData);
			
			Files.write(this.appParameter.getOutputFilePath(), (Iterable<String>) outputLines::iterator);
			
			LOGGER.info("Files successfuly processed.");
			
			System.exit(0);
		} catch (Exception e) {
			LOGGER.error("Error processing files from path.", e);
			System.exit(1);
		}
	}

	private Stream<DomainObject> calculateLastCoordinates(final List<DomainObject> domainValues) {
		return DomainObject.calculateLastCoordinates(domainValues).stream();
	}

	private String convertToJson(final DomainObject mobileData) {
		return new Gson().toJson(mobileData);
	}
	
	private Optional<String> encryptJson(final String jsonToSend) {
		if (this.appParameter.encryptEnabled()) {
			return crypto.encrypt(jsonToSend);
		}
		return Optional.of(jsonToSend);
	}
	
	private boolean isNonEmptyData(Optional<String> checkingData) {
		return checkingData.isPresent();
	}
	
	private String getNonEmptyData(Optional<String> nonEmptyData) {
		return nonEmptyData.get();
	}

}
