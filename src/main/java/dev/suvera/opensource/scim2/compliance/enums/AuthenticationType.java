package dev.suvera.opensource.scim2.compliance.enums;

import lombok.Getter;

/**
 * author: suvera
 * date: 9/3/2020 3:07 PM
 */
@Getter
public enum AuthenticationType {
    BASIC(1), BEARER(2);

    private final int id;

    AuthenticationType(int i) {
        id = i;
    }
}
