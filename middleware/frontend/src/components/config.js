module.exports = global.config = {
  url: {
    allCampaigns:
      process.env.REACT_APP_BASE_URL +
      "service/execution/status/getPage?page=0&size=15",
    campaigns:
      process.env.REACT_APP_BASE_URL +
      "service/execution/status/details/getPage?",
    transferredRecipients:
      process.env.REACT_APP_BASE_URL +
      "service/execution/status/transferred-recipients/getPage?",
    errors:
      process.env.REACT_APP_BASE_URL +
      "service/execution/status/error/getPage?",
    security: process.env.REACT_APP_BASE_URL + "security/authenticate",
  },
  requestHeaders: {
    "Content-Type": "application/json",
    Authorization: "Basic " + process.env.REACT_APP_HEADERS_AUTH,
  },
  backgroundColor: ["#4E9CD2", "#4A285F", "#A4021E", "#72C6E3", "#016C7D"],
};
