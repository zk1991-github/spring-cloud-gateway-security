<template>
  <div class="home">
    <!-- 头部 -->
    <Header />
    <!-- tab切换 -->
    <TabPannel @activeTabFun="activeTabFun" />
    <Authority v-if="active == 0" />
    <Role v-else />
    <!--承载游动小鱼特效的div容器-->
    <div id="jsi-flying-fish-container" class="container" @click="clickCanvas"></div>
    <!-- <canvas id="boatCanvas" width="170" height="80" style="position: fixed;
    bottom: 30px;
    left: 100px;"></canvas> -->
    <!-- <div class="div-icon" @click="showScreenInteraction">
      <div class="qz-div">生成密码</div>
    </div>
    <div class="div-icon2 div-icon" @click="qzRefresh">
      <div class="qz-div">刷新权限</div>
      <div class="font-div"></div>
    </div> -->
    <div class="dy-div"  @click="showScreenInteraction">生成密码</div>
    <div class="dy-div right-div"  @click="qzRefresh">刷新权限</div>
    <div :class="['dy-div2',{'theme-bk':!switchTheme}]"></div>
    <!-- 密码按钮 -->
    <div class="btn-box" id="box" @click="showScreenInteraction" v-show="false">
      <i></i>
    </div>
    <!-- 密码生成 -->
    <el-dialog
      title="密码生成"
      :before-close="handleClose"
      :visible.sync="passwordDialog"
      width="830px"
      append-to-body
      :close-on-click-modal="false"
    >
      <GeneratePassword ref="GeneratePassword" />
    </el-dialog>
  </div>
</template>

