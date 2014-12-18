package bjug

sealed trait Node
// 1
case class Num(value: Int) extends Node // Num(1)
// 1 + (2 + 3)
case class Add(left: Node, right: Node) extends Node
case class Sub(left: Node, right: Node) extends Node
case class Mul(left: Node, right: Node) extends Node

// if (0) 1 else 2
case class If(cond: Node, yes: Node, no: Node) extends Node

// function (param) {
//   return param + 1;
// }
case class Fun(param: String, body: Node) extends Node

// (function (param) {
//   return param + 1;
// })(2);
case class App(fn: Node, arg: Node) extends Node

case class Ref(name: String) extends Node


// var a = function (param) {
//   return param + 1;
// };
//
// a(2);
case class Let(name: String, value: Node, body: Node) extends Node

// Let("a", Fun(...), App(Ref("a"), Num(2)))











