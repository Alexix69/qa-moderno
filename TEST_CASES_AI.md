### HU-001A: Procesar pedido de cocina (Descomposición y Asignación)

#### <a id="tc-01"></a>TC-01: Registro exitoso de una orden con datos válidos

```
Caso de prueba: Registro exitoso de una orden con datos válidos
  Dado que un usuario autenticado cuenta con un número de mesa válido
  Y la solicitud contiene al menos un producto válido
  Cuando el usuario envía una solicitud para crear una orden
  Entonces la orden es registrada en la base de datos
  Y la orden se descompone en tareas según la estación de preparación configurada
  Y cada tarea queda registrada en estado PENDING
```

#### <a id="tc-02"></a>TC-02: Intento de registrar una orden sin productos

```
Caso de prueba: Intento de registrar una orden sin productos
  Dado que un usuario autenticado intenta crear una orden
  Y la solicitud no contiene productos
  Cuando el usuario envía la solicitud de creación de orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que se requiere al menos un producto
```

#### <a id="tc-03"></a>TC-03: Registro de orden con número de mesa inválido

```
Caso de prueba: Registro de orden con número de mesa inválido
  Dado que un usuario autenticado intenta crear una orden
  Y la solicitud contiene un número de mesa que no existe en el sistema
  Cuando el usuario envía la solicitud de creación de orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que el número de mesa es inválido
```

#### <a id="tc-04"></a>TC-04: Registro de orden con longitud máxima permitida en campos

```
Caso de prueba: Registro de orden con longitud máxima permitida en campos
  Dado que un usuario autenticado llena los campos de la orden con la longitud máxima permitida
  Cuando el usuario crea la orden
  Entonces el sistema acepta la orden y la registra correctamente
```

#### <a id="tc-05"></a>TC-05: Registro de orden con datos que exceden los límites permitidos

```
Caso de prueba: Registro de orden con datos que exceden los límites permitidos
  Dado que un usuario autenticado llena los campos de la orden excediendo la longitud máxima permitida
  Cuando el usuario intenta crear la orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que los datos exceden los límites permitidos
```

---

### HU-001B: Validaciones de Pedido

#### <a id="tc-06"></a>TC-06: Detección de atributos mínimos faltantes en la solicitud

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

#### <a id="tc-07"></a>TC-07: Consulta exitosa de tareas pendientes por estación

```
Caso de prueba: Consulta exitosa de tareas pendientes por estación
  Dado que un usuario autenticado desea consultar las tareas pendientes
  Y existen tareas pendientes para la estación solicitada
  Cuando el usuario consulta las tareas pendientes por estación
  Entonces el sistema retorna la lista de tareas en estado PENDING para esa estación
```

#### <a id="tc-08"></a>TC-08: Consulta de tareas pendientes cuando no existen tareas

```
Caso de prueba: Consulta de tareas pendientes cuando no existen tareas
  Dado que un usuario autenticado consulta las tareas pendientes por estación
  Y no existen tareas en estado PENDING para esa estación
  Cuando el usuario realiza la consulta
  Entonces el sistema retorna una lista vacía
```

---

### HU-004: Consultar historial de tareas completadas por estación

#### <a id="tc-09"></a>TC-09: Consulta de historial de tareas completadas por estación

```
Caso de prueba: Consulta de historial de tareas completadas por estación
  Dado que un usuario autenticado desea consultar el historial de tareas
  Y existen tareas completadas para la estación solicitada dentro del periodo de retención
  Cuando el usuario consulta el historial de tareas por estación
  Entonces el sistema retorna la lista de tareas en estado COMPLETED dentro del periodo de retención
```

#### <a id="tc-10"></a>TC-10: Consulta de historial fuera del periodo de retención

```
Caso de prueba: Consulta de historial fuera del periodo de retención
  Dado que un usuario autenticado consulta el historial de tareas completadas por estación
  Y las tareas completadas están fuera del periodo de retención configurado
  Cuando el usuario realiza la consulta
  Entonces el sistema no retorna dichas tareas
```

---

### HU-003: Iniciar y completar tareas de preparación

#### <a id="tc-11"></a>TC-11: Inicio exitoso de tarea en estado PENDING

```
Caso de prueba: Inicio exitoso de tarea en estado PENDING
  Dado que existe una tarea en estado PENDING
  Cuando un usuario autenticado inicia la preparación de la tarea
  Entonces la tarea cambia a estado IN_PREPARATION
```

