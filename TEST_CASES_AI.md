### HU-001A: Procesar pedido de cocina (Descomposición y Asignación)

#### Caso de prueba: Registro exitoso de una orden con datos válidos

```
Caso de prueba: Registro exitoso de una orden con datos válidos
  Dado que un usuario autenticado cuenta con un número de mesa válido
  Y la solicitud contiene al menos un producto válido
  Cuando el usuario envía una solicitud para crear una orden
  Entonces la orden es registrada en la base de datos
  Y la orden se descompone en tareas según la estación de preparación configurada
  Y cada tarea queda registrada en estado PENDING
```

#### Caso de prueba: Intento de registrar una orden sin productos

```
Caso de prueba: Intento de registrar una orden sin productos
  Dado que un usuario autenticado intenta crear una orden
  Y la solicitud no contiene productos
  Cuando el usuario envía la solicitud de creación de orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que se requiere al menos un producto
```

#### Caso de prueba: Registro de orden con número de mesa inválido

```
Caso de prueba: Registro de orden con número de mesa inválido
  Dado que un usuario autenticado intenta crear una orden
  Y la solicitud contiene un número de mesa que no existe en el sistema
  Cuando el usuario envía la solicitud de creación de orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que el número de mesa es inválido
```

#### Caso de prueba: Registro de orden con longitud máxima permitida en campos

```
Caso de prueba: Registro de orden con longitud máxima permitida en campos
  Dado que un usuario autenticado llena los campos de la orden con la longitud máxima permitida
  Cuando el usuario crea la orden
  Entonces el sistema acepta la orden y la registra correctamente
```

#### Caso de prueba: Registro de orden con datos que exceden los límites permitidos

```
Caso de prueba: Registro de orden con datos que exceden los límites permitidos
  Dado que un usuario autenticado llena los campos de la orden excediendo la longitud máxima permitida
  Cuando el usuario intenta crear la orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que los datos exceden los límites permitidos
```

---

### HU-001B: Validaciones de Pedido

#### Caso de prueba: Detección de atributos mínimos faltantes en la solicitud

```
Caso de prueba: Detección de atributos mínimos faltantes en la solicitud
  Dado que un usuario autenticado intenta crear una orden
  Y la solicitud omite el campo obligatorio de número de mesa
  Cuando el usuario envía la solicitud de creación de orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando los campos obligatorios faltantes
```

---

### HU-002: Consultar tareas pendientes por estación

#### Caso de prueba: Consulta exitosa de tareas pendientes por estación

```
Caso de prueba: Consulta exitosa de tareas pendientes por estación
  Dado que un usuario autenticado desea consultar las tareas pendientes
  Y existen tareas pendientes para la estación solicitada
  Cuando el usuario consulta las tareas pendientes por estación
  Entonces el sistema retorna la lista de tareas en estado PENDING para esa estación
```

#### Caso de prueba: Consulta de tareas pendientes cuando no existen tareas

```
Caso de prueba: Consulta de tareas pendientes cuando no existen tareas
  Dado que un usuario autenticado consulta las tareas pendientes por estación
  Y no existen tareas en estado PENDING para esa estación
  Cuando el usuario realiza la consulta
  Entonces el sistema retorna una lista vacía
```

---

### HU-004: Consultar historial de tareas completadas por estación

#### Caso de prueba: Consulta de historial de tareas completadas por estación

```
Caso de prueba: Consulta de historial de tareas completadas por estación
  Dado que un usuario autenticado desea consultar el historial de tareas
  Y existen tareas completadas para la estación solicitada dentro del periodo de retención
  Cuando el usuario consulta el historial de tareas por estación
  Entonces el sistema retorna la lista de tareas en estado COMPLETED dentro del periodo de retención
```

#### Caso de prueba: Consulta de historial fuera del periodo de retención

```
Caso de prueba: Consulta de historial fuera del periodo de retención
  Dado que un usuario autenticado consulta el historial de tareas completadas por estación
  Y las tareas completadas están fuera del periodo de retención configurado
  Cuando el usuario realiza la consulta
  Entonces el sistema no retorna dichas tareas
```

---

### HU-003: Iniciar y completar tareas de preparación

#### Caso de prueba: Inicio exitoso de tarea en estado PENDING

