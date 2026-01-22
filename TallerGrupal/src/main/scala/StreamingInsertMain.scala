import cats.effect.{IO, IOApp}
import cats.implicits.*
import fs2.io.file.{Files, Path}
import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*
import fs2.data.text.utf8.*
import doobie.implicits.*
import models.Estudiante
import dao.EstudiantesDao
import config.Database

object StreamingInsertMain extends IOApp.Simple {
  // Ruta al archivo CSV definida en los recursos
  private val path2DataFile2 = "src/main/resources/data/estudiantes.csv"

  // Decodificador automático para convertir filas CSV a objetos Estudiante
  given CsvRowDecoder[Estudiante, String] = deriveCsvRowDecoder

  /**
   * Crea un flujo (Stream) que lee el archivo y lo decodifica fila a fila
   */
  private def estudianteStream: fs2.Stream[IO, Estudiante] =
    Files[IO]
      .readAll(Path(path2DataFile2))
      .through(decodeUsingHeaders[Estudiante](','))

  /**
   * Ejecución principal del programa
   */
  override def run: IO[Unit] = {
    Database.transactor.use { xa =>
      // Primero recreamos la tabla
      EstudiantesDao.recreateTable
        .transact(xa)
        .flatMap(_ => IO.println("Tabla recreada exitosamente"))
        // Luego continuamos con el proceso de inserción
        .flatMap(_ =>
          estudianteStream
            .evalMap { est =>
              EstudiantesDao.insert(est)
                .transact(xa)
                .flatMap(_ => IO.println(s"Fila procesada e insertada: ${est.nombre}"))
            }
            .compile
            .drain
        )
        .flatMap(_ => IO.println("\nObteniendo todos los estudiantes de la base de datos:"))
        .flatMap(_ => EstudiantesDao.getAllEstudiantes.transact(xa))
        .flatMap { estudiantes =>
          estudiantes.traverse_(est =>
            IO.println(s"${est.nombre}, ${est.edad}, ${est.calificacion}, ${est.genero}")
          )
        }
        .flatMap(_ => IO.println("\nProceso completado con éxito."))
        .handleErrorWith(e => IO.println(s"Error crítico durante la ejecución: ${e.getMessage}"))
    }
  }
}