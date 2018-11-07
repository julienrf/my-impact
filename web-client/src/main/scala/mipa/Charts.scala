package mipa

import echarts._
import org.scalajs.dom.raw.HTMLDivElement
import scalm.Hook
import util.Functions.{fun, fun2}
import snabbdom.VNode

import scala.scalajs.js

object Charts {

  def echartsElem[Msg](behaviours: List[BehaviourAndModel])(select: BehaviourAndModel => Msg) = Hook[Msg](dispatch => {

    def init(vNode: VNode): Unit = {
      val el = vNode.elm.asInstanceOf[HTMLDivElement]
      val chart = echarts.module.init(el)
      chart.on("click", e => {
        // TODO error reporting
        behaviours.lift(e.dataIndex).foreach { behaviour =>
          dispatch(select(behaviour))
        }
      })
      val option =
        new OptionOpts(
          title = new TitleOpts(),
          xAxis = new AxisOpts(data = js.Array(behaviours.map(_.label: js.Any): _*)),
          yAxis = new AxisOpts(name = "kgCO₂e"),
          series = js.Array(new SeriesOpt(
            `type` = "bar",
            data = js.Array(behaviours.map(_.footprint: js.Any): _*)
          )),
          color = js.Array("#ee6e73")
        )
      chart.setOption(option)
    }

    def inserted(vnode: VNode): Unit = init(vnode)
    def replaced(old: VNode, vnode: VNode): Unit = init(vnode)

    snabbdom.h("div", js.Dynamic.literal(
      attrs = js.Dynamic.literal(style = "width: 100%; height: 600px;"),
      hook = js.Dynamic.literal(insert = fun(inserted), postpatch = fun2(replaced))
    ))
  })

}
