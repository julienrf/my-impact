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
  case class BehaviourMsg(inner: BehaviourAndMsg) extends Msg

  def init: (Model, Cmd[Msg]) = {
    val model = Model(
      BehaviourAndModel.init(VideoStreaming) ::
      BehaviourAndModel.init(Flying) ::
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
                    tag("p")()(
                      behaviour.view.map(BehaviourMsg)
                    ),
                    tag("p")()(
                      text("Source: "),
                      tag("a")(
                        attr("href", behaviour.sourceURL)
                      )(text(behaviour.sourceLabel))
                    )
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
    case BehaviourMsg(inner) =>
      model.selected match {
        case Some(inner((behaviour, innerMsg))) =>
          val updated = behaviour.update(innerMsg)
          (model.copy(
            behaviours = model.behaviours.map(bm => if (bm.behaviour == behaviour.behaviour /* Will not work if the same behaviour is compared several times */) updated else bm),
            selected = Some(behaviour)
          ), Cmd.Empty)
        case _ => (model, Cmd.Empty)
      }
    case Close => (model.copy(selected = None), Cmd.Empty)
  }

  def subscriptions(model: Model): Sub[Msg] = Sub.Empty

}
