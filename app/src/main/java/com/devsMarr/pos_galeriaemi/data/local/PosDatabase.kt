package com.devsMarr.pos_galeriaemi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devsMarr.pos_galeriaemi.data.local.dao.CashShiftDao
import com.devsMarr.pos_galeriaemi.data.local.dao.CategoryDao
import com.devsMarr.pos_galeriaemi.data.local.dao.ProductDao
import com.devsMarr.pos_galeriaemi.data.local.dao.ReportDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketDetailDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketHeadDao
import com.devsMarr.pos_galeriaemi.data.local.dao.UserDao


import com.devsMarr.pos_galeriaemi.data.local.entity.CategoryEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.ProductEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.CashShiftEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketDetailEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketHeadEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.UserEntity


@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CashShiftEntity::class,
        TicketHeadEntity::class,
        TicketDetailEntity::class,
        UserEntity::class
               ],
    version = 2,
    exportSchema = false,

)
abstract class PosDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun productDao(): ProductDao

    abstract fun cashShiftDao(): CashShiftDao

    abstract fun ticketHeadDao(): TicketHeadDao

    abstract fun ticketDetailDao(): TicketDetailDao

    abstract fun userDao(): UserDao

    abstract fun reportDao(): ReportDao

}