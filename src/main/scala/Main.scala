

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
  import net.ruippeixotog.scalascraper.browser.JsoupBrowser
  import net.ruippeixotog.scalascraper.model.Document

  trait CustomBrowser extends JsoupBrowser {
    case class ErrorMessage( statusCode: Int, message: String, url: String )

    val browser = JsoupBrowser()

    def getDocumentFromUrl( url: String ): Either[ErrorMessage, Document] = {
      try {
        Right(browser.get(url))
      } catch {
        // https://jsoup.org/apidocs/ => org.jsoup
        case e: org.jsoup.HttpStatusException =>
          Left(ErrorMessage(e.getStatusCode, e.getMessage, e.getUrl))
        case e: org.jsoup.SerializationException =>
          //Refer: http://docs.oracle.com/cloud/latest/marketingcs_gs/OMCAB/Developers/GettingStarted/API%20requests/http-status-codes.htm
          Left(ErrorMessage(400, e.getMessage, url)) //A SerializationException is raised whenever serialization of a DOM element fails.
        case e: org.jsoup.UnsupportedMimeTypeException =>
          //Refer: http://stackoverflow.com/questions/11973813/http-status-code-for-unaccepted-content-type-in-request
          Left(ErrorMessage(415, e.getMessage, e.getUrl)) //Signals that a HTTP response returned a mime type that is not supported.
      }
    }
  }
  object browser extends CustomBrowser

  val url = "http://jungbin.kim"
  val doc = browser.getDocumentFromUrl( url )

  println(doc)
}