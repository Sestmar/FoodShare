package com.example.ecorescueapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecorescueapp.data.local.UserEntity
import com.example.ecorescueapp.data.local.DonationEntity

@Database(entities = [UserEntity::class, DonationEntity::class], version = 1, exportSchema = false)
abstract class EcoDatabase : RoomDatabase() {
    abstract fun ecoDao(): EcoDao
}