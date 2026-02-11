# Metamapa - Visualización Histórica Geográfica

Metamapa es una plataforma web educativa desarrollada para la visualización de hechos históricos sobre un mapa interactivo. El proyecto fue desarrollado como parte de la formación académica en la **UTN Buenos Aires**, aplicando una arquitectura en capas y estándares de seguridad y monitoreo industriales.

## 🚀 Tecnologías y Herramientas

* **Backend:** Java con Spring Boot[cite: 36, 37].
* **Frontend:** Server-Side Rendering (SSR) con Thymeleaf y Tailwind CSS.
* **Seguridad:** Gestión de identidad con Keycloak y autorización basada en JWT.
* **Bases de Datos:** MySQL.
* **API & Comunicación:** Implementación de REST y GraphQL.
* **Infraestructura:** Containerización completa con Docker.

## 📊 Observabilidad y Monitoreo
Una de las fortalezas del proyecto es la implementación de un stack de observabilidad para el seguimiento del estado del sistema:
* **Métricas:** Prometheus y Grafana.
* **Logs:** Grafana Loki.
* **Trazabilidad:** Zipkin para el seguimiento de peticiones.

## 🛠️ Características Principales
* **Seguridad:** Implementación de Rate Limiting y bloqueo de IPs sospechosas.
* **Arquitectura:** Diseño basado en capas para asegurar la mantenibilidad.
