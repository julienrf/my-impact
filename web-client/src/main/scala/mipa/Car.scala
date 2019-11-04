package mipa

import scalm.Html
import scalm.Html._
import enum.Enum

object Car extends Behavior {

  val label = "Driving"

  val source = Source(
    "https://www.ademe.fr/consommations-carburant-emissions-co2-vehicules-particuliers-neufs-vendus-france",
    "ADEME"
  )

  case class Model(
    distance: Int /* km */,
    frequency: Int /* per week */,
    passengers: Int,
    ecoClass: EcoClass
  ) extends ModelTemplate {

    val label = "car"

    val footprint =
      "Fuel consumption" -> (ecoClass.gge * distance * frequency * 52.0 / (passengers * 1000)) :: Nil

  }

  sealed abstract class EcoClass(val gge: Int /* g / km */)
  object EcoClass {
    case object A extends EcoClass(80)  // “less than 100”
    case object B extends EcoClass(110) // “between 101 and 120”
    case object C extends EcoClass(130) // “between 121 and 140”
    case object D extends EcoClass(150) // “between 141 and 160”
    case object E extends EcoClass(180) // “between 161 and 200”
    case object F extends EcoClass(225) // “between 200 and 250”
    case object G extends EcoClass(300) // “more than 250”
    implicit val enum: Enum[EcoClass] = Enum.derived
  }

  def init = Model(30, 5, 1, EcoClass.B)

  val distanceField   = field[Int](_.distance, d => _.copy(distance = d))
  val frequencyField  = field[Int](_.frequency, f => _.copy(frequency = f))
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
