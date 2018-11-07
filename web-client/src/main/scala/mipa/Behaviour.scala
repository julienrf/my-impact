package mipa

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalm.Html
import scalm.Html._

trait Behaviour {

  type Model <: ModelTemplate

  final case class Modify(f: Model => Model)

  def init: Model

  def view(model: Model): Html[Modify]

  final def update(model: Model, modify: Modify): Model =
    modify.f(model)

  final def modifyNumber(f: Int => Model => Model): String => Modify = { stringValue =>
    if (stringValue.forall(_.isDigit)) Modify(f(stringValue.toInt))
    else Modify(identity)
  }

  trait ModelTemplate {
    def label: String
    def footprint: Double
  }

  def sourceURL: String
  def sourceLabel: String

  final def numberField(value: String)(onChange: String => Modify): Html[Modify] =
    div(attr("class", "input-field inline"))(
      input(
        attr("type", "number"),
        attr("value", value),
        onEvent("change", (e: dom.Event) => onChange(e.target.asInstanceOf[HTMLInputElement].value))
      )
    )
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
  def apply(_behaviour: Behaviour)(_model: _behaviour.Model): BehaviourAndModel { val behaviour: _behaviour.type } = {
      new BehaviourAndModel {
        val behaviour: _behaviour.type = _behaviour
        def model = _model
      }
    }

  def init(_behaviour: Behaviour): BehaviourAndModel { val behaviour: _behaviour.type } = {
    BehaviourAndModel(_behaviour)(_behaviour.init)
  }
}

trait BehaviourAndMsg { outer =>

  val behaviour: Behaviour

  def msg: behaviour.Modify

  def unapply(bam: BehaviourAndModel): Option[(BehaviourAndModel { val behaviour: bam.behaviour.type }, BehaviourAndMsg { val behaviour: bam.behaviour.type })] =
    if (bam.behaviour == this.behaviour) Some((bam.asInstanceOf[BehaviourAndModel { val behaviour: bam.behaviour.type }], this.asInstanceOf[BehaviourAndMsg { val behaviour: bam.behaviour.type }]))
    else None

}

object BehaviourAndMsg {
  def apply(_behaviour: Behaviour)(_msg: _behaviour.Modify): BehaviourAndMsg { val behaviour: _behaviour.type } =
    new BehaviourAndMsg {
      val behaviour: _behaviour.type = _behaviour
      def msg = _msg
    }
}
