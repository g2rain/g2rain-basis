package com.g2rain.basis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 租户成员同步统计结果。
 */
@Setter
@Getter
public class TenantIdpMemberSyncResult {

    private int membersCreated;

    private int membersUpdated;

    private int bindingsCreated;

    private int bindingsUpdated;

    private int membersDeleted;

    private int bindingsDeleted;

    private final Map<String, Long> unionIdToUserId = new LinkedHashMap<>();
}
