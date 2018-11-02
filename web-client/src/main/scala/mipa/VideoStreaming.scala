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

  def init: Model = Model(20.minutes, 5)

  def update(model: Model, msg: Msg): Model =
    msg match {
      case SetDuration(n) => model.copy(duration = n.minutes)
      case SetFrequency(n) => model.copy(frequency = n)
    }

  def view(model: Model): Html[Msg] =
    div()(
      text("Watching a video of "),
      div(attr("class", "input-field inline"))(
        input(
          attr("type", "number"),
          attr("value", model.duration.toMinutes.toString),
          onEvent("change", (e: dom.Event) => SetDuration(e.target.asInstanceOf[HTMLInputElement].value.toInt /* TODO error handling */))
        )
      ),
      text(" minutes "),
      div(attr("class", "input-field inline"))(
        input(
          attr("type", "number"),
          attr("value", model.frequency.toString),
          onEvent("change", (e: dom.Event) => SetFrequency(e.target.asInstanceOf[HTMLInputElement].value.toInt /* TODO validation */))
        ),
      ),
      text(s" times a week.")
    )

}