package mipa

import scalm.Html
import scalm.Html._

object Flying extends Behavior {

  val label = "Flying"

  val source = Source(
    "https://en.wikipedia.org/wiki/Environmental_impact_of_aviation",
    "Wikipedia"
  )

  case class Model(
    distance: Int /* km */,
    frequency: Int /* per year */
  ) extends ModelTemplate {

    val label: String = "flight"

    val footprint =
      "Fuel consumption" -> (frequency * 259 * distance / 1000.0) :: Nil
  }

  val distanceField  = field[Int](_.distance, d => _.copy(distance = d))
  val frequencyField = field[Int](_.frequency, f => _.copy(frequency = f))

  def init = Model(500, 1)

  def view(form: Form): Html[Update] =
    div()(
      text("Flying for "),
      form.number(distanceField, maxWidth = 5),
      text(" km, "),
      form.number(frequencyField),
      text(" times per year.")
    )

}
