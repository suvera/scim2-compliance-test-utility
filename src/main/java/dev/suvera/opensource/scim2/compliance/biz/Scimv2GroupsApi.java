package dev.suvera.opensource.scim2.compliance.biz;

import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;

public class Scimv2GroupsApi extends Scimv2BaseApi {
    public Scimv2GroupsApi(ScimApiClient client) {
        super(client);
    }

    public ScimApiResponse<String> updateGroupWithHttpInfo(String reqBody, HttpMethod method) throws ScimApiException {
        return updateWithHttpInfo(reqBody, method);
    }

    public ScimApiResponse<String> createGroupWithHttpInfo(String reqBody) throws ScimApiException {
        return createWithHttpInfo(reqBody);
    }

    public ScimApiResponse<String> deleteGroupWithHttpInfo() throws ScimApiException {
        return deleteWithHttpInfo();
    }

    public ScimApiResponse<String> getGroupWithHttpInfo(String filter, int startIndex, int count, String sortBy, String sortOrder) throws ScimApiException {
        return getSearchWithHttpInfo(filter, startIndex, count, sortBy, sortOrder);
    }

    public ScimApiResponse<String> getGroupByIdWithHttpInfo() throws ScimApiException {
        return getRecordByIdWithHttpInfo();
    }
}
