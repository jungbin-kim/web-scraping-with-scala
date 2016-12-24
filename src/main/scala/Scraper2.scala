
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Document

object Scraper2 extends App {

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

/*
* 1. Wiki 페이지 안에 항목 페이지로 연결되는 Path 출력
*   - id가 bodyContent 인 element 안에 존재
*   - Path는 /wiki/ 로 시작
*   - Path는 세미콜론 포함하지 않음 ex) /wiki/Category: 와 같은 형태는 항목 페이지로 연결되지 않음.
* 2. Wiki 페이지 안에 항목 페이지로 연결되는 Path들의 리스트를 반환하는 getLinks 함수
* */
object Scraper3 extends App {
  object browser extends CustomBrowser

  type Result[T] = Either[ErrorMessage, T]

  val url = "http://en.wikipedia.org/wiki/Kevin_Bacon"

  def getEither[T, U](eitherBlock: Either[ErrorMessage, U])
                     (nextBlock: (U => Either[ErrorMessage, T])): Either[ErrorMessage, T] = eitherBlock match {
    case Left(e) => Left(e)
    case Right(doc) => nextBlock(doc)
  }
  def getOpt[T, U](optBlock: Option[T], error: => ErrorMessage)
                  (nextBlock: T => Either[ErrorMessage, U]): Either[ErrorMessage, U] = optBlock match {
    case Some(e) => nextBlock(e)
    case _ => Left(error)
  }

  def getLinks( url: String ): Either[ErrorMessage, List[String]] = getEither( browser.getDocumentFromUrl( url ) ){ doc =>
    lazy val NotSelectedElement = ErrorMessage(0, "Not Selected Element", url)
    getOpt(doc >?> element("#bodyContent"), NotSelectedElement) { r =>
      Right(
        ( r >> elementList("a") ).filter(_.attr("href").matches("^(/wiki/)((?!:).)*$")).map(_.attr("href"))
      )
    }
  }

  getLinks(url) fold(l=> println(l), r=>println(r mkString "\n"))
}