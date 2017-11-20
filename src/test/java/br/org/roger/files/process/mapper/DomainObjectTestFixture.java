package br.org.roger.files.process.mapper;

import java.util.ArrayList;
import java.util.List;

public class DomainObjectTestFixture {

	public static List<DomainObject> getSingleItemList() {
		ArrayList<DomainObject> domainObjects = new ArrayList<>();
		DomainObject domainObject = new DomainObject("abc", "123456", "-40.000000", "-17.000000", "Sao Paulo");
		domainObjects.add(domainObject);
		return domainObjects;
	}

	public static List<DomainObject> getMultipleItemsList() {
		ArrayList<DomainObject> domainObjects = new ArrayList<>();
		domainObjects.add(new DomainObject("abc", "123456", "-40.000000", "-17.000000", "Sao Paulo"));
		domainObjects.add(new DomainObject("abc", "123456", "-35.122545", "-17.465486", "Sao Paulo"));
		domainObjects.add(new DomainObject("abc", "123456", "11.000000", "-1.135494", "Sao Paulo"));
		domainObjects.add(new DomainObject("abc", "123456", "10.132465", "-5.465217", "Sao Paulo"));
		domainObjects.add(new DomainObject("abc", "123456", "-14.321546", "-3.354875", "Sao Paulo"));
		return domainObjects;
	}

}
