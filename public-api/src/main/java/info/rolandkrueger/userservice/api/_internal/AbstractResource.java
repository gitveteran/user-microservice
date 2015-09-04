package info.rolandkrueger.userservice.api._internal;

import com.google.common.base.Preconditions;
import info.rolandkrueger.userservice.api._internal.model.BaseApiData;
import info.rolandkrueger.userservice.api.exceptions.UnexpectedAPIFormatException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * @author Roland Krüger
 */
public abstract class AbstractResource<T extends BaseApiData> extends AbstractRestClient {

    protected Link self;
    private final static HttpHeaders HEADERS;
    private ResponseEntity<T> responseEntity;

    static {
        HEADERS = new HttpHeaders();
        HEADERS.setAccept(MediaType.parseMediaTypes("application/x-spring-data-verbose+json"));
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    public AbstractResource(Link self) {
        Preconditions.checkNotNull(self);
        this.self = self;
    }

    protected abstract ParameterizedTypeReference<T> getParameterizedTypeReference();

    protected abstract Class<T> getResourceType();

    protected final ResponseEntity<T> createInternal(T entity) throws RestClientException {
        return restTemplate.exchange(
                self.expand().getHref(),
                HttpMethod.POST,
                new HttpEntity<>(entity, HEADERS),
                getParameterizedTypeReference());
    }

    protected final void updateInternal(T entity) throws RestClientException {
        restTemplate.put(entity.getSelf().expand().getHref(), entity);
    }

    protected ResponseEntity<T> getResponseEntity() {
        loadIfNecessary();
        return responseEntity;
    }

    protected final void deleteInternal(T entity) throws RestClientException {
        restTemplate.delete(entity.getSelf().expand().getHref());
    }

    private void loadIfNecessary() {
        if (responseEntity == null) {
            responseEntity = restTemplate.exchange(
                    self.expand().getHref(),
                    HttpMethod.GET,
                    entityForHALData,
                    getParameterizedTypeReference());
        }
    }

    protected Link getLinkFor(ResponseEntity<? extends BaseApiData> responseEntity, String rel) {
        Optional<Link> linkOptional = responseEntity.getBody().getLinks()
                .stream()
                .filter(link -> link.getRel().equals(rel))
                .findAny();

        return linkOptional.orElseThrow(() -> new UnexpectedAPIFormatException("Link for resource" +
                " \"{0}\" not found at {1}", rel, self));
    }
}
