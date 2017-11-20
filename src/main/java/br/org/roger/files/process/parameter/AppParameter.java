package br.org.roger.files.process.parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Strings;

public class AppParameter {
	
	private static final String OUTPUT_FILE = "process-file-output";
	
	private CommandLine commandLine;
	private Options options;

	public AppParameter(final String[] args) {
		try {
			commandLine = createCommandLine(args);
			validateParameters(commandLine);
		} catch (ParseException e) {
			throw new AppParameterException(e);
		}
	}

	private CommandLine createCommandLine(final String[] args) throws ParseException {
		options = createOptions();
		return new DefaultParser().parse(options, args);
	}

	private void validateParameters(final CommandLine commandLine) {
		if (Strings.isNullOrEmpty(commandLine.getOptionValue("i")) ||
			Strings.isNullOrEmpty(commandLine.getOptionValue("o"))) {
			printHelp();
			throw new AppParameterMissingException("Some of the required parameters is missing.");
		}
	}

	private Options createOptions() {
		Options options = new Options();
		options.addOption("i", "inputPath", true, "Root path of the files to process");
		options.addOption("o", "outputPath", true, "Path of the output file");
		options.addOption("e", "encryptEnabled", false, "Enables encrypt of the record");
		return options;
	}

	public String getInputPath() {
		return commandLine.getOptionValue("i");
	}

	public String getOutputPath() {
		return commandLine.getOptionValue("o");
	}
	
	public Path getOutputFilePath() {
		return Paths.get(this.getOutputPath() + File.separatorChar + OUTPUT_FILE);
	}

	public boolean encryptEnabled() {
		return commandLine.hasOption("e");
	}

	private void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("process-files -i <filesInputPath> -o <fileOutputPath> [-e]", options );
	}
}
