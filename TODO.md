# TODO — Piloto en tienda

Lista simple para llevar la app a un **piloto en tienda** fiable. Casillas: `- [ ]` pendiente, `- [x]` hecho. Actualizado según el estado del código y la revisión de madurez.

## Completado

- [x] **POS base** — Carrito, categorías, ítem “Varios”, cobro en efectivo, cambio, guardar ticket en Room, descuento de stock.
- [x] **Login y roles** — Pantalla de inicio, admin vs cajero; el panel de administración solo visible para administrador en el menú lateral.
- [x] **Turnos de caja** — Abrir caja, cerrar caja y diálogo de corte con totales esperados.
- [x] **Inventario** — CRUD de productos y categorías; lista de inventario para admin.
- [x] **Empleados** — Lista, alta, edición y desactivación de usuarios.
- [x] **Reporte diario** — Pantalla de resumen por categoría y exportación a PDF.
- [x] **Historial de ventas** — Por rango de fechas; exportación a CSV (compartir).
- [x] **Exportar todas las ventas (CSV)** — Desde el panel de administración; mismo formato que el historial, sin filtro de fechas.
- [x] **Configuración del sistema** — Preferencias (DataStore), permisos Bluetooth, elegir impresora emparejada y guardar MAC / opciones de ticket.
- [x] **Impresión térmica al cobrar** — Tras guardar la venta se intenta conectar e imprimir vía `PrinterService` (ESC/POS); validar en el hardware del piloto.

## Pendiente (antes o durante el piloto)

- [ ] **Migraciones Room seguras** — Evitar depender de `fallbackToDestructiveMigration` en un despliegue real; añadir migraciones por versión para no borrar ventas al actualizar la app.
- [ ] **Respaldo y recuperación** — Definir e implementar o documentar un plan: copia del SQLite, export completo recuperable, o procedimiento manual verificado para no perder datos si falla el dispositivo.
- [ ] **UX cuando falla la impresión** — Aviso claro al cajero (la venta ya quedó guardada), y opción de reintento o reimprimir último ticket sin depender solo del log.
- [ ] **Pruebas en tienda** — Recorrido acordado: cobro normal, corte, historial, export CSV, impresora apagada o fuera de alcance, permisos revocados, muchos ítems en el carrito.
- [ ] **Tablet y rotación** — Probar en el dispositivo objetivo; fijar orientación o ajustar layout en landscape si aplica al negocio.
- [ ] **Instalación piloto** — APK firmada, nombre de la app e icono alineados al comercio (lista para instalar fuera de Android Studio).
- [ ] **Configuración solo admin (recomendado)** — El cajero hoy puede abrir la misma pantalla de ajustes que el admin; restringir impresora y datos sensibles a rol administrador si el piloto lo exige.

## Después del piloto (mejoras)

- [ ] **PDF del historial de tickets** — Completar exportación PDF del listado filtrado (hoy el CSV sí; el PDF del historial está pendiente en código).
- [ ] **Fotos de producto** — Sustituir el icono placeholder por imágenes reales en el grid si el catálogo lo necesita.
