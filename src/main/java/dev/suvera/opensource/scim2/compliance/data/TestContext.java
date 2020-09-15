package dev.suvera.opensource.scim2.compliance.data;

import dev.suvera.opensource.scim2.compliance.enums.AuthenticationType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * author: suvera
 * date: 9/3/2020 2:24 PM
 */
@Data
public class TestContext {
    private String endPoint;

    private AuthenticationType authType;

    private String userName;
    private String password;

    private String bearerToken;

    private boolean meIncluded;
    private boolean searchIncluded;
    private boolean bulkIncluded;
    private boolean usersIncluded;
    private boolean groupsIncluded;

    @Override
    public String toString() {
        return "TestContext{" +
                "\n\tendPoint=" + endPoint +
                "\n\t, authType=" + authType +
                "\n\t, userName=" + (userName != null ? StringUtils.substring(userName, 0, 1) + "******" : "") +
                "\n\t, password=" + (password != null ? "******" : "") +
                "\n\t, bearerToken=" + (bearerToken != null ? StringUtils.substring(bearerToken, 0, 1)
                           + "******" : "") +
                "\n\t, meIncluded=" + meIncluded +
                "\n\t, searchIncluded=" + searchIncluded +
                "\n\t, bulkIncluded=" + bulkIncluded +
                "\n\t, usersIncluded=" + usersIncluded +
                "\n\t, groupsIncluded=" + groupsIncluded +
                "\n}";
    }
}
