# case-class-gym

This is a sample repo to showcase one thing of the Scala 2.12 syntax.

## Private constructors leak when it comes to case classes

```
case class A private (v: Int)
```

For case classes, Scala auto-creates an `.apply` method for the case class so that users can skip the `new` keyword.

What is less known, I think, is that the `private` restriction given above for the constructor does not get relayed to the auto-generated `.apply`. This means, efforts to make case class constructors private demand more boilerplate.

```
  case class B private (v: Int)

  object B {
    override private
    def apply(v: Int): B = throw new NotImplementedError    // we don't wish to allow its use
  }
```

This works.

Why I'm bringing this up is that the above seems inconsistent to me, and maybe Scala authors want to have a look at it, for 2.13 or 2.14 time frame?

## Why would one use a case class with private constructor, anyhow?

Well. Depends on coding style, I guess.

I've used them a couple of times, when there's an entry (often enum-like) that is created based on some network stream.

1. It deserves to have a private constructor, since (apart from tests) the callers never should/can create these instances. Having it private enforces this.

  For tests, I made a `.forTest` method that bypasses the limitation but also makes it very explicit.
  
2. It deserves to be a case class, since those items were often used in pattern matching, and this way I get `.unapply` etc. free of charge.


## Disclaimer

I don't know how big a change this would be, internally in Scala. I hope it's small. The benefit would be more consistent behavior (less surprises) to the users.


## Run this repo

Just `sbt run`.

It should fail with "method apply in ... cannot be accessed".

Please see the comments in the source code.


---

## References

Seems this has already been brought up quite a bit.

- [here](http://grokbase.com/t/gg/scala-user/148qmgmgqr/case-class-and-classifier-private-protected) (2014)
  - it seems to be a slightly different case, maybe even so that the author actually wants `.apply` to be available when `new` is private (which now seems to be Scala 2.12 behavior). The discussion is concerning Scala 2.11.

- [SO #20030826](https://stackoverflow.com/questions/20030826/scala-case-class-private-constructor-but-public-apply-method) (2013; Scala 2.10.3)
  - the answer by `farmor` uses a trait and a fully private case class. That would solve my use case need, but I agree with one of the comments:

  >I think this is a good workaround because you get the asked behavior and some of the benefits from case classes (e.g. implemented equals). But it's kind of heavy compared to the single case class.

- [Scala-lang.org archives](https://www.scala-lang.org/old/node/8112) (2010)
  - on `.copy` method, but otherwise the author seems to be having a similar use case as presented here

  - the answer is: "The compiler will only generate the copy method if you haven't already supplied your own"

  The author's question "Is this by design, and how do I work around it?" is actually precisely what's on my mind, in 2018, as well.


- [Manning Forums](https://forums.manning.com/posts/list/38567.page) (2016)
  Again, similar concern as here, and in Scala-lang archives.
  
- [scala-users](https://groups.google.com/forum/#!topic/scala-user/4VeM4jumnaw) (2012)

  >It seems to me that the limitation is intentional as case classes must generate apply/unapply to support pattern matching. If you restrict the users ability to use either, you dont really have a case class any more.
  
  Is that true? I've thought that only `.unapply` is necessary, to do pattern matching.
  
- [Scala GitHub Issues #7884](https://github.com/scala/bug/issues/7884) (2013; still open)

  - From where I see this, I agree with `scabug` on:

  >"I think the principled thing is to give the synthetic copy method the same access modifiers as the primary constructor."

	(and the same treatment to `.apply`)
	
	Where can I suggest this for Scala 2.13?
