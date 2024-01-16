let GATEWAY_URL = "/gatewayservice";
let strArr = document.location.pathname.match(new RegExp("\\w*(?=/web)", "g"));
if (strArr && strArr.length) {
  if (strArr.length == 2) {
    GATEWAY_URL = "/" + strArr[0];
  } else if (strArr.length == 3) {
    GATEWAY_URL = "/" + strArr[1];
  } else {
    GATEWAY_URL = "";
  }
}

var $url = {
  DATA_URL: "http://" + window.location.host + GATEWAY_URL,
};
