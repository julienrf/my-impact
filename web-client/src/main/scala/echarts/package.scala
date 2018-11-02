package echarts

import org.scalajs.dom.raw.{HTMLCanvasElement, HTMLDivElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}
import scala.scalajs.js.{UndefOr, |}

@js.native
@JSImport("echarts", JSImport.Default)
object module extends js.Object {

  def init(
    dom: HTMLDivElement | HTMLCanvasElement,
    theme: UndefOr[js.Object | String] = js.undefined,
    opts: UndefOr[InitOpts] = js.undefined
  ): ECharts = js.native

}

class InitOpts(
  val devicePixelRatio: UndefOr[Double] = js.undefined,
  val renderer: UndefOr[String] = js.undefined,
  val width: UndefOr[Double | String] = js.undefined,
  val height: UndefOr[Double | String] = js.undefined
) extends js.Object

@js.native
trait ECharts extends js.Object {
  def setOption(option: OptionOpts): Unit = js.native
  def on(eventName: String, handler: js.Function1[MouseEvent, Unit]): Unit = js.native
  def on(eventName: String, query: String | js.Object, handler: js.Function1[MouseEvent, Unit]): Unit = js.native
}

class OptionOpts(
  val title: UndefOr[TitleOpts] = js.undefined,
  val xAxis: UndefOr[AxisOpts] = js.undefined,
  val yAxis: UndefOr[AxisOpts] = js.undefined,
  val series: UndefOr[js.Array[SeriesOpt]] = js.undefined,
  val color: UndefOr[js.Array[String]] = js.undefined
) extends js.Object

class TitleOpts(val text: UndefOr[String] = js.undefined) extends js.Object

class AxisOpts(
  val data: UndefOr[js.Array[js.Any]] = js.undefined,
  val name: UndefOr[String] = js.undefined
) extends js.Object

class SeriesOpt(
  val name: UndefOr[String] = js.undefined,
  val `type`: String,
  val data: js.Array[js.Any]
) extends js.Object

trait MouseEvent extends js.Object {
  def dataIndex: Int
}
