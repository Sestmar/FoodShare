package com.example.ecorescueapp.di

import android.content.Context
import androidx.room.Room
import com.example.ecorescueapp.data.local.DonationDao
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

    // 1. Enseñamos a Hilt a crear la BASE DE DATOS
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EcoDatabase {
        return Room.databaseBuilder(
            context,
            EcoDatabase::class.java,
            "foodshare_database" // Le ponemos nombre nuevo para que empiece limpia
        )
            .fallbackToDestructiveMigration() // Esto evita crashes si cambias tablas
            .build()
    }

    // 2. Enseñamos a Hilt a obtener el DAO desde la Base de Datos
    // ESTA ES LA PARTE QUE TE FALTABA Y DABA EL ERROR
    @Provides
    @Singleton
    fun provideDonationDao(database: EcoDatabase): DonationDao {
        return database.donationDao()
    }
}