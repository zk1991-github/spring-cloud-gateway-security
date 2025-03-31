/*
 * @Author: 于嘉昱 yujiayu@piesat.cn
 * @Date: 2023-10-31 17:49:33
 * @LastEditors: 于嘉昱 yujiayu@piesat.cn
 * @LastEditTime: 2024-03-07 15:59:44
 * @FilePath: \GatewayWeb\public\config\config.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
///gatewayservice
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
    //DATA_URL: GATEWAY_URL,
     DATA_URL: "http://" + window.location.host + GATEWAY_URL,
};