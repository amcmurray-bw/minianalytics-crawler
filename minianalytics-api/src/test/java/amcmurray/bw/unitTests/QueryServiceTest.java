package amcmurray.bw.unitTests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import amcmurray.bw.QueryRequestDTO;
import amcmurray.bw.exceptions.QueryExceptions;
import amcmurray.bw.repositories.QueryRepository;
import amcmurray.bw.services.QueryService;
import amcmurray.bw.twitterdomainobjects.Query;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    private QueryService queryService;

    private final QueryRequestDTO testQueryDTO = new QueryRequestDTO("test", "en");
    private final Query testQuery = new Query(12345, "test", "en");


    @Test
    public void createQuery_createsExpectedQuery() {
        //create a query with the same text as DTO, and a random ID.
        Query expectedQuery = new Query(56789, testQueryDTO.getSearch(), "en");

        when(queryRepository.save(any(Query.class))).thenReturn(expectedQuery);

        assertEquals(expectedQuery, queryService.createQuery(testQueryDTO));
    }

    @Test
    public void findQueryById_equalsExpectedQuery() {
        when(queryRepository.findById(testQuery.getId())).thenReturn(testQuery);

        assertEquals(testQuery, queryService.findQueryById(testQuery.getId()));
    }

    @Test(expected = QueryExceptions.QueryNotFoundException.class)
    public void searchingForNonExistentQuery_throwsQueryNotFoundException() {

        //return null, eg query does not exist
        when(queryRepository.findById(13579)).thenReturn(null);
        queryService.findQueryById(13579);
    }

    @Test(expected = QueryExceptions.QuerySearchNullException.class)
    public void blankQueryDTOSearchField_throwsQuerySearchNullException() {

        QueryRequestDTO nullQuery = new QueryRequestDTO("", "en");
        queryService.createQuery(nullQuery);
    }

}
