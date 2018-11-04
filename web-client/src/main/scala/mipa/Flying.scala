package mipa

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement
import scalm.Html
import scalm.Html._

object Flying extends Behaviour {

  val sourceURL = "https://en.wikipedia.org/wiki/Environmental_impact_of_aviation"
  val sourceLabel = "Wikipedia"

  case class Model(
    distance: Int /* km */,
    frequency: Int /* per year */
  ) extends ModelTemplate {

    val label: String = "flight"

    val footprint: Double =
      frequency * 259 * distance / 1000.0
  }

  sealed trait Msg
  case class SetDistance(n: Int) extends Msg
  case class SetFrequency(n: Int) extends Msg

  def init = Model(200, 1)
  def update(model: Model, msg: Msg): Model =
    msg match {
      case SetDistance(n) => model.copy(distance = n)
      case SetFrequency(n) => model.copy(frequency = n)
    }

  def view(model: Model): Html[Msg] =
    div()(
      text("Flying for "),
      div(attr("class", "input-field inline"))(
        input(
          attr("type", "number"),
          attr("value", model.distance.toString),
          onEvent("change", (e: dom.Event) => SetDistance(e.target.asInstanceOf[HTMLInputElement].value.toInt))
        )
      ),
      text(" km, "),
      div(attr("class", "input-field inline"))(
        input(
          attr("type", "number"),
          attr("value", model.frequency.toString),
          onEvent("change", (e: dom.Event) => SetFrequency(e.target.asInstanceOf[HTMLInputElement].value.toInt))
        )
      ),
      text(" times per year.")
    )

}
