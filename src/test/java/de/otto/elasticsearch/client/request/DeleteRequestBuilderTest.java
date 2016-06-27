package de.otto.elasticsearch.client.request;

import com.ning.http.client.AsyncHttpClient;
import de.otto.elasticsearch.client.CompletedFuture;
import de.otto.elasticsearch.client.MockResponse;
import de.otto.elasticsearch.client.response.HttpServerErrorException;
import de.otto.elasticsearch.client.util.HttpClientWrapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class DeleteRequestBuilderTest {

    private HttpClientWrapper httpClient;
    private DeleteRequestBuilder testee;

    @BeforeMethod
    private void setup() {
        httpClient = mock(HttpClientWrapper.class);
        testee = new DeleteRequestBuilder(httpClient);
    }

    @Test
    public void shouldDeleteDocument() {
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);

        when(httpClient.prepareDelete("/someIndexName/someType/someId")).thenReturn(boundRequestBuilderMock);
        when(boundRequestBuilderMock.execute()).thenReturn(new CompletedFuture(new MockResponse(200, "ok", "")));
        testee.setDocumentType("someType")
                .setIndexName("someIndexName")
                .setId("someId")
                .execute();
        verify(httpClient).prepareDelete("/someIndexName/someType/someId");
    }

    @Test
    public void shouldThrowExceptionIfIndexNameIsMissing() {
        try {
            testee.setDocumentType("someType")
                    .setId("someId")
                    .execute();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("missing property 'indexName'"));
        }
    }

    @Test
    public void shouldThrowExceptionIfTypeIsMissing() {
        try {
            testee.setIndexName("someIndexName")
                    .setId("someId")
                    .execute();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("missing property 'type'"));
        }
    }

    @Test
    public void shouldThrowExceptionIfIdIsMissing() {
        try {
            testee.setDocumentType("someType")
                    .setIndexName("someIndexName")
                    .execute();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("missing property 'id'"));
        }
    }

    @Test(expectedExceptions = HttpServerErrorException.class)
    public void shouldThrowExceptionIfStatusIsNot200() {
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);

        when(httpClient.prepareDelete("/someIndexName/someType/someId")).thenReturn(boundRequestBuilderMock);
        when(boundRequestBuilderMock.execute()).thenReturn(new CompletedFuture(new MockResponse(400, "not ok", "")));
        try {
            testee.setDocumentType("someType")
                    .setId("someId")
                    .setIndexName("someIndexName")
                    .execute();
        } catch (HttpServerErrorException e) {
            assertThat(e.getMessage(), is("400 not ok"));
            throw e;
        }
    }
}