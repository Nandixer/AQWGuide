package ml.nandixer.aqwguide.data

import ml.nandixer.aqwguide.domain.model.CombatClass
import retrofit2.http.GET

interface BackendAPI {
    @GET("/classes.json")
    suspend fun getClasses(): List<CombatClass>
}