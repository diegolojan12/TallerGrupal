# Sistema de Gestión de Estudiantes con Scala

Este proyecto implementa un sistema de lectura y almacenamiento de datos de estudiantes utilizando Scala, FS2 para streaming de datos, Doobie para interacción con bases de datos MySQL, y Cats Effect para programación funcional con efectos.

## Descripción del Proyecto

El sistema permite:
1. Leer datos de estudiantes desde un archivo CSV
2. Insertar los datos de forma streaming en una base de datos MySQL
3. Recuperar todos los registros de estudiantes almacenados

## Tecnologías Utilizadas

- **Scala 3.3.7**: Lenguaje de programación principal
- **FS2**: Biblioteca para streaming funcional
- **Doobie**: Capa de acceso a base de datos funcional
- **Cats Effect**: Biblioteca para efectos funcionales (IO)
- **MySQL**: Sistema de gestión de base de datos
- **HikariCP**: Pool de conexiones a base de datos

## Estructura del Proyecto

```
src/
├── main/
│   ├── scala/
│   │   ├── StreamingInsertMain.scala  # Programa principal
│   │   ├── models/
│   │   │   └── Estudiante.scala       # Modelo de datos
│   │   ├── dao/
│   │   │   └── EstudiantesDao.scala   # Capa de acceso a datos
│   │   └── config/
│   │       └── Database.scala         # Configuración de BD
│   └── resources/
│       ├── data/
│       │   └── estudiantes.csv        # Datos de entrada
│       └── application.conf           # Configuración de la aplicación
```

## Configuración de Base de Datos

### 1. Crear la tabla en MySQL

```sql
CREATE DATABASE IF NOT EXISTS estudiantes_db;

USE estudiantes_db;

CREATE TABLE estudiante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    calificacion DOUBLE NOT NULL,
    genero VARCHAR(1) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Configurar conexión (application.conf)

```hocon
db {
  driver = "com.mysql.cj.jdbc.Driver"
  url = "jdbc:mysql://localhost:3306/estudiantes_db"
  user = "tu_usuario"
  password = "tu_contraseña"
}
```

## Datos del CSV

El archivo `estudiantes.csv` contiene los siguientes datos:

```csv
nombre,edad,calificacion,genero
Andrés,10,20,M
Ana,11,19,F
Luis,9,18,M
Cecilia,9,18,F
Katy,11,15,F
Jorge,8,17,M
Rosario,11,18,F
Nieves,10,20,F
Pablo,9,19,M
Daniel,10,20,M
```

## Dependencias (build.sbt)

```scala
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "TallerGrupal",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.2",
      "co.fs2" %% "fs2-core" % "3.9.3",
      "co.fs2" %% "fs2-io" % "3.9.3",
      "org.gnieh" %% "fs2-data-csv" % "1.9.1",
      "org.gnieh" %% "fs2-data-csv-generic" % "1.9.1",
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "com.typesafe.play" %% "play-json" % "2.10.4",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC11",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC11",
      "com.mysql" % "mysql-connector-j" % "9.1.0",
      "com.typesafe" % "config" % "1.4.2",
      "org.slf4j" % "slf4j-simple" % "2.0.16"
    )
  )
```

## Ejecución del Proyecto

### Prerrequisitos

1. Tener instalado JDK 11 o superior
2. Tener instalado SBT (Scala Build Tool)
3. Tener MySQL ejecutándose
4. Haber creado la base de datos y tabla

### Pasos para ejecutar

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd <nombre-del-proyecto>

# 2. Compilar el proyecto
sbt compile

# 3. Ejecutar el programa
sbt run
```

## Funcionalidades Implementadas

### ✅ Punto 1: Archivo CSV
Archivo `estudiantes.csv` con los datos de 10 estudiantes

### ✅ Punto 2: Tabla MySQL
Script SQL para crear la tabla `estudiante` con los campos apropiados

### ✅ Punto 3: Inserción desde CSV
Programa que lee el CSV usando streaming (FS2) e inserta los datos fila por fila en la base de datos

### ✅ Punto 4: Obtención de registros
Funcionalidad para recuperar todos los estudiantes almacenados en la base de datos y mostrarlos en consola

## Componentes Principales

### StreamingInsertMain.scala
Punto de entrada de la aplicación que:
- Lee el archivo CSV usando FS2
- Inserta cada estudiante en la base de datos
- Obtiene y muestra todos los estudiantes almacenados

### EstudiantesDao.scala
Capa de acceso a datos con métodos:
- `insert`: Inserta un estudiante
- `insertAll`: Inserta múltiples estudiantes
- `getAllEstudiantes`: Recupera todos los estudiantes

### Estudiante.scala
Case class que representa el modelo de datos:
```scala
case class Estudiante(
  nombre: String,
  edad: Int,
  calificacion: Double,
  genero: String
)
```

### Database.scala
Configuración del transactor de Doobie usando HikariCP para pool de conexiones

## Características Técnicas

- **Streaming funcional**: Procesa el CSV sin cargar todo en memoria
- **Manejo de errores**: Implementa manejo robusto de errores con IO
- **Pool de conexiones**: Usa HikariCP para gestión eficiente de conexiones
- **Type-safe**: Aprovecha el sistema de tipos de Scala para mayor seguridad
- **Programación funcional**: Usa Cats Effect para efectos puros y componibles

## Salida Esperada

```
Fila procesada e insertada: Andrés
Fila procesada e insertada: Ana
Fila procesada e insertada: Luis
Fila procesada e insertada: Cecilia
Fila procesada e insertada: Katy
Fila procesada e insertada: Jorge
Fila procesada e insertada: Rosario
Fila procesada e insertada: Nieves
Fila procesada e insertada: Pablo
Fila procesada e insertada: Daniel

Obteniendo todos los estudiantes de la base de datos:
Andrés, 10, 20.0, M
Ana, 11, 19.0, F
Luis, 9, 18.0, M
Cecilia, 9, 18.0, F
Katy, 11, 15.0, F
Jorge, 8, 17.0, M
Rosario, 11, 18.0, F
Nieves, 10, 20.0, F
Pablo, 9, 19.0, M
Daniel, 10, 20.0, M

Proceso completado con éxito.
```

## Autores

  DIEGO SEBASTIAN LOJÁN SISALIMA (LIDER) 

  PABLO SEBASTIÁN ORDOÑEZ CARRIÓN 
  
  EDUARDO SEBASTIÁN PEÑARRIETA CHÁVEZ  

  CARLOS DANIEL MEDINA LARREATEGUI 

## Licencia

Este proyecto es de uso académico.