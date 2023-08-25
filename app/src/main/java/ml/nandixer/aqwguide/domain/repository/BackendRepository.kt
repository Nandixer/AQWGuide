package ml.nandixer.aqwguide.domain.repository

import ml.nandixer.aqwguide.domain.model.AppVersion
import ml.nandixer.aqwguide.domain.model.CombatClass

interface BackendRepository {
    suspend fun getClasses(): List<CombatClass>
    suspend fun getAppVersions(): List<AppVersion>
}