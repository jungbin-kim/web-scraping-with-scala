
object Scraper1 {

  import scala.io.Source
  import java.nio.charset.CodingErrorAction
  import scala.io.Codec

  def main(args: Array[String]): Unit = {

    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.IGNORE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    val url = "http://blog.jungbin.kim"
    val html = Source.fromURL(url)
    val s = html.getLines.mkString("\n")
    println(s)
  }
}

object Scraper2 extends App {
  println("2")
}