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

    val filteredClasses = derivedStateOf {
        classes.value.filter { combatClass ->
            val filterStr = classSearchText.value.lowercase()
            filterStr in combatClass.names.joinToString(" ").lowercase() ||
            filterStr in combatClass.abbr.lowercase() ||
            filterStr in combatClass.tags.joinToString(" ").lowercase()
        }
    }

    init {
        getClassesUseCase().onEach { result ->
            if (result is Resource.Success && result.data != null){
                _classes.value = result.data
            }
        }.launchIn(viewModelScope)

        getVersionsUseCase().onEach { result ->
            Log.d("nandorsss", "b'")
            if (result is Resource.Success && result.data != null){
                _newestVersion.value = result.data[0]
                Log.d("nandorsss", "a'")
            }
            if ( result is Resource.Error){
                Log.d("nandorsss", "e'")
            }
            if ( result is Resource.Loading){
                Log.d("nandorsss", "l'")
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