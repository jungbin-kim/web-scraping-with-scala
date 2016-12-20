import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

/**
  * Created by jungbin on 2016. 12. 20..
  */

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