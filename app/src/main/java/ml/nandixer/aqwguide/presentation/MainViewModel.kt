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

            var filterStr = classSearchText.value.lowercase()

            val negative:Boolean = filterStr.startsWith('-')

            if (negative){
                filterStr = filterStr.substring(1)
            }

            val matched = filterStr in combatClass.names.joinToString(" ").lowercase() ||
            filterStr in combatClass.abbr.lowercase() ||
            filterStr in combatClass.tags.joinToString(" ").lowercase()

            matched != negative
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
                "classhall DPS" -> it.maxPerformance().classhall
                "classhall DPS+" -> it.maxPerformance().classhallNsod
                "revenant KPM" -> it.maxPerformance().revenant
                "revenant KPM+" -> it.maxPerformance().revenantNsod
                "icestormunder KPM" -> it.maxPerformance().icestorm
                "icestormunder KPM+" -> it.maxPerformance().icestormNsod
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

    val sortValueLength = derivedStateOf {
        when(_sortType.value){
            "name" -> 1
            "overall rating" -> 1
            "damage rating" -> 1
            "survival rating" -> 1
            "support rating" -> 1
            "farming rating" -> 1
            "PvP rating" -> 1
            "ultras rating" -> 1
            "classhall DPS" -> 3
            "classhall DPS+" -> 3
            "revenant KPM" -> 3
            "revenant KPM+" -> 3
            "icestormunder KPM" -> 3
            "icestormunder KPM+" -> 3
            else -> 1
        }
    }

    fun getClassSortLabel(theClass: CombatClass):String{
        with(theClass.ratings){
            val rats = (damage+pvp+farming+support+ultras+survival).uppercase()
            return when(_sortType.value){
                "name" -> ( if ("S" in rats) "S" else rats.toCharArray().apply { sort() }[0].toString())
                "overall rating" -> ( if ("S" in rats) "S" else rats.toCharArray().apply { sort() }[0].toString())
                "damage rating" -> damage
                "survival rating" -> survival
                "support rating" -> support
                "farming rating" -> farming
                "PvP rating" -> pvp
                "ultras rating" -> ultras
                "classhall DPS" -> dpsToString(theClass.maxPerformance().classhall)
                "classhall DPS+" -> dpsToString(theClass.maxPerformance().classhallNsod)
                "revenant KPM" -> kpmToString(theClass.maxPerformance().revenant)
                "revenant KPM+" -> kpmToString(theClass.maxPerformance().revenantNsod)
                "icestormunder KPM" -> kpmToString(theClass.maxPerformance().icestorm)
                "icestormunder KPM+" -> kpmToString(theClass.maxPerformance().icestormNsod)
                else -> if ("S" in rats) "S" else rats.toCharArray().apply { sort() }[0].toString()
            }
        }

    }

    private fun dpsToString(dps: Int?):String{
        if (dps == null)
            return "  ?"
        return (dps/1000).toString().padStart(2)+"k"

    }
    private fun kpmToString(kpm: Int?):String{
        if (kpm == null)
            return " ?"
        return kpm.toString().padStart(3)
    }

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
                _newestVersion.value = result.data
            }
        }.launchIn(viewModelScope)
    }

    private val _newestVersion = mutableStateOf<List<AppVersion>>(listOf())
    val newestVersion = _newestVersion

    private val _compareClass = mutableStateOf<CombatClass?>(null)
    val compareClass = _compareClass

    fun chooseComparison(theClass: CombatClass?){
        _compareClass.value = theClass
    }
}