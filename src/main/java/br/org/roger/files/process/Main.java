package br.org.roger.files.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import br.org.roger.files.process.crypto.Crypto;
import br.org.roger.files.process.mapper.MapperFactory;
import br.org.roger.files.process.mapper.ModelMapper;
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
	private ModelMapper<String[]> modelMapper;
	private Crypto crypto;
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private Main(final String[] args) {
		this.appParameter = new AppParameter(args);
		//this.modelMapper = MapperFactory.createMapper(MapperFactory.FileType.EXCEL);
		this.modelMapper = MapperFactory.createMapper(MapperFactory.FileType.TEXT);
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
		long start = System.currentTimeMillis();
		
		Path inputFilesDir = Paths.get(this.appParameter.getInputPath());
		//try (Stream<Path> files = Files.walk(inputFilesDir)) {
		try (Stream<Path> files = Files.walk(inputFilesDir).collect(Collectors.toList()).parallelStream()) {
			//Stream<String> outputLines = files.parallel()
			Stream<String> outputLines = files
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.flatMap(modelMapper::streamFromFile)
				.map(this::convertToJson)
				.map(this::encryptJson)
				.filter(this::isNonEmptyData)
				.map(this::getNonEmptyData);
			
			Files.write(this.appParameter.getOutputFilePath(), (Iterable<String>) outputLines::iterator);
			
			long end = System.currentTimeMillis();
			
			LOGGER.info("Files successfuly processed in " + (end - start) / 1000.0 + " s");
			
			System.exit(0);
		} catch (Exception e) {
			LOGGER.error("Error processing files from path.", e);
			System.exit(1);
		}
	}

	private String convertToJson(final String[] mobileData) {
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
