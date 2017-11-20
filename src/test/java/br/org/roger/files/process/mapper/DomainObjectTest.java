package br.org.roger.files.process.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;

public class DomainObjectTest {
	
	@Test
	public void shouldCalculateLastCoordinates_singleItem() {
		
		List<DomainObject> singleItemList = DomainObjectTestFixture.getSingleItem();
		
		List<DomainObject> domainItems = DomainObject.calculateLastCoordinates(singleItemList);
		
		DomainObject domainObject = domainItems.get(0);
		assertThat(domainItems.size(), equalTo(1));
		assertThat(domainObject.getLastLatitude(), equalTo(domainObject.getLatitude()));
		assertThat(domainObject.getLastLongitude(), equalTo(domainObject.getLongitude()));
		
	}
	
	

}
