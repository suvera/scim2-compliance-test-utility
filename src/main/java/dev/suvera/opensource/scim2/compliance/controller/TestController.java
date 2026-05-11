package dev.suvera.opensource.scim2.compliance.controller;

import dev.suvera.opensource.scim2.compliance.data.TestCaseResult;
import dev.suvera.opensource.scim2.compliance.data.TestContext;
import dev.suvera.opensource.scim2.compliance.enums.AuthenticationType;
import dev.suvera.opensource.scim2.compliance.tests.TestExecutionService;
import dev.suvera.opensource.scim2.compliance.utils.Xmap;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * author: suvera
 * date: 9/3/2020 2:12 PM
 */
@RestController("test")
@RequestMapping(value = "/test/", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @Autowired
    private TestExecutionService executionService;

    @PostMapping("/run")
    public Object runTests(
            @RequestParam(required = true, name = "endPoint") @NotNull String endPoint,
            @RequestParam(required = false, name = "username") String username,
            @RequestParam(required = false, name = "password") String password,
            @RequestParam(required = false, name = "bearerToken") String bearerToken,
            @RequestParam(required = false, name = "jwtToken") String jwtToken,
            @RequestParam(required = false, name = "meCheck", defaultValue = "0") int meCheck,
            @RequestParam(required = false, name = "searchCheck", defaultValue = "0") int searchCheck,
            @RequestParam(required = false, name = "usersCheck", defaultValue = "0") int usersCheck,
            @RequestParam(required = false, name = "groupsCheck", defaultValue = "0") int groupsCheck,
            @RequestParam(required = false, name = "bulkCheck", defaultValue = "0") int bulkCheck,
            @RequestParam(required = false, name = "checkIndResLocation", defaultValue = "0") int checkIndResource,
            @RequestParam(required = false, name = "httpHeaders") String httpHeaders
    ) {
        Xmap map = Xmap.q();

        TestContext context = new TestContext();
        context.setEndPoint(endPoint);
        String token = bearerToken;
        if (token == null || token.isEmpty()) {
            token = jwtToken;
        }

        if (token != null && !token.isEmpty()) {
            context.setAuthType(AuthenticationType.BEARER);
            context.setBearerToken(token);
        } else if (username != null && !username.isEmpty()) {
            context.setAuthType(AuthenticationType.BASIC);
            context.setUserName(username);
            context.setPassword(password);
        } else {
            context.setAuthType(AuthenticationType.NONE);
        }

        context.setMeIncluded(meCheck == 1);
        context.setSearchIncluded(searchCheck == 1);
        context.setUsersIncluded(usersCheck == 1);
        context.setGroupsIncluded(groupsCheck == 1);
        context.setBulkIncluded(bulkCheck == 1);
        context.setCheckIndResource(checkIndResource == 1);
        context.setHttpHeaders(parseHeaders(httpHeaders));

        String runId = UUID.randomUUID().toString();
        executionService.executeTests(runId, context);

        map.k("id", runId);

        return map.get();
    }

    @GetMapping("/status")
    public Object runTests(
            @RequestParam(required = true, name = "runId") @NotNull String runId,
            @RequestParam(required = true, name = "lastIndex", defaultValue = "0") int lastIndex
    ) {
        Xmap map = Xmap.q();

        List<TestCaseResult> results = executionService.getDatabase().getTestResults(runId);

        if (results == null) {
            map.k("data", Collections.emptyList());
            map.k("nextIndex", lastIndex);
            return map.get();
        }

        int length = results.size();
        List<TestCaseResult> out = new ArrayList<>();
        for (int i = lastIndex; i < length; i++) {
            out.add(results.get(i));
            lastIndex++;
        }

        map.k("nextIndex", lastIndex);

        map.k("data", out);
        return map.get();
    }

    private Map<String, String> parseHeaders(String headers) {
        if (headers == null || headers.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        String[] lines = headers.split("\\r?\\n");
        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }
}
