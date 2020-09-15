package dev.suvera.opensource.scim2.compliance.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * author: suvera
 * date: 9/13/2020 9:27 AM
 */
@Data
@AllArgsConstructor
public class SchemaExtension {
    private Schema schema;
    private boolean required;
}