#### <a id="tc-12"></a>TC-12: Intento de iniciar tarea que no está en estado PENDING

```
Caso de prueba: Intento de iniciar tarea que no está en estado PENDING
  Dado que existe una tarea en estado IN_PREPARATION o COMPLETED
  Cuando un usuario autenticado intenta iniciar la preparación de la tarea
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando transición de estado inválida
```

#### <a id="tc-13"></a>TC-13: Completado exitoso de tarea en estado IN_PREPARATION

```
Caso de prueba: Completado exitoso de tarea en estado IN_PREPARATION
  Dado que una tarea se encuentra en estado IN_PREPARATION
  Cuando un usuario autenticado marca la tarea como completada
  Entonces la tarea cambia a estado COMPLETED
```

#### <a id="tc-14"></a>TC-14: Intento de completar tarea que no está en estado IN_PREPARATION

```
Caso de prueba: Intento de completar tarea que no está en estado IN_PREPARATION
  Dado que una tarea está en estado PENDING o COMPLETED
  Cuando un usuario autenticado intenta marcar la tarea como completada
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando transición de estado inválida
```

#### <a id="tc-15"></a>TC-15: Fallo durante el proceso asíncrono de preparación

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

#### <a id="tc-16"></a>TC-16: El pedido pasa a COMPLETED cuando todas las tareas están completadas

```
Caso de prueba: El pedido pasa a COMPLETED cuando todas las tareas están completadas
  Dado que una orden tiene múltiples tareas
  Y todas las tareas de la orden están en estado COMPLETED
  Cuando se verifica el estado de la orden
  Entonces la orden cambia a estado COMPLETED
```

#### <a id="tc-17"></a>TC-17: El pedido permanece IN_PROGRESS si al menos una tarea no está completada

```
Caso de prueba: El pedido permanece IN_PROGRESS si al menos una tarea no está completada
  Dado que una orden tiene múltiples tareas
  Y al menos una tarea no está en estado COMPLETED
  Cuando se verifica el estado de la orden
  Entonces la orden permanece en estado IN_PROGRESS
```

---

### HU-006: Procesar facturación de pedido completado

#### <a id="tc-18"></a>TC-18: Facturación exitosa de pedido completado

```
Caso de prueba: Facturación exitosa de pedido completado
  Dado que una orden se encuentra en estado COMPLETED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema valida el estado de la orden
  Y genera un evento de facturación mediante el patrón Outbox
  Y la orden cambia a estado INVOICED
```

#### <a id="tc-19"></a>TC-19: Intento de facturación de pedido no completado

```
Caso de prueba: Intento de facturación de pedido no completado
  Dado que una orden no está en estado COMPLETED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema rechaza la solicitud
  Y se genera una notificación de error indicando que la orden no puede ser facturada
```

#### <a id="tc-20"></a>TC-20: Intento de facturación de pedido ya facturado

```
Caso de prueba: Intento de facturación de pedido ya facturado
  Dado que una orden ya se encuentra en estado INVOICED
  Cuando un usuario autenticado solicita la facturación de la orden
  Entonces el sistema rechaza la solicitud o ignora el evento
  Y se genera una notificación de error indicando que la orden ya fue facturada
```

---

### HU-007: Notificaciones de errores y validaciones

#### <a id="tc-21"></a>TC-21: Notificación de error en transición de estado inválida

```
Caso de prueba: Notificación de error en transición de estado inválida
  Dado que un usuario autenticado intenta realizar una transición de estado no permitida en una tarea o pedido
  Cuando el sistema detecta la acción
  Entonces el sistema rechaza la acción
  Y retorna una notificación de error con el formato y canal establecidos
```

#### <a id="tc-22"></a>TC-22: Notificación de error por duplicidad de usuario

```
Caso de prueba: Notificación de error por duplicidad de usuario
  Dado que un usuario intenta registrarse con un email o username ya existente
  Cuando el sistema procesa la solicitud de registro
  Entonces el sistema rechaza la solicitud
  Y retorna una notificación de error indicando duplicidad de usuario
```


## Análisis de Casos de Prueba vs Implementación Real

