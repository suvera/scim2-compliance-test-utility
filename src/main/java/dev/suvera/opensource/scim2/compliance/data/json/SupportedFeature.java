package dev.suvera.opensource.scim2.compliance.data.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * author: suvera
 * date: 9/6/2020 12:41 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportedFeature {
    private boolean supported;
}
