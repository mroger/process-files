package br.org.roger.files.process.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;

public class DomainObjectTest {
	
	@Test
	public void shouldCalculateLastCoordinates_singleItem() {
		
		List<DomainObject> singleItemList = DomainObjectTestFixture.getSingleItemList();
		
		List<DomainObject> domainItems = DomainObject.calculateLastCoordinates(singleItemList);
		
		DomainObject domainObject = domainItems.get(0);
		assertThat(domainItems.size(), equalTo(1));
		
		assertThat(domainObject.getLastLatitude(), equalTo(domainObject.getLatitude()));
		assertThat(domainObject.getLastLongitude(), equalTo(domainObject.getLongitude()));
		
	}
	
	@Test
	public void shouldPreserveOriginalDomain_singleItem() throws CloneNotSupportedException {
		
		List<DomainObject> singleItemList = DomainObjectTestFixture.getSingleItemList();
		DomainObject originalDomain = (DomainObject) singleItemList.get(0).clone();
		
		DomainObject.calculateLastCoordinates(singleItemList);
		
		DomainObject domainObject = singleItemList.get(0);
		assertThat(domainObject, equalTo(originalDomain));
	}
	
	@Test
	public void shouldCalculateLastCoordinates_multipleItem() {
		
		List<DomainObject> multipleItemsList = DomainObjectTestFixture.getMultipleItemsList();
		
		List<DomainObject> domainItems = DomainObject.calculateLastCoordinates(multipleItemsList);
		
		DomainObject firstDomainObject = domainItems.get(0);
		assertThat(domainItems.size(), equalTo(multipleItemsList.size()));
		assertThat(firstDomainObject.getLastLatitude(), equalTo(firstDomainObject.getLatitude()));
		assertThat(firstDomainObject.getLastLongitude(), equalTo(firstDomainObject.getLongitude()));
		
	}

}