| ID | Caso de Prueba generado por la instrucción | Ajuste del probador | Razón del ajuste |
|---|---|---|---|
| [TC-01](#tc-01) | Registro exitoso de una orden con datos válidos | Mismo caso, con la verificación adicional de que la orden arranca en estado **CREATED** | La IA no contempló que toda orden nueva comienza en `CREATED`. Al revisar el código se ve claramente que ese es el estado de partida, no `PENDING`. |
| [TC-03](#tc-03) | Registro de orden con número de mesa inválido (que no existe en el sistema) | Registro de orden con número de mesa **nulo o vacío** | El sistema no tiene un catálogo de mesas; solo revisa que el campo llegue con algún valor. Nunca valida si esa mesa existe o no. |
| [TC-04](#tc-04) | Registro de orden con longitud máxima permitida en campos |  | No aplica |
| [TC-05](#tc-05) | Registro de orden con datos que exceden los límites permitidos |  | Similar al anterior, no aplica |
| [TC-07](#tc-07) | Consulta exitosa de tareas pendientes por estación (solo PENDING) | Consulta exitosa de tareas por estación **con filtro opcional de estado** (no solo PENDING) | El endpoint admite cualquier estado de tarea como parámetro opcional, no únicamente PENDING. La IA asumió un filtro fijo que no existe así en el código. |
| [TC-09](#tc-09) | Consulta de historial de tareas completadas por estación con periodo de retención | **Eliminar o replantear** — no existe historial ni periodo de retención | En el código no hay endpoint de historial ni restricción temporal. Se puede filtrar por estado COMPLETED, pero sin límite de fechas. |
| [TC-10](#tc-10) | Consulta de historial fuera del periodo de retención | **Eliminar este caso** | El concepto de periodo de retención no está implementado en ninguna parte del código. |
| [TC-11](#tc-11) | Inicio exitoso de tarea en estado PENDING | Al iniciar la tarea, también se verifica que **se guarde la fecha de inicio (`startedAt`) y que la orden pase a IN_PROGRESS** | La instrucción no mencionó esos efectos colaterales que sí ocurren en el código: se registra el timestamp y se actualiza el estado de la orden. |
| [TC-13](#tc-13) | Completado exitoso de tarea en estado IN_PREPARATION (usuario marca como completada) | El completado **lo ejecuta el sistema automáticamente** tras el comando asíncrono; el usuario no interviene | No existe un endpoint para completar tareas a mano. El dispatcher asíncrono lo hace por sí solo una vez que termina el comando. |
| [TC-14](#tc-14) | Intento de completar tarea que no está en estado IN_PREPARATION | **Eliminar o convertir en test unitario** de lógica de dominio | Al no existir endpoint REST para completar tareas manualmente, este escenario solo tiene sentido verificarlo en una prueba unitaria de dominio. |
| [TC-15](#tc-15) | Fallo durante el proceso asíncrono de preparación | Fallo durante el proceso asíncrono **sin reintentos y sin cambio de estado** | El proceso es fire-and-forget: ante cualquier error solo se deja traza en el log, sin persistir el fallo ni notificar a nadie. |
| [TC-16](#tc-16) | El pedido pasa a COMPLETED cuando todas las tareas están completadas | | No aplica |
| [TC-20](#tc-20) | Intento de facturación de pedido ya facturado (rechaza la solicitud o ignora) | Cuando la orden ya está facturada, **el sistema simplemente no hace nada** — no lanza error ni devuelve advertencia | El código ignora la solicitud en silencio, muy distinto a lo que la IA propuso (rechazar con error). |
| [TC-19](#tc-19) | Intento de facturación de pedido no completado (orden no está en COMPLETED) | Intento de facturación **con la orden en estado CREATED, IN_PROGRESS o INVOICED** | Hay que ser precisos con qué estados activan el rechazo; decir solo "no completado" es demasiado vago para el test. |
| [TC-21](#tc-21) | Notificación de error en transición de estado inválida | Se añade la verificación del **formato exacto de la respuesta de error (`ErrorResponse`) y el código HTTP 400** | La instrucción no especificó cómo luce el cuerpo del error ni que el código de estado debería ser 400, ambos definidos en el manejador global de excepciones. |
| [TC-22](#tc-22) | Notificación de error por duplicidad de usuario | Se aclara que la respuesta debe devolver **HTTP 409 CONFLICT** | La instrucción dejó abierto el código de estado. El código usa específicamente HTTP 409 para este escenario. |