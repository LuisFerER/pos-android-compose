package com.devsMarr.pos_galeriaemi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import com.devsMarr.pos_galeriaemi.data.local.entity.CategoryEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.ProductEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.CashShiftEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketDetailEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketHeadEntity


@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CashShiftEntity::class,
        TicketHeadEntity::class,
        TicketDetailEntity::class
               ],
    version = 1,
    exportSchema = false,

)
abstract class PosDatabase : RoomDatabase() {

}