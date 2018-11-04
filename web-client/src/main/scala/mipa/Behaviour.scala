package mipa

import scalm.Html

trait Behaviour {

  type Model <: ModelTemplate
  type Msg
  def update(model: Model, msg: Msg): Model
  def init: Model
  def view(model: Model): Html[Msg]

  trait ModelTemplate {
    def label: String
    def footprint: Double
  }

  def sourceURL: String
  def sourceLabel: String

}

trait BehaviourAndModel { outer =>

  val behaviour: Behaviour

  def model: behaviour.Model

  final def label = model.label
  final def footprint = model.footprint

  final def sourceURL = behaviour.sourceURL
  final def sourceLabel = behaviour.sourceLabel

  final def update(msg: BehaviourAndMsg { val behaviour: outer.behaviour.type }): BehaviourAndModel { val behaviour: outer.behaviour.type } =
    BehaviourAndModel(behaviour)(behaviour.update(model, msg.msg))

  final def view: Html[BehaviourAndMsg { val behaviour: outer.behaviour.type }] =
    behaviour.view(model).map(BehaviourAndMsg(behaviour))

}

object BehaviourAndModel {
  def apply(_behaviour: Behaviour): _behaviour.Model => BehaviourAndModel { val behaviour: _behaviour.type } =
    _model =>
      new BehaviourAndModel {
        val behaviour: _behaviour.type = _behaviour
        def model = _model
      }
}

trait BehaviourAndMsg { outer =>

  val behaviour: Behaviour

  def msg: behaviour.Msg

  def unapply(bam: BehaviourAndModel): Option[(BehaviourAndModel { val behaviour: bam.behaviour.type }, BehaviourAndMsg { val behaviour: bam.behaviour.type })] =
    if (bam.behaviour == this.behaviour) Some((bam.asInstanceOf[BehaviourAndModel { val behaviour: bam.behaviour.type }], this.asInstanceOf[BehaviourAndMsg { val behaviour: bam.behaviour.type }]))
    else None

}

object BehaviourAndMsg {
  def apply(_behaviour: Behaviour)(_msg: _behaviour.Msg): BehaviourAndMsg { val behaviour: _behaviour.type } =
    new BehaviourAndMsg {
      val behaviour: _behaviour.type = _behaviour
      def msg = _msg
    }
}
