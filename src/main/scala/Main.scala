object Main extends App {

  case class A private (v: Int)

  //val x = new A(2)   // does not compile (correct): "cannot be accessed in object Main"

  val xx = A(2)   // 'private' constructor does not make the 'apply' private

  println(xx)


  case class B private (v: Int)

  object B {
    override private
    def apply(v: Int): B = throw new NotImplementedError    // we don't wish to allow its use
  }

  val xy = B(2)   // "method apply in ... cannot be accessed" (correct)

  println(xy)
}
