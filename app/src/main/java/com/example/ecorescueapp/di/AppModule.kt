package com.example.ecorescueapp.di

import android.content.Context
import androidx.room.Room
import com.example.ecorescueapp.data.local.EcoDao
import com.example.ecorescueapp.data.local.EcoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EcoDatabase {
        return Room.databaseBuilder(
            context,
            EcoDatabase::class.java,
            "eco_rescue_db"
        )
            // Esto destruye la BBDD si cambias las tablas (útil para desarrollo rápido)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEcoDao(database: EcoDatabase): EcoDao {
        return database.ecoDao()
    }
}