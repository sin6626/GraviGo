package com.antigravity.mobile.data.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.*
import org.junit.Test

class AccountProfileTest {
    @Test fun readsDesktopFieldsAndAllowsMissingOptionalDetails() {
        val profile = parseAccountProfile(Json.parseToJsonElement("""{"userStatus":{
            "name":" 测试用户 ","email":" test@example.com ","profilePictureUrl":"data:image/jpeg;base64,abc",
            "planStatus":{"planInfo":{"planName":"Pro"}},"unknown":"ignored"}}""").jsonObject)
        assertEquals(AccountProfile("测试用户", "test@example.com", "data:image/jpeg;base64,abc", "Pro"), profile)
        assertEquals(AccountProfile("", "test@example.com", "", ""),
            parseAccountProfile(Json.parseToJsonElement("""{"userStatus":{"email":"test@example.com"}}""").jsonObject))
        for (missing in listOf("{}", """{"userStatus":{}}""")) {
            assertTrue(runCatching { parseAccountProfile(Json.parseToJsonElement(missing).jsonObject) }.isFailure)
        }
    }
}
