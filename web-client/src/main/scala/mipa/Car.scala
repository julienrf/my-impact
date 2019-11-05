package mipa

import scalm.Html
import scalm.Html._
import enum.Enum
import squants.motion.Distance
import squants.space.Kilometers
import squants.time.Frequency

object Car extends Behavior {

  val label = "Driving"

  val source = Source(
    "https://www.ademe.fr/consommations-carburant-emissions-co2-vehicules-particuliers-neufs-vendus-france",
    "ADEME"
  )

  case class Model(
    distance: Distance,
    frequency: Frequency,
    passengers: Int,
    ecoClass: EcoClass
  ) extends ModelTemplate {

    val label = "car"

    val footprint = {
      val gge = ecoClass.gge * distance * frequency / passengers
      ("Fuel consumption" -> gge) :: Nil
    }

  }

  sealed abstract class EcoClass(val gge: LinearDensity)
  object EcoClass {
    case object A extends EcoClass(GramsPerKilometer(80))  // “less than 100”
    case object B extends EcoClass(GramsPerKilometer(110)) // “between 101 and 120”
    case object C extends EcoClass(GramsPerKilometer(130)) // “between 121 and 140”
    case object D extends EcoClass(GramsPerKilometer(150)) // “between 141 and 160”
    case object E extends EcoClass(GramsPerKilometer(180)) // “between 161 and 200”
    case object F extends EcoClass(GramsPerKilometer(225)) // “between 200 and 250”
    case object G extends EcoClass(GramsPerKilometer(300)) // “more than 250”
    implicit val enum: Enum[EcoClass] = Enum.derived
  }

  def init = Model(Kilometers(30), Weekly(5), 1, EcoClass.B)

  val distanceField   = field[Int](_.distance.to(Kilometers).toInt, d => _.copy(distance = Kilometers(d)))
  val frequencyField  = field[Int](_.frequency.to(Weekly).toInt, f => _.copy(frequency = Weekly(f)))
  val passengersField = field[Int](_.passengers, p => _.copy(passengers = p))
  val ecoClassField   = field[EcoClass](_.ecoClass, c => _.copy(ecoClass = c))

  def view(form: Form): Html[Update] =
    div()(
      text("Driving a car of class "),
      form.enum(ecoClassField),
      text(" for "),
      form.number(distanceField, maxWidth = 5),
      text(" km, "),
      form.number(frequencyField),
      text(" times per week, with "),
      form.number(passengersField),
      text(" passengers.")
    )

}
