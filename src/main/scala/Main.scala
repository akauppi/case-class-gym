object Main extends App {

  case class A private (v: Int)

  //val x = new A(2)   // does not compile (correct): "cannot be accessed in object Main"

  val xx = A(2)   // 'private' constructor does not make the 'apply' private
  println(xx)


  case class B private (v: Int)

  object B {
    // Note: Blocking the '.apply' is prone to parameter changes. Since we are not overriding anything
    //      (case class causes an 'apply' to be generated unless we provide one), we just have to have
    //      the same parameters as the case class constructor. If we don't, all compiles, but our blocking
    //      silently stops being effective. e.g. changing 'Int' to 'Long' would cause this.
    //
    private
    def apply(v: Int): B = throw new NotImplementedError    // we don't wish to allow its use

    val fourtyTwo: B = new B(42)
  }

  val xy = B(2)   // "method apply in ... cannot be accessed" (correct)

  // Can we still do pattern matching? Yep.
  //
  B.fourtyTwo match {
    case B(i) => println(s"matched $i")
  }

  println(":)")
}
