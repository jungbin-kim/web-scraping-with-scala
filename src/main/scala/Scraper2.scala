
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

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
* */
object Scraper3 extends App {
  object browser extends CustomBrowser

  val url = "http://en.wikipedia.org/wiki/Kevin_Bacon"
  val doc = browser.getDocumentFromUrl( url )
  doc match {
    case Left(e) =>
      println(e)
    case Right(b) =>
      val eles = ((b >> element("#bodyContent")) >> elementList("a")).filter(_.attr("href").matches("^(/wiki/)((?!:).)*$"))
      eles foreach ( e => println(e.attr("href")) )
  }
}