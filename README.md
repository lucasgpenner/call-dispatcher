# call-dispatcher

## Consigna

Existe un call center donde hay 3 tipos de empleados: operador, supervisor y director. El proceso de la atención de una llamada 
telefónica en primera instancia debe ser atendida por un operador, si no hay ninguno libre debe ser atendida por un supervisor, y de no 
haber tampoco supervisores libres debe ser atendida por un director.

## Requerimientos

- Diseñar el modelado de clases y diagramas UML necesarios para documentar y comunicar el diseño.

- Debe existir una clase Dispatcher encargada de manejar las llamadas, y debe contener el método dispatchCall para que las asigne a los empleados disponibles.

- La clase Dispatcher debe tener la capacidad de poder procesar 10 llamadas al mismo tiempo (de modo concurrente).

- Cada llamada puede durar un tiempo aleatorio entre 5 y 10 segundos

- Debe tener un test unitario donde lleguen 10 llamadas.


## Extras/Plus


- Dar alguna solución sobre qué pasa con una llamada cuando no hay ningún empleado libre.

- Dar alguna solución sobre qué pasa con una llamada cuando entran más de 10 llamadas concurrentes.

- Agregar los tests unitarios que se crean convenientes.

- Agregar documentación de código.


## Solución

- Se decidió incorporar el patrón “Producer-Consumer” en donde el producer, vaya guardando las llamadas dentro de una cola ConcurrentLinkedDeque ilimitada. Esta permite obtener la primer llamada en la cola, como así también, si no hay empleados disponibles para atender esta llamada, permite volver a almacenarla en la primera posición.
- Las colas utilizadas por el consumer, están implementadas con LinkedBlockingQueue para poder aprovechar de estas sus capacidades y asi, pasar de un empleado a otro si su cola esta llena.
- La configuración del tiempo de llamada, se encuentra dentro de un archivo de properties para poder separar la configuración del código.
- La implementación permite el acceso concurrente de llamadas, separando la cantidad de llamadas con la cantidad de llamadas concurrentes que se puedan estar manejando.
- En caso de que no haber empleados disponibles para atender la llamada, esta vuelve a la cola de entrada, tratando de recuperar la primer posición nuevamente hasta que pueda ser asignada a un empleado.
- El problema que puede presentarse cuando las llamadas concurrentes superan a los empleados asignados, es que por mas que la llamada vuelva a la primera posición de la cola, otra llamada posterior pueda ser tomada y por consiguiente, pierde la prioridad respecto a esta.


#### Inicialización

```
$ git clone https://github.com/lucasgpenner/call-dispatcher.git
$ mvn clean install
```


#### Test

```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0

[INFO] 
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ call-dispatcher ---
[INFO] Building jar: /home/lpenner/workspace/workspace-sts-3.9.0.RELEASE/almundo/call-dispatcher/target/call-dispatcher-0.0.1-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:49 min
[INFO] ------------------------------------------------------------------------
```


#### Demo

```
$ mvn exec:java -Dexec.mainClass="ar.com.lpenner.App"
```

```
Bienvenido a la aplicacion call-dispatcher. Por favor, siga las instrucciones a continuación.
Ingrese la cantidad de llamadas que desea realizar: 10
Ingrese la cantidad de llamadas concurrentes: 10
Ingrese la cantidad de operadores: 5
Ingrese la cantidad de supervisores: 2
Ingrese la cantidad de directores: 1
```


#### Modelado

![alt tag](https://github.com/lucasgpenner/call-dispatcher/blob/master/etc/architecture_diagram.png)


#### Diagrama de secuencia

![alt tag](https://github.com/lucasgpenner/call-dispatcher/blob/master/etc/secuence_diagram.png)
