package br.org.roger.files.process;

public class AnotherObject {

	private String value1;
	private int value2;
	private int value3;
	private int value4;
	private int value5;

	public AnotherObject(
			String stringCellValue, double numericCellValue, double numericCellValue2,
			double numericCellValue3, double numericCellValue4) {
		this.value1 = stringCellValue;
		this.value2 = Double.valueOf(numericCellValue).intValue();
		this.value3 = Double.valueOf(numericCellValue2).intValue();
		this.value4 = Double.valueOf(numericCellValue3).intValue();
		this.value5 = Double.valueOf(numericCellValue4).intValue();
	}

	public String getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}

	public int getValue3() {
		return value3;
	}

	public int getValue4() {
		return value4;
	}

	public int getValue5() {
		return value5;
	}

	
}
