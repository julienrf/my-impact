package mipa

import scalm.Html
import scalm.Html._
import squants.motion.Distance
import squants.space.Kilometers
import squants.time.Frequency

object Flying extends Behavior {

  val label = "Flying"

  val source = Source(
    "https://en.wikipedia.org/wiki/Environmental_impact_of_aviation",
    "Wikipedia"
  )

  case class Model(
    distance: Distance,
    frequency: Frequency
  ) extends ModelTemplate {

    val label: String = "flight"

    val footprint = {
      val gge = GramsPerKilometer(150 /* very approximate... */) * distance * frequency
      ("Fuel consumption" -> gge) :: Nil
    }
  }

  val distanceField  = field[Int](_.distance.to(Kilometers).toInt, d => _.copy(distance = Kilometers(d)))
  val frequencyField = field[Int](_.frequency.to(Yearly).toInt, f => _.copy(frequency = Yearly(f)))

  def init = Model(Kilometers(6000), Yearly(1))

  def view(form: Form): Html[Update] =
    div()(
      text("Flying for "),
      form.number(distanceField, maxWidth = 5),
      text(" km, "),
      form.number(frequencyField),
      text(" times per year.")
    )

}
