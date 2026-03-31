package com.devsMarr.pos_galeriaemi.di

import com.devsMarr.pos_galeriaemi.data.printer.BluetoothPrinterManager
import com.devsMarr.pos_galeriaemi.data.printer.FakePrinterManager
import com.devsMarr.pos_galeriaemi.domain.service.PrinterService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrinterModule {

    @Binds
    @Singleton
    abstract fun bindPrinterService(
        bluetoothPrinterManager: FakePrinterManager
        // bluetoothPrinterManager: BluetoothPrinterManager
    ): PrinterService
}