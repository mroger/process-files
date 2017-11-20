package br.org.roger.files.process.parameter;

public class AppParameterMissingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AppParameterMissingException(String message) {
		super(message);
	}
}
