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

	public static List<String[]> getFileItemsFixture() {
		ArrayList<String[]> domainObjects = new ArrayList<>();
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76b", "1.510766301005E12", "-73.856077", 	"40.848447", "Sao Paulo"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76b", "1.510766321318E12", "-73.961704", 	"40.662942", "Osasco"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76b", "1.510766333986E12", "-73.9851355999999", 	"40.7676919", "Barueri"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76b", "1.510766341886E12", "-73.9824199999999", 	"40.579505", "Campinas"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76f", "1.510766349451E12", "-73.8601152", 	"40.7311739", "Sao Jose dos Campos"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76f", "1.510766373139E12", "-73.8803827", 	"40.7643124", "Itu"});
		domainObjects.add(new String[] {"59f52d9d67b9a1d1c3ceb76f", "1.510766379782E12", "-73.9068506", 	"40.6199034", "Itapeva"});
		return domainObjects;
	}

}
