package com.devsMarr.pos_galeriaemi.di

import android.content.Context
import androidx.room.Room
import com.devsMarr.pos_galeriaemi.data.local.PosDatabase
import com.devsMarr.pos_galeriaemi.data.local.dao.CashShiftDao
import com.devsMarr.pos_galeriaemi.data.local.dao.CategoryDao
import com.devsMarr.pos_galeriaemi.data.local.dao.ProductDao
import com.devsMarr.pos_galeriaemi.data.local.dao.ReportDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketDetailDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketHeadDao
import com.devsMarr.pos_galeriaemi.data.local.dao.UserDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PosDatabase {
        return Room.databaseBuilder(
            context,
            PosDatabase::class.java,
            "pos_database_local" // El nombre físico del archivo en la tablet
        )
            .fallbackToDestructiveMigration() // Si cambia una tabla, borra todo y empieza de cero para no crashear.
            .build()
    }

    @Provides
    fun provideCategoryDao(db: PosDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideProductDao(db: PosDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCashShiftDao(db: PosDatabase): CashShiftDao = db.cashShiftDao()

    @Provides
    fun provideTicketHeadDao(db: PosDatabase): TicketHeadDao = db.ticketHeadDao()

    @Provides
    fun provideTicketDetailDao(db: PosDatabase): TicketDetailDao = db.ticketDetailDao()

    @Provides
    fun provideUserDao(database: PosDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideReportDao(database: PosDatabase): ReportDao {
        return database.reportDao()
    }
}