<script>
import "../../plugins/jquery.js";
import RENDERER from "../../plugins/fish.js";
import Header from "../../components/header/index.vue";
import TabPannel from "../../components/tabPannel/index.vue";
// 权限
import Authority from "../authority/index.vue";
// 角色
import Role from "../role/index.vue";
//生成密码
import GeneratePassword from "../password/index.vue";
import { nextTick } from 'vue';
export default {
  components: {
    Header,
    TabPannel,
    Authority,
    Role,
    GeneratePassword,
  },
  data() {
    return {
      active: 0,
      passwordDialog: false,
      switchTheme: true
    };
  },
  mounted() {
    // window.oDiv = document.getElementById("box");
    // oDiv.addEventListener("mousedown", this.down, false);
    // 下面两个事件之所以不注册在box上，而是注册在document，
    // 是因为鼠标拖拽很可能会出现鼠标脱离物体的情况，
    // 如果注册在物体上，可能会导致move和up突然监听不到了
    // document.addEventListener("mousemove", this.move, false);
    // document.addEventListener("mouseup", this.up, false);
    // this.initCircle();
    // window.addEventListener("resize", this.setMinMaxPos);
    console.log("yjy", RENDERER);
    RENDERER.init();
    window.addEventListener("resize", this.resizeCanvas);
    this.addListenBk()
  },
  methods: {
    addListenBk(){
      let that = this;
      const btn = document.getElementById('theme')
      // 点击按钮时切换主题
      // btn.addEventListener('click', (e) => {
      //   setTimeout(()=>{
      //     let theme = window.document.documentElement.getAttribute("data-theme")
      //     if(theme == 'dark'){
      //       that.switchTheme = false;
      //     }else{
      //       that.switchTheme = true;
      //     }
      //     },300);
      // })
      // 监听全局主题变化事件
    this.$bus.$on('theme-changed', (newTheme) => {
      this.switchTheme = newTheme;
    });
    },
    resizeCanvas() {
      let canvas = document.getElementById("jsi-flying-fish-container").children[0];
      var ctx = canvas.getContext("2d");
      // 设置 Canvas 的宽高为浏览器窗口的宽高
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;

      // 重新绘制内容（这里是一个简单的示例，您可以根据需求重新绘制具体的内容）
      ctx.fillStyle = "lightblue";
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // 添加一些文本，以便更好地看到效果
      ctx.font = "30px Arial";
      ctx.fillStyle = "black";
      ctx.fillText("Resizable Canvas", 50, 50);
      ctx.fillText("Width: " + canvas.width, 50, 100);
      ctx.fillText("Height: " + canvas.height, 50, 150);
    },
    qzRefresh() {
      this.$confirm("此操作将导致所有用户强制登出，是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        this.refreshPage();
      });
    },
    refreshPage() {
      this.$get($url.DATA_URL + "/gateway/clearAllSessions").then((res) => {
        if (res.status == 200) {
          console.log("refresh", res);
          window.location.reload();
        } else {
          this.$message({
            type: "error",
            message: res.data.msg,
          });
        }
      });
    },
    clickCanvas(e) {
      let that = this;
      let canvas = document.getElementById("jsi-flying-fish-container").children[0];
      canvas.onclick = function (e) {
        let p = that.getEventPosition(e);
        var ctx = canvas.getContext("2d");
        console.log(p);
        // if (ctx.isPointInPath(p.x, p.y)) {
        //   console.log("in");
        // } else {
        //   that.showScreenInteraction();
        // }
        if (p.x <= 118 && p.x > 0 && p.y <= 88 && p.y > 0) {
          that.showScreenInteraction();
        }
      };
    },
    getEventPosition(ev) {
      var x, y;
      if (ev.layerX || ev.layerX == 0) {
        x = ev.layerX;
        y = ev.layerY;
      } else if (ev.offsetX || ev.offsetX == 0) {
        // Opera
        x = ev.offsetX;
        y = ev.offsetY;
      }
      return { x: x, y: y };
    },
    //注：使用上面这个函数，需要给Canvas元素的position设为absolute。
    initBoat() {
      // 获取 Canvas 元素
      var canvas = document.getElementById("boatCanvas");
      var ctx = canvas.getContext("2d");

      // 缩小船的比例
      var scale = 0.09;

      // 绘制小船船体
      ctx.beginPath();
      ctx.moveTo(200 * scale, 300 * scale); // 船体左下角
      ctx.lineTo(600 * scale, 300 * scale); // 船体右下角
      ctx.quadraticCurveTo(660 * scale, 240 * scale, 700 * scale, 200 * scale); // 船体右侧曲线
      ctx.lineTo(700 * scale, 150 * scale); // 船体右侧上角
      ctx.arc(700 * scale, 150 * scale, 50 * scale, 0, Math.PI, true); // 船体底部（月牙形状）
      ctx.lineTo(300 * scale, 150 * scale); // 船体左侧上角
      ctx.quadraticCurveTo(260 * scale, 240 * scale, 200 * scale, 300 * scale); // 船体左侧曲线
      ctx.fillStyle = "#964B00"; // 棕色
      ctx.fill();
      ctx.closePath();

      // 绘制船帆
      ctx.beginPath();
      ctx.moveTo(500 * scale, 50 * scale); // 帆顶点
      ctx.lineTo(700 * scale, 150 * scale); // 帆底部右侧
      ctx.lineTo(500 * scale, 150 * scale); // 帆底部左侧
      ctx.fillStyle = "#FFF"; // 白色
      ctx.fill();
      ctx.font = "16px Georgia";
      //设置文字及其位置
      ctx.fillStyle = "#964B00";
      ctx.fillText("密码生成", 50, 12);
      ctx.font = "30px Verdana";
      ctx.closePath();

      // 绘制帆桅
      ctx.beginPath();
      ctx.moveTo(500 * scale, 50 * scale); // 帆顶点
      ctx.lineTo(500 * scale, 300 * scale); // 船体顶端
      ctx.lineWidth = 3 * scale;
      ctx.strokeStyle = "#000"; // 黑色
      ctx.stroke();
      ctx.closePath();
    },
    activeTabFun(index) {
      this.active = index;
    },
    handleClose() {
      this.$refs["GeneratePassword"].password = "";
      this.$refs["GeneratePassword"].newPassword = "";
      this.passwordDialog = false;
    },
    showScreenInteraction() {
      this.passwordDialog = true;
    },
    initCircle() {
      window.ball = {
        isMouseDown: false,
        flyTimer: null,
        dropTimer: null,
        startX: 0,
        startY: 0,
        moveX: 0,
        moveY: 0,
        endX: 0,
        endY: 0,
        startL: 0,
        startT: 0,
        minLeft: 0,
        maxLeft: 0,
        minTop: 0,
        maxTop: 0,
        flySpeed: 0,
        dropSpeed: 0,
      };
    },
    setMinMaxPos(_this) {
      var _box = _this || oDiv;
      ball.maxLeft = (document.documentElement.clientWidth || document.body.clientWidth) - 30;
      ball.maxTop = (document.documentElement.clientHeight || document.body.clientHeight) - 30;
    },

    down(e) {
      ball.isMouseDown = true;
      ball.startX = ball.moveX = e.clientX;
      ball.startY = ball.moveY = e.clientY;
      ball.startL = e.pageX; //offsetLeft;
      ball.startT = e.pageY; //offsetTop;

      this.setMinMaxPos(e);

      if (ball.flyTimer) {
        window.clearInterval(ball.flyTimer);
        ball.flyTimer = null;
      }
      if (ball.dropTimer) {
        window.clearInterval(ball.dropTimer);
        ball.dropTimer = null;
      }
    },
    move(e) {
      if (!ball.isMouseDown) {
        return false;
      }
      var _this = oDiv;
      if (ball.endX !== 0 || ball.endY !== 0) {
        ball.moveX = ball.endX;
        ball.moveY = ball.endY;
      }
      var curL = e.clientX - ball.startX + ball.startL;
      var curT = e.clientY - ball.startY + ball.startT;

      ball.endX = curL < ball.minLeft ? ball.minLeft : curL > ball.maxLeft ? ball.maxLeft : curL;
      ball.endY = curT < ball.minTop ? ball.minTop : curT > ball.maxTop ? ball.maxTop : curT;
      _this.style.left = ball.endX + "px";
      _this.style.top = ball.endY + "px";

      ball.flySpeed = ball.endX - ball.moveX;
      ball.dropSpeed = ball.endY - ball.moveY;
      //console.log(ball.flySpeed);
    },
    up(e) {
      if (!ball.isMouseDown) {
        return false;
      }
      ball.isMouseDown = false;
      this.fly.call(e);

      this.drop.call(e, e);
    },

    // dropSpeed初始是0(如果有个往下滑动的过程则会有一个滑动间隔作为初始值)，
    // dropSpeed每次轮询加10，直到触底，触底后将dropSpeed*-1变成向上运动
    // dropSpeed向上运动，值为负，轮询一次依然加10，负值加正值，实际在变小，变成0或负数
    // 循环上面1和2步骤
    // 如果 弹起的高度不超过增量那么就静止
    drop(e) {
      var _this = oDiv;
      var dropG = 10; // 下坠增量
      var dropSpeed = ball.dropSpeed;
      var dropBottomNum = 0;
      var curT;
      ball.dropTimer = setInterval(function () {
        dropSpeed += 10;
        curT = _this.offsetTop + dropSpeed;
        if (curT >= ball.maxTop) {
          // 触底
          _this.style.top = ball.maxTop + "px";
          dropSpeed *= -1;
          // 让弹起的高度 下降
          //dropS peed = -dropSpeed + parseInt(dropSpeed/6);
          dropBottomNum++;
          console.log(dropBottomNum, dropSpeed);
          if (dropSpeed < 0 && dropSpeed >= -10) {
            clearInterval(ball.dropTimer);
            ball.dropTimer = null;
          }
        } else {
          _this.style.top = curT + "px";
        }
      }, 1000 / 50);
    },

    // 模拟水平惯性移动，和下坠加速度类似，物体当前的left值 + 增量值，如果需要反弹，则将left值乘于-1
    // 和下坠不同，水平惯性的增量是受摩擦外力影响，应该是越来越小，最后停止的
    fly() {
      var _this = oDiv;
      var flyMocha = 0.95;
      var flySpeed = ball.flySpeed; // 水平惯性增量
      var flyBorderNum = 0; //碰撞边框次数
      var curL;
      ball.flyTimer = setInterval(function () {
        if (Math.abs(flySpeed) < 0.3) {
          // 水平惯性小于0.3像素
          clearInterval(ball.flyTimer);
          ball.flyTimer = null;
        }
        flySpeed *= flyMocha; // 每次轮询惯性速度只有上一次的0.95
        curL = _this.offsetLeft + flySpeed;
        if (curL <= ball.minLeft) {
          _this.style.left = 0;
          flySpeed *= -1;
        } else if (curL >= ball.maxLeft) {
          _this.style.left = ball.maxLeft + "px";
          flySpeed *= -1;
        } else {
          _this.style.left = curL + "px";
        }
      }, 1000 / 50);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.home {
  width: 100%;
  height: 100%;
  // background: #2b315c;
  // background: #171b22;
  @include background_color("bg-color");
  // background: url('../../assets/images/1.png') no-repeat;
  background-size: 100% 100%;
  border-radius: 3px;
  // padding: 0 50px 30px 50px;
  box-sizing: border-box;
  // 头部
  .password-box {
    width: 80%;
    height: 80%;
    left: 10%;
    position: absolute;
    top: 10%;
    background: #171b22;
    z-index: 9999;
  }
  .btn-box {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: linear-gradient(-90deg, #12b3ff 0, #0548a5 100%);
    position: absolute;
    top: 90%;
    z-index: 999;
    left: 0px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 10px;
    box-sizing: content-box;
    cursor: pointer;
    i {
      width: 35px;
      height: 35px;
      display: inline-block;
      background: url("../../assets/icons/pwd.png") no-repeat center;
      background-size: 100% 100%;
    }
  }
  .container {
    margin: 0;
    padding: 0;
    background-color: transparent;
    width: 100%;
    height: 200px;
    z-index: 100;
    position: fixed;
    bottom: 0;
    left: 0;
  }
  .div-icon {
    width: 80px;
    height: 40px;
    position: absolute;
    bottom: 90px;
    left: 70px;
    z-index: 999;
    background: url("../../assets/icons/boat.png") no-repeat center;
    background-size: 100% 100%;
    cursor: pointer;
    .font-div {
      position: absolute;
      left: 30px;
      bottom: 8px;
      color: #18aefa;
      font-size: 12px;
    }
    .qz-div {
      width: 85px;
      height: 45px;
      background: url("../../assets/icons/qz.png") no-repeat center;
      background-size: 100% 100%;
      position: absolute;
      color: #fff;
      left: 20px;
      top: -45px;
      z-index: 999;
      padding: 5px 0 0 7px;
      text-align: center;
      font-size: 16px;
      transition: 1s;
      aspect-ratio: 1;
      transform: perspective(400px) rotate3d(var(--i, 1, -1), 0, var(--a));
      mask: linear-gradient(135deg, #000c 40%, #000, #000c 60%) 100% 100%/240% 240%;
      transition: 1s;
    }
    &:hover {
      .qz-div {
        --i: -1, 1;
        mask-position: 0 0;
        transform: scale(1.2);
        // opacity:1.2;
        //  transform: skewx(-25deg);
        // background-image: -webkit-linear-gradient(0deg, rgba(255,255,255,0), rgba(255,255,255,0.5), rgba(255,255,255,0));
      }
    }
  }

  .div-icon2 {
    position: absolute;
    bottom: 90px;
    left: 170px;
  }
  .dy-div2 {
    width: 977px;
    height: 300px;
    position: absolute;
    bottom: 0px;
    left: 10px;
    background: url("../../assets/images/dark.png") no-repeat;
    background-size: 100% 100%;
    pointer-events: none;
  }
  .theme-bk{
    background: url("../../assets/images/light.png") no-repeat!important;
    background-size: 100% 100%!important;
  }
  .dy-div {
    width: 150px;
    height: 100px;
    position: absolute;
    bottom: 140px;
    left: 290px;
    background: url("../../assets/images/true.png") no-repeat;
    background-size: 100% 100%;
    cursor:pointer;
    // z-index:9999;
    color:#111;
    text-align:center;
    line-height: 70px;
    font-size: 22px;
    // font-weight: bold;
    font-family: "AlibabaPuHuiTi";
    // font-family: 'MyFont'

  }
  .right-div{
    position: absolute;
    bottom: 140px;
    left: 490px;
  }
  .dy-div:hover{
    transform: scale(1.2); /* 放大 1.2 倍 */
  }
}
</style>
