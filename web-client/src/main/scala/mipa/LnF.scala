package mipa

import scalm.{Elem, Html}
import scalm.Html._

object LnF {

  def cardWithActions[M](content: Elem[M]*)(actions: Elem[M]*): Html[M] =
    div(attr("class", "card"))(
      div(attr("class", "card-content"))(content: _*),
      div(attr("class", "card-action"))(actions: _*)
    )

  def card[M](content: Elem[M]*): Html[M] =
    div(attr("class", "card"))(
      div(attr("class", "card-content"))(content: _*)
    )

}
