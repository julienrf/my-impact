package mipa

import scalm.Html
import scalm.Html._

object Flying extends Behaviour {

  val source = Source(
    "https://en.wikipedia.org/wiki/Environmental_impact_of_aviation",
    "Wikipedia"
  )

  case class Model(
    distance: Int /* km */,
    frequency: Int /* per year */
  ) extends ModelTemplate {

    val label: String = "flight"

    val footprint: Double =
      frequency * 259 * distance / 1000.0
  }

  def init = Model(200, 1)

  def view(model: Model): Html[Modify] =
    div()(
      text("Flying for "),
      numberField(model.distance.toString)(n => _.copy(distance = n)),
      text(" km, "),
      numberField(model.frequency.toString)(n => _.copy(frequency = n)),
      text(" times per year.")
    )

}
