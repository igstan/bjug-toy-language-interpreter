package bjug
package test

import org.scalatest.{ FunSuite, Matchers }

class InterpreterTest extends FunSuite with Matchers {
  val env = new Environment(Map.empty)

  test("evaluates numbers") {
    val ast = Num(1)
    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(1))
  }

  test("evaluates addition") {
    val ast = Add(
      Add(Num(2), Num(3)),
      Add(Num(2), Num(3))
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(10))
  }

  test("evaluates subtraction") {
    val ast = Sub(
      Add(Num(2), Num(3)),
      Add(Num(2), Num(3))
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(0))
  }

  test("evaluates if expression with cond == 0") {
    val ast = If(Num(0), Num(1), Num(2))
    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(2))
  }

  test("evaluates if expression with cond != 0") {
    val ast = If(Num(42), Num(1), Num(2))
    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(1))
  }

  test("evaluates function expressions") {
    val ast = Fun("a", Num(1))

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Fun("a", Num(1)))
  }

  test("evaluates function application") {
    val ast = App(Fun("a", Num(1)), Num(2))

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(1))
  }

  test("evaluates function application with param reference") {
    val ast = App(
      Fun("a", Ref("a")),
      Num(2)
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(2))
  }

  test("evaluates function application with param reference #2") {
    val ast = App(
      Fun("a", Ref("a")),
      Num(3)
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(3))
  }

  // var a = function (param) {
  //   return param + 1;
  // };
  //
  // a(2);
  test("evaluates let expressions") {
    val ast = Let(
      "a", Fun("param", Add(Ref("param"), Num(1))),
      App(Ref("a"), Num(2))
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should be(Value.Num(3))
  }

  // function f1(y) {
  //   return x + y;
  // }
  //
  // function f2(x) {
  //   return f1(4);
  // }
  //
  // f2(1);
  //
  // answer? 5? or unbound identifier x?
  test("evaluates tricky") {
    val ast = Let(
      "f1", Fun("y", Add(Ref("x"), Ref("y"))),
      Let(
        "f2", Fun("x", App(Ref("f1"), Num(4))),
        App(Ref("f2"), Num(1))
      )
    )

    val interpreter = new Interpreter
    interpreter.eval(ast, env) should not be(Value.Num(5))

    val e = intercept[UnboundIdentifier] {
      interpreter.eval(ast, env)
    }

    e.identifierName should be("x")
  }
}
