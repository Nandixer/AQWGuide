package ml.nandixer.aqwguide.data

import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.repository.BackendRepository
import javax.inject.Inject

class BackendRepositoryImp @Inject constructor(
    private val api: BackendAPI
) : BackendRepository{
    override suspend fun getClasses(): List<CombatClass> {
        return api.getClasses()
    }
}