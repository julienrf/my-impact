package mipa

import scalm.Html
import scalm.Html._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationLong
import enum.Enum

object VideoStreaming extends Behavior {

  val label = "Video Streaming"

  val source = Source(
    "https://theshiftproject.org/wp-content/uploads/2018/10/2018-10-04_Rapport_Pour-une-sobri%C3%A9t%C3%A9-num%C3%A9rique_Rapport_The-Shift-Project.pdf",
    "Rapport — Pour une sobriété numérique, The Shift Project"
  )

  case class Model(
    duration: FiniteDuration,
    frequency: Int, // Times per week
    quality: Quality,
    device: Device,
    network: Network
  ) extends ModelTemplate {

    val label: String = "video"

    val footprint = {
      val minutesPerYear = duration.toMinutes * frequency * 52
      val bytes = quality.bytesForMinutes(minutesPerYear)
      val dataCenterEnergy = bytes * 7.20E-11
      // TODO Refine per world region
      val region = Region.World
      "Device" -> region.ggeForEnergy(device.energyForMinutes(minutesPerYear)) ::
      "Data Center" -> region.ggeForEnergy(dataCenterEnergy) ::
      "Network" -> region.ggeForEnergy(network.energyForBytes(bytes)) ::
      Nil
    }
  }

  val durationField  = field[Long](_.duration.toMinutes, d => _.copy(duration = d.minutes))
  val frequencyField = field[Int](_.frequency, f => _.copy(frequency = f))
  val qualityField   = field[Quality](_.quality, q => _.copy(quality = q))
  val deviceField    = field[Device](_.device, d => _.copy(device = d))
  val networkField   = field[Network](_.network, n => _.copy(network = n))

  // Bitrate values have been taken from this article:
  // https://teradek.com/blogs/articles/what-is-the-optimal-bitrate-for-your-resolution
  sealed abstract class Quality(val bitrate: Double /* Mb/s */) {
    def bytesForMinutes(minutes: Long): Long /* Byte */ =
      math.round(minutes * 60 * bitrate * 1024 * 1024 / 8)
  }
  object Quality {
    case object `480p` extends Quality(1.5)
    case object `1080p` extends Quality(5)
    implicit val values: Enum[Quality] = Enum.derived
  }

  // Usage impact taken from this sheet:
  // https://docs.google.com/spreadsheets/d/1IF92yVXX6t_qXa4SFsngko4OKfQ92lYxlnF2L7rOrYo/edit#gid=228945235
  sealed abstract class Device(val energyPerMin: Double /* kWh/min */) {
    def energyForMinutes(minutes: Long): Double = minutes * energyPerMin
  }
  object Device {
    case object Smartphone extends Device(1.1E-04)
    case object Laptop extends Device(3.2E-04)
    implicit val values: Enum[Device] = Enum.derived
  }

  sealed abstract class Network(val energyPerByte: Double /* kWh/min */) {
    def energyForBytes(bytes: Long): Double = bytes * energyPerByte
  }
  object Network {
    case object Fixed extends Network((4.29E-10 /* Wired */ + 1.52E-10 /* WiFi */) / 2)
    case object Mobile extends Network(8.84E-10)
    implicit val values: Enum[Network] = Enum.derived
  }

  sealed trait Region {
    def ggeForEnergy(energy: Double /* kWh */): Double /* kgCO2e */
  }
  object Region {
    case object World extends Region {
      def ggeForEnergy(energy: Double): Double = energy * 0.519
    }
  }

  def init: Model = Model(
    30.minutes,
    5,
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
