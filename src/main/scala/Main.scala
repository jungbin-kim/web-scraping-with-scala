

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
  import net.ruippeixotog.scalascraper.dsl.DSL._
  import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
  import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

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

  val url = "http://www.pythonscraping.com/pages/warandpeace.html"
  val doc = browser.getDocumentFromUrl( url )
/*
* (1)Either를 처리하는 함수를 만들어 결과가 Right가 나올 때만 다음 함수를 실행하게 하는 방법
* */
//  def getEither[T,A](eitherBlock: Either[A, T])(nextBlock: (T => Unit)): Unit = eitherBlock match {
//    case Left(e) => println(e)
//    case Right(d) => nextBlock(d)
//  }
//  getEither(doc){
//    d => println(d >> elementList("span.green"))
//  }

/*
* (2) 그냥 Either를 pattern matching으로 풀어 주는 방법
* */
  val nameList = doc match {
    case Left(e) => println(e)
    case Right(d) => d >> elementList("span.green")
  }
  println(nameList)
}