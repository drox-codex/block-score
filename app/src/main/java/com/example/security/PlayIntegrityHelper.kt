package com.example.security

import android.content.Context

/**
 * Interface and decoupled helper for optional future Google Play online services
 * (such as online leaderboards, cloud saves, or secure online events).
 *
 * CRITICAL ARCHITECTURE RULE:
 * This helper is completely optional and isolated.
 * Normal gameplay, launching the game, Classic mode, Adventure mode,
 * local achievements, and offline saves NEVER depend on this class or network connectivity.
 */
interface PlayIntegrityProvider {
    suspend fun requestIntegrityToken(nonce: String): Result<String>
}

object PlayIntegrityHelper {
    private var customProvider: PlayIntegrityProvider? = null

    /**
     * Allows attaching an external Play Integrity provider if online features are enabled in the future.
     */
    fun setProvider(provider: PlayIntegrityProvider?) {
        customProvider = provider
    }

    /**
     * Returns whether online Play services are currently configured.
     * Always returns false in pure offline mode.
     */
    fun isPlayServicesEnabled(): Boolean {
        return customProvider != null
    }

    /**
     * Obtains an integrity token if online services are attached; otherwise safely returns an offline indication.
     */
    suspend fun obtainTokenIfAvailable(nonce: String): Result<String> {
        val provider = customProvider
        return if (provider != null) {
            provider.requestIntegrityToken(nonce)
        } else {
            Result.failure(IllegalStateException("Game is operating in standalone offline mode."))
        }
    }
}
