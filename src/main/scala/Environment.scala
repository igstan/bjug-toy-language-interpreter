package bjug

class Environment(bindings: Map[String, Value]) {
  def get(name: String): Option[Value] = {
    bindings.get(name)
  }

  def set(name: String, value: Value): Environment = {
    new Environment(bindings + (name -> value))
  }
}

case class UnboundIdentifier(identifierName: String)
  extends RuntimeException(s"Unbound identifier: $identifierName")
