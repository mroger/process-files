package br.org.roger.files.process.mapper;

import java.io.File;
import java.util.stream.Stream;

public class ArrayLineMapper implements ModelMapper<String[]> {

	@Override
	public Stream<String[]> streamFromFile(File inputFile) {
		return null;
	}

}
