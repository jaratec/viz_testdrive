
import io.continuum.bokeh._
import scala.util.Random

object ScatterPlot extends App with Tools {

  def rnd(i: Int) = (1 to i).map(_ => Random.nextDouble)

  val x = rnd(50)
  val y = rnd(50)

  val source = new ColumnDataSource()
      .addColumn('x, x)
      .addColumn('y, y)

  val xdr = new DataRange1d().sources(source.columns('x) :: Nil)
  val ydr = new DataRange1d().sources(source.columns('y) :: Nil)

  val plot = new Plot().x_range(xdr).y_range(ydr).tools(Pan|WheelZoom)

  val xaxis = new LinearAxis().plot(plot).location(Location.Below)
  val yaxis = new LinearAxis().plot(plot).location(Location.Left)
  plot.below <<= (xaxis :: _)
  plot.left <<= (yaxis :: _)

  val circle = new Glyph().data_source(source)
      .glyph(new Circle().x('x).y('y).size(5).fill_color(Color.AquaMarine).line_color(Color.Black))

  plot.renderers := List(xaxis, yaxis, circle)

  val document = new Document(plot)
  val html = document.save("scatterplot.html")
  println(s"Wrote ${html.file}. Open ${html.url} in a web browser.")
  html.view()
}

