package br.org.roger.files.process.mapper;

import java.io.File;
import java.util.stream.Stream;

public interface ModelMapper<T> {

	Stream<T> streamFromFile(final File inputFile);
	
}
