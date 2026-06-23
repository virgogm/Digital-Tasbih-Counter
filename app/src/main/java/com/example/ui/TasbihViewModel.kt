package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TasbihCounter
import com.example.data.TasbihRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    A_Z, RECENT_ACTIVITY, HIGHEST_COUNT
}

class TasbihViewModel(private val repository: TasbihRepository) : ViewModel() {

    // Main screen state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.RECENT_ACTIVITY)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    // Filtered and sorted list for Main Screen
    val counters: StateFlow<List<TasbihCounter>> = combine(
        repository.allCounters,
        _searchQuery,
        _sortBy
    ) { counterList, query, sortOpt ->
        val filtered = if (query.isBlank()) {
            counterList
        } else {
            counterList.filter { it.title.contains(query, ignoreCase = true) }
        }

        when (sortOpt) {
            SortOption.A_Z -> filtered.sortedBy { it.title.lowercase() }
            SortOption.RECENT_ACTIVITY -> filtered.sortedByDescending { it.lastUpdatedAt }
            SortOption.HIGHEST_COUNT -> filtered.sortedByDescending { it.totalAccumulated }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Isolated active counter states for smooth 60fps counting without global list re-renders
    private val _activeCounter = MutableStateFlow<TasbihCounter?>(null)
    val activeCounter: StateFlow<TasbihCounter?> = _activeCounter.asStateFlow()

    private val _activeCurrentCount = MutableStateFlow(0)
    val activeCurrentCount: StateFlow<Int> = _activeCurrentCount.asStateFlow()

    private val _activeLapCount = MutableStateFlow(0)
    val activeLapCount: StateFlow<Int> = _activeLapCount.asStateFlow()

    private val _activeTotalAccumulated = MutableStateFlow(0)
    val activeTotalAccumulated: StateFlow<Int> = _activeTotalAccumulated.asStateFlow()

    private var dbSaveJob: Job? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortBy(option: SortOption) {
        _sortBy.value = option
    }

    // Active screen navigation triggers
    fun selectActiveCounter(counter: TasbihCounter) {
        // Cancel existing saves if any
        dbSaveJob?.cancel()
        
        _activeCounter.value = counter
        _activeCurrentCount.value = counter.currentCount
        _activeLapCount.value = counter.lapCount
        _activeTotalAccumulated.value = counter.totalAccumulated
    }

    fun incrementActiveCounter() {
        val currentActive = _activeCounter.value ?: return

        val nextCount = _activeCurrentCount.value + 1
        val nextTotal = _activeTotalAccumulated.value + 1
        var nextLap = _activeLapCount.value

        val limit = currentActive.lapLimit
        val finalCount = if (limit > 0 && nextCount >= limit) {
            nextLap += 1
            0 // automatically resets primary loop back to zero upon crossover
        } else {
            nextCount
        }

        _activeCurrentCount.value = finalCount
        _activeLapCount.value = nextLap
        _activeTotalAccumulated.value = nextTotal

        // High-frequency debounce save (500ms delay) to prevent SQLite lockups
        dbSaveJob?.cancel()
        dbSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            val updated = currentActive.copy(
                currentCount = finalCount,
                lapCount = nextLap,
                totalAccumulated = nextTotal,
                lastUpdatedAt = System.currentTimeMillis()
            )
            repository.updateCounter(updated)
            _activeCounter.value = updated
        }
    }

    fun decrementActiveCounter() {
        val currentActive = _activeCounter.value ?: return
        if (_activeCurrentCount.value <= 0) return

        val finalCount = _activeCurrentCount.value - 1
        val finalTotal = if (_activeTotalAccumulated.value > 0) _activeTotalAccumulated.value - 1 else 0

        _activeCurrentCount.value = finalCount
        _activeTotalAccumulated.value = finalTotal

        dbSaveJob?.cancel()
        dbSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            val updated = currentActive.copy(
                currentCount = finalCount,
                totalAccumulated = finalTotal,
                lastUpdatedAt = System.currentTimeMillis()
            )
            repository.updateCounter(updated)
            _activeCounter.value = updated
        }
    }

    fun saveActiveCounterImmediately() {
        val currentActive = _activeCounter.value ?: return
        dbSaveJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            val updated = currentActive.copy(
                currentCount = _activeCurrentCount.value,
                lapCount = _activeLapCount.value,
                totalAccumulated = _activeTotalAccumulated.value,
                lastUpdatedAt = System.currentTimeMillis()
            )
            repository.updateCounter(updated)
            _activeCounter.value = null
        }
    }

    fun quickResetCounter(counter: TasbihCounter) {
        viewModelScope.launch(Dispatchers.IO) {
            val resetCounter = counter.copy(
                currentCount = 0,
                lapCount = 0,
                lastUpdatedAt = System.currentTimeMillis()
            )
            repository.updateCounter(resetCounter)
        }
    }

    fun deleteCounter(counter: TasbihCounter) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCounter(counter)
        }
    }

    fun createCounter(
        title: String,
        initialCount: Int,
        targetLimit: Int,
        lapLimit: Int,
        themeColorHex: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newCounter = TasbihCounter(
                title = title.trim().ifEmpty { "Counter" },
                currentCount = initialCount,
                lapCount = 0,
                targetLimit = targetLimit,
                lapLimit = lapLimit,
                themeColorHex = themeColorHex,
                totalAccumulated = initialCount
            )
            repository.insertCounter(newCounter)
        }
    }

    fun updateCounterMetadata(
        id: Int,
        title: String,
        targetLimit: Int,
        lapLimit: Int,
        themeColorHex: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentActive = _activeCounter.value ?: return@launch
            if (currentActive.id == id) {
                val updated = currentActive.copy(
                    title = title.trim().ifEmpty { currentActive.title },
                    targetLimit = targetLimit,
                    lapLimit = lapLimit,
                    themeColorHex = themeColorHex,
                    lastUpdatedAt = System.currentTimeMillis()
                )
                repository.updateCounter(updated)
                _activeCounter.value = updated
            }
        }
    }
}

class TasbihViewModelFactory(private val repository: TasbihRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasbihViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasbihViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
