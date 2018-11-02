package mipa

import scalm.Html

trait Behaviour {

  type Model
  type Msg
  def update(model: Model, msg: Msg): Model
  def init: Model
  def form(model: Model): Html[Msg]

  def label(model: Model): String
  def footprint(model: Model): Double

}

trait BehaviourAndModel {
  val behaviour: Behaviour
  def model: behaviour.Model

  final def label = behaviour.label(model)
  final def footprint = behaviour.footprint(model)
}

object BehaviourAndModel {
  def apply(_behaviour: Behaviour): _behaviour.Model => BehaviourAndModel = _model =>
    new BehaviourAndModel {
      val behaviour: _behaviour.type = _behaviour
      def model = _model
    }
}
