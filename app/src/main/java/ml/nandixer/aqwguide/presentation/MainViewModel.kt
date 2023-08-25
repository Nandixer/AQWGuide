package ml.nandixer.aqwguide.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ml.nandixer.aqwguide.domain.Resource
import ml.nandixer.aqwguide.domain.model.AppVersion
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.usecase.GetClassesUseCase
import ml.nandixer.aqwguide.domain.usecase.GetVersionsUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getClassesUseCase: GetClassesUseCase,
    private val getVersionsUseCase: GetVersionsUseCase
): ViewModel() {

    private val _classes = mutableStateOf(listOf<CombatClass>())
    val classes: State<List<CombatClass>> = _classes

    private val _chosenClass = mutableStateOf("")
    val chosenClass: State<String> = _chosenClass

    fun chooseClass(classAbbr: String){
        if (_chosenClass.value == classAbbr){
            _chosenClass.value = ""
        } else {
            _chosenClass.value = classAbbr
        }
    }

    val classSearchText = mutableStateOf("")

    val sortTypes = listOf<String>(
        "name",
        "overall rating",
        "damage rating",
        "survival rating",
        "support rating",
        "farming rating",
        "PvP rating",
        "ultras rating",
        "group DPS",
        "group DPS+",
        "classhall DPS",
        "classhall DPS+",
        "revenant KPM",
        "revenant KPM+",
        "icestormunder KPM",
        "icestormunder KPM+"
    )

    val filteredClasses = derivedStateOf {
        classes.value.filter { combatClass ->
            val filterStr = classSearchText.value.lowercase()
            filterStr in combatClass.names.joinToString(" ").lowercase() ||
            filterStr in combatClass.abbr.lowercase() ||
            filterStr in combatClass.tags.joinToString(" ").lowercase()
        }.sortedByDescending {
            val rats = it.ratings.damage+it.ratings.pvp+it.ratings.farming+it.ratings.support+it.ratings.ultras+it.ratings.survival.uppercase()
            when (_sortType.value){
                // todo: don't assume enhancements [0]
                "name" -> 0// unsorted, keep default order
                "overall rating" -> ratingToNumber( if ("S" in rats) "S" else rats.toCharArray().apply { sort() }[0].toString())
                "damage rating" -> ratingToNumber(it.ratings.damage)
                "survival rating" -> ratingToNumber(it.ratings.survival)
                "support rating" -> ratingToNumber(it.ratings.support)
                "farming rating" -> ratingToNumber(it.ratings.farming)
                "PvP rating" -> ratingToNumber(it.ratings.pvp)
                "ultras rating" -> ratingToNumber(it.ratings.ultras)
                "classhall DPS" -> it.enhancements[0].dps.classhall
                "classhall DPS" -> it.enhancements[0].dps.classhall
                "classhall DPS+" -> it.enhancements[0].dps.classhallNsod
                "revenant KPM" -> it.enhancements[0].dps.revenant
                "revenant KPM+" -> it.enhancements[0].dps.revenantNsod
                "icestormunder KPM" -> it.enhancements[0].dps.icestorm
                "icestormunder KPM+" -> it.enhancements[0].dps.icestormNsod
                else -> it.enhancements.size// unsorted, keep default order
            }

        }

    }

    private fun ratingToNumber(rating:String): Int {
        return when(rating.lowercase()){
            "f" -> 0
            "e" -> 1
            "d" -> 2
            "c" -> 3
            "b" -> 4
            "a" -> 5
            "s" -> 6
            else -> -1
        }

    }

    private val _sortType = mutableStateOf("name")

    fun setSortType(sortType: String){
        _sortType.value = sortType
    }

    init {
        getClassesUseCase().onEach { result ->
            if (result is Resource.Success && result.data != null){
                _classes.value = result.data
            }
        }.launchIn(viewModelScope)

        getVersionsUseCase().onEach { result ->
            if (result is Resource.Success && result.data != null){
                _newestVersion.value = result.data[0]
            }
        }.launchIn(viewModelScope)
    }

    private val _newestVersion = mutableStateOf<AppVersion?>(null)
    val newestVersion = _newestVersion

    private val _compareClass = mutableStateOf<CombatClass?>(null)
    val compareClass = _compareClass

    fun chooseComparison(theClass: CombatClass?){
        _compareClass.value = theClass
    }
}