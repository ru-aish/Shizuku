package moe.shizuku.manager.service

/**
 * Throttles recovery submissions so periodic checks do not continuously replace
 * an already queued WorkManager ADB start attempt.
 */
internal class WatchdogRecoveryThrottle(
    private val intervalMs: Long = 5 * 60_000L
) {
    private var lastAttemptMs: Long? = null

    fun shouldRecover(serverAlive: Boolean, starting: Boolean, nowMs: Long): Boolean {
        if (serverAlive) {
            lastAttemptMs = null
            return false
        }
        if (starting) return false

        val previous = lastAttemptMs
        if (previous != null && nowMs - previous in 0 until intervalMs) return false
        lastAttemptMs = nowMs
        return true
    }
}
