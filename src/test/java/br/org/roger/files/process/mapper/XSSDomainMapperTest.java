package br.org.roger.files.process.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class XSSDomainMapperTest {
	
	private Path path;
	
	@Before
	public void setUp() throws URISyntaxException {
		this.path = Paths.get(getClass().getClassLoader().getResource("file/SpreadSheet1.xlsx").toURI());
	}
	
	@Test
	public void shouldCreateStreamOfDomainFromFile() {
		
		List<DomainObject> fileItemsFixture = DomainObjectTestFixture.getFileItemsFixture();
		XSSDomainMapper mapper = new XSSDomainMapper();

		Stream<DomainObject> stream = mapper.streamOfDomainFromFile(this.path.toFile());
		
		List<DomainObject> domainObjects = stream.collect(Collectors.toList());
		MatcherAssert.assertThat(domainObjects.size(), Matchers.equalTo(7));
		
		for (int i = 0; i < fileItemsFixture.size(); i++) {
			assertThat(fileItemsFixture.get(i), equalTo(domainObjects.get(i)));
		}
	}

}
