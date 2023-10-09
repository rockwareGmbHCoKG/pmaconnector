import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Table from "./Table";
import "./config";

const Errors = () => {
  let { oid } = useParams();
  // eslint-disable-next-line
  const [errors, setErrors] = useState("");
  let contObj = [];

  useEffect(() => {
    let url = global.config.url.errors;
    let params = "page=0&size=15";
    if (oid) {
      params = params + "&detailsOid=" + oid;
    }
    url = url + params;
    const fetchData = () => {
      try {
        // GET request using fetch with set headers
        const headers = global.config.requestHeaders;
        fetch(url, { headers, method: "get" })
          .then((response) => response.json())
          .then((data) => handleContent(data.content));
      } catch (error) {
        console.log("error", error);
      }
    };

    fetchData();
    // eslint-disable-next-line
  }, []);
  // eslint-disable-next-line
  function handleContent(content) {
    for (var i = 0; i < content.length; i++) {
      contObj.push({
        step: content[i].step,
        resourceName: content[i].resourceName,
        renamedResourceName: content[i].newResourceName
          ? content[i].newResourceName
          : "",
        msg: content[i].message,
        exception: (
          <p className="error-exception-msg">{content[i].exceptionMessage}</p>
        ),
        time: content[i].time,
      });
    }
    setErrors(contObj);
  }
  const tableData = {
    columns: [
      {
        label: "Step",
        field: "step",
        sort: "asc",
        width: 150,
      },
      {
        label: "Resource Name",
        field: "resourceName",
        sort: "asc",
        width: 270,
      },
      {
        label: "Renamed Resource Name",
        field: "renamedResourceName",
        sort: "asc",
        width: 200,
      },
      {
        label: "Message",
        field: "msg",
        sort: "asc",
        width: 100,
      },
      {
        label: "Exception",
        field: "exception",
        sort: "asc",
        width: 150,
      },
      {
        label: "Time",
        field: "time",
        sort: "asc",
        width: 100,
      },
    ],
    rows: errors,
  };
  return (
    <div className="">
      <div className="row justify-content-md-center">
        <div className="header mt-35">
          <h2 className="title">Errors details</h2>
        </div>
      </div>
      <div className="container">
        <Table data={tableData} />
      </div>
    </div>
  );
};
export default Errors;