```
Caso de prueba: Inicio exitoso de tarea en estado PENDING
  Dado que existe una tarea en estado PENDING
  Cuando un usuario autenticado inicia la preparación de la tarea
  Entonces la tarea cambia a estado IN_PREPARATION
```

#### Caso de prueba: Intento de iniciar tarea que no está en estado PENDING

```
Caso de prueba: Intento de iniciar tarea que no está en estado PENDING
  Dado que existe una tarea en estado IN_PREPARATION o COMPLETED
  Cuando un usuario autenticado intenta iniciar la preparación de la tarea
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando transición de estado inválida
```

#### Caso de prueba: Completado exitoso de tarea en estado IN_PREPARATION

```
Caso de prueba: Completado exitoso de tarea en estado IN_PREPARATION
  Dado que una tarea se encuentra en estado IN_PREPARATION
  Cuando un usuario autenticado marca la tarea como completada
  Entonces la tarea cambia a estado COMPLETED
```

#### Caso de prueba: Intento de completar tarea que no está en estado IN_PREPARATION

```
Caso de prueba: Intento de completar tarea que no está en estado IN_PREPARATION
  Dado que una tarea está en estado PENDING o COMPLETED
  Cuando un usuario autenticado intenta marcar la tarea como completada
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando transición de estado inválida
```

#### Caso de prueba: Fallo durante el proceso asíncrono de preparación

```
Caso de prueba: Fallo durante el proceso asíncrono de preparación
  Dado que una tarea está en estado IN_PREPARATION
  Y ocurre un error durante la ejecución asíncrona
  Cuando el sistema intenta completar la tarea
  Entonces la tarea permanece en estado IN_PREPARATION
  Y el evento de error es registrado en los logs
```

---

### HU-005: Estado agregado del pedido según tareas

#### Caso de prueba: El pedido pasa a COMPLETED cuando todas las tareas están completadas

```
Caso de prueba: El pedido pasa a COMPLETED cuando todas las tareas están completadas
  Dado que una orden tiene múltiples tareas
  Y todas las tareas de la orden están en estado COMPLETED
  Cuando se verifica el estado de la orden
  Entonces la orden cambia a estado COMPLETED
```

#### Caso de prueba: El pedido permanece IN_PROGRESS si al menos una tarea no está completada

```
Caso de prueba: El pedido permanece IN_PROGRESS si al menos una tarea no está completada
  Dado que una orden tiene múltiples tareas
  Y al menos una tarea no está en estado COMPLETED
  Cuando se verifica el estado de la orden
  Entonces la orden permanece en estado IN_PROGRESS
```

---

### HU-006: Procesar facturación de pedido completado

#### Caso de prueba: Facturación exitosa de pedido completado

```
Caso de prueba: Facturación exitosa de pedido completado
  Dado que una orden se encuentra en estado COMPLETED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema valida el estado de la orden
  Y genera un evento de facturación mediante el patrón Outbox
  Y la orden cambia a estado INVOICED
```

#### Caso de prueba: Intento de facturación de pedido no completado

```
Caso de prueba: Intento de facturación de pedido no completado
  Dado que una orden no está en estado COMPLETED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que la orden no puede ser facturada
```

#### Caso de prueba: Intento de facturación de pedido ya facturado

```
Caso de prueba: Intento de facturación de pedido ya facturado
  Dado que una orden ya se encuentra en estado INVOICED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema rechaza la solicitud o ignora el evento
  Y se genera una notificación de error indicando que la orden ya fue facturada
```

---

### HU-007: Notificaciones de errores y validaciones

#### Caso de prueba: Notificación de error en transición de estado inválida

```
Caso de prueba: Notificación de error en transición de estado inválida
  Dado que un usuario autenticado intenta realizar una transición de estado no permitida en una tarea o pedido
  Cuando el sistema detecta la acción
  Entonces el sistema rechaza la acción
  Y retorna una notificación de error con el formato y canal establecidos
```

#### Caso de prueba: Notificación de error por duplicidad de usuario

```
Caso de prueba: Notificación de error por duplicidad de usuario
  Dado que un usuario intenta registrarse con un email o username ya existente
  Cuando el sistema procesa la solicitud de registro
  Entonces el sistema rechaza la solicitud
  Y retorna una notificación de error indicando duplicidad de usuario
```


