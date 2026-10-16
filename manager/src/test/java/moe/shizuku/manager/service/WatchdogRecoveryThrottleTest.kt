package moe.shizuku.manager.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WatchdogRecoveryThrottleTest {
    @Test fun recoversWhenServerMissingAtStartup() {
        val throttle = WatchdogRecoveryThrottle()
        assertTrue(throttle.shouldRecover(serverAlive = false, starting = false, nowMs = 0))
    }

    @Test fun doesNotReplacePendingRecoveryAtEveryHealthCheck() {
        val throttle = WatchdogRecoveryThrottle()
        assertTrue(throttle.shouldRecover(false, false, 1_000))
        assertFalse(throttle.shouldRecover(false, false, 60_000))
        assertTrue(throttle.shouldRecover(false, false, 301_000))
    }

    @Test fun waitsForExistingStartupAndResetsOnceHealthy() {
        val throttle = WatchdogRecoveryThrottle()
        assertFalse(throttle.shouldRecover(false, true, 0))
        assertTrue(throttle.shouldRecover(false, false, 0))
        assertFalse(throttle.shouldRecover(false, false, 60_000))
        assertFalse(throttle.shouldRecover(true, false, 61_000))
        assertTrue(throttle.shouldRecover(false, false, 62_000))
    }

    @Test fun monotonicClockResetAllowsRecovery() {
        val throttle = WatchdogRecoveryThrottle()
        assertTrue(throttle.shouldRecover(false, false, 900_000))
        assertTrue(throttle.shouldRecover(false, false, 10))
    }
}
