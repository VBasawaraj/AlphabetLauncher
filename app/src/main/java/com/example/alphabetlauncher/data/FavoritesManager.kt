package com.example.alphabetlauncher.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persisting and retrieving favorite apps using SharedPreferences.
 */
class FavoritesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun hasCustomFavorites(): Boolean {
        return prefs.contains(KEY_FAVORITES)
    }

    fun addFavorite(packageName: String) {
        val current = getFavorites().toMutableSet()
        current.add(packageName)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun removeFavorite(packageName: String) {
        val current = getFavorites().toMutableSet()
        current.remove(packageName)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun toggleFavorite(packageName: String): Boolean {
        val current = getFavorites().toMutableSet()
        val willBeFavorite = if (current.contains(packageName)) {
            current.remove(packageName)
            false
        } else {
            current.add(packageName)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return willBeFavorite
    }

    fun isFavorite(packageName: String): Boolean {
        return getFavorites().contains(packageName)
    }

    companion object {
        private const val PREFS_NAME = "alphabet_launcher_favorites"
        private const val KEY_FAVORITES = "favorite_packages"
    }
}
