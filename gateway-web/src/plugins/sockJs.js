import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import urlConfig from '../../public/config/config'
import Vue from 'vue'
//sockJs连接
export function connect(){
  return new Promise((resolve)=>{
    // 建立连接对象
    let socket = new SockJS(urlConfig.DATA_URL+'/websocket');
    // 获取STOMP子协议的客户端对象
    let stompClient = Stomp.over(socket);
    // 定义客户端的认证信息,按需求配置
    let headers = {
      Authorization: ''
    }
    // 向服务器发起websocket连接
    stompClient.connect(headers, () => {
      console.log('连接成功')
      Vue.prototype.$stompClient = stompClient
      resolve({code:'success',msg:'连接成功'})
    }, (err) => {
      resolve({code:'error',msg:'连接失败'})
      // 连接发生错误时的处理函数
      console.log('失败')
      console.log(err);
      //重连
      connect()
    });
    
  })
    
}
