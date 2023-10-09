import React, { useState, useEffect } from "react";
import Table from "./Table";
import BarChart from "./BarChart";
import PmaCards from "./PmaCards";
import "./config";
import helpers from "./helpers";

const AllCampaigns = () => {
  // eslint-disable-next-line
  const [tableContent, setTableContent] = useState("");
  const [campaignCreated, setCampaignCreated] = useState("");
  const [deliveryCreated, setDeliveryCreated] = useState("");
  const [fieldsDefinitionDone, setFieldsDefinitionDone] = useState("");

  let contObj = [],
    campaignCreatedObj = 0,
    deliveryCreatedObj = 0,
    fieldsDefinitionDoneObj = 0;

  useEffect(() => {
    const url = global.config.url.allCampaigns;
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
      let link =
        "/executions/" +
        content[i].id.campaignId +
        "/" +
        content[i].id.deliveryId;
      contObj.push({
        campaign_id: content[i].id.campaignId,
        campaign_Name: content[i].campaignName,
        campaign_start_date: helpers.formatDate(content[i].campaignStartDate),
        campaign_end_date: helpers.formatDate(content[i].campaignEndDate),
        delivery_id: content[i].id.deliveryId,
        delivery_name: content[i].deliveryName,
        is_campaign_created_on_pma: content[i].campaignCreated ? "Yes" : "No",
        created_campaign_id: content[i].createdCampaignId,
        is_delivery_created_on_pma: content[i].deliveryCreated ? "Yes" : "No",
        created_delivery_id: content[i].createdDeliveryId,
        is_fields_definition_done: content[i].fieldsDefinitionDone
          ? "Yes"
          : "No",
        last_execution_time: helpers.formatDate(content[i].lastExecutionTime),
        show_execution: <a href={link}>Show</a>,
      });
      if (content[i].campaignCreated) {
        campaignCreatedObj++;
      }
      if (content[i].deliveryCreated) {
        deliveryCreatedObj++;
      }
      if (content[i].fieldsDefinitionDone) {
        fieldsDefinitionDoneObj++;
      }
    }
    setTableContent(contObj);
    setCampaignCreated(campaignCreatedObj);
    setDeliveryCreated(deliveryCreatedObj);
    setFieldsDefinitionDone(fieldsDefinitionDoneObj);
  }
  const tableData = {
    columns: [
      {
        label: "Campaign ID",
        field: "campaign_id",
        sort: "asc",
        width: 150,
      },
      {
        label: "Campaign Name",
        field: "campaign_Name",
        sort: "asc",
        width: 270,
      },
      {
        label: "Campaign Start Date",
        field: "campaign_start_date",
        sort: "asc",
        width: 200,
      },
      {
        label: "Campaign End Date",
        field: "campaign_end_date",
        sort: "asc",
        width: 100,
      },
      {
        label: "Delivery ID",
        field: "delivery_id",
        sort: "asc",
        width: 150,
      },
      {
        label: "Delivery Name",
        field: "delivery_name",
        sort: "asc",
        width: 100,
      },
      {
        label: "Is Campaign Created On PMA",
        field: "is_campaign_created_on_pma",
        sort: "asc",
        width: 100,
      },
      {
        label: "Created Campaign ID",
        field: "created_campaign_id",
        sort: "asc",
        width: 100,
      },
      {
        label: "Is Delivery Created On PMA",
        field: "is_delivery_created_on_pma",
        sort: "asc",
        width: 100,
      },
      {
        label: "Created Delivery ID",
        field: "created_delivery_id",
        sort: "asc",
        width: 100,
      },
      {
        label: "Is Fields Definition Done",
        field: "is_fields_definition_done",
        sort: "asc",
        width: 100,
      },
      {
        label: "Last Execution Time",
        field: "last_execution_time",
        sort: "asc",
        width: 100,
      },
      {
        label: "Show Executions",
        field: "show_execution",
        sort: "asc",
        width: 100,
      },
    ],
    rows: tableContent,
  };
  const chartData = {
    labels: ["Campaign Created", "Delivery Created", "Fields Definition Done"],
    datasets: [
      {
        label: "# of Votes",
        data: [campaignCreated, deliveryCreated, fieldsDefinitionDone],
        backgroundColor: [
          global.config.backgroundColor[0],
          global.config.backgroundColor[1],
          global.config.backgroundColor[2],
        ],
      },
    ],
  };
  const campaignCreatedData = {
    number: campaignCreated,
    percentage:
      ((campaignCreated / tableContent.length) * 100).toFixed(1) + "%",
    text: "Campaigns created on PMA",
    total: tableContent.length,
    totalLabel: "Campaigns",
    numberLabel: "PMA Campaigns",
    bgColors: [
      global.config.backgroundColor[2],
      global.config.backgroundColor[3],
    ],
  };
  const deliveryCreatedData = {
    number: deliveryCreated,
    percentage:
      ((deliveryCreated / tableContent.length) * 100).toFixed(1) + "%",
    text: "Delivery created on PMA",
    total: tableContent.length,
    totalLabel: "Delivery",
    numberLabel: "PMA Delivery",
    bgColors: [
      global.config.backgroundColor[0],
      global.config.backgroundColor[1],
    ],
  };
  const fieldsDefinitionDoneData = {
    number: fieldsDefinitionDone,
    percentage:
      ((fieldsDefinitionDone / tableContent.length) * 100).toFixed(1) + "%",
    text: "Fields Definition Done",
    total: tableContent.length,
    totalLabel: "Campaigns",
    numberLabel: "Fields Definition Done",
    bgColors: [
      global.config.backgroundColor[4],
      global.config.backgroundColor[0],
    ],
  };
  return (
    <div className="">
      <div className="pma-cards">
        <div className="row">
          <div className="col-lg-4 col-md-6 col-sm-6 col-xs-12">
            <PmaCards data={campaignCreatedData} />
          </div>
          <div className="col-lg-4 col-md-6 col-sm-6 col-xs-12">
            <PmaCards data={deliveryCreatedData} />
          </div>
          <div className="col-lg-4 col-md-6 col-sm-6 col-xs-12">
            <PmaCards data={fieldsDefinitionDoneData} />
          </div>
        </div>
      </div>
      <div className="row justify-content-md-center">
        <div className="header">
          <h2 className="title">All Campaigns Chart</h2>
        </div>
      </div>
      <div className="container">
        <BarChart data={chartData} />
      </div>
      <div className="row justify-content-md-center">
        <div className="header mt-200">
          <h2 className="title">All Campaigns Table</h2>
        </div>
      </div>
      <div className="row justify-content-md-center">
        <div className="col col-lg-10">
          <Table data={tableData} />
        </div>
      </div>
    </div>
  );
};
export default AllCampaigns;
