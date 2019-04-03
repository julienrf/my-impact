package mipa

import java.util.UUID

import enum.Enum
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import scalm.{Html, Prop, Style}
import scalm.Html._

trait Behaviour {

  def label: String

  case class Source(url: String, label: String)

  def source: Source

  type Model <: ModelTemplate

  trait ModelTemplate {
    def label: String
    def footprint: List[(String, Double)]
  }

  final case class Modify(f: Model => Model)

  def init: Model

  def view(model: Model): Html[Modify]

  trait Field {
    def form: Html[Modify]
  }

  final def numberField(value: String, maxWidth: Int = 3)(f: Int => Model => Model): Html[Modify] =
    input(
      Prop("type", "number"),
      Prop("value", value),
      style(Style("max-width", s"${maxWidth}rem"), Style("font-weight", "bold")),
      onEvent("change", { e: dom.Event =>
        val stringValue = e.target.asInstanceOf[HTMLInputElement].value
        if (stringValue.forall(_.isDigit)) Modify(f(stringValue.toInt))
        else Modify(identity)
      })
    )

  final def enumField[A](value: A)(f: A => Model => Model)(implicit enumeration: Enum[A]): Html[Modify] = {
    tag("select")(
      // Reset style applied by CSS framework
      style(Style("display", "initial"), Style("width", "initial"), Style("font-weight", "bold")),
      onEvent("change", { e: dom.Event =>
        enumeration
          .decode(e.target.asInstanceOf[HTMLSelectElement].value)
          .fold(_ => Modify(identity), value => Modify(f(value)))
      })
    )(
      enumeration.values.to[Seq].map { v =>
        val label = enumeration.encode(v)
        tag("option")(
          Prop("value", label),
          cond(v == value)(Prop("selected", "true"))
        )(
          text(label)
        )
      }: _*
    )
  }

}

object Behaviour {

  val all: List[Behaviour] =
    VideoStreaming ::
    Flying ::
    Nil

}

trait BehaviourAndModel { outer =>

  val behaviour: Behaviour

  def model: behaviour.Model

  def uuid: UUID

  final def label: String = model.label
  final def footprint = model.footprint

  final def source: behaviour.Source = behaviour.source

  final def update(msg: BehaviourAndMsg { val behaviour: outer.behaviour.type }): BehaviourAndModel { val behaviour: outer.behaviour.type } =
    BehaviourAndModel(behaviour)(uuid, msg.modify.f(model))

  final def view: Html[BehaviourAndMsg { val behaviour: outer.behaviour.type }] =
    behaviour.view(model).map(BehaviourAndMsg(behaviour))

}

object BehaviourAndModel {
  def apply(_behaviour: Behaviour)(_uuid: UUID, _model: _behaviour.Model): BehaviourAndModel { val behaviour: _behaviour.type } = {
      new BehaviourAndModel {
        val behaviour: _behaviour.type = _behaviour
        def model = _model
        def uuid = _uuid
      }
    }

  def newInstance(_behaviour: Behaviour): BehaviourAndModel { val behaviour: _behaviour.type } = {
    BehaviourAndModel(_behaviour)(UUID.randomUUID(), _behaviour.init)
  }
}

trait BehaviourAndMsg { outer =>

  val behaviour: Behaviour

  def modify: behaviour.Modify

  def unapply(bam: BehaviourAndModel): Option[(BehaviourAndModel { val behaviour: bam.behaviour.type }, BehaviourAndMsg { val behaviour: bam.behaviour.type })] =
    if (bam.behaviour == this.behaviour) Some((bam.asInstanceOf[BehaviourAndModel { val behaviour: bam.behaviour.type }], this.asInstanceOf[BehaviourAndMsg { val behaviour: bam.behaviour.type }]))
    else None

}

object BehaviourAndMsg {
  def apply(_behaviour: Behaviour)(_modify: _behaviour.Modify): BehaviourAndMsg { val behaviour: _behaviour.type } =
    new BehaviourAndMsg {
      val behaviour: _behaviour.type = _behaviour
      def modify = _modify
    }
}
