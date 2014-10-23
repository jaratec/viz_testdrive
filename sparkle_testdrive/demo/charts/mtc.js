require(["lib/d3", "sg/dashboard", "sg/sideAxis", "sg/palette", "sg/linePlot"],
  function(_d3, dashboard, sideAxis, palette, linePlot) {

  var mtcChart = {
    title: "Milk, Tea and Coffee Consumption in USA",
    // transformName: "Raw",
    groups: [{
      label: "Gallons/capita",
      axis: sideAxis(),
      // colors: ["gray", "darkgreen", "darkbrown"],
      // colors: ["#C0C0C0", "#006400", "#800000"],
      // colors: d3.scale.ordinal.range(["#006400"]),
      named: [
        {name: "milk-tea-coffee/Milk"},
        {name: "milk-tea-coffee/Tea"},
        {name: "milk-tea-coffee/Coffee"}
      ]
    }]
  };

  var mtcBoard = dashboard().size([1000,600]),
      update = d3.selectAll("body").data([{charts:[mtcChart]}])

  mtcBoard(update);
});
