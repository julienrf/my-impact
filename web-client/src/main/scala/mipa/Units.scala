package mipa

import squants.energy.{Energy, KilowattHours}
import squants.information.Information
import squants.mass.{Grams, Mass}
import squants.motion.{Distance, MassFlowUnit}
import squants.time.{Days, FrequencyUnit}
import squants.{Dimension, PrimaryUnit, Quantity, SiUnit, UnitOfMeasure}

final class LinearDensity private (val value: Double, val unit: LinearDensityUnit)
  extends Quantity[LinearDensity] {

  def dimension: Dimension[LinearDensity] = LinearDensity

  def * (that: Distance): Mass = Grams(this.to(GramsPerKilometer) * that.toKilometers)

}

object LinearDensity extends Dimension[LinearDensity] {
  private[mipa] def apply[A](n: A, unit: LinearDensityUnit)(implicit num: Numeric[A]): LinearDensity =
    new LinearDensity(num.toDouble(n), unit)

  def name = "LinearDensity"
  def units = Set(GramsPerKilometer)
  def primaryUnit = GramsPerKilometer
  def siUnit = GramsPerKilometer
}

trait LinearDensityUnit extends UnitOfMeasure[LinearDensity] {
  def apply[N](n: N)(implicit num: Numeric[N]): LinearDensity =
    LinearDensity(n, this)
}

object GramsPerKilometer extends LinearDensityUnit with PrimaryUnit with SiUnit {
  def symbol: String = "g/km"
}

object Weekly extends FrequencyUnit {
  def symbol: String = "weekly"
  def conversionFactor: Double = 1d / Days(7).toSeconds
}

object Yearly extends FrequencyUnit {
  def symbol: String = "yearly"
  def conversionFactor: Double = 1d / Days(365.25).toSeconds
}

object KilogramsPerYear extends MassFlowUnit {
  def symbol: String = "kg/year"
  def conversionFactor: Double = 1d / Days(365.25).toSeconds
}

final class EnergyPerInformation private[mipa] (val value: Double, val unit: EnergyPerInformationUnit)
  extends Quantity[EnergyPerInformation] {

  def dimension: Dimension[EnergyPerInformation] = EnergyPerInformation

  def * (that: Information): Energy =
    KilowattHours(this.to(KilowattHoursPerByte) * that.toBytes)

}

object EnergyPerInformation extends Dimension[EnergyPerInformation] {
  private[mipa] def apply[A](n: A, unit: EnergyPerInformationUnit)(implicit num: Numeric[A]): EnergyPerInformation =
    new EnergyPerInformation(num.toDouble(n), unit)
  def name = "EnergyPerInformation"
  def units = Set(KilowattHoursPerByte)
  def primaryUnit = KilowattHoursPerByte
  def siUnit = KilowattHoursPerByte
}

trait EnergyPerInformationUnit extends UnitOfMeasure[EnergyPerInformation] {
  def apply[N](n: N)(implicit num: Numeric[N]): EnergyPerInformation =
    EnergyPerInformation(num.toDouble(n), this)
}

object KilowattHoursPerByte extends EnergyPerInformationUnit with PrimaryUnit with SiUnit {
  def symbol: String = "kWh/byte"
}
