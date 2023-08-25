package ml.nandixer.aqwguide.data

import ml.nandixer.aqwguide.domain.model.AppVersion
import ml.nandixer.aqwguide.domain.model.CombatClass
import retrofit2.http.GET

interface BackendAPI {
    @GET("/Nandixer/aqw-app-backend/master/classes.json")
    suspend fun getClasses(): List<CombatClass>

    @GET("/Nandixer/aqw-app-backend/master/versions.json")
    suspend fun getVersions(): List<AppVersion>
}