import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Table from "./Table";
import helpers from "./helpers";
import "./config";

const Executions = () => {
  let { campaignId, deliveryId } = useParams();
  // eslint-disable-next-line
  const [content, setContent] = useState("");
  let contObj = [];

  useEffect(() => {
    let url = global.config.url.campaigns;
    let params = "page=0&size=15";
    if (campaignId) {
      params = params + "&campaignId=" + campaignId;
    }
    if (deliveryId) {
      params = params + "&deliveryId=" + deliveryId;
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
      let contentObj = content[i],
        errorsLink = "/errors/" + contentObj.oid,
        transferredRecipientLink = "/transferred-recipients/" + contentObj.oid,
        extractionMessages = "",
        transformationMessages = "",
        loadingMessages = "";

      for (var e = 0; e < contentObj.extractionMessages.length; e++) {
        extractionMessages =
          extractionMessages + " " + contentObj.extractionMessages[e];
      }
      for (var t = 0; t < contentObj.transformationMessages.length; t++) {
        transformationMessages =
          transformationMessages + " " + contentObj.transformationMessages[t];
      }

      for (var l = 0; l < contentObj.loadingMessages.length; l++) {
        loadingMessages = loadingMessages + " " + contentObj.loadingMessages[l];
      }
      contObj.push({
        oid: contentObj.oid,
        startTime: helpers.formatDate(contentObj.startTime),
        endTime: helpers.formatDate(contentObj.endTime),
        duration: helpers.formatDuration(contentObj.duration),
        extractionMessages: extractionMessages,
        transformationMessages: transformationMessages,
        loadingMessages: loadingMessages,
        errors: contentObj.errors ? <a href={errorsLink}>Show</a> : "",
        transferredRecipient: contentObj.transferredRecipients ? (
          <a href={transferredRecipientLink}>Show</a>
        ) : (
          ""
        ),
      });
    }
    setContent(contObj);
  }
  const tableData = {
    columns: [
      {
        label: "OID",
        field: "oid",
        sort: "asc",
        width: 150,
      },
      {
        label: "Start Time",
        field: "startTime",
        sort: "asc",
        width: 270,
      },
      {
        label: "End Time",
        field: "endTime",
        sort: "asc",
        width: 200,
      },
      {
        label: "Duration",
        field: "duration",
        sort: "asc",
        width: 100,
      },
      {
        label: "Extraction Messages",
        field: "extractionMessages",
        sort: "asc",
        width: 150,
      },
      {
        label: "Transformation Messages",
        field: "transformationMessages",
        sort: "asc",
        width: 100,
      },
      {
        label: "Loading Messages",
        field: "loadingMessages",
        sort: "asc",
        width: 100,
      },
      {
        label: "Errors",
        field: "errors",
        sort: "asc",
        width: 100,
      },
      {
        label: "Transferred Recipient",
        field: "transferredRecipient",
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
          <h2 className="title">Execution details</h2>
        </div>
      </div>
      <div className="container">
        <Table data={tableData} />
      </div>
    </div>
  );
};
export default Executions;
