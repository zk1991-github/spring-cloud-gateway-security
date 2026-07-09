<template>
  <!-- 角色管理 -->
  <div class="role">
    <!-- 内容 -->
    <div class="table-pannel">
      <!-- 操作栏 -->
      <div class="btn-box"></div>
      <!-- 表格数据 -->
      <div class="flex-list" style="justify-content: left">
        <div
          class="role-div"
          v-for="item in roleOptions"
          :key="item.id"
          @click="editRoleFun($event, item)"
        >
          <div class="role-img">
            <div class="favorite-edit">
              <i class="el-icon-edit"></i>
            </div>
          </div>
          <div class="role-main-name">{{ item.roleName }}</div>
        </div>
      </div>
    </div>
    <el-dialog
      v-drag
      :title="title"
      :visible.sync="editRole"
      width="400px"
      append-to-body
      top="3vh"
    >
      <div style="display: flex">
        <div class="search-input search-margin">
          <el-input
            v-model="keywords2"
            placeholder="请输入权限名称或URL路径搜索"
            clearable
            @keyup.enter.native="searchInputFun2"
          />
        </div>

        <div class="search-btn-input" @click="searchInputFun2">
          <i class="el-icon-search"></i>搜索
        </div>
      </div>
      <ul class="role-ul">
        <li
          v-for="item in tableData2"
          :key="item.id.toString()"
          style="position: relative"
          :class="{
            active: editRoleForm.permissionInfos.indexOf(item.id.toString()) != -1,
          }"
          @click="changeRolePermiss(item)"
          @mousedown="createRipple($event, this)"
          @mouseup="removeRipple($event, this)"
          v-show="item.open == 0"
        >
          <span class="span-img"></span>
          <div class="font-desc">
            <div class="font-desc-name">{{ item.urlName }}</div>
            <div class="font-desc-url">{{ item.url }}</div>
          </div>
        </li>
      </ul>
      <div class="mini-pagination">
        <el-pagination
          @size-change="handleSizeChange2"
          @current-change="handleCurrentChange2"
          :current-page.sync="pageNum2"
          background
          :page-size="pageSize2"
          layout="total, prev, pager, next"
          :total="total"
        >
        </el-pagination>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="editRoleSubmit" class="btn-confirm">确 定</el-button>
        <el-button @click="beforeClose">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      roleTabShow: "-350",
      jsId: "",
      file: null,
      questionObject: {
        // questionsNmae: '',
        file: null,
      },
      questionIds: [],
      formLabelWidth: "120px",
      fileList: [{ name: undefined }],
      templateShow: false,
      username: "",
      tel: "",
      dataStatus: "",
      pageSize: 10,
      pageNum: 0,
      pageSize2: 10,
      pageNum2: 0,
      total: 0,
      time: ["", ""],
      tableData: [],
      tableData2: [],
      multipleSelection: [],
      switchStatus: "0",
      currentPage: 5,
      add: false,
      addForm: {
        urlName: "",
        url: "",
        open: "0",
        description: "",
        roleInfos: [],
      },
      title: "角色授权",
      // 部门树选项
      deptOptions: undefined,
      // 性别状态字典
      //修改
      // 状态数据字典
      statusOptions: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      edit: false,
      editrules: {
        urlName: [{ required: true, message: "权限名称不能为空", trigger: "blur" }],
        url: [{ required: true, message: "URL路径不能为空", trigger: "blur" }],
      },
      editForm: {
        urlName: "",
        url: "",
        open: "0",
        description: "",
        roleInfos: [],
        id: "",
      },
      editRoleForm: {
        permissionInfos: [],
      },

      resetFlag: false,
      resetForm: {
        password: "",
      },
      editRole: false,
      activeRole: {},
      dictOptions: [],
      keywords: "",
      keywords2: "",
    };
  },
  mounted() {
    // this.canvas();
    // this.searchFn()
    // this.searchFn2();
    this.getRoles();
    this.queryDictByDictTypeId();
  },
  methods: {
    beforeClose() {
      this.editRole = false;
      this.pageNum2 = 1;
      this.qxName2 = "";
      this.qxUrl2 = "";
      this.searchFn2();
    },
    changeRolePermiss(item) {
      if (this.editRoleForm.permissionInfos.indexOf(item.id.toString()) == -1) {
        this.editRoleForm.permissionInfos.push(item.id.toString());
      } else {
        this.editRoleForm.permissionInfos.splice(
          this.editRoleForm.permissionInfos.indexOf(item.id.toString()),
          1
        );
      }
    },
    queryDictByDictTypeId() {
      this.$get($url.DATA_URL + "/gateway/queryDictByDictTypeId?dictTypeId=1").then((res) => {
        if (res.data.code == 200) {
          this.dictOptions = res.data.data;
        } else {
          this.$router.push("/");
          // this.$message({
          //   type: "error",
          //   message: res.data.msg
          // });
        }
      });
    },
    queryPermissionByRoleId() {
      this.$get(
        $url.DATA_URL + "/gateway/queryPermissionsByRoleId?roleId=" + this.activeRole.id
      ).then((res) => {
        if (res.data.code == 200) {
          this.editRoleForm.permissionInfos = [];
          for (let i = 0; i < res.data.data.length; i++) {
            this.editRoleForm.permissionInfos.push(res.data.data[i].id.toString());
          }
        } else {
          if (res.data && res.data.msg) {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
        }
      });
    },
    editRoleFun(e, item) {
      this.keywords2 = "";
      this.activeRole = item;
      this.editRole = true;
      this.searchFn3();

      //
    },

    handleSizeChange2(val) {
      this.pageSize2 = val;
      // this.getUserList()
      this.searchFn2();
    },
    handleCurrentChange2(val) {
      this.pageNum2 = val;
      // this.getUserList()
      this.searchFn2();
    },
    handleSelectChange() {},
    getRoles() {
      this.$get($url.DATA_URL + "/gateway/queryAllRoles").then((res) => {
        if (res.data.code == 200) {
          this.roleOptions = res.data.data;
        } else {
          this.$router.push("/login");
          // this.$message({
          //   type: "error",
          //   message: res.data.msg
          // });
        }
      });
    },
    addCancel() {
      this.$refs["form"].resetFields();
      this.add = false;
    },
    editRoleSubmit() {
      let params = {
        permissionInfos: [],
        id: this.activeRole.id,
      };
      for (let i = 0; i < this.editRoleForm.permissionInfos.length; i++) {
        params.permissionInfos.push({
          id: this.editRoleForm.permissionInfos[i],
        });
      }
      this.$post($url.DATA_URL + "/gateway/bindPermissionByRole", params).then((res) => {
        if (res.data.code == 200) {
          this.$message({
            type: "success",
            message: this.title + res.data.msg,
          });
          // this.searchFn();
        } else {
          if (res.data && res.data.msg) {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
        }
      });
      this.editRole = false;
      this.pageNum2 = 1;
      //   this.handleCurrentChange2(1);
      this.activeRole = {};
    },
    editCancel() {
      this.edit = false;
    },
    // searchInputFun() {
    //   this.pageNum = 1;
    //   this.searchFn();
    // },
    searchInputFun2() {
      this.pageNum2 = 1;
      this.searchFn2();
    },
    searchFn2() {
      if (!this.cookie.getCookie("username")) {
        this.$router.push("/login");
      }
      let params = {
        keywords: this.keywords2,
        permissionInfoPage: {
          current: this.pageNum2,
          size: this.pageSize2,
        },
      };
      this.$get(
        $url.DATA_URL +
          "/gateway/queryPrivatePermission?keywords=" +
          this.keywords2 +
          "&current=" +
          this.pageNum2 +
          "&size=" +
          this.pageSize2
      ).then((res) => {
        if (res.data.code === 200) {
          this.tableData2 = res.data.data.records;
          this.total = res.data.data.total;
        } else {
          this.$router.push("/login");
        }
      });
    },
    searchFn3() {
      if (!this.cookie.getCookie("username")) {
        this.$router.push("/login");
      }
      let params = {
        keywords: this.keywords2,
        permissionInfoPage: {
          current: 1,
          size: this.pageSize2,
        },
      };
      this.$get(
        $url.DATA_URL +
          "/gateway/queryPrivatePermission?keywords=" +
          this.keywords2 +
          "&current=" +
          1 +
          "&size=" +
          this.pageSize2
      ).then((res) => {
        if (res.data.code === 200) {
          this.tableData2 = res.data.data.records;
          this.total = res.data.data.total;
          this.queryPermissionByRoleId();
        } else {
          this.$router.push("/login");
        }
      });
    },
    searchFn() {
      if (!this.cookie.getCookie("username")) {
        this.$router.push("/login");
      }
      let params = {
        keywords: this.keywords,
        permissionInfoPage: {
          current: this.pageNum,
          size: this.pageSize,
        },
      };
      this.$post($url.DATA_URL + "/gateway/queryPrivatePermission", params).then((res) => {
        if (res.data.code === 200) {
          this.tableData = res.data.data.records;
          this.total = res.data.data.total;
        } else {
          if (res.data && res.data.msg) {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
          this.$router.push("/login");
        }
      });
    },
    //按钮水波纹
    computeRippleStyles(element, event) {
      const { top, left } = element.getBoundingClientRect();
      const { clientWidth, clientHeight } = element;

      const radius = Math.sqrt(clientWidth ** 2 + clientHeight ** 2) / 2;
      const size = radius * 2;

      const localX = event.clientX - left;
      const localY = event.clientY - top;

      const centerX = (clientWidth - radius * 2) / 2;
      const centerY = (clientHeight - radius * 2) / 2;

      const x = localX - radius;
      const y = localY - radius;

      return { x, y, centerX, centerY, size };
    },
    createRipple(event) {
      const container = event.currentTarget;
      const { x, y, centerX, centerY, size } = this.computeRippleStyles(container, event);
      const ripple = document.createElement("div");
      ripple.classList.add("myripple");
      ripple.style.opacity = `0`;
      ripple.style.transform = `translate(${x}px, ${y}px) scale3d(.3, .3, .3)`;
      ripple.style.width = `${size}px`;
      ripple.style.height = `${size}px`;
      ripple.style.position = `absolute`;
      ripple.style.top = `0`;
      ripple.style.left = `0`;
      ripple.style["z-index"] = `100`;
      ripple.style["border-radius"] = `50%`;
      ripple.style["background-color"] = "#fad4a1"; //`currentColor`
      ripple.style["opacity"] = `0`;
      ripple.style.transition =
        "transform 0.2s cubic-bezier(0.68, 0.01, 0.62, 0.6)," + "opacity 0.08s linear";
      ripple.style["will-change"] = "transform, opacity";
      ripple.style["pointer-events"] = "none";
      // 记录水波的创建时间
      ripple.dataset.createdAt = String(performance.now());

      const { position } = window.getComputedStyle(container);
      container.style.overflow = "hidden";
      position === "static" && (this.style.position = "relative");

      container.appendChild(ripple);

      window.setTimeout(() => {
        ripple.style.transform = `translate(${centerX}px, ${centerY}px) scale3d(1, 1, 1)`;
        ripple.style.opacity = `.25`;
      });
    },
    removeRipple(event) {
      const container = event.currentTarget;
      const ripples = container.querySelectorAll(".myripple");
      if (!ripples.length) {
        return;
      }

      const lastRipple = ripples[ripples.length - 1];
      // 通过水波的创建时间计算出扩散动画还需要执行多久，确保每一个水波都完整的执行了扩散动画
      const delay = 300 - performance.now() + Number(lastRipple.dataset.createdAt);

      setTimeout(() => {
        lastRipple.style.opacity = `0`;

        setTimeout(() => lastRipple.parentNode?.removeChild(lastRipple), 300);
      }, delay);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.form-box {
  margin-top: 37px;
}

.role {
  width: 100%;
  height: 100%;
  // background: #2b315c;
  // background: #171b22;
  //   @include background_color("bg-color");
  border-radius: 3px;
  // padding: 0 50px 30px 50px;
  box-sizing: border-box;
  .table-pannel {
    // background: linear-gradient(to right, #333b6c, #73859f);
    // border-radius: 10px;
    // background: url('../../assets/gif/table.gif') no-repeat;
    // background-size: 100% 100%;
    padding: 20px;
    box-sizing: border-box;
    height: calc(100% - 150px);
    margin: 0 40px;
    .btn-box {
      width: calc(100% - 50px);
      height: 34px;
      margin-left: 23px;
      display: flex;
      justify-content: space-between;
      .add-btn-content {
        transform: skewX(40deg);
        display: flex;
      }
      .add-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        background: #0548a5;
        // border-radius: 3px;
        border: 1px solid #0548a5;
        cursor: pointer;
        transform: skewX(-40deg);

        .add-btn-bg {
          width: 25px;
          height: 25px;
          background: url("../../assets/images/add.png") no-repeat;
          background-size: 100%;
          margin-left: 19px;
          margin-top: 5px;
          margin-right: 5px;
        }
        .add-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #fff;
          margin-top: 7px;
        }
      }
      .edit-btn:hover,
      .add-btn:hover {
        background: #1763af;
      }
      .edit-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        background: #0548a5;
        // border-radius: 3px;
        border: 1px solid #0548a5;
        display: flex;
        cursor: pointer;
        transform: skewX(-40deg);
        .edit-btn-bg {
          width: 20px;
          height: 20px;
          background: url("../../assets/images/edit.png") no-repeat;
          background-size: 20px 20px;
          margin-left: 19px;
          margin-top: 8px;
          margin-right: 8px;
        }
        .edit-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #fff;
          margin-top: 7px;
        }
      }
      .del-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        background: #fff9f9;
        border-radius: 3px;
        border: 1px solid #ffcccc;
        display: flex;
        cursor: pointer;
        transform: skewX(-40deg);
        .del-btn-bg {
          width: 16px;
          height: 16px;
          background: url("../../assets/images/del.png") no-repeat;
          background-size: 16px 16px;
          margin-left: 19px;
          margin-top: 9px;
          margin-right: 16px;
        }
        .del-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #3a3f56;
          margin-top: 7px;
        }
      }
      .border-box-search {
        width: calc(100% - 130px);
        display: flex;
        flex-direction: row-reverse;
        transform: skewX(-40deg);
        border: 1px solid;
        border-color: #3a4659 transparent transparent #3a4659;
        position: relative;
      }
      .border-box-search::after,
      .border-box-search::before {
        position: absolute;
        left: 0px;
        top: 0;
        bottom: 0;
        right: 0;
      }
      .border-box-search::after,
      .border-box-search::before {
        content: "";
        // box-shadow:inset 0 0 0 2px #42b983;
        // animation:clipAnimate 10s linear infinite;
        // z-index: 11000
      }
      .border-box-search::before {
        animation-delay: 5s;
      }
      @keyframes clipAnimate {
        // 0% {
        //   clip:rect(200px,0,0,20px)
        // }
        // 25% {
        //   clip:rect(0,20px,0,30px)
        // }
        // 50% {
        //   clip:rect(198px,200px,200px,0)
        // }
        // 75% {
        //   clip:rect(0,200px,200px,198px)
        // }
        // 100% {
        //   clip:rect(0,200px,2px,0)
        // }
      }
      .import-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        background: #fbfff9;
        border-radius: 3px;
        border: 1px solid #afdc8a;
        display: flex;
        cursor: pointer;
        .import-btn-bg {
          width: 16px;
          height: 16px;
          background: url("../../assets/images/import.png") no-repeat;
          background-size: 16px 16px;
          margin-left: 19px;
          margin-top: 9px;
          margin-right: 16px;
        }
        .import-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #3a3f56;
          margin-top: 7px;
        }
      }
      .export-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        background: #fcfcfc;
        border-radius: 3px;
        border: 1px solid #d4d7df;
        display: flex;
        cursor: pointer;
        .export-btn-bg {
          width: 16px;
          height: 16px;
          background: url("../../assets/images/export.png") no-repeat;
          background-size: 16px 16px;
          margin-left: 19px;
          margin-top: 9px;
          margin-right: 16px;
        }
        .export-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #3a3f56;
          margin-top: 7px;
        }
      }
    }
    .flex-list {
      display: flex;
      flex-wrap: wrap;
      align-content: flex-start;
      justify-content: space-between;
      padding: 20px 0;
      height: calc(100% - 130px);
      overflow-y: auto;
      .role-div {
        display: flex;
        flex-direction: column;
        width: 258px;
        height: 184px;
        // border: 1px solid #3a4659;
        transition: 0.2s;
        margin: 0 20px 20px 0;
        cursor: pointer;
        z-index: 999;
        .role-img {
          width: 100%;
          height: calc(100% - 30px);
          background: url("../../assets/gif/js.gif") no-repeat center;
          background-size: 154px 154px;
          position: relative;
          .favorite-edit {
            opacity: 0;
            pointer-events: all;
            background: rgba(0, 0, 0, 0.65);
            width: 100px;
            height: 100px;
            // border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #fff;
            position: absolute;
            left: 80px;
            top: 30px;
            z-index: 99;
            border-radius: 50%;
            font-size: 18px;
          }
        }
        .role-main-name {
          cursor: pointer;
          font-size: 14px;
          text-align: center;
          //   color: #bcc9d4;
          @include font_color("font-color");
          padding: 0 5px;
          line-height: 28px;
          text-overflow: ellipsis;
          overflow: hidden;
          white-space: nowrap;
        }
      }
      .role-div:hover {
        // border: 1px solid #0548a5;
        // box-shadow: 0 0 10px 0 #000;
        .favorite-edit {
          opacity: 1;
        }
      }
    }
  }
}
.search-btn {
  background: #465cb6;
  border: none;
  margin-left: 64px;
}
.pagination {
  height: 60px;
  margin-left: 24px;
  // margin-bottom: 20px;
  margin-right: 24px;
  // position: absolute;
  // bottom: -20px;
  // right: 0;
  width: calc(100% - 40px);
}
.mini-pagination {
  margin-top: 20px;
}
.pagination .el-pagination {
  position: absolute;
  right: 93px;
  margin-top: 15px;
  // top: 18px;
}
.btn-confirm {
  @include border_color("list-border");
  @include background_color("btn-bg");
}
.mini-pagination .el-pagination {
  position: absolute;
  right: 13px;
  // top: 18px;
}
.el-pagination__total {
  color: #ddd !important;
}
.special-row {
  // border: 1px solid #ddd;
  //   border-radius: 5px;
  //   padding: 10px;
  margin-bottom: 10px;
}
.desc-list {
  list-style-type: none;
  padding: 0 30px 0 80px;
  color: #c5c4c4;
  li {
    line-height: 30px;
  }
}
.role-manage {
  width: 250px;
  height: 100%;
  position: absolute;
  right: 0;
  top: 0;
  background: rgba(37, 37, 37, 0.8);
  z-index: 999;
  padding: 20px 50px;
  overflow-y: auto;
  transition: right linear 0.5s;
  .box-title {
    display: flex;
    height: 40px;
    line-height: 40px;
    border-bottom: 1px solid #0548a5;
    .box-title-font {
      color: #fff;
      font-size: 16px;
      // font-family: 'MyFont';
    }
  }
  .close-btn {
    position: absolute;
    right: 18px;
    top: 25px;
    width: 30px;
    height: 30px;
    background: url("../../assets/icons/close.png") no-repeat center;
    background-size: 100%;
    cursor: pointer;
    transition-duration: 1s;
  }
  .close-btn:hover {
    transform: rotate(180deg);
  }
  ul {
    height: 90%;
    overflow-y: auto;
  }
  ul,
  li {
    list-style-type: none;
  }
  li {
    width: 100%;
    height: 65px;
    // background: #506ea1;
    line-height: 65px;
    //text-align: center;
    color: #fff;
    border-radius: 5px;
    margin: 10px 0;
    display: flex;
    justify-content: space-between;
    cursor: pointer;
    transition: 0.1s;
    .left-div {
      width: 50px;
      height: 65px;
      display: inline-block;
      background: #ed8323;
      border-top-left-radius: 5px;
      border-bottom-left-radius: 5px;
      position: relative;
      span {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        display: inline-block;
        background: #fff url("../../assets/icons/pen.png") no-repeat center;
        background-size: 80%;
        position: absolute;
        right: -15px;
        top: 17px;
      }
    }
    .active {
      background: #ed8323;
    }
    .active1 {
      background: #23ed34;
    }
    .active2 {
      background: #4823ed;
    }
    .active3 {
      background: #ed2366;
    }
    .active4 {
      background: #ed6323;
    }
    .active5 {
      background: #23beed;
    }
    .active6 {
      background: #ed23ed;
    }
    .active7 {
      background: #ed2323;
    }
    .font {
      width: calc(100% - 53px);
      text-align: center;
      background: rgba(228, 231, 237, 0.7);
      border-top-right-radius: 5px;
      border-bottom-right-radius: 5px;
      color: #3a3f56;
      font-weight: bold;
    }
    .myripple {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 100;
      border-radius: 50%;
      background-color: currentColor;
      opacity: 0;
      transition: transform 0.2s cubic-bezier(0.68, 0.01, 0.62, 0.6), opacity 0.08s linear;
      will-change: transform, opacity;
      pointer-events: none;
    }
  }
}
.leftIn {
  animation-name: leftIn;
  animation-duration: 1s;
}
@keyframes leftIn {
  from {
    transform: translateX(-350px);
    opacity: 0;
  }
  to {
    transform: translateX(0px);
    opacity: 1;
  }
}
.leftOut {
  animation-name: leftOut;
  animation-duration: 1s;
}
@keyframes leftOut {
  from {
    transform: translateX(0px);
    opacity: 0;
  }
  to {
    transform: translateX(-35px);
    opacity: 1;
  }
}

