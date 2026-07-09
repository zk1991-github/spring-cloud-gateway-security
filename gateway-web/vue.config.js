/*
 * @Author: 于嘉昱 yujiayu@piesat.cn
 * @Date: 2023-10-31 17:49:35
 * @LastEditors: 于嘉昱 yujiayu@piesat.cn
 * @LastEditTime: 2024-04-16 10:08:43
 * @FilePath: \GatewayWeb\vue.config.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
const path = require("path");

function resolve(dir) {
    return path.join(__dirname, dir);
}
const autoprefixer = require("autoprefixer");
const pxtorem = require("postcss-pxtorem");
// const proxy = require('http-proxy-middleware');
module.exports = {
    publicPath: "./",
    assetsDir: "./static",
    lintOnSave: false,
    css: {
        loaderOptions: {
            postcss: {
                plugins: [
                    require("postcss-px2rem")({
                        remUnit: 185,
                    }),
                ],
            },
        },
    },
    // css: {
    //     loaderOptions: {
    //         // pxtorem配置
    //         postcss: {
    //             postcssOptions: {
    //                 plugins: [
    //                     autoprefixer(),
    //                     pxtorem({
    //                         rootValue: 192, //设计稿宽度为1920px
    //                         propList: ["*"], //['*'],
    //                     }),
    //                 ],
    //             },
    //         },
    //     },
    // },
    devServer: {
        host: "0.0.0.0",
        port: "2222",
        open: true,
        proxy: {
            "/gatewayservice": {
                target: "http://127.0.0.1:8888", //服务地址 gatewayservice
                // target: "http://10.2.22.190:8888",
                ws: true,
                changeOrigin: true,
                pathRewrite: { "^/gatewayservice": "" }, //路径从写
            },
        },
    },
    configureWebpack: {
        name: "网关系统",
        resolve: {
            alias: {
                "@": resolve("src"),
            },
        },
    },
};