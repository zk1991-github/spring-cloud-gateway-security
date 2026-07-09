/*
 * @Author: 于嘉昱 yujiayu@piesat.cn
 * @Date: 2024-04-10 09:42:26
 * @LastEditors: 于嘉昱 yujiayu@piesat.cn
 * @LastEditTime: 2024-04-10 16:23:08
 * @FilePath: \gateway-web\src\plugins\common.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
//获取指定父级下的路由
export function getTargetMeun(pid) {
  let routerArr = JSON.parse(window.sessionStorage.getItem("newRouter"));
  return recursiveMsg(pid, routerArr);
}
//递归获取路由
function recursiveMsg(pid, Arr) {
  let objArr = [];
  for (let i = 0; i < Arr.length; i++) {
    if (Arr[i].pid == pid) {
      objArr.push({
        path: Arr[i].path,
        name: Arr[i].name,
        url: Arr[i].url,
        id: Arr[i].id,
        pid: Arr[i].pid,
      });
    }
    if (Arr[i].children && Arr[i].children.length > 0) {
      objArr = objArr.concat(recursiveMsg(pid, Arr[i].children));
    }
  }
  return objArr;
}
//判断当前路由的权限
export function hasPermsion(val) {
  let routerArr = JSON.parse(window.sessionStorage.getItem("newRouter"));
  let k = recursiveHasPerms(val, routerArr, false);
  return k;
}
//递归获取当前路由的权限
function recursiveHasPerms(val, Arr, n) {
  for (let i = 0; i < Arr.length; i++) {
    if (Arr[i].path == val) {
      n = n || true;
    }
    if (Arr[i].children && Arr[i].children.length > 0) {
      n = n || recursiveHasPerms(val, Arr[i].children, n);
    }
  }
  return n;
}
//校验接口权限
export function hasInterface(val) {
  let interfaceArr = JSON.parse(window.sessionStorage.getItem("newInterface"));
  return recursiveInterface(val, interfaceArr);
}

function recursiveInterface(val, Arr) {
  for (let i = 0; i < Arr.length; i++) {
    if (Arr[i].path == val) {
      return true;
    }
  }
  return false;
}
//判断对象是都包含某属性 并返回属性值
export function hasProperty(obj, prop) {
  // 首先检查对象本身是否具有该属性
  if (obj.hasOwnProperty(prop)) {
    //return true;
    return obj[prop];
  }

  // 遍历对象的所有属性值，如果是对象则递归调用hasProperty函数
  for (var key in obj) {
    if (
      Object.prototype.toString.call(obj[key]) === "[object Object]" &&
      obj[key] !== null
    ) {
      if (hasProperty(obj[key], prop)) {
        return obj[key][prop];
      }
    }
  }

  // 如果没有找到属性，则返回false
  return "";
}
