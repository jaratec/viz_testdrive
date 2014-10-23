
import java.io.{File,InputStream,FileInputStream,InputStreamReader,FileNotFoundException}

import scala.collection.JavaConverters._
import scalax.io.JavaConverters._
import scalax.file.Path

import au.com.bytecode.opencsv.CSVReader
import io.continuum.bokeh._

object MTC extends App with Tools {

  val mtc = TestData.loadData("milk_tea_coffee.tsv")
  val source = new ColumnDataSource()
      .addColumn('year, mtc.year)
      .addColumn('milk, mtc.milk)
      .addColumn('tea, mtc.tea)
      .addColumn('coffee, mtc.coffee)

  val xdr = new DataRange1d().sources(List(source.columns('year)))
  val ydr = new DataRange1d().sources(List(source.columns('milk, 'tea, 'coffee)))

  val milkLine = new Glyph()
                  .data_source(source)
                  .glyph(new Line().x('year).y('milk).line_color(Color.White))
  val teaLine = new Glyph()
                  .data_source(source)
                  .glyph(new Line().x('year).y('tea).line_color(Color.DarkGreen))
  val coffeeLine = new Glyph()
                  .data_source(source)
                  .glyph(new Line().x('year).y('coffee).line_color(Color.Black))

  val plot = new Plot().title("Milk, Tea and Coffee Consumption in USA")
                       .x_range(xdr).y_range(ydr)
                       .background_fill(Color.Silver).border_fill(Color.White)
                       //.background_fill("#f0e1d2").border_fill("#f0e1d2")
  val xaxis = new LinearAxis().axis_label("Year").plot(plot)
  val yaxis = new LinearAxis().axis_label("Gallons per capita").plot(plot)
  plot.below <<= (xaxis :: _)
  plot.left <<= (yaxis :: _)

  val pantool = new PanTool().plot(plot)
  val wheelzoomtool = new WheelZoomTool().plot(plot)

  val legends = Map("milk" -> List(milkLine),
                    "tea"  -> List(teaLine),
                    "coffee"  -> List(coffeeLine))
  val legend = new Legend().plot(plot).legends(legends)

  plot.renderers := List(xaxis, yaxis, milkLine, teaLine, coffeeLine, legend)
  plot.tools := List(pantool, wheelzoomtool)

  val document = new Document(plot)
  val html = document.save("timeseries.html")
  println(s"Wrote ${html.file}. Open ${html.url} in a web browser.")
  html.view()
}

case class MTCData(
    year: List[Int],
    milk: List[Double],
    tea: List[Double],
    coffee: List[Double])

    
// copied and adapted from SampleData, because of
// needed support for tab delimited files (.tsv)
object TestData {

  def loadData(fileName: String): MTCData = {
    val List(year, milk, tea, coffee) = loadRows(fileName).transpose
    MTCData(year.map(_.toInt),
            milk.map(_.toDouble),
            tea.map(_.toDouble),
            coffee.map(_.toDouble))
  }

  def loadRows(fileName: String): List[Array[String]] = load(fileName)

  lazy val dataPath: Path = {
    val home = Path.fromString(System.getProperty("user.home"))
    val path = home / ".bokeh" / "data"
    if (!path.exists) path.createDirectory()
    path
  }

  def load(fileName: String): List[Array[String]] = {
    val inputStream = getStream(fileName)
    if (! inputStream.isDefined) {
      throw new FileNotFoundException(s"can't locate $fileName in resources, nor .bokeh/data")
    }
    val reader = new CSVReader(new InputStreamReader(inputStream.get), '\t', '\'', 1) // skips header
    reader.readAll().asScala.toList
  }

  def getStreamFromResources(fileName: String): Option[InputStream] = {
    Option(getClass.getClassLoader.getResourceAsStream(fileName))
  }

  def getStreamFromFile(fileName: String): Option[InputStream] = {
    val filePath = dataPath / fileName
    val fileOption = if (filePath.exists) filePath.fileOption else throw new FileNotFoundException(s"can't locate $fileName in .bokeh/data")
    fileOption.map(new FileInputStream(_))
  }

  def getStream(fileName: String): Option[InputStream] = {
    getStreamFromResources(fileName) orElse getStreamFromFile(fileName)
  }
    
}
