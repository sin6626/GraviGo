package io.github.sin6626.gravigo.data.model

import org.junit.Assert.*
import org.junit.Test

class PairingInfoTest {
    @Test fun cloudPairingRetainsItsCustomLocalPort() {
        val info = checkNotNull(PairingInfo.parseFromUri("agy://pair?host=example.com&port=443&ssl=1&code=test&lan=192.168.1.5&lan_port=58913"))
        assertEquals("https://example.com", info.serverBaseUrl)
        assertEquals("http://192.168.1.5:58913", info.lanBaseUrl)
    }

    @Test fun oldPairingLinksKeepTheirDefaultLocalPort() {
        val info = checkNotNull(PairingInfo.parseFromUri("agy://pair?host=example.com&port=443&ssl=1&code=test&lan=192.168.1.5"))
        assertEquals("http://192.168.1.5:58900", info.lanBaseUrl)
    }

    @Test fun invalidLocalPortsAreRejected() {
        for (port in listOf("0", "65536", "bad")) {
            assertNull(PairingInfo.parseFromUri("agy://pair?host=example.com&port=443&code=test&lan_port=$port"))
        }
    }
}
