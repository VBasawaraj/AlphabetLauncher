package com.example.alphabetlauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphabetlauncher.data.AppRepository
import com.example.alphabetlauncher.data.FavoritesManager
import com.example.alphabetlauncher.model.AppInfo
import com.example.alphabetlauncher.utils.DOT_CHAR
import com.example.alphabetlauncher.utils.STAR_CHAR
import com.example.alphabetlauncher.utils.getAppsForLetter
import com.example.alphabetlauncher.utils.getAvailableLetters
import com.example.alphabetlauncher.utils.groupAppsByLetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val favoritesManager = FavoritesManager(application)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _selectedLetter = MutableStateFlow<Char?>(null)
    val selectedLetter: StateFlow<Char?> = _selectedLetter.asStateFlow()

    private val _favoritePackageNames =
        MutableStateFlow<Set<String>>(favoritesManager.getFavorites())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private var appsByLetter: Map<Char, List<AppInfo>> = emptyMap()

    private val _availableLetters = MutableStateFlow<Set<Char>>(emptySet())
    val availableLetters: StateFlow<Set<Char>> = _availableLetters.asStateFlow()

    // Favourites list: either user-persisted favorites, or defaults to top 6-7 apps
    val favouriteApps: StateFlow<List<AppInfo>> = combine(_apps, _favoritePackageNames) { allApps, favs ->
        if (favs.isNotEmpty()) {
            val favMap = allApps.associateBy { it.packageName }
            favs.mapNotNull { favMap[it] }
        } else {
            allApps.take(7)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Search results filtered in real time
    val searchResults: StateFlow<List<AppInfo>> = combine(_apps, _searchQuery) { allApps, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val trimmed = query.trim().lowercase()
            allApps.filter { it.name.lowercase().contains(trimmed) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadApps()
    }

    fun reloadApps() {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedApps = repository.loadLaunchableApps()
            appsByLetter = groupAppsByLetter(loadedApps)
            _availableLetters.value = getAvailableLetters(loadedApps)
            _apps.value = loadedApps
            _favoritePackageNames.value = favoritesManager.getFavorites()
        }
    }

    fun selectLetter(letter: Char) {
        _selectedLetter.value = letter
    }

    fun clearSelection() {
        _selectedLetter.value = null
    }

    fun getAppsForSelectedLetter(): List<AppInfo> {
        val letter = _selectedLetter.value ?: return emptyList()
        return when (letter) {
            STAR_CHAR -> favouriteApps.value
            DOT_CHAR -> {
                // Apps starting with numbers, symbols or non-alpha
                appsByLetter[DOT_CHAR] ?: emptyList()
            }
            else -> getAppsForLetter(appsByLetter, letter)
        }
    }

    fun toggleFavorite(packageName: String): Boolean {
        val isNowFav = favoritesManager.toggleFavorite(packageName)
        _favoritePackageNames.value = favoritesManager.getFavorites()
        return isNowFav
    }

    fun isFavorite(packageName: String): Boolean {
        return favoritesManager.isFavorite(packageName)
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}