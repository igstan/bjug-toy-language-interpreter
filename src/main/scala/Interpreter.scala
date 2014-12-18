package bjug

class Interpreter {
  def eval(ast: Node, env: Environment): Value = {
    ast match {
      case Num(value) => Value.Num(value)
      case Add(l, r) => arith(l, r, env, (x, y) => x + y)
      case Sub(l, r) => arith(l, r, env, (x, y) => x - y)
      case Mul(l, r) => arith(l, r, env, (x, y) => x * y)
      // case Div(...)
      case If(cond, yes, no) =>
        eval(cond, env) match {
          case Value.Num(0) => eval(no, env)
          case _            => eval(yes, env)
        }

      case Fun(param, body) => Value.Fun(param, body)

      case App(fn, arg) =>
        eval(fn, env) match {
          case Value.Fun(param, body) =>
            val emptyEnv = new Environment(Map.empty)
            val augmentedEnv = emptyEnv.set(param, eval(arg, env))
            eval(body, augmentedEnv)
          case Value.Num(_) =>
            throw new RuntimeException("Number in function position")
        }

      case Ref(name) =>
        env.get(name).getOrElse {
          throw UnboundIdentifier(name)
        }

      case Let(name, value, body) =>
        // (function (name) { return body; })(value);
        val desugared = App(
          Fun(name, body),
          value
        )

        eval(desugared, env)
    }
  }

  private def arith(l: Node, r: Node, env: Environment, op: (Int,Int) => Int): Value = {
    eval(l, env) match {
      case Value.Num(a) =>
        eval(r, env) match {
          case Value.Num(b) =>
            Value.Num( op(a, b) )
          case Value.Fun(_, _) =>
            throw new RuntimeException("Right operand was not a number")
        }
      case Value.Fun(_, _) =>
        throw new RuntimeException("Left operand was not a number")
    }
  }
}
