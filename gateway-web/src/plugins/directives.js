/*
 * @Author: 于嘉昱 yujiayu@piesat.cn
 * @Date: 2024-04-10 09:42:26
 * @LastEditors: 于嘉昱 yujiayu@piesat.cn
 * @LastEditTime: 2024-04-16 15:51:44
 * @FilePath: \gateway-web\src\plugins\directives.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import Vue from "vue";

Vue.directive("drag", {
    bind(el, binding, vnode, oldVnode) {
        const dialogHeaderEl = el.querySelector(".el-dialog__header");
        const dragDom = el.querySelector(".el-dialog");
        dialogHeaderEl.style.cursor = "move";
        // dialogHeaderEl.style.cssText += ';cursor:move;'
        dragDom.style.cssText += ";top:0px;";
        // 获取原有属性 ie dom元素.currentStyle 火狐谷歌 window.getComputedStyle(dom元素, null);
        const sty = (function() {
            if (window.document.currentStyle) {
                return (dom, attr) => dom.currentStyle[attr];
            } else {
                return (dom, attr) => getComputedStyle(dom, false)[attr];
            }
        })();

        dialogHeaderEl.onmousedown = (e) => {
            document.onselectstart = function() {
                return false;
            }; //解决拖动会选中文字的问题
            document.ondragstart = function() {
                return false;
            };
            // 鼠标按下，计算当前元素距离可视区的距离
            const disX = e.clientX - dialogHeaderEl.offsetLeft;
            const disY = e.clientY - dialogHeaderEl.offsetTop;

            const screenWidth = document.body.clientWidth; // body当前宽度
            const screenHeight = document.documentElement.clientHeight; // 可见区域高度(应为body高度，可某些环境下无法获取)

            const dragDomWidth = dragDom.offsetWidth; // 对话框宽度
            const dragDomheight = dragDom.offsetHeight; // 对话框高度

            const minDragDomLeft = dragDom.offsetLeft;
            const maxDragDomLeft = screenWidth - dragDom.offsetLeft - dragDomWidth;

            const minDragDomTop = dragDom.offsetTop;
            const maxDragDomTop = screenHeight - dragDom.offsetTop - dragDomheight;

            // 获取到的值带px 正则匹配替换
            let styL = sty(dragDom, "left");
            let styT = sty(dragDom, "top");

            // 注意在ie中 第一次获取到的值为组件自带50% 移动之后赋值为px
            if (styL.includes("%")) {
                styL = +document.body.clientWidth * (+styL.replace(/\%/g, "") / 100);
                styT = +document.body.clientHeight * (+styT.replace(/\%/g, "") / 100);
            } else {
                styL = +styL.replace(/\px/g, "");
                styT = +styT.replace(/\px/g, "");
            }

            document.onmousemove = function(e) {
                // 通过事件委托，计算移动的距离
                let left = e.clientX - disX;
                let top = e.clientY - disY;

                // 边界处理
                if (-left > minDragDomLeft) {
                    left = -minDragDomLeft;
                } else if (left > maxDragDomLeft) {
                    left = maxDragDomLeft;
                }

                if (-top > minDragDomTop) {
                    top = -minDragDomTop;
                } else if (top > maxDragDomTop) {
                    top = maxDragDomTop;
                }

                // 移动当前元素
                dragDom.style.cssText += `;left:${left + styL}px;top:${top + styT}px;`;
            };

            document.onmouseup = function(e) {
                document.onmousemove = null;
                document.onmouseup = null;
            };
        };
    },
});