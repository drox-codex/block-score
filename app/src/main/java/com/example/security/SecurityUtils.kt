package com.example.security

import android.content.Context
import android.content.pm.ApplicationInfo
import java.security.MessageDigest

/**
 * Lightweight offline security and integrity utilities for BLOCK SCORE.
 * Designed to prevent casual save tampering without impacting game performance or requiring network.
 */
object SecurityUtils {
    // Obfuscated salt fragments combined at runtime
    private val SALT_PART_A = byteArrayOf(0x44, 0x52, 0x4F, 0x58) // "DROX"
    private val SALT_PART_B = byteArrayOf(0x53, 0x54, 0x55, 0x44, 0x49, 0x4F) // "STUDIO"
    private val SALT_PART_C = byteArrayOf(0x42, 0x4C, 0x4F, 0x43, 0x4B) // "BLOCK"

    private fun getInternalSalt(context: Context): String {
        val appSalt = String(SALT_PART_A) + String(SALT_PART_B) + String(SALT_PART_C)
        return "${context.packageName}_${appSalt}_v1"
    }

    /**
     * Computes a SHA-256 integrity signature for a specific key-value pair.
     */
    fun computeChecksum(key: String, value: String, context: Context): String {
        return try {
            val salt = getInternalSalt(context)
            val input = "$key:$value:$salt"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            // Graceful fallback in any edge case
            ""
        }
    }

    /**
     * Verifies that the stored checksum matches the current value.
     */
    fun verifyChecksum(key: String, value: String, expectedChecksum: String?, context: Context): Boolean {
        if (expectedChecksum.isNullOrEmpty()) {
            return false
        }
        val computed = computeChecksum(key, value, context)
        return computed.isNotEmpty() && computed == expectedChecksum
    }

    /**
     * Performs a lightweight offline package identity check.
     * Fails safely to guarantee that normal gameplay is never blocked.
     */
    fun isPackageValid(context: Context): Boolean {
        return try {
            val currentPackage = context.packageName
            // Matches our assigned application package or development package
            currentPackage.startsWith("com.aistudio.blockscore") || currentPackage == "com.example"
        } catch (_: Exception) {
            true // Fail safely to never crash or lock out users
        }
    }
}
