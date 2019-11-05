package mipa

import scalm.Html
import scalm.Html._
import enum.Enum
import squants.energy.{Energy, KilowattHours, Kilowatts, Power}
import squants.information.{DataRate, Information, MegabytesPerSecond}
import squants.mass.{Kilograms, Mass}
import squants.time.{Frequency, Minutes, Time}

object VideoStreaming extends Behavior {

  val label = "Video Streaming"

  val source = Source(
    "https://theshiftproject.org/wp-content/uploads/2018/10/2018-10-04_Rapport_Pour-une-sobri%C3%A9t%C3%A9-num%C3%A9rique_Rapport_The-Shift-Project.pdf",
    "Rapport — Pour une sobriété numérique, The Shift Project"
  )

  case class Model(
    duration: Time,
    frequency: Frequency,
    quality: Quality,
    device: Device,
    network: Network
  ) extends ModelTemplate {

    val label: String = "video"

    val footprint = {
      val bytes = quality.bitrate * duration
      val dataCenterEnergy = KilowattHoursPerByte(7.20E-11) * bytes
      // TODO Refine per world region
      val region = Region.World
      "Device"      -> region.gge(device.power * duration) * frequency ::
      "Data Center" -> region.gge(dataCenterEnergy) * frequency ::
      "Network"     -> region.gge(network.energyPerInformation * bytes) * frequency ::
      Nil
    }
  }

  val durationField  = field[Long](_.duration.toMinutes.toLong, d => _.copy(duration = Minutes(d)))
  val frequencyField = field[Int](_.frequency.to(Weekly).toInt, f => _.copy(frequency = Weekly(f)))
  val qualityField   = field[Quality](_.quality, q => _.copy(quality = q))
  val deviceField    = field[Device](_.device, d => _.copy(device = d))
  val networkField   = field[Network](_.network, n => _.copy(network = n))

  // Bitrate values have been taken from this article:
  // https://teradek.com/blogs/articles/what-is-the-optimal-bitrate-for-your-resolution
  sealed abstract class Quality(val bitrate: DataRate)
  object Quality {
    case object `480p` extends Quality(MegabytesPerSecond(1.5 / 8))
    case object `1080p` extends Quality(MegabytesPerSecond(5 / 8d))
    implicit val values: Enum[Quality] = Enum.derived
  }

  // Usage impact taken from this sheet:
  // https://docs.google.com/spreadsheets/d/1IF92yVXX6t_qXa4SFsngko4OKfQ92lYxlnF2L7rOrYo/edit#gid=228945235
  sealed abstract class Device(val power: Power)
  object Device {
    case object Smartphone extends Device(Kilowatts(1.1E-04 * 60))
    case object Laptop extends Device(Kilowatts(3.2E-04 * 60))
    implicit val values: Enum[Device] = Enum.derived
  }

  sealed abstract class Network(val energyPerInformation: EnergyPerInformation)
  object Network {
    case object Fixed extends Network(KilowattHoursPerByte((4.29E-10 /* Wired */ + 1.52E-10 /* WiFi */) / 2))
    case object Mobile extends Network(KilowattHoursPerByte(8.84E-10))
    implicit val values: Enum[Network] = Enum.derived
  }

  sealed trait Region {
    def gge(energy: Energy): Mass
  }
  object Region {
    case object World extends Region {
      def gge(energy: Energy): Mass =
        Kilograms(energy.to(KilowattHours) * 0.519)
    }
  }

  def init: Model = Model(
    Minutes(30),
    Weekly(5),
    Quality.`1080p`,
    Device.Laptop,
    Network.Fixed
  )

  def view(form: Form): Html[Update] =
    div()(
      text("Watching an online video of "),
      form.number(durationField),
      text(" minutes, "),
      form.number(frequencyField),
      text(s" times a week, in "),
      form.enum(qualityField),
      text(", from a "),
      form.enum(deviceField),
      text(", with a "),
      form.enum(networkField),
      text(" connection.")
    )

}
