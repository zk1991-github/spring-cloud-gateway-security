let  GATEWAY_URL = '/';
debugger
let strArr =document.location.pathname.match(new RegExp("\\w*(?=/web)","g"));
if(strArr && strArr.length){
  if(strArr.length == 2){
    GATEWAY_URL = '/'+strArr[0]
  }else if(strArr.length == 3){
    GATEWAY_URL = '/'+strArr[1]
  }else{
    GATEWAY_URL='';
  }
}

var $url = {
  // 后台API 请求数据使用
  DATA_URL: 'http://'+window.location.host + GATEWAY_URL,
};
// export default $url