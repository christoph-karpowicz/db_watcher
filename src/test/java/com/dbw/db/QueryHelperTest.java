package com.dbw.db;

import com.dbw.db.query.QueryHelper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class QueryHelperTest {
    private final static Map<String, String> tableNamesToExpectedHashes = ImmutableMap.of(
            "film", "d0607f7ad2628b2a",
            "actor_test", "3fa25cdc7e61df5d",
            "address", "d80c9bf910f14473",
            "inventory_test", "131f710ff12eb9a3",
            "LARGE_CASE", "913f59bcbdeda1c0"
    );

    @Test
    public void shouldCreateTheRightShortHash() {
        tableNamesToExpectedHashes.forEach((key, value) -> assertEquals(Common.DBW_PREFIX + value, QueryHelper.buildAuditTriggerName(key)));
    }

}
