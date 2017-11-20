package br.org.roger.files.process.mapper;

import java.util.ArrayList;
import java.util.List;

public class DomainObjectTestFixture {

	public static List<DomainObject> getSingleItem() {
		ArrayList<DomainObject> domainObjects = new ArrayList<>();
		DomainObject domainObject = new DomainObject("abc", "123456", "-40.000000", "-17.000000", "Sao Paulo");
		domainObjects.add(domainObject);
		return domainObjects;
	}

}
