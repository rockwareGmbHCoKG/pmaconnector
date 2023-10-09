import React from "react";
import { Bar } from "react-chartjs-2";

const options = {
  scales: {
    yAxes: [
      {
        ticks: {
          beginAtZero: true,
        },
      },
    ],
  },
};
const BarChart = (props) => (
  <>
    <Bar data={props.data} options={options} />
  </>
);

export default BarChart;
