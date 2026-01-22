package dao

import cats.effect.IO
import cats.implicits.*
import config.Database
import doobie.*
import doobie.implicits.*
import models.Estudiante

object EstudiantesDao {

  // Elimina la tabla si existe
  def dropTable: ConnectionIO[Int] = {
    sql"""DROP TABLE IF EXISTS estudiante""".update.run
  }

  // Crea la tabla
  def createTable: ConnectionIO[Int] = {
    sql"""
       CREATE TABLE estudiante (
         id SERIAL PRIMARY KEY,
         nombre VARCHAR(255) NOT NULL,
         edad INTEGER NOT NULL,
         calificacion DOUBLE PRECISION NOT NULL,
         genero VARCHAR(1) NOT NULL
       )
     """.update.run
  }

  // Método conveniente que elimina y recrea la tabla
  def recreateTable: ConnectionIO[Unit] = {
    for {
      _ <- dropTable
      _ <- createTable
    } yield ()
  }

  def insert(estudiante: Estudiante): ConnectionIO[Int] = {
    sql"""
       INSERT INTO estudiante (nombre, edad, calificacion, genero)
       VALUES (
         ${estudiante.nombre},
         ${estudiante.edad},
         ${estudiante.calificacion},
         ${estudiante.genero}
       )
     """.update.run
  }

  def insertAll(estudiante: List[Estudiante]): IO[List[Int]] = {
    Database.transactor.use { xa =>
      estudiante.traverse(t => insert(t).transact(xa))
    }
  }

  def getAllEstudiantes: ConnectionIO[List[Estudiante]] = {
    sql"""
         SELECT nombre, edad, calificacion, genero
         FROM estudiante
       """.query[Estudiante].to[List]
  }
}