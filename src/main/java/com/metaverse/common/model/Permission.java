package com.metaverse.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Permission {
    private String resource;
    private String action;
    private String locator;

    public Permission(String permissionStr) {
        String[] permission = permissionStr.split("\\.");
        resource = permission[0];
        action = permission[1];
        locator = permission[2];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return ("*".equals(that.resource) || Objects.equals(resource, that.resource))
                && ("*".equals(that.action) || Objects.equals(action, that.action))
                && ("*".equals(that.locator) || Objects.equals(locator, that.locator));
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
