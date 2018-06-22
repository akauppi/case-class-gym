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

The compilation should fail on `B(2)` - but passes on `A(2)`.
