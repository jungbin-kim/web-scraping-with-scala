
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
* 3. 처음 getLinks를 호출해서 반환한 주소들 중 랜덤한 한 주소를 선택하여 getLinks 를 다시 호출하는 작업을, 프로그램을 끝내거나 새 페이지에 항목 링크가 없을 때 까지 반복
* */
object Scraper3 extends App {
  import scala.util.Random
  import scala.annotation.tailrec

  object browser extends CustomBrowser

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

  @tailrec
  def loopGetLinks(targetUrl: String ): Either[ErrorMessage, List[String]] = getLinks( targetUrl ) match {
    case Left(l) => Left(l)
    case Right(r) if r.isEmpty => Left( ErrorMessage(0, "Empty Links", targetUrl) )
    case Right(r) =>
      val nextUrl =  "http://en.wikipedia.org" + Random.shuffle(r).head
      println(s"Next Url: $nextUrl")
      loopGetLinks( nextUrl )
  }

  val seedUrl = "http://en.wikipedia.org/wiki/Kevin_Bacon"
  loopGetLinks( seedUrl )
}