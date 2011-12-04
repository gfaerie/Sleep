package se.faerie.sleep.server.state

trait GameObjectMetadata {
}

object GameObjectMetadata {
    case object Player extends GameObjectMetadata
    case object Solid extends GameObjectMetadata
    case object Ghost extends GameObjectMetadata
}