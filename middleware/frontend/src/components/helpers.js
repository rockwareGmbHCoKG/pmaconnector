const helpers = {
  formatDate: function (dateStr) {
    var date = new Date(dateStr);
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? "pm" : "am";
    hours = hours % 12;
    hours = hours ? hours : 12; // the hour '0' should be '12'
    minutes = minutes < 10 ? "0" + minutes : minutes;
    var strTime = hours + ":" + minutes + " " + ampm;
    return (
      date.getMonth() +
      1 +
      "/" +
      date.getDate() +
      "/" +
      date.getFullYear() +
      " " +
      strTime
    );
  },
  formatDuration: function (duration) {
    var date = new Date(duration);
    date.setSeconds(60); // specify value for SECONDS here
    var timeString = date.toISOString().substr(11, 8);
    return timeString;
  },
};

export default helpers;
