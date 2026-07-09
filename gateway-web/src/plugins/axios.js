"use strict";

import { clearStore } from "./locastorage";
import axios from "axios";
import JSONBIG from "json-bigint";
import { Message } from "element-ui";
import { Loading } from "element-ui";
import { hasProperty } from "./common.js";
import _debounce from "lodash/debounce";
import { DonMessage } from "./messageOnce.js";
// baseURL: 'http://localhost:9010',
var MessageOnce = new DonMessage();
const showErrorNotification = _debounce((data) => {
  Message({ message: data.message, type: data.type, duration: 5 * 1000 });
});
//baseURL: 'http://10.1.100.84:9999/metedisaster'
var service = axios.create({
  baseURL: "",
  withCredentials: true, // 跨域请求时发送Cookie
  cancelRequest: true,
  timeout: 1000 * 30 * 10,
  transformResponse: [
    (data) => {
      //data是原始字符串数据
      try {
        return JSONBIG.parse(data);
      } catch (error) {
        return data;
      }
    },
  ],
});
let loadingInstance;
service.interceptors.request.use(
  function (config) {
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  },
);

// Add a response interceptor
service.interceptors.response.use(
  function (response) {
    if (loadingInstance) {
      loadingInstance.close();
    }
    // debugger;
    if (response.data.code == 500) {
      MessageOnce.error({ message: response.data.msg });
      // showErrorNotification({
      //     message: response.data.msg,
      //     type: "error",
      // });
    } else if (response.data.code == 403) {
      //没有权限
      // showErrorNotification({
      //     message: "无权限",
      //     type: "error",
      // });
      MessageOnce.error({ message: "无权限！" });
      // Message({
      //     showClose: true,
      //     message: "无权限！",
      //     type: "error",
      // });
    } else if (response.data.code == 9000) {
      //登录失效
      // Message({
      //     showClose: true,
      //     message: response.data.msg,
      //     type: "error",
      // });
      MessageOnce.error({ message: response.data.msg });
      // showErrorNotification({
      //     message: response.data.msg,
      //     type: "error",
      // });
      location.href = location.pathname;
    } else if (response.data.code == 706 || response.data.code == 711) {
      // 未登录或登录超时 711token值无效，请重新登录
      // Message({
      //     showClose: true,
      //     message: response.data.msg,
      //     type: "error",
      // });
      // showErrorNotification({
      //     message: response.data.msg,
      //     type: "error",
      // });
      MessageOnce.error({ message: response.data.msg });
      location.href = location.pathname;
    } else if (
      response.data.code == 707 ||
      response.data.code == 708 || //密码错误
      response.data.code == 709 || //账户已锁定
      response.data.code == 710 || //请求格式错误
      response.data.code == 712
    ) {
      //  707用户不存在
      // Message({
      //     showClose: true,
      //     message: response.data.msg,
      //     type: "error",
      // });
      // showErrorNotification({
      //     message: response.data.msg,
      //     type: "error",
      // });
      MessageOnce.error({ message: response.data.msg });
    } else {
      return Promise.resolve(response);
    }
    // return response;
  },
  function (error) {
    console.log("axios-1", error);
    if (loadingInstance) {
      loadingInstance.close();
    }
    console.log("axios", error);
    if (error.response) {
      if (error.response.status == 403) {
        // showErrorNotification({
        //     message: "无权限！",
        //     type: "error",
        // });
        MessageOnce.error({ message: "无权限！" });
        // location.href = location.pathname;
        // clearStore();
      }
    }
    return error;
  },
);

export default {
  get(url, data, header) {
    return new Promise((resolve) => {
      //全局loading
      loadingInstance = Loading.service({
        fullscreen: true,
        background: "rgba(0, 0, 0, 0.7)",
        text: "请稍后",
      });
      service
        .get(
          url,
          {
            data: data,
          },
          header,
        )
        .then(
          (res) => {
            // if (res.data.code == 9000) {
            //   //this.$message.error('登录失效，请重新登录');
            //   location.href = '/';
            // }
            resolve(res);
          },
          (err) => {
            if (err.response) {
              Message({
                showClose: true,
                message: err.response.data.message,
                type: "error",
              });
            }
          },
        );
    });
  },
  post(url, data, header) {
    return new Promise((resolve) => {
      //全局loading
      loadingInstance = Loading.service({
        fullscreen: true,
        background: "rgba(0, 0, 0, 0.7)",
        text: "请稍后",
      });
      //将分页加到url里传参
      // current: this.pageNum,
      // size: this.pageSize,
      //   if (Object.prototype.toString.call(data) === "[object Object]") {
      //     const current = hasProperty(data, "current");
      //     const size = hasProperty(data, "size");
      //     url = url + "?current=" + current + "&size=" + size;
      //   }
      service.post(url, data, header).then(
        (res) => {
          //   if (res.data.code == 9000) {
          //     //this.$message.error('登录失效，请重新登录');
          //     location.href = '/';
          //   }
          resolve(res);
        },
        (err) => {
          if (err.response) {
            Message({
              showClose: true,
              message: err.response.data.message,
              type: "error",
            });
          }
        },
      );
    });
  },
  postNL(url, data, header) {
    return new Promise((resolve) => {
      service.post(url, data, header).then(
        (res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        },
        (err) => {
          if (err.response) {
            Message({
              showClose: true,
              message: err.response.data.message,
              type: "error",
            });
          }
        },
      );
    });
  },
  put(url, data, header) {
    return new Promise((resolve) => {
      //全局loading
      loadingInstance = Loading.service({
        fullscreen: true,
        background: "rgba(0, 0, 0, 0.7)",
        text: "请稍后",
      });
      service.put(url, data, header).then(
        (res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        },
        (err) => {
          if (err.response) {
            Message({
              showClose: true,
              message: err.response.data.message,
              type: "error",
            });
          }
        },
      );
    });
  },
  delete(url, data, header) {
    return new Promise((resolve) => {
      //全局loading
      loadingInstance = Loading.service({
        fullscreen: true,
        background: "rgba(0, 0, 0, 0.7)",
        text: "请稍后",
      });
      service.delete(url, data, header).then(
        (res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        },
        (err) => {
          if (err.response) {
            Message({
              showClose: true,
              message: err.response.data.message,
              type: "error",
            });
          }
        },
      );
    });
  },
  Lpost(url, data, header) {
    return new Promise((resolve) => {
      //全局loading
      loadingInstance = Loading.service({
        fullscreen: true,
        background: "rgba(0, 0, 0, 0.7)",
        text: "请稍后",
      });
      service.post(url, data, header).then(
        (res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        },
        (err) => {
          if (err.response) {
            Message({
              showClose: true,
              message: err.response.data.message,
              type: "error",
            });
          }
        },
      );
    });
  },
  download(url, data, header) {
    return new Promise((resolve, reject) => {
      axios({
        method: "get",
        url: url,
        data: data,
        responseType: "blob", // 注意返回的数据格式
        headers: header,
      })
        .then((res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },
  postDownload(url, data, header) {
    return new Promise((resolve, reject) => {
      axios({
        method: "post",
        url: url,
        data: data,
        responseType: "blob", // 注意返回的数据格式
        headers: header,
      })
        .then((res) => {
          //   if (res.data.code == 9000) {
          //     this.$message.error("登录失效，请重新登录");
          //     location.href = '/';
          //   }
          resolve(res);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },
};
