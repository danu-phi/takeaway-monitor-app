package com.phsmk.id.takeaway_monitor.util

object StringUtil {
    fun checkLastVersion(current: String, remote: String): Int {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val remoteParts = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val length = maxOf(currentParts.size, remoteParts.size)
        for (i in 0 until length) {
            val curr = currentParts.getOrElse(i) { 0 }
            val rem = remoteParts.getOrElse(i) { 0 }
            if (curr < rem) return -1
            if (curr > rem) return 1
        }
        return 0
    }
}
