package ml.nandixer.aqwguide.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ml.nandixer.aqwguide.domain.Resource
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.usecase.GetClassesUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getClassesUseCase: GetClassesUseCase
): ViewModel() {

    private val _classes = mutableStateOf(listOf<CombatClass>())
    val classes = _classes

    init {
        getClassesUseCase().onEach { result ->
            if (result is Resource.Success && result.data != null){
                _classes.value = result.data
            }
        }.launchIn(viewModelScope)
    }
}