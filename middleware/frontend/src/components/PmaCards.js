import React from "react";
import PieChart from "./PieChart";

const PmaCards = (props) => {
  const chartData = {
    labels: [props.data.numberLabel, props.data.totalLabel],
    datasets: {
      data: [props.data.number, props.data.total],
      backgroundColor: props.data.bgColors,
    },
    height: 300,
    width: 300,
    options: {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        display: false,
      },
      scales: {
        y: {
          display: false,
        },
      },
    },
  };

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">{props.data.text}</h5>
        <div className="card-chart">
          <PieChart data={chartData} />
        </div>
        <h6 className="card-subtitle mb-2 text-muted">
          {props.data.percentage} of {props.data.numberLabel} created on PMA
        </h6>
        <h6 className="card-subtitle mb-2 text-muted">
          {props.data.number} out of {props.data.total} created on PMA{" "}
        </h6>
      </div>
    </div>
  );
};

export default PmaCards;
