import java.time.LocalDateTime
import scala.io.StdIn

object HelloWorld extends App {
  val name = StdIn.readLine("Hi! Whats your name: ")

  var qt = {
    var q = 12
    var t = 34
    q + t
  }

  val qtp = qt.toDouble

  var i = {

  }

  println(i == ())

  println(factorialFun(5))

  println(s"Hello $name $qtp")
  printSomething("Hi")

  def printSomething (str:String) : Unit = {
    val day = LocalDateTime.now().getDayOfWeek
    println(str + " " + day)
  }

  val sqr = (x : Double) => x * x

  var result = sqr(5)

  val add = (x : Int, y : Int) => x + y
  val add1 : (Int, Int) => Int =  _ + _
  val add2 = (_ : Int) + (_ :  Int)

//  val factorial : Int => Int =
//    n => if (n == 0 ) 1 else n * factorial(n - 1)


  def factorialFun(n: Int): Int = {
    if (n == 0 ) 1 else n * factorialFun(n - 1)
  }
}