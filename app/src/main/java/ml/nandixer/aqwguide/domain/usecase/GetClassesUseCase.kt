package ml.nandixer.aqwguide.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ml.nandixer.aqwguide.domain.Resource
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.repository.BackendRepository
import java.lang.Exception
import javax.inject.Inject

class GetClassesUseCase @Inject constructor(
    private val repository: BackendRepository
) {
    operator fun invoke(): Flow<Resource<List<CombatClass>>> = flow {
        emit(Resource.Loading())
        try {
            val classes = repository.getClasses()
            emit(Resource.Success(classes))

        } catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }

    }
}