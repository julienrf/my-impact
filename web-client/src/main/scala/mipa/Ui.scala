package mipa

import scalm._
import scalm.Html._

object Ui extends scalm.App {

  case class Model(
    showBehavioursSelect: Boolean,
    behaviours: List[BehaviourAndModel],
    selected: Option[BehaviourAndModel]
  ) {
    assert(selected.forall(b => behaviours.exists(_.uuid == b.uuid)))
  }

  sealed trait Msg
  case object ListBehaviours extends Msg
  case class AddInstance(behaviour: Behaviour) extends Msg
  case class RemoveInstance(behaviour: BehaviourAndModel) extends Msg
  case class Select(behaviour: BehaviourAndModel) extends Msg
  case object Close extends Msg
  case class BehaviourMsg(inner: BehaviourAndMsg) extends Msg

  def init: (Model, Cmd[Msg]) = {
    val model = Model(
      showBehavioursSelect = false,
      BehaviourAndModel.newInstance(VideoStreaming) ::
//      BehaviourAndModel.newInstance(Flying) ::
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
          div(attr("class", "col s12"), style(Style("position", "relative")))(
            Charts.echartsElem(model.behaviours)(Select),
            if (model.showBehavioursSelect) {
              span()(
                div(
                  attr("class", "modal open"),
                  style(Style("z-index", "1003"), Style("display", "block"), Style("opacity", "1"), Style("top", "10%"))
                )(
                  tag("div")(attr("class", "collection"))(
                    Behaviour.all.map { behaviour =>
                      tag("a")(
                        attr("class", "collection-item waves-effect"),
                        onClick(AddInstance(behaviour))
                      )(
                        text(behaviour.label)
                      )
                    }: _*
                  )
                ),
                div(
                  attr("class", "modal-overlay"),
                  style(Style("z-index", "1002"), Style("opacity", "0.5"), Style("display", "block"))
                )()
              )
            } else {
              tag("a")(
                attr("title", "Add a behaviour"),
                attr("class", "btn-floating btn-large waves-effect waves-light red"),
                style(Style("position", "absolute"), Style("right", "1em"), Style("top", "300px")),
                onClick(ListBehaviours)
              )(
                tag("i")(attr("class", "material-icons"))(text("add"))
              )
            }
          )
        ),
        model.selected match {
          case None => Elem.Empty
          case Some(behaviour) =>
            div(attr("class", "row"))(
              div(attr("class", "col s12"))(
                LnF.cardWithActions[Msg](
                  tag("p")()(
                    behaviour.view.map(BehaviourMsg)
                  ),
                  tag("p")()(
                    text("Source: "),
                    tag("a")(
                      attr("href", behaviour.source.url)
                    )(text(behaviour.source.label))
                  )
                )(
                  tag("a")(
                    attr("class", "waves-effect waves-teal btn-flat"),
                    onClick(Close)
                  )(text("Close")),
                  tag("a")(
                    attr("class", "waves-effect btn red"),
                    onClick(RemoveInstance(behaviour))
                  )(
                    tag("i")(attr("class", "material-icons"))(text("delete"))
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
    case ListBehaviours => (model.copy(showBehavioursSelect = true), Cmd.Empty)
    case AddInstance(behaviour) =>
      val instance = BehaviourAndModel.newInstance(behaviour)
      val updatedModel =
        model.copy(
          showBehavioursSelect = false,
          behaviours = model.behaviours :+ instance,
          selected = Some(instance)
        )
      (updatedModel, Cmd.Empty)
    case RemoveInstance(behaviour) =>
      val updatedModel =
        model.copy(
          behaviours = model.behaviours.filter(_.uuid != behaviour.uuid),
          selected = None
        )
      (updatedModel, Cmd.Empty)
    case Select(behaviour) =>
      if (model.behaviours.exists(_.uuid == behaviour.uuid))
        (model.copy(selected = Some(behaviour)), Cmd.Empty)
      else
        (model, Cmd.Empty)
    case BehaviourMsg(inner) =>
      model.selected match {
        case Some(inner((behaviour, innerMsg))) =>
          val updated = behaviour.update(innerMsg)
          (model.copy(
            behaviours = model.behaviours.map(bm => if (bm.uuid == behaviour.uuid) updated else bm),
            selected = Some(updated)
          ), Cmd.Empty)
        case _ => (model, Cmd.Empty)
      }
    case Close => (model.copy(selected = None), Cmd.Empty)
  }

  def subscriptions(model: Model): Sub[Msg] = Sub.Empty

}
