import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Table from "./Table";
import helpers from "./helpers";
import "./config";

const TransferredRecipients = () => {
  let { oid } = useParams();
  // eslint-disable-next-line
  const [content, setContent] = useState("");
  let contObj = [];

  useEffect(() => {
    let url = global.config.url.transferredRecipients;
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
        count: content[i].recipientsCount,
        time: helpers.formatDate(content[i].time),
      });
    }

    setContent(contObj);
  }
  const tableData = {
    columns: [
      {
        label: "Count",
        field: "count",
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
    rows: content,
  };

  return (
    <div className="">
      <div className="row justify-content-md-center">
        <div className="header mt-35">
          <h2 className="title">Transferred Recipients details</h2>
        </div>
      </div>
      <div className="container">
        <Table data={tableData} />
      </div>
    </div>
  );
};
export default TransferredRecipients;
