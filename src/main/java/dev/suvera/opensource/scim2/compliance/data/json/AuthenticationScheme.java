package dev.suvera.opensource.scim2.compliance.data.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * author: suvera
 * date: 9/6/2020 12:45 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationScheme {
    private String type;
    private String name;
    private String description;
    private String specUri;
    private String documentationUri;
}
