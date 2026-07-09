import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "./plugins/axios";
import urlConfig from "../public/config/config";

import "./utils/base.css";
import "lib-flexible";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
//图片
import Viewer from "v-viewer";
import "viewerjs/dist/viewer.css";
//视频
import Video from "video.js";
import "video.js/dist/video-js.css";

import store from "../src/store/index.js";

import cookie from "./utils/cookie.js";
Vue.prototype.cookie = cookie;

// import * as echarts from 'echarts'
// Vue.prototype.$echarts = echarts
// 引入自定义拖拽 -- v-drag
import "./plugins/directives.js";
// import "./plugins/directivesItem.js";
//主题样式
import "@/styles/index.scss";

Vue.use(ElementUI);
Vue.config.productionTip = false;
Vue.prototype.$post = axios.post;
Vue.prototype.$postNL = axios.postNL;
Vue.prototype.$get = axios.get;
Vue.prototype.$put = axios.put;
Vue.prototype.$delete = axios.delete;
Vue.prototype.$Lpost = axios.Lpost;
// Vue.prototype.$url=urlConfig
Vue.prototype.$download = axios.download;
Vue.prototype.$postDownload = axios.postDownload;
//路由拦截
// router.beforeEach((to,from,next)=>{
//   //如果跳转的页面不存在，跳转到404页面
//   if(to.matched.length<=0){
//     //获取本地缓存的路由数据
//     let routerArr=window.sessionStorage.getItem('newRouter')
//     if(cookie.getCookie("username")&&routerArr){
//       let asyncRouterMap=filterAsyncRouter(JSON.parse(routerArr))
//       router.addRoutes(asyncRouterMap)
//       next(to.fullPath)
//     }
//     else{
//       next('/404')
//     }
//   }
//   else{
//     if(cookie.getCookie("username")){
//       next()
//     }else{
//       if(to.path=="/login"){
//         next()
//       }
//       else{
//         next('/login')
//       }

//     }
//   }

// })
function filterAsyncRouter(rows) {
    let dataArr = [];
    for (let i = 0; i < rows.length; i++) {
        let res = rows[i];
        let children = [];
        if (res.children && res.children.length > 0) {
            children = filterAsyncRouter(res.children);
        }
        dataArr.push({
            path: res.path,
            name: res.name,
            component: () =>
                import (`@/pages/${res.url}`),
            children: children,
        });
    }
    return dataArr;
}
const eventBus = new Vue(); // 创建事件总线
new Vue({
    beforeCreate() {
        Vue.prototype.$bus = this;
    },
    render: (h) => h(App),
    router,
    store, //每个组件可以通过this.$store拿到对应的store对象
    Video,
    Viewer,
    // provide: {
    //     eventBus // 将事件总线提供给整个应用
    // },
    defaultOptions: {
        zIndex: 9999,
    },
}).$mount("#app");