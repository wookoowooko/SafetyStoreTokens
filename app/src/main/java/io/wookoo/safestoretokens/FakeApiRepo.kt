package io.wookoo.safestoretokens

import java.util.UUID

class FakeApiRepo {
    fun returnJwt(): String {
        return UUID.randomUUID().toString()
    }
}