package br.org.roger.files.process.mapper;

public class MapperFactory {
	
	public enum FileType { EXCEL, TEXT };
	
	public static ModelMapper<String[]> createMapper(FileType fileType) {
		
		switch(fileType) {
			case EXCEL:
				return new XSSDomainMapper();
			case TEXT:
				return new TextMapper();
			default:
				return new TextMapper();
		}
		
	}

}
