import io.continuum.bokeh._

object Burtin extends App with Tools {

  val antibiotics = """
    bacteria,                        penicillin, streptomycin, neomycin, gram
    Mycobacterium tuberculosis,      800,        5,            2,        negative
    Salmonella schottmuelleri,       10,         0.8,          0.09,     negative
    Proteus vulgaris,                3,          0.1,          0.1,      negative
    Klebsiella pneumoniae,           850,        1.2,          1,        negative
    Brucella abortus,                1,          2,            0.02,     negative
    Pseudomonas aeruginosa,          850,        2,            0.4,      negative
    Escherichia coli,                100,        0.4,          0.1,      negative
    Salmonella (Eberthella) typhosa, 1,          0.4,          0.008,    negative
    Aerobacter aerogenes,            870,        1,            1.6,      negative
    Brucella antracis,               0.001,      0.01,         0.007,    positive
    Streptococcus fecalis,           1,          1,            0.1,      positive
    Staphylococcus aureus,           0.03,       0.03,         0.001,    positive
    Staphylococcus albus,            0.007,      0.1,          0.001,    positive
    Streptococcus hemolyticus,       0.001,      14,           10,       positive
    Streptococcus viridans,          0.005,      10,           40,       positive
    Diplococcus pneumoniae,          0.005,      11,           10,       positive
    """

  //val drugColor = Map("Penicillin" -> Color.DeepPink, "Streptomycin" -> Color.Purple, "Neomycin" -> Color.RoyalBlue)
  //val gramColor = Map("positive" -> Color.LightCoral, "negative" -> Color.Lavender)
  val drugColor = Map("Penicillin" -> "#0d3362", "Streptomycin" -> "#c64737", "Neomycin" -> "#000000")
  val gramColor = Map("positive" -> "#aeaeb8", "negative" -> "#e69584")

  val bacteria = Array("Mycobacterium tuberculosis", "Salmonella schottmuelleri", "Proteus vulgaris", "Klebsiella pneumoniae",
                                  "Brucella abortus", "Pseudomonas aeruginosa", "Escherichia coli", "Salmonella (Eberthella) typhosa",
                                  "Aerobacter aerogenes", "Brucella antracis", "Streptococcus fecalis", "Staphylococcus aureus",
                                  "Staphylococcus albus", "Streptococcus hemolyticus", "Streptococcus viridans", "Diplococcus pneumoniae")
  val penicillin = Array(800.0, 10.0, 3.0, 850.0, 1.0, 850.0, 100.0, 1.0, 870.0, 0.001, 1.0, 0.03, 0.007, 0.001, 0.005, 0.005)
  val streptomycin = Array(5.0, 0.8, 0.1, 1.2, 2.0, 2.0, 0.4, 0.4, 1.0, 0.01, 1.0, 0.03, 0.1, 14.0, 10.0, 11.0)
  val neomycin = Array(2.0, 0.09, 0.1, 1.0, 0.02, 0.4, 0.1, 0.008, 1.6, 0.007, 0.1, 0.001, 0.001, 10.0, 40.0, 10.0)
  val gram = Array("negative", "negative", "negative", "negative", "negative", "negative", "negative", "negative", 
                   "negative", "positive", "positive", "positive", "positive", "positive", "positive", "positive")

