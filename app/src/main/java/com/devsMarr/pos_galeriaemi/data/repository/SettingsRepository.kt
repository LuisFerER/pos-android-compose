package com.devsMarr.pos_galeriaemi.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.devsMarr.pos_galeriaemi.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Definición de las llaves con las que se guarda cada dato
    private object PreferencesKeys {
        val BUSINESS_NAME = stringPreferencesKey("business_name")
        val ADDRESS = stringPreferencesKey("address")
        val PHONE = stringPreferencesKey("phone")
        val TICKET_FOOTER = stringPreferencesKey("ticket_footer")
        val PRINTER_MAC = stringPreferencesKey("printer_mac")
        val PAPER_WIDTH = intPreferencesKey("paper_width")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // Lee los datos reactivamente. Si algo cambia, esto emite el nuevo valor automáticamente.
    val appConfigFlow: Flow<AppConfig> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Mapeamos las preferencias guardadas a nuestro modelo de dominio.
            AppConfig(
                businessName = preferences[PreferencesKeys.BUSINESS_NAME] ?: "",
                address = preferences[PreferencesKeys.ADDRESS] ?: "",
                phone = preferences[PreferencesKeys.PHONE] ?: "",
                ticketFooter = preferences[PreferencesKeys.TICKET_FOOTER] ?: "¡Gracias por su preferencia!",
                printerMacAddress = preferences[PreferencesKeys.PRINTER_MAC] ?: "",
                paperWidth = preferences[PreferencesKeys.PAPER_WIDTH] ?: 58,
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false
            )
        }

    // Guarda o actualiza toda la configuración de golpe
    suspend fun updateConfig(config: AppConfig) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUSINESS_NAME] = config.businessName
            preferences[PreferencesKeys.ADDRESS] = config.address
            preferences[PreferencesKeys.PHONE] = config.phone
            preferences[PreferencesKeys.TICKET_FOOTER] = config.ticketFooter
            preferences[PreferencesKeys.PRINTER_MAC] = config.printerMacAddress
            preferences[PreferencesKeys.PAPER_WIDTH] = config.paperWidth
            preferences[PreferencesKeys.IS_DARK_MODE] = config.isDarkMode
        }
    }
}