package mipa

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalm.Html
import scalm.Html._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

// https://theshiftproject.org/wp-content/uploads/2018/10/2018-10-04_Rapport_Pour-une-sobri%C3%A9t%C3%A9-num%C3%A9rique_Rapport_The-Shift-Project.pdf
object VideoStreaming extends Behaviour {

  case class Model(
    duration: FiniteDuration,
    frequency: Int // Times per week
  )

  def label(model: Model): String = "video"

  def footprint(model: Model): Double =
    model.duration.toMinutes * 9e-3 * 0.276 * (model.frequency * 52)

  sealed trait Msg
  case class SetDuration(n: Int) extends Msg
  case class SetFrequency(n: Int) extends Msg

  def init: Model = Model(10.minutes, 1)

  def update(model: Model, msg: Msg): Model =
    msg match {
      case SetDuration(n) => model.copy(duration = n.minutes)
      case SetFrequency(n) => model.copy(frequency = n)
    }

  def form(model: Model): Html[Msg] =
    div()(
      text("I watch a video of "),
      input(
        attr("type", "number"),
        attr("value", model.duration.toMinutes.toString),
        onEvent("change", (e: dom.Event) => SetDuration(e.target.asInstanceOf[HTMLInputElement].value.toInt /* TODO error handling */))
      ),
      text(s" minutes ${model.frequency} times a week.")
    )

}
