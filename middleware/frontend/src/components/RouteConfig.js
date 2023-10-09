import AllCampaigns from "./AllCampaigns";
import Executions from "./Executions";
import Errors from "./Errors";
import TransferredRecipients from "./TransferredRecipients";
const RouteConfig = [
  {
    path: "/executions/:campaignId?/:deliveryId?",
    component: Executions,
  },
  {
    path: "/errors/:oid?",
    component: Errors,
  },
  {
    path: "/transferred-recipients/:oid?",
    component: TransferredRecipients,
  },
  {
    path: "/",
    component: AllCampaigns,
  },
];

export default RouteConfig;