## Análisis de Casos de Prueba vs Implementación Real

| ID | Caso de Prueba generado por la instrucción | Ajuste del realizado por el probador | ¿Por qué se ajustó? |
|---|---|---|---|
| TC-01 | Registro exitoso de una orden con datos válidos | Registro exitoso de una orden con datos válidos **y verificar estado inicial CREATED** | La instrucción omitió que la orden inicia en estado `CREATED` (no `PENDING`). El código en Order.java demuestra que toda orden nueva tiene `OrderStatus.CREATED`. |
| TC-02 | Registro de orden con número de mesa inválido (que no existe en el sistema) | Registro de orden con número de mesa **null o vacío** | El código NO valida existencia de mesas; solo valida que `tableNumber` no sea null ni vacío (Order.java línea 42). No existe catálogo de mesas. |
| TC-03 | Registro de orden con longitud máxima permitida en campos |  | No aplica |
| TC-04 | Registro de orden con datos que exceden los límites permitidos |  | Similar a TC-03 |
| TC-05 | Consulta exitosa de tareas pendientes por estación (solo PENDING) | Consulta exitosa de tareas por estación **con filtro opcional de status** | El endpoint `GET /api/tasks/station/{station}?status=` acepta cualquier `TaskStatus` como parámetro opcional, no solo PENDING. |
| TC-06 | Consulta de historial de tareas completadas por estación con periodo de retención | **Eliminar o replantear caso de prueba** | NO existe endpoint específico de historial ni funcionalidad de periodo de retención. El endpoint existente permite filtrar por status=COMPLETED pero sin límite temporal. |
| TC-07 | Consulta de historial fuera del periodo de retención | **Eliminar caso de prueba** | No existe implementación de periodo de retención en el código. |
| TC-08 | Inicio exitoso de tarea en estado PENDING | Inicio exitoso de tarea en estado PENDING **verificando que se asigne `startedAt` y la orden pase a IN_PROGRESS** | La instrucción omitió que `Task.start()` asigna timestamp `startedAt` y que `StartTaskPreparationUseCase` actualiza la orden a `IN_PROGRESS`. |
| TC-09 | Completado exitoso de tarea en estado IN_PREPARATION (usuario marca como completada) | Completado **automático** de tarea tras ejecución de comando async | El completado NO es manual. `ReactorAsyncCommandDispatcher` completa automáticamente la tarea tras ejecutar el comando. No existe endpoint para completar manualmente. |
| TC-10 | Intento de completar tarea que no está en IN_PREPARATION | **Eliminar o replantear como test unitario de dominio** | No existe endpoint REST para completar tareas manualmente; el completado es interno/automático. Solo aplica como validación de dominio en `Task.complete()`. |
| TC-11 | Fallo durante el proceso asíncrono de preparación | Fallo durante el proceso asíncrono de preparación **sin reintentos ni actualización de estado** | La instrucción no especificó que es fire-and-forget sin política de reintentos. El error solo se loguea a stderr sin persistir ni notificar. |
| TC-12 | El pedido pasa a COMPLETED cuando todas las tareas están completadas | | No aplica |
| TC-13 | Intento de facturación de pedido ya facturado (rechaza la solicitud o ignora) | Intento de facturación de pedido ya facturado **retorna silenciosamente sin error** | No genera notificación de error, simplemente no hace nada. |
| TC-14 | Intento de facturación de pedido no completado (orden no está en COMPLETED) | Intento de facturación de pedido **en estado distinto a COMPLETED** con verificación de estados CREATED o IN_PROGRESS | Especificar que aplica a estados `CREATED`, `IN_PROGRESS` y `INVOICED`. |
| TC-15 | Notificación de error en transición de estado inválida | Notificación de error en transición de estado inválida **con código HTTP 400 y formato ErrorResponse** | La instrucción no especificó el formato de respuesta (`ErrorResponse` con message, details, status) ni el código HTTP 400 definido en `GlobalExceptionHandler`. |
| TC-16 | Notificación de error por duplicidad de usuario | Notificación de error por duplicidad de usuario **con código HTTP 409 CONFLICT** | La instrucción omitió el código HTTP específico. El código usa HTTP 409 (CONFLICT) |