.delay200 {
  animation-delay: 200ms;
  animation-fill-mode: backwards !important;
}
.delay400 {
  animation-delay: 400ms;
  animation-fill-mode: backwards !important;
}
.delay600 {
  animation-delay: 600ms;
  animation-fill-mode: backwards !important;
}
.delay800 {
  animation-delay: 800ms;
  animation-fill-mode: backwards !important;
}
.role-ul {
  height: 610px;
  overflow-y: auto;
}
.role-ul li {
  list-style-type: none;
  width: 100%;
  height: 52px;
  margin: 8px 0;
  // background: #40455c;
  @include background_color("role-list-bg");
  // border-radius: 15px;
  color: #fff;
  padding: 5px 40px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  cursor: pointer;
  border-left: 2px solid;
  @include border_color("btn-bg");
}
.role-ul li:hover {
  background: linear-gradient(
    -90deg,
    rgba(0, 222, 255, 0) 10%,
    rgba(0, 221, 255, 0.03) 49%,
    #1894ff 110%
  ) !important;
  //  background: linear-gradient(to right, #d5628a, #9769dd)!important;
  border-image-slice: 1;
  // border: 2px solid #ddd;
  border-image: linear-gradient(to right, #d5628a, #9769dd) 5 5 !important;
}
.role-ul li:hover .font-desc .font-desc-name {
  // color: #fff;
  @include font_color("font-color");
}
.role-ul .font-desc-url {
  width: 100%;
  line-height: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  @include font_color("font-color");
}
.role-ul .active {
  @include background_color("operate-list-bg")
    //   background: linear-gradient(-90deg, #46c5ff 0%, #0a2e6d 100%);
;
}
.role-ul .span-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: inline-block;
  background: #fff url("../../assets/icons/sn.png") no-repeat center;
  background-size: 80%;
  margin-top: 5px;
}
.role-ul .font-desc {
  width: calc(100% - 50px);
}
.role-ul .font-desc .font-desc-name {
  //color: #edcb23;
  // color: #8590b6;
  font-size: 16px;
  line-height: 25px;
  @include font_color("font-color");
  //  background: #18aefa1c;
}
.role-ul .active .font-desc .font-desc-name {
  // color: #fff;
  @include font_color("font-color");
}
.role-ul .active .span-img {
  background: #fff url("../../assets/icons/s.png") no-repeat center;
  background-size: 80%;
}
.search-input {
  display: flex;
  width: 470px;
  .search-name {
    width: 120px;
    text-align: center;
    line-height: 40px;
    color: #fff;
    font-size: 16px;
  }
}
.search-margin {
  // margin: 5px;
}
.search-btn-input {
  width: 90px;
  height: 40px;
  color: #fff;
  // margin-left: 8px;
  // margin-top: 2px;
  //   background: #0548a5;
  @include background_color("btn-bg");
  border-top-left-radius: 0;
  border-bottom-right-radius: 0;
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
  // border-radius: 3px;
  // border: 1px solid #e8eaf6;
  color: #fff;
  text-align: center;
  line-height: 40px;
  cursor: pointer;
  .search-btn-bg {
    width: 16px;
    height: 16px;
    background: url("../../assets/icons/search.png") no-repeat;
    background-size: 16px 16px;
    margin-top: 13px;
    margin-left: 13px;
  }
  .search-btn-name {
    font-size: 14px;
    font-family: PingFangSC-Medium, PingFang SC;
    font-weight: 500;
    color: #3a3f56;
    margin-top: 7px;
  }
}
.animated {
  // background-image: url(/css/images/logo.png);
  // background-repeat: no-repeat;
  // background-position: left top;
  // padding-top:95px;
  // margin-bottom:60px;
  // transition: 3.9s ease-out;
  // -webkit-animation-fill-mode: both;
  // animation-fill-mode: both;
}
@-webkit-keyframes flip {
  0% {
    -webkit-transform: rotateX(-180deg);
    transform: rotateX(-180deg);
    -webkit-animation-timing-function: ease-out;
    animation-timing-function: ease-out;
  }
  100% {
    -webkit-transform: rotateX(0deg);
    transform: rotateX(0deg);
    -webkit-animation-timing-function: ease-in;
    animation-timing-function: ease-in;
  }
}

