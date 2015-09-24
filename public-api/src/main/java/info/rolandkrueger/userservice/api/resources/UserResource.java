package info.rolandkrueger.userservice.api.resources;

import info.rolandkrueger.userservice.api._internal.AbstractResource;
import info.rolandkrueger.userservice.api._internal.RestApiConstants;
import info.rolandkrueger.userservice.api.enums.UserProjections;
import info.rolandkrueger.userservice.api.model.UserApiData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.web.client.RestClientException;

/**
 * @author Roland Krüger
 */
public class UserResource extends AbstractResource<UserApiData> implements CanDeleteResource  {

    public UserResource(Link self, UserApiData data) {
        super(self, data);
    }

    @Override
    protected ParameterizedTypeReference<UserApiData> getParameterizedTypeReference() {
        return new ParameterizedTypeReference<UserApiData>() {
        };
    }

    public final UserResource useProjection(UserProjections projection) {
        return new UserResource(getProjectionLink(templatedBaseLink, projection.getName()), getApiData());
    }

    @Override
    protected Class<UserApiData> getResourceType() {
        return UserApiData.class;
    }

    @Override
    public void delete() throws RestClientException {
        super.deleteInternal(getApiData());
    }

    public UpdateUserResource getUpdateResource() {
        return new UpdateUserResource(getLinkFor(getResponseEntity(), RestApiConstants.UPDATE), getApiData());
    }
}
