package mipa

import scalm.{Cmd, Elem, Html, Sub}
import scalm.Html._

object Ui extends scalm.App {

  case class Model(
    behaviours: List[BehaviourAndModel],
    selected: Option[BehaviourAndModel]
  )

  sealed trait Msg
  case class Select(behaviour: BehaviourAndModel) extends Msg
  case object Close extends Msg
  trait BehaviourMsg extends Msg {
    val behaviour: Behaviour
    def msg: behaviour.Msg
  }
  object BehaviourMsg {
    def apply(_behaviour: Behaviour): _behaviour.Msg => BehaviourMsg { val behaviour: _behaviour.type } = _msg =>
      new BehaviourMsg {
        val behaviour: _behaviour.type = _behaviour
        def msg = _msg
      }
  }

  def init: (Model, Cmd[Msg]) = {
    val model = Model(
      BehaviourAndModel(VideoStreaming)(VideoStreaming.init) ::
      BehaviourAndModel(Flying)(Flying.init) ::
      Nil,
      None
    )
    (model, Cmd.Empty)
  }

  def view(model: Model): Html[Msg] =
    tag("body")()(
      tag("nav")()(
        div(attr("class", "container"))(
          div(attr("class", "brand-logo"))(text("My Impact"))
        )
      ),
      tag("main")(attr("class", "container"))(
        div(attr("class", "row"))(
          div(attr("class", "col s12"))(
            Charts.echartsElem(model.behaviours)(Select)
          )
        ),
        model.selected match {
          case None => Elem.Empty
          case Some(behaviour) =>
            div(attr("class", "row"))(
              div(attr("class", "col s12"))(
                div(attr("class", "card"))(
                  div(attr("class", "card-content"))(
                    behaviour.behaviour.view(behaviour.model).map(BehaviourMsg(behaviour.behaviour))
                  ),
                  div(attr("class", "ard-action"))(
                    tag("a")(
                      attr("class", "waves-effect waves-teal btn-flat"),
                      onClick(Close)
                    )(text("Close"))
                  )
                )
              )
            )
        }
      ),
      tag("footer")(attr("class", "page-footer"))(
        div(attr("class", "footer-copyright"))(
          div(attr("class", "container"))(
            div(attr("class", "row"))(
              text("© 2018 Julien Richard-Foy")
            )
          )
        )
      )
    )

  def update(msg: Msg, model: Model): (Model, Cmd[Msg]) = msg match {
    case Select(behaviour) => (model.copy(selected = Some(behaviour)), Cmd.Empty)
    case m: BehaviourMsg =>
      model.selected match {
        case Some(b) if b.behaviour eq m.behaviour =>
          val updated = BehaviourAndModel(m.behaviour)(m.behaviour.update(b.model.asInstanceOf[m.behaviour.Model], m.msg))
          (model.copy(
            behaviours = model.behaviours.map(bm => if (bm.behaviour eq m.behaviour) updated else bm),
            selected = Some(b)
          ), Cmd.Empty)
        case _ => (model, Cmd.Empty)
      }
    case Close => (model.copy(selected = None), Cmd.Empty)
  }

  def subscriptions(model: Model): Sub[Msg] = Sub.Empty

}