.flip {
  animation: flip 0.5s linear;
  // -webkit-backface-visibility: visible !important;
  // -webkit-animation-name: flip;
  // backface-visibility: visible !important;
  // animation-name: flip;
}

.el-pagination__total {
  /* color: #fff !important; */
  @include font_color("logo-font-color");
}
.mini-pagination .el-pagination__total {
  color: #2681ff !important;
}
/* .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #2681FF !important;
} */
/* 弹框 */
.el-dialog {
  /* background: #303640 !important; */
  @include background_color("dialog-bg");
  border-top: 2px solid;
  border-image: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2;
}
.el-form-item__label {
  color: #fff !important;
}
#role .el-input__inner,
#role .el-textarea__inner,
#role .el-select-dropdown__empty,
#role .el-select-dropdown {
  background-color: #1c222b !important;
  border: 1px solid #0b0c0d !important;
  color: #ddd !important;
}
.search-input .el-input__inner {
  border: 1px solid #0548a5 !important;
  /* border-top-left-radius: 5px;
    border-bottom-left-radius: 30px; */
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  color: #fff;
}
.el-input__inner:hover {
  border: 1px solid #2681ff !important;
}
.el-pagination.is-background .el-pager li:not(.disabled).active {
  background: #0548a5 !important;
}
.popper__arrow::after,
.popper__arrow {
  border-bottom-color: #0b0c0d !important;
}
::-webkit-scrollbar {
  display: none;
}
.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover,
.el-select-dropdown.is-multiple .el-select-dropdown__item.selected {
  background: #2681ff !important;
}
.el-select-dropdown__item {
  color: #fff !important;
}
</style>
