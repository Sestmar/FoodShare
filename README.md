# FoodShare - App de Gestión de Excedentes Alimentarios

> **Proyecto Final de Grado (TFG) - Desarrollo de Aplicaciones Multiplataforma**
> *Conectando comercios locales con voluntarios para reducir el desperdicio de alimentos.*

## 📱 Descripción del Proyecto
**FoodShare** es una aplicación nativa Android diseñada para optimizar la recolección de alimentos perecederos. Permite a los comercios publicar donaciones de forma rápida y a los voluntarios reservarlas en tiempo real, fomentando la economía circular y la solidaridad.

La aplicación destaca por su **Accesibilidad** e **Interfaz Natural (NUI)**, permitiendo el uso mediante voz, gestos y biometría.

---

## Características Principales

### Ciclo de Logística Cerrada (Novedad)
- **Validación por PIN de Seguridad:** Implementación de un flujo de verificación único. Cuando un voluntario reserva, se genera un código aleatorio que el comercio debe validar para completar la entrega.
- **Historial de Pedidos:** Sección dedicada para el usuario donde puede consultar sus códigos activos y ver el estado de sus pedidos pasados con efectos visuales de opacidad.
- **Soft Delete (Persistencia Histórica):** Los datos no se eliminan al finalizar una venta; se marcan como "Completados" para mantener la integridad de las métricas y el historial.

### Análisis de Impacto Avanzado
- **Gráficos Circulares (Pie Chart):** Visualización avanzada mediante `Canvas` con animaciones de dibujo en tiempo real.
- **Métricas de Éxito:** Cálculo dinámico del porcentaje de éxito (comida salvada vs. disponible).
- **Exportación Profesional:** Generación de informes PDF con barra de progreso visual y metadatos del sistema.

---

## Stack Tecnológico

El proyecto ha sido desarrollado siguiendo las mejores prácticas de la industria y la arquitectura recomendada por Google:

* **Lenguaje:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (Diseño declarativo moderno Material Design 3).
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture.
* **Inyección de Dependencias:** [Dagger Hilt](https://dagger.dev/hilt/).
* **Base de Datos:** [Room Database](https://developer.android.com/training/data-storage/room) (Persistencia local reactiva).
* **Asincronía:** Coroutines & StateFlow.
* **Testing:** JUnit 4 & Mockito (Tests unitarios de ViewModels).

---

## Capturas de Pantalla

| Panel Comercio (Admin) | Dictado por Voz | Login Biométrico |
|:---:|:---:|:---:|
| | | |

| Zona Voluntarios | Informes PDF | Gráficos |
|:---:|:---:|:---:|
| | | |

---

## Autor
Desarrollado por **Sergio Estudillo** como Proyecto Final de Ciclo.
*Contacto (LinkedIn): Sergio Estudillo Marabot. Enlace --> https://www.linkedin.com/in/sergio-estudillo-marabot-6562331b7/
