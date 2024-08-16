package dev.suvera.opensource.scim2.compliance.biz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import dev.suvera.opensource.scim2.compliance.data.ScimConstants;
import io.scim2.swagger.client.ScimApiException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * author: suvera
 * date: 9/7/2020 4:31 PM
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ScimResponseValidator {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static <T> T processResponse(
            String response,
            String jsonSchemaFile,
            Class<T> dataClass
    ) throws Exception {
        return processResponse(response, JsonLoader.fromResource(jsonSchemaFile), dataClass);
    }

    public static <T> T processResponse(
            String response,
            JsonNode jsonSchema,
            Class<T> dataClass
    ) throws Exception {
        final JsonNode dataNode = JsonLoader.fromString(response);

        ProcessingReport report = validateResponse(response, jsonSchema);

        if (report.isSuccess()) {
            return jsonMapper.readValue(dataNode.toString(), dataClass);
        }

        throw new ScimApiException(report.toString() + "\n" + "Actual Response: " + response);
    }

    public static ProcessingReport validateResponse(
            String response,
            String jsonSchema,
            boolean isFile
    ) throws Exception {
        return validateResponse(
                response,
                isFile ? JsonLoader.fromResource(jsonSchema) : JsonLoader.fromString(jsonSchema)
        );
    }

    public static ProcessingReport validateResponse(
            String response,
            JsonNode jsonSchema
    ) throws Exception {
        final JsonNode dataNode = JsonLoader.fromString(response);

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(jsonSchema);

        return schema.validate(dataNode);
    }

    public static void processResponseHeaders(
            int statusCode,
            Map<String, List<String>> headers,
            List<Integer> expectedCode
    ) throws Exception {
        if (!expectedCode.contains(statusCode)) {
            throw new ScimApiException("HTTP Response code expected was "
                    + StringUtils.join(expectedCode, ", ")
                    + ", but received " + statusCode
            );
        }

        // No reason to check Content-Type on 204
        if (statusCode == 204) {
            return;
        }

        List<String> contentTypes = headers.get("Content-Type");
        boolean found = false;
        for (String expected : ScimConstants.CONTENT_TYPES) {
            for (String actual : contentTypes) {
                if (actual.contains(expected)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                break;
            }
        }

        if (!found) {
            throw new ScimApiException("HTTP Response Content-Type expected was ["
                    + StringUtils.join(ScimConstants.CONTENT_TYPES, " or ")
                    + "], but received " + StringUtils.join(contentTypes, ", ")
            );
        }
    }
}
