package ml.nandixer.aqwguide.data

import ml.nandixer.aqwguide.domain.model.AppVersion
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.repository.BackendRepository
import javax.inject.Inject

class BackendRepositoryImp @Inject constructor(
    private val api: BackendAPI
) : BackendRepository{
    override suspend fun getClasses(): List<CombatClass> {
        return api.getClasses()
    }

    override suspend fun getAppVersions(): List<AppVersion>{
        return api.getVersions()
    }
}