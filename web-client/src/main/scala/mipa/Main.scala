package mipa

import org.scalajs.dom.document
import scalm._

object Main {

  materializecss.CSS
  materializecss.JS

  def main(args: Array[String]): Unit = {
    Scalm.start(Ui, document.body)
    ()
  }

}
