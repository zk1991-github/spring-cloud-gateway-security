<template>
  <div id="loginPage">
    <img src="../../assets/images/bk.jpg" alt="" class="login-bk lazy-loadImg" />
    <!-- <div class="titleName"></div>
    <div class="ValueName"></div> -->
    <div class="loginWindow">
      <div class="accountPassword">
        <div class="welcomeLogin">
          <span class="login-text">&nbsp;欢&nbsp;迎&nbsp;登&nbsp;录&nbsp;</span>
          <span aria-hidden="true" class="hover-div"> &nbsp;欢&nbsp;迎&nbsp;登&nbsp;录&nbsp; </span>
          <!-- <span class="hover-div"></span> -->
        </div>
        <!-- <div class="account">
          <el-input v-model="userLogin.username" class="accountNum" placeholder="请输入账号"></el-input>
        </div>
        <div class="password">
          <el-input type="password" v-model="userLogin.password" class="passwordInput" placeholder="请输入密码" @keyup.enter.native="toHome"></el-input>
        </div> -->
        <div class="field-box" style="margin-top: 60px">
          <input
            v-model="userLogin.username"
            class="account field"
            type="text"
            required="required"
            autocomplete="off"
          />
          <span class="holder">Account</span>
          <span class="active-border"></span>
        </div>
        <div class="field-box">
          <input
            v-model="userLogin.password"
            class="account field"
            type="password"
            required="required"
            autocomplete="off"
          />
          <span class="holder">Password</span>
          <span class="active-border"></span>
        </div>
        <button class="submit" @click="toHome" :disabled="isLoading">
          <span class="submit-text" :class="{ 'loading': isLoading }">
            {{ isLoading ? '登录中...' : '登录' }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>
<script>
import qs from "qs";
// import 'querystring'
export default {
  data() {
    return {
      checked: false,
      userLogin: {
        username: "",
        password: "",
      },
      isLoading: false,
    };
  },
  created() {},
  mounted() {
    this.getCsrfToken();
    document.addEventListener('keydown', this.handleKeyDown);
    // document.querySelector(".loginBtn").onmousemove = (e) => {
    //   const x =
    //     e.pageX - e.target.offsetLeft - (document.body.clientWidth - 505) / 2;
    //   const y =
    //     e.pageY - e.target.offsetTop - (document.body.clientHeight - 465) / 2;
    //   e.target.style.setProperty("--x", `${x}px`);
    //   e.target.style.setProperty("--y", `${y}px`);
    // };
  },
  beforeDestroy() {
    document.removeEventListener('keydown', this.handleKeyDown);
  },
  methods: {
    handleKeyDown(e) {
      if (e.key === 'Enter') {
        this.toHome();
      }
    },
    getCsrfToken() {
      //gateway/csrfTokenGenerator
      this.$get($url.DATA_URL + "/gateway/csrfTokenGenerator").then((res) => {
        if (res.status !== 200) {
          this.$message.error(res.data.msg);
        }
      });
    },
    async toHome() {
      // this.$router.push("/index");
      if (!this.userLogin.username) {
        this.$message.error("请输入用户名！");
        return;
      }
      if (!this.userLogin.password) {
        this.$message.error("请输入密码！");
        return;
      }

      try {
        this.isLoading = true;
        const res = await this.$Lpost(
          $url.DATA_URL + "/login", 
          qs.stringify(this.userLogin), 
          {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
            },
          }
        );

        if (res.data.code === 200) {
          this.$message.success('登录成功');
          localStorage.setItem('username',this.userLogin.username)
          this.cookie.setCookie({ username: this.userLogin.username }, 7);
          await this.$router.push("/index");
          this.userLogin = {
            username: "",
            password: "",
          };
        } else {
          this.$message.error(res.data.msg);
        }
      } catch (error) {
        this.$message.error("登录失败，请稍后重试");
        console.error('Login error:', error);
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>
<style scoped lang="scss">
:root {
  --x: 0;
  --y: 0;
  --size: 0;
}
#loginPage {
  width: 100%;
  height: 100%;
  // background: url('../../assets/images/bk.jpg');
  // // background: url('../../assets/images/bk.gif');
  // background-size: 100% 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
  .login-bk {
    width: 100%;
    height: 100%;
    position: absolute;
    object-fit: cover;
    animation: reveal 1s ease-out;
  }
  .loginWindow {
    width: 525px;
    height: 525px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: center;
    align-items: center;
    transform: translateY(0);
    animation: slideUp 0.6s ease-out;
    border: 1px solid rgba(255, 255, 255, 0.2);
    .accountPassword {
      width: 80%;
      height: 80%;
      .welcomeLogin {
        width: 190px;
        letter-spacing: 3px;
        text-decoration: none;
        font-size: 30px;
        font-family: Arial;
        position: relative;
        text-transform: uppercase;
        color: transparent;
        -webkit-text-stroke: 1px #fff;
        -webkit-text-fill-color: #8dc0eb;
        cursor: pointer;
        .hover-div {
          position: absolute;
          box-sizing: border-box;
          content: "";
          //   color: var(--title-animation-color);
          width: 0%;
          top: 0;
          right: 0;
          bottom: 0;
          left: 0;
          border-right: 6px solid #215390;
          overflow: hidden;
          transition: 0.5s;
          -webkit-text-stroke: 1px #215390;
        }
      }
      .welcomeLogin:hover .hover-div {
        width: 100%;
        filter: drop-shadow(0 0 23px #215390);
      }
      //   .account {
      //     margin-top: 40px;
      //     color: #8dc0eb;
      //     font-size: 18px;
      //     .accountText {
      //       font-size: 16px;
      //     }
      //     .accountNum {
      //       margin-top: 5px;
      //       width: 100%;
      //       height: 40px;
      //       border-radius: 5px;
      //       font-size: 16px;
      //     }
      //   }
      .password {
        margin-top: 35px;
        color: #8dc0eb;
        font-size: 18;
        .passwordText {
          font-size: 16px;
        }
        .passwordInput {
          margin-top: 5px;
          width: 100%;
          height: 40px;
          border-radius: 5px;
          font-size: 14px;
        }
      }
      .checkbox {
        margin-top: 10px;
        color: #424b79;
        // font-size: 18px;
        .checkedBox {
          font-size: 16px;
        }
      }
      .loginBtn {
        // width: 100%;
        // height: 50px;
        background: #215390;
        font-size: 20px;
        color: #fff;
        font-weight: 800;
        line-height: 50px;
        text-align: center;
        border: none;
        border-radius: 5px;
        margin-top: 70px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        span {
          position: relative;
        }
        &::before {
          --size: 0;
          content: "";
          position: absolute;
          left: var(--x);
          top: var(--y);
          width: var(--size);
          height: var(--size);
          background: radial-gradient(circle closest-side, #4405f7, transparent);
          transform: translate(-50%, -50%);
          transition: width 0.2s ease, height 0.2s ease;
        }
        &:hover::before {
          --size: 400px;
        }
      }
    }
  }
  .active-border {
    content: "";
    position: absolute;
    left: 0;
    bottom: -1px;
    height: 1px;
    width: 100%;
    transform: scaleX(0);
    background-color: #4405f7;
    transition: all 0.5s;
    transform-origin: center;
  }

  .field:focus ~ .active-border {
    transform: scaleX(1);
  }

  .field-box {
    border-bottom: 1px solid #b2b2b2;
    position: relative;
    font-size: 18px;
    margin-top: 40px;
  }

  .field {
    border: none;
    outline: none;
    width: 100%;
    height: 84px;
    background: none;
    transition: all 0.5s;
    caret-color: #fff;
    color: #cfc4c4;
    font-size: 16px;
  }

  .holder {
    color: #999;
    position: absolute;
    left: 0;
    top: 0;
    pointer-events: none;
    z-index: 1;
    transition: all 0.5s;
    font-size: 23px;
    line-height: 54px;
  }

  .field:focus ~ .holder,
  .field:valid ~ .holder {
    top: -30px;
    color: #fff;
    font-size: 22px;
  }
  .submit {
    position: relative;
    display: block;
    top: 45px;
    width: 130px;
    margin: 20px auto 0;
    padding: 10px 22px;
    border-radius: 6px;
    border: none;
    color: #fff;
    cursor: pointer;
    background-color: #215390;
    transition: all 0.2s ease;
    z-index: 2;
    height: 45px;
    font-size: 20px;
  }

  .submit:active {
    transform: scale(0.96);
  }

  .submit:before,
  .submit:after {
    position: absolute;
    z-index: 2;
    content: "";
    width: 150%;
    left: 50%;
    height: 100%;
    transform: translateX(-50%);
    z-index: -1000;
    background-repeat: no-repeat;
  }

  .submit:hover:before {
    top: -70%;
    background-image: radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, transparent 20%, #4405f7 20%, transparent 30%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, transparent 10%, #4405f7 15%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%);
    background-size: 10% 10%, 20% 20%, 15% 15%, 20% 20%, 18% 18%, 10% 10%, 15% 15%, 10% 10%, 18% 18%;
    background-position: 50% 120%;
    animation: greentopBubbles 0.6s ease;
  }

  @keyframes greentopBubbles {
    0% {
      background-position: 5% 90%, 10% 90%, 10% 90%, 15% 90%, 25% 90%, 25% 90%, 40% 90%, 55% 90%,
        70% 90%;
    }

    50% {
      background-position: 0% 80%, 0% 20%, 10% 40%, 20% 0%, 30% 30%, 22% 50%, 50% 50%, 65% 20%,
        90% 30%;
    }

    100% {
      background-position: 0% 70%, 0% 10%, 10% 30%, 20% -10%, 30% 20%, 22% 40%, 50% 40%, 65% 10%,
        90% 20%;
      background-size: 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%;
    }
  }

  .submit:hover::after {
    bottom: -70%;
    background-image: radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, transparent 10%, #4405f7 15%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%),
      radial-gradient(circle, #4405f7 20%, transparent 20%);
    background-size: 15% 15%, 20% 20%, 18% 18%, 20% 20%, 15% 15%, 20% 20%, 18% 18%;
    background-position: 50% 0%;
    animation: greenbottomBubbles 0.6s ease;
  }

  @keyframes greenbottomBubbles {
    0% {
      background-position: 10% -10%, 30% 10%, 55% -10%, 70% -10%, 85% -10%, 70% -10%, 70% 0%;
    }

    50% {
      background-position: 0% 80%, 20% 80%, 45% 60%, 60% 100%, 75% 70%, 95% 60%, 105% 0%;
    }

    100% {
      background-position: 0% 90%, 20% 90%, 45% 70%, 60% 110%, 75% 80%, 95% 70%, 110% 10%;
      background-size: 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%, 0% 0%;
    }
  }
}
.lazy-loadImg {
  animation: reveal 1s ease-out;
}
@keyframes reveal {
  0% {
    transform: scale(1.1);
    opacity: 0;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
}
@keyframes slideUp {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}
</style>
<style>
#loginPage .el-input__inner {
  background: none;
  border: none;
  /* background: #243f5c;
  border: 1px solid #354d65;
  color: #ddd; */
}
#loginPage .el-input__inner::-webkit-input-placeholder {
  /* color: #7792af; */
}
/* white：背景颜色（与input的背景颜色一样就可以了） */
#loginPage input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0px 1000px #00385e inset !important;
  -webkit-transition-delay: 99999s;
  -webkit-transition: transparent 99999s ease-out, transparent 99999s ease-out;
  -webkit-text-fill-color: #fff;
  border-radius: 0;
  width: 240px;
}
</style>
