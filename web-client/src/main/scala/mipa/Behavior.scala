package mipa

import java.util.UUID

import enum.Enum
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import scalm.{Html, Prop, Style}
import scalm.Html._
import squants.motion.MassFlow

trait Behavior {

  def label: String

  case class Source(url: String, label: String)

  def source: Source

  type Model <: ModelTemplate

  trait ModelTemplate {
    def label: String
    def footprint: List[(String, MassFlow /* kg (CO₂ eq) / year */)]
  }

  final case class Update(f: Model => Model)

  def init: Model

  def view(form: Form): Html[Update]

  class Field[A](val get: Model => A, val set: A => Model => Model)

  def field[A](get: Model => A, set: A => Model => Model): Field[A] =
    new Field(get, set)

  class Form(model: Model) {

    def number[A](field: Field[A], maxWidth: Int = 3)(implicit num: Integral[A]): Html[Update] =
      input(
        Prop("type", "number"),
        Prop("value", field.get(model).toString),
        style(Style("max-width", s"${maxWidth}rem"), Style("font-weight", "bold")),
        onEvent("change", { e: dom.Event =>
          val stringValue = e.target.asInstanceOf[HTMLInputElement].value
          if (stringValue.forall(_.isDigit)) Update(field.set(num.fromInt(stringValue.toInt)))
          else Update(identity)
        })
      )

    def enum[A](field: Field[A])(implicit enumeration: Enum[A]): Html[Update] = {
      val value = field.get(model)
      tag("select")(
        // Reset style applied by CSS framework
        style(Style("display", "initial"), Style("width", "initial"), Style("font-weight", "bold")),
        onEvent("change", { e: dom.Event =>
          enumeration
            .decode(e.target.asInstanceOf[HTMLSelectElement].value)
            .fold(_ => Update(identity), value => Update(field.set(value)))
        })
      )(
        enumeration.values.to[Seq].sortBy(enumeration.encode).map { v =>
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

}

object Behavior {

  val all: List[Behavior] =
    VideoStreaming ::
    Flying ::
    Car ::
    Eating ::
    Nil

}

trait BehaviorAndModel { outer =>

  val behavior: Behavior

  def model: behavior.Model

  def uuid: UUID

  final def label: String = model.label
  final def footprint = model.footprint

  final def source: behavior.Source = behavior.source

  final def update(msg: BehaviorAndMsg { val behavior: outer.behavior.type }): BehaviorAndModel { val behavior: outer.behavior.type } =
    BehaviorAndModel(behavior)(uuid, msg.modify.f(model))

  final def view: Html[BehaviorAndMsg { val behavior: outer.behavior.type }] =
    behavior.view(new behavior.Form(model)).map(BehaviorAndMsg(behavior))

}

object BehaviorAndModel {
  def apply(_behavior: Behavior)(_uuid: UUID, _model: _behavior.Model): BehaviorAndModel { val behavior: _behavior.type } = {
      new BehaviorAndModel {
        val behavior: _behavior.type = _behavior
        def model = _model
        def uuid = _uuid
      }
    }

  def newInstance(_behavior: Behavior): BehaviorAndModel { val behavior: _behavior.type } = {
    BehaviorAndModel(_behavior)(UUID.randomUUID(), _behavior.init)
  }
}

trait BehaviorAndMsg { outer =>

  val behavior: Behavior

  def modify: behavior.Update

  def unapply(bam: BehaviorAndModel): Option[(BehaviorAndModel { val behavior: bam.behavior.type }, BehaviorAndMsg { val behavior: bam.behavior.type })] =
    if (bam.behavior == this.behavior) Some((bam.asInstanceOf[BehaviorAndModel { val behavior: bam.behavior.type }], this.asInstanceOf[BehaviorAndMsg { val behavior: bam.behavior.type }]))
    else None

}

object BehaviorAndMsg {
  def apply(_behavior: Behavior)(_modify: _behavior.Update): BehaviorAndMsg { val behavior: _behavior.type } =
    new BehaviorAndMsg {
      val behavior: _behavior.type = _behavior
      def modify = _modify
    }
}
