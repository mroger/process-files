package br.org.roger.files.process.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

public class TextMapper implements ModelMapper<String[]> {
	
	private static final String TAB_CHAR = "\t";
	private static final Logger LOGGER = Logger.getLogger(XSSDomainMapper.class.getName());

	@Override
	public Stream<String[]> streamFromFile(final File inputFile) {
		try (Stream<String> stream = Files.lines(Paths.get(inputFile.getAbsolutePath()))) {
			return stream
				.map(this::mapToArray)
				.collect(Collectors.toList())
				.stream();
		} catch (IOException e) {
			LOGGER.error("Error processing file " + inputFile.getAbsolutePath(), e);
			return Stream.empty();
		}
	}

	private String[] mapToArray(final String line) {
		return line.split(TAB_CHAR);
	}

}
