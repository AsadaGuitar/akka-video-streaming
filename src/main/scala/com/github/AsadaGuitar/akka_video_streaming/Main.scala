package com.github.AsadaGuitar.akka_video_streaming

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import java.io.File
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Main extends App {

  private def stream(rangeHeader: String): HttpResponse = {
    val range = rangeHeader.split("=")(1).split("-")
    val start = range(0).toInt
    val end = fileSize - 1

    val headers = List(
      RawHeader("Content-Range", s"bytes $start-$end/$fileSize"),
      RawHeader("Accept-Ranges", "bytes"))

    val fileSource: Source[ByteString, Future[IOResult]] = FileIO.fromPath(videoFile.toPath, 1024, start)
    val responseEntity = HttpEntity(MediaTypes.`video/mp4`, fileSource)
    HttpResponse(StatusCodes.PartialContent, headers, responseEntity)
  }

  // any video file.
  val videoFile = new File("./src/main/resources/movie.mp4")
  val fileSize = videoFile.length()

  val defaultPage = new File("./src/main/resources/reference.html")
  val streamingPage = new File("./src/main/resources/streaming.html")

  val router =
    pathPrefix("video") {
      get {
        optionalHeaderValueByName("Range") {
          case None => complete(StatusCodes.RangeNotSatisfiable)
          case Some(range) => complete(stream(range))
        }
      }
    } ~ pathPrefix("default") {
      get {
        getFromFile(defaultPage, ContentTypes.`text/html(UTF-8)`)
      }
    } ~ pathPrefix("streaming") {
      get {
        getFromFile(streamingPage, ContentTypes.`text/html(UTF-8)`)
      }
    } ~ pathPrefix("assets") {
      get {
        pathPrefix(Segment) { fileName =>
          getFromFile(new File(s"./src/main/resources/$fileName"))
        }
      }
    }

  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "videoStreaming")
  implicit val ec: ExecutionContext = system.executionContext

  val host = "localhost"
  val port = 9000
  val bindingFuture = Http().newServerAt(host, port).bind(router)

  StdIn.readLine()
  system.terminate()
}
