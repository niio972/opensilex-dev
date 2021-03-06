package org.opensilex.sparql.mapping;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensilex.sparql.exceptions.SPARQLInvalidClassDefinitionException;
import org.opensilex.sparql.rdf4j.RDF4JConnection;
import org.opensilex.sparql.service.SPARQLService;
import test.opensilex.sparql.SPARQLServiceTest;

public class SPARQLClassAnalyzerTest {

    @BeforeClass
    public static void initialize() throws Exception {

        Repository repository = new SailRepository(new MemoryStore());
        repository.init();

        SPARQLService localService = new SPARQLService(new RDF4JConnection(repository.getConnection()));
        SPARQLServiceTest.initialize(localService);
    }

    @Test(expected = SPARQLInvalidClassDefinitionException.class)
    public void testNoGetterClass() throws SPARQLInvalidClassDefinitionException {
        NoGetterClass c = new NoGetterClass();
        new SPARQLClassAnalyzer(c.getClass());
    }

    @Test(expected = SPARQLInvalidClassDefinitionException.class)
    public void testNoSetterClass() throws SPARQLInvalidClassDefinitionException {
        NoSetterClass c = new NoSetterClass();
        new SPARQLClassAnalyzer(c.getClass());
    }

}