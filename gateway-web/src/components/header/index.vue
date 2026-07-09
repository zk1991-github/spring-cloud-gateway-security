<template>
  <div class="header">
    <!-- 头部 -->
    <div class="logo-div">
      <div class="logo-font">网关鉴权平台</div>
      <!-- <div class="line-left"></div> -->
    </div>
    <div class="login-out" title="退出" @click="logout"></div>
    <div class="switch-btn">
      <label for="theme" class="theme">
        <span class="theme__toggle-wrap">
          <input id="theme" @change="switchTheme" class="theme__toggle" type="checkbox" role="switch" name="theme" v-model="switchValue">
          <span class="theme__icon">
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
            <span class="theme__icon-part"></span>
          </span>
        </span>
      </label>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      switchValue: true,
    };
  },
  watch: {
    // switchValue() {
    //   if (this.switchValue) {
    //     window.document.documentElement.setAttribute("data-theme", "dark");
    //   } else {
    //     window.document.documentElement.setAttribute("data-theme", "light");
    //   }
    // },
  },
  mounted() {
    if (this.switchValue) {
      window.document.documentElement.setAttribute("data-theme", "dark");
    } else {
      window.document.documentElement.setAttribute("data-theme", "light");
    }
    this.addEvent()
  },
  methods: {
    logout() {
      this.$confirm("确认退出系统吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        this.$post($url.DATA_URL + "/logout").then((res) => {
          if (res.data.code == 200) {
            this.$router.push("/");
          } else {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
        });
      });
    },
    //添加更改主题动画
    addEvent(){
      let that = this;
      const btn = document.getElementById('theme')
      // 点击按钮时切换主题
      btn.addEventListener('click', (e) => {
        const transition = document.startViewTransition(() => {
           if (that.switchValue) {
            window.document.documentElement.setAttribute("data-theme", "dark");
           } else {
             window.document.documentElement.setAttribute("data-theme", "light");
           }
        })
        transition.ready.then(() => {
          const { clientX, clientY } = e

          // 计算半径，以鼠标点击的位置为圆心，到四个角的距离中最大的那个作为半径
          const radius = Math.hypot(
            Math.max(clientX, innerWidth - clientX),
            Math.max(clientY, innerHeight - clientY)
          )
          const clipPath = [
            `circle(0% at ${clientX}px ${clientY}px)`,
            `circle(${radius}px at ${clientX}px ${clientY}px)`
          ]
          const isDark = window.document.documentElement.getAttribute("data-theme") == 'dark';
          // 自定义动画
          document.documentElement.animate(
            {
              // 如果要切换到暗色主题，我们在过渡的时候从半径 100% 的圆开始，到 0% 的圆结束
              clipPath: isDark ? clipPath.reverse() : clipPath
            },
            {
              duration: 500,
              // 如果要切换到暗色主题，我们应该裁剪 view-transition-old(root) 的内容
              pseudoElement: isDark
                ? '::view-transition-old(root)'
                : '::view-transition-new(root)'
            }
          )
        })
      })
    },
    //更改主题
    switchTheme(event) {
      this.$bus.$emit('theme-changed', this.switchValue);  // 通过事件总线广播主题变化
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.header {
  width: 100%;
  height: 120px;
  // background: #2b315c;
  // background: #171b22;
  //   @include background_color("bg-color");
  border-radius: 3px;
  // padding: 0 50px 30px 50px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  // 头部
  .logo-div {
    height: 120px;
    line-height: 120px;
    padding: 0 30px;
    .logo-font {
      // color: #fff;
      @include font_color("logo-font-color");
      font-size: 50px;
      font-family: "MyFont";
      height: 115px;
      width: 400px;
      background: url("../../assets/images/logobk.png") 100% 100%;
    }
    .line-left::before {
      content: "";
      position: absolute;
      height: 2px; // 流体的宽度，可以适当宽一些，但是注意位置偏移
      width: 240px; // 流体路线长度，最好与线保持一致
      // @include background_color("header-animation");
      background: linear-gradient(
        to right,
        transparent,
        red
      ); // 流体样式，这里是渐变
      animation: fade-left 3s linear infinite; //使用fade-left动画
      box-shadow: 0px 0px 10px 2px #72dffa; // 外发光
    }

    // 向左流动，right改left为向右流动
    @keyframes fade-left {
      0% {
        right: calc(100% - 40px); // 到达终点时位置要减去自身的长度
        opacity: 0;
      }
      10% {
        opacity: 1;
      }
      90% {
        opacity: 1;
      }
      100% {
        right: 0px;
        opacity: 0;
      }
    }
  }
  .login-out {
    width: 30px;
    height: 30px;
    // background: url("../../assets/icons/logout.png") no-repeat center;
    @include background_image("logout-icon");
    position: absolute;
    right: 25px;
    cursor: pointer;
    z-index: 999;
  }
  .switch-btn {
    position: absolute;
    right: 60px;
  }
  /* Default */
  .theme {
    display: flex;
    align-items: center;
    -webkit-tap-highlight-color: transparent;
  }

  .theme__fill,
  .theme__icon {
    transition: 0.3s;
  }

  .theme__fill {
    background-color: var(--bg);
    display: block;
    mix-blend-mode: difference;
    position: fixed;
    inset: 0;
    height: 100%;
    transform: translateX(-100%);
  }

  .theme__icon,
  .theme__toggle {
    z-index: 1;
  }

  .theme__icon,
  .theme__icon-part {
    position: absolute;
  }

  .theme__icon {
    display: block;
    top: 0.9em;
    left: 0.9em;
    width: 1.5em;
    height: 1.5em;
  }

  .theme__icon-part {
    border-radius: 50%;
    box-shadow: 0.4em -0.4em 0 0.5em hsl(0, 0%, 100%) inset;
    top: calc(50% - 0.5em);
    left: calc(50% - 0.5em);
    width: 1em;
    height: 1em;
    transition: box-shadow var(--transDur) ease-in-out,
      opacity var(--transDur) ease-in-out, transform var(--transDur) ease-in-out;
    transform: scale(0.5);
  }

  .theme__icon-part ~ .theme__icon-part {
    background-color: hsl(0, 0%, 100%);
    border-radius: 0.05em;
    top: 50%;
    left: calc(50% - 0.05em);
    transform: rotate(0deg) translateY(0.5em);
    transform-origin: 50% 0;
    width: 0.1em;
    height: 0.2em;
  }

  .theme__icon-part:nth-child(3) {
    transform: rotate(45deg) translateY(0.45em);
  }

  .theme__icon-part:nth-child(4) {
    transform: rotate(90deg) translateY(0.45em);
  }

  .theme__icon-part:nth-child(5) {
    transform: rotate(135deg) translateY(0.45em);
  }

  .theme__icon-part:nth-child(6) {
    transform: rotate(180deg) translateY(0.45em);
  }

  .theme__icon-part:nth-child(7) {
    transform: rotate(225deg) translateY(0.45em);
  }

  .theme__icon-part:nth-child(8) {
    transform: rotate(270deg) translateY(0.5em);
  }

  .theme__icon-part:nth-child(9) {
    transform: rotate(315deg) translateY(0.5em);
  }

  .theme__label,
  .theme__toggle,
  .theme__toggle-wrap {
    position: relative;
  }

  .theme__toggle,
  .theme__toggle:before {
    display: block;
  }

  .theme__toggle {
    background-color: #d7e7e7;
    border-radius: 25% / 50%;
    box-shadow: 0 0 0 0.125em var(--primaryT);
    padding: 0.25em;
    width: 6em;
    height: 3em;
    -webkit-appearance: none;
    appearance: none;
    transition: background-color var(--transDur) ease-in-out,
      box-shadow 0.15s ease-in-out, transform var(--transDur) ease-in-out;
  }

  .theme__toggle:before {
    background-color: black;
    border-radius: 50%;
    content: "";
    width: 2.5em;
    height: 2.5em;
    transition: 0.3s;
  }

  .theme__toggle:focus {
    box-shadow: 0 0 0 0.125em var(--primary);
    outline: transparent;
  }

  /* Checked */
  .theme__toggle:checked {
    background-color: #41464b;
  }

  .theme__toggle:checked:before,
  .theme__toggle:checked ~ .theme__icon {
    transform: translateX(3em);
  }

  .theme__toggle:checked:before {
    // background-color: hsl(198, 90%, 55%);
    background: #fff;
  }

  .theme__toggle:checked ~ .theme__fill {
    transform: translateX(0);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(1) {
    box-shadow: 0.2em -0.2em 0 0.2em black inset;
    transform: scale(1);
    top: 0.3em;
    left: 0.7em;
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part ~ .theme__icon-part {
    opacity: 0;
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(2) {
    transform: rotate(45deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(3) {
    transform: rotate(90deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(4) {
    transform: rotate(135deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(5) {
    transform: rotate(180deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(6) {
    transform: rotate(225deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(7) {
    transform: rotate(270deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(8) {
    transform: rotate(315deg) translateY(0.8em);
  }

  .theme__toggle:checked ~ .theme__icon .theme__icon-part:nth-child(9) {
    transform: rotate(360deg) translateY(0.8em);
  }

  .theme__toggle-wrap {
    margin: 0 0.75em;
  }

  @supports selector(:focus-visible) {
    .theme__toggle:focus {
      box-shadow: 0 0 0 0.125em var(--primaryT);
    }

    .theme__toggle:focus-visible {
      box-shadow: 0 0 0 0.125em var(--primary);
    }
  }
  
}

</style>
