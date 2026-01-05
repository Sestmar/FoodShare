# 🍏 FoodShare - App de Gestión de Excedentes Alimentarios

> **Proyecto Final de Grado (TFG) - Desarrollo de Aplicaciones Multiplataforma**
> *Conectando comercios locales con voluntarios para reducir el desperdicio de alimentos.*

## 📱 Descripción del Proyecto
**FoodShare** es una aplicación nativa Android diseñada para optimizar la recolección de alimentos perecederos. Permite a los comercios publicar donaciones de forma rápida y a los voluntarios reservarlas en tiempo real, fomentando la economía circular y la solidaridad.

La aplicación destaca por su **Accesibilidad** e **Interfaz Natural (NUI)**, permitiendo el uso mediante voz, gestos y biometría.

---

## 🚀 Características Principales (Highlights)

### 🔐 Seguridad y Biometría
- **Login con Huella Dactilar:** Autenticación biométrica integrada para un acceso rápido y seguro sin contraseñas.
- Gestión de roles (Administrador/Comercio y Usuario/Voluntario).

### 🎙️ Interfaz Natural (NUI)
- **Dictado por Voz:** Los comercios pueden dar de alta productos dictando su descripción gracias a la integración con Speech-to-Text.
- **Control Gestual:** Eliminación de elementos mediante gestos de deslizamiento (*Swipe-to-Dismiss*) con retroalimentación visual.

### 📊 Informes y Datos
- **Generación de PDFs:** Creación automática de informes de impacto y donaciones guardados directamente en el dispositivo.
- **Gráficos Integrados:** Visualización estadística del estado de las donaciones (Disponibles vs Reservadas).
- **Filtrado Dinámico:** Sistema de Chips para filtrar listados en tiempo real.

---

## 🛠️ Stack Tecnológico

El proyecto ha sido desarrollado siguiendo las mejores prácticas de la industria y la arquitectura recomendada por Google:

* **Lenguaje:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (Diseño declarativo moderno Material Design 3).
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture.
* **Inyección de Dependencias:** [Dagger Hilt](https://dagger.dev/hilt/).
* **Base de Datos:** [Room Database](https://developer.android.com/training/data-storage/room) (Persistencia local reactiva).
* **Asincronía:** Coroutines & StateFlow.
* **Testing:** JUnit 4 & Mockito (Tests unitarios de ViewModels).

---

## 📸 Capturas de Pantalla

| Panel Comercio (Admin) | Dictado por Voz | Login Biométrico |
|:---:|:---:|:---:|
| | | |

| Zona Voluntarios | Informes PDF | Gráficos |
|:---:|:---:|:---:|
| | | |

---

## 👨‍💻 Autor
Desarrollado por **Sergio Estudillo** como Proyecto Final de Ciclo.
*Contacto (LinkedIn): Sergio Estudillo Marabot. Enlace --> https://www.linkedin.com/in/sergio-estudillo-marabot-6562331b7/
