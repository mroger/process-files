package br.org.roger.files.process.parameter;

import org.apache.commons.cli.ParseException;

public class AppParameterException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AppParameterException(ParseException e) {
		super(e);
	}

}
