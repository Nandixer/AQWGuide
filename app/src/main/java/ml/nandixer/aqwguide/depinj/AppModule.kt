package ml.nandixer.aqwguide.depinj

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ml.nandixer.aqwguide.data.BackendAPI
import ml.nandixer.aqwguide.data.BackendRepositoryImp
import ml.nandixer.aqwguide.domain.repository.BackendRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesHappeningAPI(): BackendAPI{
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/Nandixer/aqw-app-backend/master/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendAPI::class.java)
    }

    @Provides
    @Singleton
    fun providesBackendRepository(api: BackendAPI): BackendRepository{
        return BackendRepositoryImp(api)
    }
}