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
          yAxis = new AxisOpts(name = "kgCO₂e / year"),
          tooltip = new TooltipOpts(),
          series = js.Array(
            behaviours.flatMap { behaviour =>
              behaviour.footprint.map { case (label, footprint) =>
                new SeriesOpt(
                  name = label,
                  `type` = "bar",
                  stack = "footprint",
                  data = js.Array(behaviours.map { b =>
                    (
                      if (b == behaviour) footprint
                      else 0.0
                    ): js.Any
                  }: _*)
                )
              }
            }: _*
          ),
          color = js.Array("#f44336", "#009688", "#3f51b5", "#8bc34a", "#ffc107", "#795548")
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
