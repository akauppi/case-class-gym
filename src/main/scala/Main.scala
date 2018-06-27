object Main extends App {

  // ---
  // First try: check that private constructor still allows construction via '.apply'

  case class A private (v: Int)

  //val x = new A(2)   // does not compile (correct): "cannot be accessed in object Main"

  val a1 = A(1)   // compiles: 'private' constructor does not make '.apply' private
  println(a1)

  val a2 = a1.copy(2)

  println(s"A: $a1 $a2")


  // ---
  // Second: how to prevent access to '.apply' and '.copy', explicitly

  case class B private (v: Int) {
    private
    def copy: B = throw new IllegalAccessError    // blocks generation of any 'copy' variants
  }

  object B {
    // Note: Explicitly blocking the '.apply' is prone to parameter changes. Since we are not overriding
    //      anything (case class causes an 'apply' to be generated unless we provide one), we just have to
    //      have the same parameters as the case class constructor. If we don't, all compiles, but our
    //      blocking silently stops being effective. e.g. changing 'Int' to 'Long' would cause this.
    //
    // Note: Unlike with '.copy', defining a bare 'apply: B' does not block generation of other apply
    //      methods, so we need to provide the precise parameters.
    //
    private
    def apply(v: Int): B = throw new IllegalAccessError

    val fourtyTwo: B = new B(42)
  }

  //val b1 = B(1)   // "method apply in ... cannot be accessed" (correct)
  //val b1: B = B.fourtyTwo
  //val bx = b1.copy()    // "method copy in ... cannot be accessed" (correct)
  //val b2 = b1.copy(2)
  //val b2b = b1.copy(v=2)

  // Can we still do pattern matching? Yep.
  //
  B.fourtyTwo match {
    case B(i) => println(s"matched $i")
  }

  // ---
  // Third: Suggested workaround by @tpolecat at https://users.scala-lang.org/t/ending-the-confusion-of-private-case-class-constructor-in-scala-2-13-or-2-14/2915/6
  //
  sealed abstract case class C private (v: Int)

  object C {
    //val twentyFour: C = new C(24)   // can we still create them, internally? Nope. ".. is abstract; cannot be instantiated"
  }

  //val c1 = C(1)         // "Main.C.type does not take parameters" (okay-ish)
  //val c2 = c1.copy(v=2)   // "copy is not a member" :)
  //println(s"C: $c1 $c2")

  println(":)")
}
