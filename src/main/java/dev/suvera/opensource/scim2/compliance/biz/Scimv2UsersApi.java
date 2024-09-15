package dev.suvera.opensource.scim2.compliance.biz;


import dev.suvera.opensource.scim2.compliance.data.ScimApiResponse;
import dev.suvera.opensource.scim2.compliance.enums.HttpMethod;
import okhttp3.Response;

public class Scimv2UsersApi extends Scimv2BaseApi {
    public Scimv2UsersApi(ScimApiClient client) {
        super(client);
    }

    public ScimApiResponse<String> updateUserWithHttpInfo(String reqBody, HttpMethod method) throws ScimApiException {
        return updateWithHttpInfo(reqBody, method);
    }

    public ScimApiResponse<String> createUserWithHttpInfo(String reqBody) throws ScimApiException {
        return createWithHttpInfo(reqBody);
    }

    public ScimApiResponse<String> deleteUserWithHttpInfo() throws ScimApiException {
        return deleteWithHttpInfo();
    }

    public ScimApiResponse<String> getUserWithHttpInfo(String filter, Integer startIndex, Integer count, String sortBy, String sortOrder) throws ScimApiException {
        return getSearchWithHttpInfo(filter, startIndex, count, sortBy, sortOrder);
    }

    public ScimApiResponse<String> getUserByIdWithHttpInfo() throws ScimApiException {
        return getRecordByIdWithHttpInfo();
    }
}
