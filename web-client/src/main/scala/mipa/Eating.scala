package mipa

import enum.Enum
import scalm.Html
import scalm.Html._
import squants.Mass
import squants.mass.Grams
import squants.time.Frequency

object Eating extends Behavior {

  val label: String = "Eating"

  val source = Source(
    // Also: https://pubs.acs.org/doi/full/10.1021/es702969f
    // And also: https://science.sciencemag.org/content/sci/360/6392/987.full.pdf
    "https://ourworldindata.org/environmental-impacts-of-food",
    "Environmental impacts of food production"
  )

  case class Model(
    food: Food,
    quantity: Mass,
    transport: Transport,
    frequency: Frequency
  ) extends ModelTemplate {

    val label = "eating"

    val footprint = {
      // grams of CO₂eq per gram of transported food
      val transportFactor: Double = transport match {
        // Assumption: foot or bike
        case Transport.Local     => 0
        // Assumption: a few thousand kilometers by truck
        case Transport.Road => 1500 /* km */ * 0.0002
        // Assumption: 10 thousands km by boat, plus 200 km by road
        case Transport.Boat      => 10000 /* km */ * 0.000023 + 200 /* km */ * 0.0002
        // Assumption: 10 thousands km by plane, plus 200 km by road
        case Transport.Plane     => 10000 /* km */ * 0.00113 + 200 /* km */ * 0.0002
      }
      "Land use change" -> quantity * food.landUseChange * frequency ::
      "Animal feed"     -> quantity * food.animalFeed * frequency ::
      "Farm"            -> quantity * food.farm * frequency ::
      "Processing"      -> quantity * food.processing * frequency ::
      "Packaging"       -> quantity * food.packaging * frequency ::
      "Retail"          -> quantity * food.retail * frequency ::
      "Transport"       -> quantity * transportFactor * frequency ::
      Nil
    }

  }

  sealed abstract class Food(
    val landUseChange: Double,
    val animalFeed: Double,
    val farm: Double,
    val processing: Double,
    val packaging: Double,
    val retail: Double
  )
  object Food {
    case object Beef extends Food(
      landUseChange = 16.3,
      animalFeed = 1.9,
      farm = 39.4,
      processing = 1.3,
      packaging = 0.2,
      retail = 0.2
    )
    case object Lamb extends Food(
      landUseChange = 0.5,
      animalFeed = 2.4,
      farm = 19.5,
      processing = 1.1,
      packaging = 0.3,
      retail = 0.2
    )
    case object Poultry extends Food(
      landUseChange = 2.5,
      animalFeed = 1.8,
      farm = 0.7,
      processing = 0.4,
      packaging = 0.2,
      retail = 0.2
    )
    case object Pig extends Food(
      landUseChange = 1.5,
      animalFeed = 2.9,
      farm = 1.7,
      processing = 0.3,
      packaging = 0.3,
      retail = 0.2
    )
    case object WildFish extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 3,
      processing = 0,
      packaging = 0.2,
      retail = 0.1
    )
    case object Cheese extends Food(
      landUseChange = 4.5,
      animalFeed = 2.3,
      farm = 13.1,
      processing = 0.7,
      packaging = 0.2,
      retail = 0.3
    )
    case object Chocolate extends Food(
      landUseChange = 14.3,
      animalFeed = 0,
      farm = 3.7,
      processing = 0.2,
      packaging = 0.4,
      retail = 0
    )
    case object Coffee extends Food(
      landUseChange = 3.7,
      animalFeed = 0,
      farm = 10.4,
      processing = 0.6,
      packaging = 1.6,
      retail = 0.1
    )
    case object PalmOil extends Food(
      landUseChange = 3.1,
      animalFeed = 0,
      farm = 2.1,
      processing = 1.3,
      packaging = 0.9,
      retail = 0
    )
    case object OliveOil extends Food(
      landUseChange = -0.4,
      animalFeed = 0,
      farm = 4.3,
      processing = 0.7,
      packaging = 0.9,
      retail = 0
    )
    case object Eggs extends Food(
      landUseChange = 0.7,
      animalFeed = 2.2,
      farm = 1.3,
      processing = 0,
      packaging = 0.2,
      retail = 0
    )
    case object Rice extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 3.6,
      processing = 0.1,
      packaging = 0.1,
      retail = 0.1
    )
    case object Wheat extends Food(
      landUseChange = 0.1,
      animalFeed = 0,
      farm = 0.8,
      processing = 0.2,
      packaging = 0.1,
      retail = 0.1
    )
    case object Nuts extends Food(
      landUseChange = -2.1,
      animalFeed = 0,
      farm = 2.1,
      processing = 0,
      packaging = 0.1,
      retail = 0
    )
    case object Vegetables extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.2,
      processing = 0.1,
      packaging = 0,
      retail = 0
    )
    case object Peas extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.7,
      processing = 0,
      packaging = 0,
      retail = 0
    )
    case object Tofu extends Food(
      landUseChange = 1,
      animalFeed = 0,
      farm = 0.5,
      processing = 0.8,
      packaging = 0.2,
      retail = 0.3
    )
    case object Apples extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.2,
      processing = 0,
      packaging = 0,
      retail = 0
    )
    case object Berries extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.7,
      processing = 0,
      packaging = 0.2,
      retail = 0
    )
    case object Bananas extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.3,
      processing = 0.1,
      packaging = 0.1,
      retail = 0
    )
    case object Tomatoes extends Food(
      landUseChange = 0.4,
      animalFeed = 0,
      farm = 0.7,
      processing = 0,
      packaging = 0.1,
      retail = 0
    )
    case object Potatoes extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.2,
      processing = 0,
      packaging = 0,
      retail = 0
    )
    case object Brassicas extends Food(
      landUseChange = 0,
      animalFeed = 0,
      farm = 0.3,
      processing = 0,
      packaging = 0,
      retail = 0
    )
    implicit val enum: Enum[Food] = Enum.derived
  }

  sealed trait Transport
  object Transport {
    case object Local extends Transport
    case object Road extends Transport
    case object Boat extends Transport
    case object Plane extends Transport
    implicit val enum: Enum[Transport] = Enum.derived
  }


  def init: Model = Model(Food.Vegetables, Grams(200), Transport.Road, Weekly(7))

  val foodField = field[Food](_.food, f => _.copy(food = f))
  val quantityField = field[Int](_.quantity.to(Grams).toInt, m => _.copy(quantity = Grams(m)))
  val transportField = field[Transport](_.transport, t => _.copy(transport = t))
  val frequencyField = field[Int](_.frequency.to(Weekly).toInt, f => _.copy(frequency = Weekly(f)))

  def view(form: Form): Html[Update] =
    div()(
      text("Eating "),
      form.number(quantityField),
      text(" g of "),
      form.enum(foodField),
      text(" transported by "),
      form.`enum`(transportField),
      text(", "),
      form.number(frequencyField),
      text(" times per week")
    )

}
