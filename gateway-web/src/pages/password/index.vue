
<template>
  <div id="password">
    <el-form ref="form" label-width="0px" class="password-form">
      <el-row>
        <el-col :span="24">
          <el-form-item label="">
            <input class="password-input" v-model="password" placeholder="password" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <div class="arrow-down" @click="addSubmit">
        生成
        <!-- <i></i> -->
      </div>
      <el-row>
        <el-col :span="24">
          <div class="arrow-down-div">{{  newPassword}}</div>
          <!-- class="special-textarea" -->
        </el-col>
      </el-row>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <!-- <el-button type="primary" @click="addSubmit" style="background: #0548a5;border-color:#0548a5;">确 定</el-button> -->
      <!-- <el-button>取 消</el-button> -->
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
      password: "",
      newPassword: "",
    };
  },
  created() {},
  mounted() {},
  methods: {
    addSubmit() {
      this.$get(
        $url.DATA_URL + "/gateway/passwordGenerator?password=" + this.password
      ).then((res) => {
        if (res.data.code == 200) {
          this.newPassword = res.data.data;
          this.$message({
            type: "success",
            message: res.data.msg,
          });
        } else {
          this.$message({
            type: "error",
            message: res.data.msg,
          });
        }
      });
    },
  },
};
</script>
  <style scoped lang="scss">
  @import "@/styles/mixin.scss";
:root {
  --x: 0;
  --y: 0;
  --size: 0;
}
#password {
  width: 100%;
  height: 100%;
  // background: url('../../assets/images/bk.jpg');
  // // background: url('../../assets/images/bk.gif');
  // background-size: 100% 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  .password-form {
    width: 100%;
    height: 100%;
    .password-input {
      border: none !important;
      width: 100%;
      height: 35px;
      outline: 0;
      // background: #303640;
      @include background_color("bg-color");
      border-bottom: 1px solid #b2b2b2 !important;
      padding: 0 10px;
      font-size: 20px;
      box-sizing: border-box;
      // color: #eee;
      @include font_color("font-color");
    }
  }
  .arrow-down {
    // width: 100%;
    width: 120px;
    height: 40px;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;

    border-radius: 6px;
    border: none;
    color: #fff;
    cursor: pointer;
    @include background_color("btn-bg");
    // background-color: #0548a5;
    transition: all 0.2s ease;
    font-size: 20px;
    i {
      width: 35px;
      height: 35px;
      display: inline-block;
      background: url("../../assets/icons/arrowDown.png") no-repeat center;
      background-size: 100% 100%;
    }
  }
  .arrow-down:hover {
    i {
      background: url("../../assets/icons/arrow_active.png") no-repeat center;
      background-size: 100% 100%;
    }
  }
  .arrow-down-div {
    min-height: 35px;
    // color: #eee;
    @include font_color("font-color");
    font-size: 20px;
    line-height: 35px;
    border-bottom: 1px solid #b2b2b2;
  }
  .special-textarea {
    width: 100%;
    height: 80px;
    border-radius: 5px;
    // color: #fff;
    @include font_color("font-color");
    font-size: 18px;
    padding: 5px 15px;
    line-height: 30px;
    box-sizing: border-box;
    background: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%);
  }
}
</style>
<style>
#password .el-textarea__inner {
  font-size: 18px;
}
/* #password  */
</style>