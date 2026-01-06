package com.example.ecorescueapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Si UserEntity se pone en rojo, dale a Alt+Enter para importarla.
@Database(entities = [DonationEntity::class, UserEntity::class], version = 3, exportSchema = false)
abstract class EcoDatabase : RoomDatabase() {

    // Llamada clave
    abstract fun donationDao(): DonationDao
}