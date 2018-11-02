package mipa

import scalm.Html
import scalm.Html._

// https://en.wikipedia.org/wiki/Environmental_impact_of_aviation
object Flying extends Behaviour {

  case class Model(
    distance: Int /* km */
  )

  def label(model: Model): String = "flight"
  def footprint(model: Model): Double = 259 * model.distance / 1000.0

  sealed trait Msg

  def init = Model(200)
  def update(model: Model, msg: Msg): Model = model


  def form(model: Model): Html[Nothing] =
    div()(text("I take a flight of 200 km once a year."))

}
