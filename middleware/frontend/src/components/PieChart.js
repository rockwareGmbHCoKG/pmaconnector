import React from "react";
import { Pie } from "react-chartjs-2";

const PieChart = (props) => {
  return (
    <div>
      <Pie
        data={{
          labels: props.data.labels,
          datasets: [
            {
              data: props.data.datasets.data,
              backgroundColor: props.data.datasets.backgroundColor,
            },
          ],
        }}
        height={props.data.height}
        width={props.data.width}
        options={props.data.options}
      />
    </div>
  );
};
export default PieChart;
