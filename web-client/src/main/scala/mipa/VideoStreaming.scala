package mipa

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalm.Html
import scalm.Html._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

object VideoStreaming extends Behaviour {

  val sourceURL = "https://theshiftproject.org/wp-content/uploads/2018/10/2018-10-04_Rapport_Pour-une-sobri%C3%A9t%C3%A9-num%C3%A9rique_Rapport_The-Shift-Project.pdf"
  val sourceLabel = "Rapport pour une sobriété numérique, The Shift Project"

  case class Model(
    duration: FiniteDuration,
    frequency: Int // Times per week
  ) extends ModelTemplate {

    val label: String = "video"

    val footprint: Double =
      duration.toMinutes * 9e-3 * 0.276 * (frequency * 52)
  }

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
      text("Watching an online video of "),
      div(attr("class", "input-field inline"))(
        input(
          attr("type", "number"),
          attr("value", model.duration.toMinutes.toString),
          onEvent("change", (e: dom.Event) => SetDuration(e.target.asInstanceOf[HTMLInputElement].value.toInt /* TODO error handling */))
        )
      ),
      text(" minutes, in high definition, "),
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
