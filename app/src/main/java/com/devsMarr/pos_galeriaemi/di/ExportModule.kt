package com.devsMarr.pos_galeriaemi.di

import com.devsMarr.pos_galeriaemi.data.export.PdfExportManager
import com.devsMarr.pos_galeriaemi.domain.service.PdfExportService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExportModule {

    @Binds
    @Singleton
    abstract fun bindPdfExportService(
        pdfExportManager: PdfExportManager
    ): PdfExportService
}