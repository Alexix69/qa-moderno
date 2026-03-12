## Refinamiento de Historias de Usuario - FoodTech Kitchen Service

A continuación, se presentan las historias de usuario refinadas y segmentadas, resultado del análisis y mejora de las [HISTORIAS_DE_USUARIO.md](./resources/HISTORIAS_DE_USUARIO.md) originales.

**Cambios principales:** Separación de responsabilidades, adición de HUs de facturación y notificaciones, y especificación de requisitos técnicos omitidos en las versiones originales.

---

## HU-001A: Procesar pedido de cocina (Descomposición y Asignación)

**Descripción:**  
Como responsable de cocina  
Quiero que el sistema reciba un pedido y lo descomponga automáticamente en tareas por estación configurada  
Para que cada área de preparación pueda trabajar de forma independiente y eficiente

**Criterios de aceptación:**
```gherkin
Scenario: Pedido con productos de diferentes tipos
  Given que existe un pedido para la mesa "A1"
  And el pedido contiene 2 bebidas y 1 plato caliente
  When el pedido es registrado en el sistema
  Then el sistema genera 2 tareas de preparación
  And la tarea de barra contiene las 2 bebidas
  And la tarea de cocina caliente contiene el plato correspondiente

Scenario: Pedido con productos agrupados por estación
  Given que existe un pedido para la mesa "B2"
  And el pedido contiene 3 postres y 1 bebida
  When el pedido es registrado en el sistema
  Then el sistema genera 2 tareas de preparación
  And la tarea de barra contiene la bebida
  And la tarea de cocina fría contiene los 3 postres agrupados

Scenario: Estaciones configurables
  Given que el sistema tiene configuradas las estaciones barra, cocina caliente y cocina fría
  When se registra un pedido con productos correspondientes a cada estación
  Then el sistema asigna cada tarea a la estación configurada según el tipo de producto

Scenario: Productos con atributos mínimos requeridos
  Given que el pedido contiene productos con id, nombre y tipo
  When el pedido es registrado en el sistema
  Then todos los productos son correctamente asignados a su estación
```

---

## HU-001B: Validaciones de Pedido

**Descripción:**  
Como responsable de cocina  
Quiero que el sistema valide que los pedidos contengan información mínima requerida  
Para evitar errores operativos y asegurar la integridad de los datos

**Criterios de aceptación:**
```gherkin
Scenario: Pedido sin productos
  Given que un pedido no contiene ningún producto
  When se intenta registrar el pedido en el sistema
  Then el sistema rechaza el pedido
  And se notifica al usuario que el pedido debe contener al menos un producto

Scenario: Pedido sin número de mesa
  Given que un pedido no tiene asignado un número de mesa válido
  And el pedido contiene productos válidos
  When se intenta registrar el pedido en el sistema
  Then el sistema rechaza el pedido
  And se notifica al usuario que el pedido debe tener un número de mesa válido

Scenario: Producto sin atributos mínimos
  Given que un producto no tiene id, nombre o tipo definido
  When se intenta registrar un pedido con ese producto
  Then el sistema rechaza el pedido
  And se notifica al usuario el motivo específico del rechazo
```

---

## HU-002: Consultar tareas pendientes por estación

**Descripción:**  
Como encargado de una estación de cocina  
Quiero visualizar únicamente las tareas pendientes de mi estación  
Para prepararlas sin confusión con tareas de otras áreas

**Criterios de aceptación:**
```gherkin
Scenario: Consulta de tareas pendientes de una estación específica
  Given que existen tareas pendientes asignadas a varias estaciones
  When el encargado consulta las tareas pendientes de su estación
  Then el sistema muestra únicamente las tareas con estado "PENDIENTE" de la estación consultada

Scenario: Estación sin tareas pendientes
  Given que una estación no tiene tareas pendientes
  When el encargado consulta las tareas de su estación
  Then el sistema informa que no hay tareas pendientes

Scenario: Información mínima de cada tarea
  Given que existen tareas pendientes
  When el encargado consulta las tareas
  Then el sistema muestra: número de mesa, lista de productos, hora de creación, estado y id de la tarea

Scenario: Consulta de estación no reconocida
  Given que el usuario consulta una estación que no existe en el sistema
  When realiza la consulta
  Then el sistema informa que la estación no existe
  And no se muestran tareas
```

---

## HU-003: Iniciar y completar tareas de preparación

**Descripción:**  
Como cocinero de una estación  
Quiero iniciar y completar la preparación de una tarea asignada  
Para que el sistema registre automáticamente el progreso y notifique la finalización

**Criterios de aceptación:**
```gherkin
Scenario: Iniciar tarea pendiente
  Given que existe una tarea en estado "PENDIENTE" asignada a la estación
  When el cocinero indica que inicia la preparación
  Then el sistema cambia el estado de la tarea a "EN_PREPARACION"
  And registra la hora de inicio

Scenario: Completar tarea automáticamente tras tiempo estimado
  Given que una tarea está en estado "EN_PREPARACION"
  And el tiempo estimado de preparación es configurable por tipo de producto
  When el tiempo estimado transcurre
  Then el sistema cambia el estado de la tarea a "COMPLETADA"
  And registra la hora de finalización y el tiempo total

Scenario: No se puede iniciar tarea ya en preparación
  Given que una tarea está en estado "EN_PREPARACION"
  When el cocinero intenta iniciarla nuevamente
  Then el sistema rechaza la operación
  And notifica que la tarea ya está en preparación
```

