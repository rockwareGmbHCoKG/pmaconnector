import React from "react";
import { MDBDataTable } from "mdbreact";
import "../assets/scss/tables.scss";

const Table = (props) => {
  return <MDBDataTable striped bordered hover data={props.data} />;
};
export default Table;
