
# 1. Descripción del Proyecto

### Nombre del Proyecto:

FoodTech Kitchen Service

### Objetivo del Proyecto:

El proyecto **FoodTech Kitchen Service** corresponde a un backend desarrollado con **Spring Boot** cuyo objetivo es gestionar el flujo de órdenes dentro de un entorno de cocina digitalizada.

El sistema permite registrar órdenes, descomponerlas en tareas específicas según la estación de preparación (por ejemplo, bar, cocina caliente o cocina fría), ejecutar dichas tareas de forma asíncrona y consolidar el estado final de cada orden.

Adicionalmente, el sistema expone una **API REST segura** que permite gestionar usuarios, autenticar mediante JWT, consultar el estado de las órdenes y solicitar la generación de facturas una vez completado el proceso de preparación.

La arquitectura del sistema sigue principios de **arquitectura hexagonal (clean architecture)**, separando claramente las capas de dominio, aplicación e infraestructura para facilitar la escalabilidad, mantenibilidad y pruebas del sistema.

---

# 2. Flujos Críticos del Negocio

### Principales Flujos de Trabajo:

**1. Creación de Orden**

1. Un cliente o sistema externo envía una solicitud para crear una orden.
2. El sistema valida la información recibida (por ejemplo, número de mesa y productos).
3. La orden se registra en la base de datos.
4. El sistema descompone la orden en múltiples tareas según la estación de preparación.
5. Cada tarea queda registrada en estado **PENDING**.

---

**2. Preparación de Tareas**

1. Un operador inicia la preparación de una tarea.
2. El sistema valida que la tarea se encuentre en estado **PENDING**.
3. La tarea cambia al estado **IN_PREPARATION**.
4. Se ejecuta un proceso asíncrono que simula la preparación.
5. Una vez finalizada la preparación, la tarea cambia a estado **COMPLETED**.
6. El sistema verifica si todas las tareas asociadas a la orden han finalizado.

Si todas las tareas se encuentran completadas, la orden pasa a estado **COMPLETED**.

---

**3. Solicitud de Facturación**

1. Una vez completada la orden, se puede solicitar su facturación.
2. El sistema valida que la orden se encuentre en estado **COMPLETED**.
3. Se genera un evento de facturación mediante el patrón **Outbox**.
4. La orden cambia su estado a **INVOICED**.

---

### Módulos o Funcionalidades Críticas:

Las funcionalidades principales del sistema corresponden a:

* Gestión de órdenes.
* Descomposición de órdenes en tareas por estación.
* Ejecución asíncrona del proceso de preparación.
* Gestión del estado de tareas y órdenes.
* Autenticación de usuarios mediante JWT.
* Generación de eventos de facturación mediante patrón Outbox.

Estas funcionalidades representan el núcleo del funcionamiento del sistema.

---

# 3. Reglas de Negocio y Restricciones

### Reglas de Negocio Relevantes:

Durante el análisis del sistema se identificaron las siguientes reglas de negocio:

* Una orden debe tener un **número de mesa válido**.
* Una orden debe contener **al menos un producto**.
* Las tareas de preparación siguen el flujo de estados:

PENDING → IN_PREPARATION → COMPLETED

* Una tarea solo puede iniciar su preparación si se encuentra en estado **PENDING**.
* Una tarea solo puede completarse si se encuentra en estado **IN_PREPARATION**.

En caso de que estas condiciones no se cumplan, el sistema genera excepciones que impiden continuar con el proceso.

---

### Regulaciones o Normativas:

Durante el análisis del proyecto no se identificaron referencias explícitas a normativas legales o regulatorias específicas.

Sin embargo, al tratarse de un sistema que gestiona autenticación de usuarios y datos operativos, podrían aplicarse normativas relacionadas con:

* protección de datos
* seguridad de la información
* buenas prácticas de desarrollo seguro

Estas regulaciones deberán definirse según el entorno de despliegue del sistema.

---

# 4. Perfiles de Usuario y Roles

### Perfiles o Roles de Usuario en el Sistema:

Actualmente el sistema implementa un modelo básico de autenticación basado en usuarios registrados.

Se identifican los siguientes perfiles potenciales:

**Usuario Autenticado**

Corresponde a cualquier usuario que haya iniciado sesión correctamente mediante el sistema de autenticación basado en JWT.

Este usuario puede acceder a los endpoints protegidos de la API.

---

**Operadores del Sistema (Inferencia basada en el dominio)**

Debido al contexto del sistema, se infiere la existencia de operadores como:

* personal de cocina
* meseros
* operadores del sistema

No obstante, el sistema actualmente **no implementa roles explícitos ni diferenciación de permisos por tipo de usuario**.

---

### Permisos y Limitaciones de Cada Perfil:

El sistema maneja actualmente una autorización basada únicamente en autenticación.

* Los endpoints bajo la ruta **/api/auth/** se encuentran abiertos.
* El resto de endpoints requieren un **token JWT válido**.

No se identificaron restricciones adicionales basadas en roles o permisos específicos.

---

# 5. Condiciones del Entorno Técnico

### Plataformas Soportadas:

El sistema corresponde a un **backend basado en API REST**, diseñado para ser consumido por aplicaciones externas tales como:

* aplicaciones web
* aplicaciones móviles
* sistemas POS u otros sistemas de gestión de órdenes

---

### Tecnologías o Integraciones Clave:

El sistema está construido utilizando las siguientes tecnologías principales:

* **Java**
* **Spring Boot**
* **Spring Security**
* **Spring Data JPA**
* **JWT (JSON Web Tokens) para autenticación**
* **Reactor para ejecución de procesos asíncronos**
* **PostgreSQL como base de datos principal**
* **H2 como base de datos para pruebas**
* **Docker para despliegue del sistema**
* **JaCoCo para análisis de cobertura de pruebas**

Estas tecnologías permiten construir un sistema escalable, seguro y preparado para integraciones futuras.

---

# 6. Casos Especiales o Excepciones

### Escenarios Alternos o Excepciones que Deben Considerarse:

Se identificaron algunos escenarios especiales que el sistema contempla:

**Transiciones inválidas de estado**

Intentar iniciar la preparación de una tarea que no se encuentra en estado **PENDING** genera una excepción.

Del mismo modo, intentar completar una tarea que no está en estado **IN_PREPARATION** produce un error.

---

**Facturación inválida**

Si se intenta facturar una orden que:

* no se encuentra completada
* ya fue facturada

el sistema genera una excepción o ignora la solicitud.

---

**Registro de usuarios duplicados**

El sistema valida que el **email** y el **username** no estén previamente registrados.
En caso contrario se genera una excepción de duplicidad.

---

**Fallos en procesos asíncronos**

Durante la ejecución de procesos asíncronos de preparación, si ocurre un error el sistema registra el evento en los logs y la tarea no se marca como completada.

Actualmente no se identifican mecanismos automáticos de reintento o recuperación de errores.