---

## HU-004: Consultar historial de tareas completadas por estación

**Descripción:**  
Como responsable de una estación  
Quiero consultar el historial de tareas completadas de mi estación  
Para revisar el desempeño y tiempos de preparación

**Criterios de aceptación:**
```gherkin
Scenario: Consulta de tareas completadas
  Given que la estación tiene tareas completadas
  When el responsable consulta el historial
  Then el sistema muestra las tareas completadas con: id, número de mesa, productos, hora de inicio, hora de fin y tiempo total

Scenario: Periodo de retención de historial
  Given que existen tareas completadas hace más de 30 días
  When el responsable consulta el historial
  Then el sistema solo muestra tareas completadas en los últimos 30 días
```

---

## HU-005: Estado agregado del pedido según tareas

**Descripción:**  
Como área de servicio  
Quiero que el sistema calcule el estado del pedido basado en el estado agregado de todas sus tareas  
Para informar correctamente al cliente sobre el avance de su pedido

**Criterios de aceptación:**
```gherkin
Scenario: Pedido en preparación
  Given que un pedido tiene tareas en diferentes estados
  And al menos una tarea está en "EN_PREPARACION" o "PENDIENTE"
  When se consulta el estado del pedido
  Then el sistema muestra el estado "EN_PREPARACION"

Scenario: Pedido completado
  Given que todas las tareas de un pedido están en estado "COMPLETADA"
  When se consulta el estado del pedido
  Then el sistema muestra el estado "COMPLETADO"
```

---

## HU-006: Procesar facturación de pedido completado

**Descripción:**  
Como responsable de cocina  
Quiero que el sistema solicite la facturación de una orden solo si está completamente preparada  
Para asegurar que solo se facture lo efectivamente preparado

**Criterios de aceptación:**
```gherkin
Scenario: Solicitud de factura solo si pedido está completado
  Given que una orden está en estado "COMPLETADO"
  When se solicita la factura
  Then el sistema genera un evento Outbox de facturación
  And marca la orden como "INVOICED"

Scenario: Solicitud de factura de orden no completada
  Given que una orden no está en estado "COMPLETADO"
  When se solicita la factura
  Then el sistema rechaza la solicitud
  And notifica que la orden debe estar completada para facturar

Scenario: Repetir solicitud de factura en orden ya facturada
  Given que una orden ya está en estado "INVOICED"
  When se solicita la factura nuevamente
  Then el sistema ignora la solicitud
  And no genera un nuevo evento
```

---

## HU-007: Notificaciones de errores y validaciones

**Descripción:**  
Como usuario del sistema  
Quiero recibir notificaciones claras y específicas sobre cualquier error o validación fallida al registrar pedidos o tareas  
Para poder corregirlos oportunamente y asegurar el flujo adecuado del negocio

**Criterios de aceptación:**
```gherkin
Scenario: Notificación de error en registro de pedido
  Given que el usuario registra un pedido con información incompleta o inválida
  When el sistema detecta el error
  Then se notifica el motivo específico al usuario a través de la API en formato JSON

Scenario: Notificación de error en transición de estado de tarea
  Given que el usuario intenta iniciar una tarea en estado incorrecto
  When el sistema rechaza la operación
  Then se informa el motivo específico al usuario por API
```

## Cuadro comparativo



| HU original                                                                                  | HU refinada por la instrucción                                                                                                                                | Diferencias detectadas                                                                                                                                                |
|----------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HU-001: Procesar pedido de cocina                                                            | HU-001A: Procesar pedido de cocina (Descomposición y Asignación)<br>HU-001B: Validaciones de Pedido                                                           | Se separaron la lógica de descomposición y las validaciones en dos HU. Se añadió la posibilidad de estaciones configurables y la validación de atributos mínimos.    |
| HU-002: Consultar tareas por estación                                                        | HU-002: Consultar tareas pendientes por estación<br>HU-004: Consultar historial de tareas completadas por estación                                            | Se dividió la consulta en tareas pendientes e historial de tareas completadas. Se especificaron campos mínimos y periodo de retención del historial.                 |
| HU-003: Ejecutar tarea de preparación                                                        | HU-003: Iniciar y completar tareas de preparación<br>HU-005: Estado agregado del pedido según tareas                                                          | Se separó el control de estados individuales de tareas y el estado agregado del pedido. Se detallaron transiciones automáticas y condiciones de completitud.         |
| _No existente en las originales_                                                             | HU-006: Procesar facturación de pedido completado                                                                                                             | Se añadió una HU específica para facturación y generación de eventos Outbox, alineada con el flujo crítico pero no presente en las HU originales.                   |
| _No existente en las originales_                                                             | HU-007: Notificaciones de errores y validaciones                                                                                                              | Se añadió una HU para notificaciones de errores, detallando el formato y canal de comunicación, aspecto que estaba sólo implícito en las originales.                |
