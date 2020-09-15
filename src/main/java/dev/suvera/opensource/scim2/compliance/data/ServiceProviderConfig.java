package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.suvera.opensource.scim2.compliance.data.json.*;
import lombok.Data;

import java.util.List;

/**
 * author: suvera
 * date: 9/3/2020 2:16 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProviderConfig {

    private List<String> schemas;
    private ScimMeta meta;
    private String documentationUri;

    private SupportedFeature patch;
    private BulkFeature bulk;
    private FilterFeature filter;

    private SupportedFeature changePassword;
    private SupportedFeature sort;
    private SupportedFeature etag;

    private List<AuthenticationScheme> authenticationSchemes;

}
