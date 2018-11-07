package mipa

import scalm.Html
import scalm.Html._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

object VideoStreaming extends Behaviour {

  val source = Source(
    "https://theshiftproject.org/wp-content/uploads/2018/10/2018-10-04_Rapport_Pour-une-sobri%C3%A9t%C3%A9-num%C3%A9rique_Rapport_The-Shift-Project.pdf",
    "Rapport — Pour une sobriété numérique, The Shift Project"
  )

  // TODO video quality, device type, connection type
  case class Model(
    duration: FiniteDuration,
    frequency: Int // Times per week
  ) extends ModelTemplate {

    val label: String = "video"

    val footprint: Double =
      duration.toMinutes * 9e-3 * 0.276 * (frequency * 52)
  }

  def init: Model = Model(20.minutes, 5)

  def view(model: Model): Html[Modify] =
    div()(
      text("Watching an online video of "),
      numberField(model.duration.toMinutes.toString)(modifyNumber(n => _.copy(duration = n.minutes))),
      text(" minutes, in high definition, "),
      numberField(model.frequency.toString)(modifyNumber(n => _.copy(frequency = n))),
      text(s" times a week.")
    )

}