  // load data
  val source = new ColumnDataSource()
      .addColumn('bacteria, bacteria)
      .addColumn('penicillin, penicillin)
      .addColumn('streptomycin, streptomycin)
      .addColumn('neomycin, neomycin)
      .addColumn('gram, gram)
  
  val width = 800
  val height = 800
  val innerRadius = 90
  val outerRadius = 300 - 10
  val minr = Math.sqrt(Math.log(1.0)) // sqrt(log(.001 * 1E4))
  val maxr = Math.sqrt(Math.log(1000000.0))
  val a = (outerRadius - innerRadius) / (minr - maxr)
  val b = innerRadius - a * maxr

  def rad(mic: Double) = a * Math.sqrt(Math.log(mic * 1000)) + b
  val nrow = bacteria.size
  val bigAngle = 2.0 * Math.PI / (nrow + 1)
  // val bigAngle = 2.0 * Math.PI / nrow
  val smallAngle = bigAngle / 7
  
  // val x = (0 to nrow).map(_ => 0)
  // val y = (0 to nrow).map(_ => 0)
  
  // annular wedges
  val angles = (0 to (nrow - 1)).map(index => Math.PI/2 - bigAngle/2 - index*bigAngle)
  val colors = (0 to (nrow - 1)).map(index => gramColor(gram(index)))
  val backgroundWedges = (0 to (nrow - 1)).map(index =>
    new Glyph().data_source(source).glyph(new AnnularWedge().x(0).y(0).inner_radius(innerRadius).outer_radius(outerRadius)
      .start_angle(-bigAngle+angles(index)).end_angle(angles(index)).fill_color(colors(index))))
  
  // small wedges
  val quantityWedges = (0 to (nrow - 1)).map(index =>
    new Glyph().data_source(source).glyph(new AnnularWedge().x(0).y(0).inner_radius(innerRadius).outer_radius(rad(penicillin(index)))
      .start_angle(-bigAngle + angles(index) + 5*smallAngle).end_angle(-bigAngle + angles(index) + 6*smallAngle).fill_color(drugColor("Penicillin")))
    :: new Glyph().data_source(source).glyph(new AnnularWedge().x(0).y(0).inner_radius(innerRadius).outer_radius(rad(streptomycin(index)))
      .start_angle(-bigAngle + angles(index) + 3*smallAngle).end_angle(-bigAngle + angles(index) + 4*smallAngle).fill_color(drugColor("Streptomycin")))
    :: new Glyph().data_source(source).glyph(new AnnularWedge().x(0).y(0).inner_radius(innerRadius).outer_radius(rad(neomycin(index)))
      .start_angle(-bigAngle + angles(index) + 1*smallAngle).end_angle(-bigAngle + angles(index) + 2*smallAngle).fill_color(drugColor("Neomycin")))
    :: Nil).flatten
  
  // circular axes and labels
  val labels = (-3 to 2).map(Math.pow(10.0, _))
  val radii = labels.map(lab => a * Math.sqrt(Math.log(lab * 1000)) + b)
  val levelCircles = radii.map(r => new Glyph().data_source(source).glyph(new Circle().x(0).y(0).radius(r)
                                               .line_color(Color.White).fill_color("#f0e1d2").fill_alpha(0.0)))
  val tickLables = (0 to (labels.size - 1)).map(index =>
    new Glyph().data_source(source).glyph(new Text().x(0).y(radii(index)).text(labels(index).toString)
               .angle(0).text_font_size("8pt").text_align(TextAlign.Center).text_baseline(TextBaseline.Middle))
  )
  
  // radial axes
  val radialAxes = angles.map(a =>
    new Glyph().data_source(source).glyph(
      new AnnularWedge().x(0).y(0).inner_radius(innerRadius - 10).outer_radius(outerRadius + 10)
        .start_angle(-bigAngle + a).end_angle(-bigAngle + a).fill_color(Color.Black))
  )
  
  // bacteria labels
  val xr = for (a <- angles) yield radii(0) * Math.cos(-bigAngle/2 + a)
  val yr = for (a <- angles) yield radii(0) * Math.sin(-bigAngle/2 + a)
  val labelAngles = for (a <- angles) yield {
    val v = -bigAngle/2 + a
    if (v < -Math.PI/2) v + Math.PI else v // easier to read labels on the left side
  }
  val bacteriaLabels = (0 to (nrow - 1)).map(index =>
    new Glyph().data_source(source).glyph(
      new Text().x(xr(index)).y(yr(index)).text(bacteria(index))
                .angle(labelAngles(index)).text_font_size("9pt").text_align(TextAlign.Center).text_baseline(TextBaseline.Middle))
  )
  
  // hand drawn legends
  val legendCircles = List(
    new Glyph().data_source(source).glyph(new Circle().x(-40).y(-370).radius(5).line_color(gramColor("positive")).fill_color(gramColor("positive"))),
    new Glyph().data_source(source).glyph(new Circle().x(-40).y(-390).radius(5).line_color(gramColor("negative")).fill_color(gramColor("negative")))
  )
  val gramText = List(
    new Glyph().data_source(source).glyph(new Text().x(-30).y(-370).text("Gram-positive")
               .angle(0).text_font_size("7pt").text_align(TextAlign.Left).text_baseline(TextBaseline.Middle)),
    new Glyph().data_source(source).glyph(new Text().x(-30).y(-390).text("Gram-negative")
               .angle(0).text_font_size("7pt").text_align(TextAlign.Left).text_baseline(TextBaseline.Middle))
  )
  val legendRects = List(
    new Glyph().data_source(source).glyph(new Rect().x(-40).y(18).width(30).height(13)
               .line_color(drugColor("Penicillin")).fill_color(drugColor("Penicillin"))),
    new Glyph().data_source(source).glyph(new Rect().x(-40).y(0).width(30).height(13)
               .line_color(drugColor("Streptomycin")).fill_color(drugColor("Streptomycin"))),
    new Glyph().data_source(source).glyph(new Rect().x(-40).y(-18).width(30).height(13)
               .line_color(drugColor("Neomycin")).fill_color(drugColor("Neomycin")))
  )
  val drugText = List(
    new Glyph().data_source(source).glyph(new Text().x(-15).y(18).text("Penicillin")
               .angle(0).text_font_size("9pt").text_align(TextAlign.Left).text_baseline(TextBaseline.Middle)),
    new Glyph().data_source(source).glyph(new Text().x(-15).y(0).text("Streptomycin")
               .angle(0).text_font_size("9pt").text_align(TextAlign.Left).text_baseline(TextBaseline.Middle)),
    new Glyph().data_source(source).glyph(new Text().x(-15).y(-18).text("Neomycin")
               .angle(0).text_font_size("9pt").text_align(TextAlign.Left).text_baseline(TextBaseline.Middle))
  )

  // plot
  val drx = new Range1d().start(-420).end(420)
  val dry = new Range1d().start(-420).end(420)
  val plot = new Plot().x_range(drx).y_range(dry).title("").background_fill("#f0e1d2").border_fill("#f0e1d2").plot_height(height).plot_width(width)
  // add renderers
/*
  val xaxis = new LinearAxis().axis_label("Year").plot(plot)
  val yaxis = new LinearAxis().axis_label("Gallons per capita").plot(plot)
  plot.below <<= (xaxis :: _)
  plot.left <<= (yaxis :: _)
  plot.renderers := List(xaxis, yaxis, new Glyph().data_source(source).glyph(new Circle().x(0).y(0).radius(10)))
*/
  val renderers = (backgroundWedges ++ quantityWedges ++ levelCircles ++ tickLables ++ radialAxes ++ bacteriaLabels ++ legendCircles ++ gramText ++ legendRects ++ drugText).toList
  // println(renderers)
  // plot.renderers := renderers
  // plot.renderers := (backgroundWedges ++ quantityWedges ++ levelCircles ++ tickLables ++ radialAxes ++ bacteriaLabels ++ legendCircles ++ gramText ++ legendRects ++ drugText).toList
  plot.renderers := (backgroundWedges  ++ quantityWedges ++ levelCircles ++  tickLables ++ radialAxes ++ bacteriaLabels ++ legendCircles ++ gramText ++ legendRects ++ drugText).toList
  // add tools
  val pantool = new PanTool().plot(plot)
  val wheelzoomtool = new WheelZoomTool().plot(plot)
  val boxzoomtool = new BoxZoomTool().plot(plot)
  val resettool = new ResetTool().plot(plot)
  plot.tools := List(pantool, wheelzoomtool, boxzoomtool, resettool)

  val document = new Document(plot)
  val html = document.save("burtin.html")
  println(s"Wrote ${html.file}. Open ${html.url} in a web browser.")
  html.view()
}
