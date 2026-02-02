# 🍏 FoodShare - App de Gestión de Excedentes Alimentarios

> **Proyecto Final de Ciclo - Desarrollo de Aplicaciones Multiplataforma**
> *Conectando comercios locales con voluntarios para reducir el desperdicio de alimentos.*

---

## 📺 Video Demo (Defensa del Proyecto)
¡Mira la aplicación en funcionamiento, incluyendo el Login Biométrico y la Realidad Aumentada!
### 👉 [VER VIDEO DEMO AQUÍ (Click para abrir)] https://drive.google.com/file/d/1r185wEYns1gqHJK0lNAPamT_peUdGwNt/view?usp=drive link

---

## 🚀 Descarga e Instalación (Distribución)
Para probar la aplicación en tu dispositivo Android sin necesidad de compilar:

📂 **[Descargar APK Firmado (v1.0)](./releases/app-release.apk)**
*(Ruta en repositorio: `/releases/app-release.apk`)*

---

## 📱 Descripción del Proyecto
**FoodShare** es una aplicación nativa Android diseñada para optimizar la recolección de alimentos perecederos. Permite a los comercios publicar donaciones de forma rápida mediante **voz** y a los voluntarios reservarlas en tiempo real, fomentando la economía circular y la solidaridad.

La aplicación destaca por su **Accesibilidad** e **Interfaz Natural (NUI)**, permitiendo el uso mediante voz, gestos y biometría.

## 📄 Documentación Técnica
Toda la documentación requerida se encuentra disponible en la carpeta `/doc` de este repositorio:

* 📘 **[Memoria Técnica del Proyecto](./doc/Memoria_Proyecto_FoodShare.pdf)**: Justificación, arquitectura y diseño.
* 📖 **[Manual de Usuario](./doc/Manual_De_Usuario_FoodShare.pdf)**: Guía paso a paso para Administradores y Voluntarios.

---

## Características Principales

### 🔄 Ciclo de Logística Cerrada
- **Validación por QR/PIN:** Flujo de seguridad donde el comercio valida la entrega escaneando el código del voluntario.
- **Persistencia Histórica (Soft Delete):** Los datos no se eliminan; se marcan como "Completados" para mantener métricas de impacto.
- **Gestión de Estados en Tiempo Real:** Control visual de productos (Disponibles, Reservados, Entregados, Cancelados).

### 📊 Análisis de Impacto
- **Gráficos Interactivos:** Visualización avanzada mediante `Canvas` en Jetpack Compose.
- **Exportación de Informes:** Generación de PDFs con el resumen de impacto ecológico y métricas de éxito.

---

## 🛠️ Stack Tecnológico
El proyecto sigue la arquitectura recomendada por Google (Clean Architecture + MVVM):

* **Lenguaje:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (Material Design 3).
* **Arquitectura:** MVVM + Clean Architecture.
* **Inyección de Dependencias:** [Dagger Hilt](https://dagger.dev/hilt/).
* **Base de Datos:** [Room Database](https://developer.android.com/training/data-storage/room).
* **Sensores y Hardware:** Biometría (Huella), Micrófono (Speech-to-Text), Cámara (ZXing QR).

---

## 📸 Capturas de Pantalla
*(El diseño sigue una estética Dark/Neon para reducir el consumo en pantallas OLED)*

| Login Biométrico | Panel Comercio | Zona Voluntarios |
|:---:|:---:|:---:|
| <img src="./app/src/main/res/mipmap-xxhdpi/ic_launcher.webp" width="100" alt="Logo"/> | *Interfaz de gestión con reconocimiento de voz* | *Listado de reservas con indicadores de estado* |

---

## Autor
Desarrollado por **Sergio Estudillo Marabot**.
* **Centro:** IES Rafael Alberti.
* **Módulo:** Desarrollo de Interfaces.
* **LinkedIn:** [Ver Perfil](https://www.linkedin.com/in/sergio-estudillo-marabot-6562331b7/)

---
