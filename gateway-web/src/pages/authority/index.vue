<template>
  <!-- 权限管理 -->
  <div class="authority">
    <!-- 内容 -->
    <div class="table-pannel">
      <!-- 操作栏 -->
      <div class="btn-box">
        <!-- 按钮 -->
        <div style="display: flex">
          <div class="add-btn" @click="addDialog">
            <div class="add-btn-content">
              <div class="add-btn-bg"></div>
              <div class="add-btn-name">新增</div>
            </div>
          </div>
          <div
            :class="['edit-btn', { 'edit-btn-grow': multipleSelection.length > 0 }]"
            @click="delDialog"
          >
            <div class="add-btn-content">
              <div class="del-btn-bg"></div>
              <div class="edit-btn-name">删除</div>
            </div>
          </div>
        </div>
        <!-- 搜索 -->
        <div class="border-box-search">
          <div style="display: flex; transform: skewX(40deg); margin-top: 5px; z-index: 999">
            <div class="search-input">
              <el-input
                v-model="keywords"
                placeholder="请输入URL路径或权限名称搜索"
                clearable
                @keyup.enter.native="searchInputFun"
              />
            </div>
            <div class="search-btn-input" @click="searchInputFun">
              <i class="el-icon-search"></i>
              搜 索
              <!-- <div class="search-btn-bg"></div> -->
            </div>
          </div>
        </div>
      </div>
      <!-- 表格数据 -->
      <div
        class="flex-list"
      >
        <!-- animated flip -->
        <template v-for="(item,index) in tableData">
          <div
            class="left_item"
            :key="item.groupName+index"
            handle=".info-name"
            draggable="true"
            @dragstart="onDragStart($event,item)"
            @dragover="onDragOver"
            @dragleave="onDragLeave"
            @drop="drop"
            :move="onMove"
            :ease-in-out="true"
          >
            <div class="main-dataset animated move-item info-name2" :attr="item.id" @click.stop="showItemChildren(item.permissionInfos, item)">
              <div class="dataset-operate" v-if="item.permissionInfos?.length == 1 && item.id == '0'">
                <div
                  class="dataset-edit"
                  @click.stop="handleEdit(item.permissionInfos[0])"
                  v-show="item.permissionInfos[0].fixed == 0"
                >
                  <i class="el-icon-edit"></i>
                </div>
                <div
                  class="dataset-delete"
                  @click.stop="handleDel(item.permissionInfos[0])"
                  v-show="item.permissionInfos[0].fixed == 0"
                >
                  <i class="el-icon-delete" style="padding-left: 8px"></i>
                </div>
                <div class="dataset-delete" @click.stop="handleDetail(item.permissionInfos[0])">
                  <i class="el-icon-document" style="padding-left: 8px"></i>
                </div>
              </div>
              <div
                :class="[
                  'dataset-info',
                  'ellipsis',
                  { 'group-data': item.permissionInfos?.length != 1 ,'info-name2': item.permissionInfos?.length == 1 },
                ]"
              >
                <div class="icon-box" v-if="item.permissionInfos?.length > 1">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
                <div
                  class="info-name ellipsis"
                  v-if="item.permissionInfos.length == 1 && item.id == '0'"
                  :title="item.permissionInfos[0].urlName"
                  
                >
                  {{ item.permissionInfos[0].urlName }}
                </div>
                <div
                  class="group-name"
                  :title="item.urlName"
                  
                  v-else
                >
                  <!-- <span class="group-img"></span> -->
                  <span>{{ item.groupName }} </span>
                  <span class="parent-num"
                    ><span>{{ item.permissionInfos?.length }}</span
                    >条</span
                  >
                </div>
                <div
                  class="info-url"
                  :title="item.permissionInfos[0].url"
                  v-if="item.permissionInfos?.length == 1 && item.id == '0'"
                >
                  {{ item.permissionInfos[0].url }}
                </div>
                <div class="info-open" v-if="item.permissionInfos?.length == 1 && item.id == '0'">
                  <template>
                    <span v-if="item.permissionInfos[0].open == '0'"> 私有 </span>
                    <span v-else-if="item.permissionInfos[0].open == '1'"> 公开 </span>
                    <span v-else> 匿名 </span>
                  </template>
                </div>
              </div>
              <div class="dataset-type" v-if="item.permissionInfos?.length == 1 && item.id == '0'">
                <span
                  class="dataset-type-select"
                  v-show="item.permissionInfos[0].fixed == 0"
                  @click.stop="selectData($event, item.permissionInfos[0])"
                ></span>
              </div>
            </div>
          </div>
        </template>
      </div>

      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="pageNum"
          background
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total"
        >
        </el-pagination>
      </div>
    </div>

    <!-- 添加 修改 -->
    <el-dialog
      :title="title"
      :visible.sync="add"
      :before-close="handleClose"
      width="600px"
      append-to-body
    >
      <el-form ref="form" :model="addForm" :rules="addrules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="权限名称" prop="urlName">
              <el-input v-model="addForm.urlName" :disabled="!edit" placeholder="请输入权限名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="URL路径" prop="url">
              <el-input v-model="addForm.url" :disabled="!edit" placeholder="请输入URL路径" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="special-row">
          <el-col :span="24">
            <el-form-item label="是否公开" style="margin-bottom: 5px">
              <el-select
                v-model="addForm.open"
                :disabled="!edit"
                placeholder="请选择是否公开"
                style="width: 100%"
              >
                <el-option
                  v-for="item in dictOptions"
                  :key="item.id.toString()"
                  :label="item.dictName"
                  :value="item.dictVal"
                ></el-option>
              </el-select>
            </el-form-item>
            <ul class="desc-list">
              <li>1、私有：登录后需要为角色分配权限</li>
              <li>2、公开：登录后无需为角色分配权限</li>
              <li>3、匿名：无需登录</li>
            </ul>
          </el-col>
        </el-row>
        <el-row> </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="角色选择">
              <el-select
                v-model="addForm.roleInfos"
                placeholder="请选择角色"
                multiple=""
                :disabled="addForm.open == '1' || addForm.open == '2' || !edit"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in roleOptions"
                  :key="item.id.toString()"
                  :label="item.roleName"
                  :value="item.id.toString()"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input
                v-model="addForm.description"
                :disabled="!edit"
                type="textarea"
                placeholder="请输入内容"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer" v-show="edit">
        <el-button type="primary" @click="addSubmit" class="confirm-btn">确 定</el-button>
        <el-button @click="addCancel">取 消</el-button>
      </div>
    </el-dialog>
    <div class="group-children scale-up-center" v-show="groupChildren" 
    >
      <div class="close-btn" @click="closeGroup"></div>
      <div
        class="flex-list group-list"
        
      >
        <div class="main-dataset" 
          draggable="true"
          @dragstart="onDragStart($event,it,'dialog')"
          @dragover="dialogDragOver"
          @dragend="dialogDragEnd($event,it)"
          v-for="it in childrenData" 
          :key="it.id" 
          :attr="it.id">
          <div class="dataset-operate">
            <div class="dataset-edit" @click.stop="handleEdit(it)" v-show="it.fixed == 0">
              <i class="el-icon-edit"></i>
            </div>
            <div class="dataset-delete" @click.stop="handleDel(it)" v-show="it.fixed == 0">
              <i class="el-icon-delete" style="padding-left: 8px"></i>
            </div>
            <div class="dataset-delete" @click.stop="handleDetail(it)">
              <i class="el-icon-document" style="padding-left: 8px"></i>
            </div>
          </div>
          <div class="dataset-info ellipsis">
            <div class="info-name ellipsis" :title="it.urlName" :attr="it.id">{{ it.urlName }}</div>
            <div class="info-url" :title="it.url">{{ it.url }}</div>
            <div class="info-open">
              <template>
                <span v-if="it.open == '0'"> 私有 </span>
                <span v-else-if="it.open == '1'"> 公开 </span>
                <span v-else> 匿名 </span>
              </template>
            </div>
          </div>
          <div class="dataset-type">
            <span
              class="dataset-type-select"
              v-show="it.fixed == 0"
              @click.stop="selectData($event, it)"
            ></span>
          </div>
          <!-- <div class="del"></div> -->
        </div>
        <div class="dialog-add" @click="addDialog">
          <div class="img"></div>
          <div class="font">添加</div>
        </div>
      </div>
    </div>
    <!-- 分组 添加 修改 -->
    <el-dialog
      v-drag
      :title="title"
      :visible.sync="groupAdd"
      :before-close="handleClose"
      width="600px"
      append-to-body
    >
      <el-form ref="form" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="分组名称">
              <el-input v-model="groupName" placeholder="请输入分组名称" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer" v-show="edit">
        <el-button type="primary" @click="addGroupNew" class="confirm-btn">确 定</el-button>
        <el-button @click="addCancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import draggable from "vuedraggable";
// 头部
// tab切换
// import {beginLine} from '../../plugins/canvasLine.js';
export default {
  computed: {
    groupedItems() {
      const groups = {};
      // 将项目根据组名进行分类
      this.items.forEach((item) => {
        const groupName = item.group || "Ungrouped";
        if (!groups[groupName]) {
          groups[groupName] = { name: groupName, items: [] };
        }
        groups[groupName].items.push(item);
      });
      return Object.values(groups);
    },
  },
  components: {
    draggable,
  },
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
      dragFlag: false,
      // 表单校验
      addrules: {
        urlName: [{ required: true, message: "权限名称不能为空", trigger: "blur" }],
        url: [{ required: true, message: "URL路径不能为空", trigger: "blur" }],
      },
      addForm: {
        urlName: "",
        url: "",
        open: "0",
        description: "",
        roleInfos: [],
      },
      title: "",
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
      edit: true,
      canvasnav: null,
      groupChildren: false,
      childrenData: [],
      parentData: null,
      e: null,
      items: [
        { id: 1, text: "Item 1", group: null },
        { id: 2, text: "Item 2", group: null },
        { id: 3, text: "Item 3", group: null },
        { id: 4, text: "Item 4", group: null },
      ],
      groupAdd: false,
      groupName: "",
      groupFormIds: [],
      draggedElement:null,
      moveInGroup: false,
    };
  },
  mounted() {
    //避免拖拽时文本被选中
    document.addEventListener("selectstart", (e) => {
      e.preventDefault();
    });
    document.addEventListener("dragover", (e) => {
      e.preventDefault();
    });
    
    // this.canvas();
    this.searchGroupFn();
    // this.searchFn();
    // this.searchFn2()
    this.queryDictByDictTypeId();
  },
  methods: {
    closeGroup(){
      this.groupChildren = false;
      this.multipleSelection = [];
    },
    selectData(e, data) {
      // 如果数据是固定的，直接返回
      if (data.fixed !== 0) {
        return;
      }
      // 获取当前元素的类名
      const targetElement = e.target.parentElement;
      const isSelected = targetElement.classList.toggle("dataset-type-select");

      // 更新选择状态
      if (isSelected) {
        // 如果被选中，添加到选择数组
        this.multipleSelection.push(data);
      } else {
        // 如果取消选择，从选择数组中移除
        this.multipleSelection = this.multipleSelection.filter(item => item.id !== data.id);
      }
    },
    handleClose() {
      this.$refs["form"].resetFields();
      this.add = false;
      this.edit = true;
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //点击行触发，选中或不选中复选框
    handleRowClick(row) {
      this.$refs.multipleTable.toggleRowSelection(row);
      // 获取当前选中的数据
      const _selectData = this.$refs.multipleTable.selection;
      console.log("当前选中的数据", _selectData);
      this.handleSelectionChange(_selectData);
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

    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleEdit(row) {
      // 设置编辑标题和状态
      this.title = "修改权限";
      this.add = true;
      this.edit = true;

      // 使用解构赋值简化对 row 属性的访问
      const { urlName, open, url, description, roleInfos, id } = row;

      // 更新表单数据
      this.addForm = {
        urlName,
        open: open.toString(),
        url,
        description,
        roleInfos: roleInfos.map(role => role.id), // 使用 map 简化 roleInfos 的处理
        id,
      };

      // 获取角色信息
      this.getRoles();
    },
    handleDetail(row) {
      this.title = "权限详情";
      this.add = true;
      this.edit = false;
      const { urlName, open, url, description, roleInfos, id } = row;
      this.addForm = {
        urlName,
        open: open.toString(),
        url,
        description,
        roleInfos: roleInfos.map(role => role.id), // 使用 map 简化 roleInfos 的处理
        id,
      };
      this.getRoles();
    },

    handleSizeChange(val) {
      this.pageSize = val;
      // this.getUserList()
      this.searchGroupFn();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      // this.getUserList()
      this.searchGroupFn();
    },
    handleSelectChange() {},
    addDialog() {
      this.add = true;
      this.edit = true;
      this.addForm = {
        urlName: "",
        url: "",
        open: "0",
        description: "",
        roleInfos: [],
      };
      this.getRoles();
      this.title = "新增权限";

      //this.getTreeselect()
    },
    getRoles() {
      this.$get($url.DATA_URL + "/gateway/queryAllRoles").then((res) => {
        // this.edit = true;
        // this.add = true;
        // if(this.add){
        //   this.edit = true;
        // }
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
    getGroupsId(data,id){
      let arr = [];
      for(let i = 0; i < data.length; i++){
        if(data[i].id != id){
          arr.push(data[i].id)
        }
      }
      return arr;
    },
    addGroupNew(flag,droppedData,draggedData){
      const { groupName = this.groupName, id: groupId, permissionInfos = [] } = droppedData || {};
        // 使用 map 提取 permissionIds
      const permissionIds = flag === 1 
        ? permissionInfos.map(info => info.id) 
        : this.groupFormIds;
        //   // 使用解构赋值简化对 data 属性的访问
      let srcGroupId=null,targetGroupId=null;
      if(flag === 1 ){
        if(draggedData.permissionInfos && draggedData.permissionInfos.length>0){
          srcGroupId =draggedData.id
        }else if(this.groupChildren){
          srcGroupId = this.parentData.id;
        }else{
          srcGroupId = 0;
        }
        // srcGroupId = draggedData.permissionInfos.length>0?draggedData.id:0;
        // this.parentData
        targetGroupId = droppedData.permissionInfos.length > 0?droppedData.id:0;
      }else{
        srcGroupId = 0;
      }
      
  // 构建请求参数
      const params = {
        groupName,
        permissionIds,
        srcGroupId,
        targetGroupId
      };
        this.$post(`${$url.DATA_URL}/gateway/movePermission`, params)
        .then(res => {
          if (res.data.code === 200) {
            this.groupChildren = false;
            this.groupAdd = false;
            this.groupName = null;
            this.searchGroupFn();
          } else if (res.data.msg) {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
        });
    },
    //新增分组
    // addGroup(flag, data) {
    //   // 检查组名是否存在
    //   if (flag !== 1 && !this.groupName) {
    //     return; // 如果没有组名，直接返回
    //   }

    //   // 使用解构赋值简化对 data 属性的访问
    //   const { groupName = this.groupName, id: groupId, permissionInfos = [] } = data || {};

    //   // 使用 map 提取 permissionIds
    //   const permissionIds = flag === 1 
    //     ? permissionInfos.map(info => info.id) 
    //     : this.groupFormIds;

    //   // 构建请求参数
    //   const params = {
    //     groupName,
    //     permissionIds,
    //     ...(flag === 1 && { groupId }), // 仅在 flag 为 1 时添加 groupId
    //   };

    //   // 发送请求
    //   this.$post(`${$url.DATA_URL}/gateway/groupPermission`, params)
    //     .then(res => {
    //       if (res.data.code === 200) {
            
    //         this.groupAdd = false;
    //         this.groupName = null;
    //         this.searchGroupFn();
    //       } else if (res.data.msg) {
    //         this.$message({
    //           type: "error",
    //           message: res.data.msg,
    //         });
    //       }
    //     });
    // },
    //新增确认按钮
    async addSubmit() {
      if (!this.edit) {
        return;
      }

      // 确定请求的 URL
      const url = this.addForm.id ? "/gateway/updatePermission" : "/gateway/addPermission";

      // 验证表单
      const valid = await this.$refs["form"].validate();
      if (!valid) {
        this.add = true;
        return;
      }

      // 检查角色选择
      if (this.addForm.open === "0" && this.addForm.roleInfos.length === 0) {
        this.$message.warning("请选择角色!");
        return;
      }

      // 使用 map 提取角色信息
      this.addForm.roleInfos = this.roleOptions
        .filter(option => this.addForm.roleInfos.includes(option.id))
        .map(option => option);

      // 设置 groupId
      this.addForm.groupId = this.parentData?.id || 0;

      try {
        const res = await this.$post($url.DATA_URL + url, this.addForm);
        if (res.data.code === 200) {
          this.add = false;
          this.searchGroupFn();
        } else if (res.data.msg) {
          this.$message({
            type: "error",
            message: res.data.msg,
          });
        }
      } catch (error) {
        this.$message.error("请求失败，请重试。");
      }
    },
    addCancel() {
      this.$refs["form"].resetFields();
      this.groupName = "";
      this.add = false;
      this.groupAdd = false;
    },
    handleDel(row) {
      this.$confirm("此操作将永久删除该条权限信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        this.$get($url.DATA_URL + "/gateway/delPermission?id=" + row.id).then((res) => {
          if (res.data.code == 200) {
            // this.$message({
            //   type: "success",
            //   message: "删除成功!",
            // });

            this.searchGroupFn();
          } else {
            if (res.data && res.data.msg) {
              this.$message({
                type: "error",
                message: res.data.msg,
              });
            }
          }
        });
      });
      // this.$delete( `/onlineteaching/user/${row.id}`).then((res) => {
      //   this.getUserList()
      // })
    },
    delDialog() {
      if (this.multipleSelection.length < 1) {
        return this.$message({
          type: "error",
          message: "请至少选择一条数据",
        });
      }
      let that = this;
      this.$confirm("此操作将永久删除数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        let ids = "";
        for (let i = 0; i < that.multipleSelection.length; i++) {
          if (i == that.multipleSelection.length - 1) {
            ids += that.multipleSelection[i].id.toString();
          } else {
            ids += that.multipleSelection[i].id.toString() + ",";
          }
        }
        this.$get($url.DATA_URL + "/gateway/delPermissions?ids=" + ids).then((res) => {
          if (res.data.code == 200) {
            // this.$message({
            //   type: "success",
            //   message: "删除成功!",
            // });

            this.multipleSelection = [];
            this.searchGroupFn();
          } else {
            if (res.data && res.data.msg) {
              this.$message({
                type: "error",
                message: res.data.msg,
              });
            }
          }
        });
      });
    },
    searchInputFun() {
      this.pageNum = 1;
      this.searchGroupFn();
    },
    updateChild(event) {
      setTimeout(() => {
        this.groupChildren = false;
      }, 1000);

      // const { clientY, clientX } = event;
      // console.log(`当前拖拽位置: X: ${clientX}, Y: ${clientY}`);
    },
    onDragEnd(event) {
      const movedItemId = Number(event.item._underlying_vm_.id);
      const targetGroupName = event.from.dataset.id; // 获取目标组名

      // 更新被拖动的项目的组
      const movedItem = this.items.find((i) => i.id === movedItemId);
      if (movedItem) {
        movedItem.group = targetGroupName; // 将项目的分组更新为目标分组
      }
    },
    dialogDragStart(e){
      console.log('start')
    },
    dialogDragOver(event){
      event.preventDefault();
      event.dataTransfer.dropEffect = 'move'; 
    },
    async dialogDragEnd(e,data) {
      // console.log('dialogDragEnd')
      if(this.moveInGroup){
        return;
      }
      let dialogDom = document.querySelector('.group-children').getBoundingClientRect();
      //鼠标超出弹框范围
      if(e.x < dialogDom.left || e.y < dialogDom.top || e.x > dialogDom.right || e.y > dialogDom.bottom){
         await this.dragendDialog(data.id);
         this.searchGroupFn(); // 如果 searchGroupFn 也是异步的，使用 await
      }
      document.querySelectorAll('.dragging, .drag-over').forEach(el => {
        el.classList.remove('dragging', 'drag-over');
      });
      // this.drop(e)
    },
    async dragendDialog(id) {
      // try {
        // const res = await this.$get(
        //   $url.DATA_URL + "/gateway/moveOutGroup?id=" + id + "&groupId=" + this.parentData.id
        // );
        const params = {
        permissionIds:[this.draggedElement.id],
        srcGroupId:this.draggedElement.groupId,
        targetGroupId:0
      };
      const res = await this.$post(`${$url.DATA_URL}/gateway/movePermission`, params)
        if (res.data.code === 200) {
          this.groupChildren = false;
          this.childrenData = [];
          this.parentData = null;
          // await this.searchGroupFn(); // 如果 searchGroupFn 也是异步的，使用 await
        } else {
          if (res.data && res.data.msg) {
            this.$message({
              type: "error",
              message: res.data.msg,
            });
          }
        }
      // } catch (error) {
      //   this.$message({
      //     type: "error",
      //     message: "请求失败，请重试。",
      //   });
      // }
    },
    removegroup(id) {
      this.$get($url.DATA_URL + "/gateway/delPermissionGroup?id=" + id).then((res) => {
        if (res.data.code === 200) {
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
    getData(html,type) {
      for (let i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].groupName == html) {
          return this.tableData[i];
        } else {
          for (let m = 0; m < this.tableData[i].permissionInfos.length; m++) {
            if (this.tableData[i].permissionInfos[m].urlName == html) {
              if(type){
                return this.tableData[i].permissionInfos[m]
              }else{
                return this.tableData[i];
              }
            }
          }
        }
      }
    },
    getDataId(html) {
      // 查找匹配的权限信息并返回其 ID
      const foundPermission = this.tableData
        .flatMap(group => group.permissionInfos || []) // 展平权限信息数组
        .find(permission => permission.urlName === html); // 查找匹配的权限

      // 返回找到的权限 ID 或 undefined
      return foundPermission ? foundPermission.id : undefined;
    },
    preventSorting(evt) {
      // 阻止组内排序
      return false;
    },
    handleMove(evt) {
      // 只允许拖拽分组
      return true;
    },
    putFun(e, v, d, l) {
      ;
    },
    dragend(e, originalEvent) {
      // console.log(this.tableData);
      let innerHtml = e.target.innerText.split("\n")[0];
      let draggedElement = this.getData(innerHtml);
      if(draggedElement.permissionInfos?.length > 1){
        return;
      }
      let targetDom = e.originalEvent.target;
      let targetHtml = targetDom.innerText.split("\n")[0];
      if (targetDom.classList.value.includes("flex-list")) {
        return;
      }
      
      let droppedElement = this.getData(targetHtml);
      if (draggedElement == droppedElement) {
        return;
      }
      if (droppedElement.permissionInfos.length > 1) {
        droppedElement.permissionInfos.push(draggedElement.permissionInfos[0]);
        this.addGroup(1, droppedElement);
      } else {
        this.openGroupDailog(draggedElement.permissionInfos[0], droppedElement.permissionInfos[0]);
      }
    },
    onMove(evt) {
      evt.preventDefault();
      // console.log("move");
      return false;
    },
    //js开始移动
    onDragStart(event,data,type){
      this.moveInGroup = false;
      // this.startMove(evt)
      // 
      // evt.dataTransfer.setData('text/plain', evt.target.innerText);
      event.dataTransfer.effectAllowed = 'move'; 
      event.dataTransfer.setData('text/plain', JSON.stringify(data));

      let draggedElement = null;
      let target = event.target;
      if(!type){
        if(data.permissionInfos.length > 1) {
          event.preventDefault();
          event.stopPropagation();
          return false;
        }
        // 添加拖拽开始的视觉效果
        if(target.classList.value.includes("left_item")) {
          draggedElement = this.findDomWithClass(target,'dataset-info','children');
          // draggedElement.closest('.main-dataset').classList.add('dragging');
        } else {
          if(!target.classList.value.includes('dataset-info')) {
            draggedElement = this.findDomWithClass(target,'dataset-info');
          } else {
            draggedElement = target;
          }
          // draggedElement.closest('.main-dataset').classList.add('dragging');
        }
        let innerHtml = draggedElement.innerText.split("\n")[0];
        this.draggedElement = this.getData(innerHtml);
      }else{
        draggedElement = target
        let innerHtml = draggedElement.innerText.split("\n")[0];
        this.draggedElement = this.getData(innerHtml,'group');
      }
      // 开始监听鼠标移动以创建轨迹效果
      document.addEventListener('mousemove', this.createDragTrail);
    },
    //途径
    onDragOver(event) {
      event.preventDefault(); // 阻止默认行为以允许放置
      event.dataTransfer.dropEffect = 'move'; // 设置拖拽效果为移动

      // 获取目标元素
      const target = event.target;
      const parentTarget = target.closest('.dataset-info') || target;

      // 如果目标元素是正在拖拽的元素，直接返回
      if (parentTarget.closest('.dragging')) {
        return;
      }

      // 获取悬停元素的内容
      const overHtml = parentTarget.innerText.split("\n")[0];
      const overData = this.getData(overHtml);

      // 移除其他元素的拖拽效果
      document.querySelectorAll('.drag-over').forEach(el => {
        if (el !== parentTarget.closest('.main-dataset')) {
          el.classList.remove('drag-over');
        }
      });

      // 添加拖拽效果到当前悬停元素
      const mainDataset = parentTarget.closest('.main-dataset');
      if (mainDataset && overData !== this.draggedElement) {
        mainDataset.classList.add('drag-over');
      }
    },
    //离开
    onDragLeave(event){
      event.preventDefault();
      let doms = document.getElementsByClassName('dataset-info');
      for(let  i = 0; i < doms.length; i++){
        let parentTarget =  doms[i];
        if(parentTarget.classList.value.includes('group-data')){
          parentTarget.style?.setProperty('border-image','linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2', 'important');

        }else{
          parentTarget.style?.setProperty('border-image','none', 'important');
        }
        parentTarget.parentElement.style?.setProperty('transform','scale(1)', 'important');
      }
      // event.dataTransfer.dropEffect = 'copy';    
      // let parentTarget = null; 
      // let target = event.target;
      // if(!target.classList.value.includes('dataset-info')){
      //   parentTarget = this.findDomWithClass(target,'dataset-info')
      // }else{
      //   parentTarget = target;
      // }
      // if(parentTarget.classList.value.includes('group-data')){
      //   parentTarget.style?.setProperty('border-image','linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2', 'important');

      // }else{
      //   parentTarget.style?.setProperty('border-image','none', 'important');
      // }
      // parentTarget.parentElement.style?.setProperty('transform','scale(1)', 'important');
      // console.log('leave',parentTarget)
    },
    //弹框里面拖拽结束
    dropGroup(event){
      console.log('dragGroup')
    },
    //拖拽结束
    async drop(event){
      // console.log('drop')
      // 移除鼠标移动监听
      document.removeEventListener('mousemove', this.createDragTrail);
      // 清除所有拖拽效果
      document.querySelectorAll('.dragging, .drag-over').forEach(el => {
        el.classList.remove('dragging', 'drag-over');
      });
      let droppedElement = null;
      let target = event.target;
      if(!this.draggedElement || this.draggedElement.permissionInfos?.length > 1){
        return;
      }
      if(!target.classList.value.includes('dataset-info')){
        droppedElement = this.findDomWithClass(target,'dataset-info')
      }else{
        droppedElement = target;
      }
      let dropHtml = droppedElement.innerText.split("\n")[0];
      if (droppedElement.classList.value.includes("flex-list")) {
        this.draggedElement = null;
        return;
      }
      let droppedData = this.getData(dropHtml);
      if (droppedData == this.draggedElement) {
        this.draggedElement = null;
        return;
      }
      //判断拖拽元素是否被放置在本组
      for(let m = 0; m < droppedData.permissionInfos.length; m++){
        if(droppedData.permissionInfos[m].id == this.draggedElement.id){
          this.draggedElement = null;
          return;
        }
      }
      if (droppedData.permissionInfos.length > 1 && this.draggedElement) {

        if(this.draggedElement.permissionInfos){
          droppedData.permissionInfos.push(this.draggedElement.permissionInfos[0]);
          // this.addGroup(1, droppedData);
        }else{
          droppedData.permissionInfos.push(this.draggedElement);
          if(this.groupChildren){
            //从一个组拖拽到另一个组 先删除 后加入
            this.moveInGroup = true;
            // await this.dragendDialog(this.draggedElement.id);
            // this.addGroup(1, droppedData);
          }
        }
        this.addGroupNew(1,droppedData, this.draggedElement);
      } else {
        if(this.draggedElement.permissionInfos){
          this.openGroupDailog(this.draggedElement.permissionInfos[0], droppedData.permissionInfos[0]);
        }else{
          //从组内拖拽到另一组
          this.openGroupDailog(this.draggedElement, droppedData.permissionInfos[0]);
          this.groupChildren = false;
        }  
      }
      
      this.onDragLeave(event)
      this.draggedElement = null;
    },

    findDomWithClass(element, className,type) {
      // 确保传入的是一个元素对象
      if (!(element instanceof Element)) {
        return null;
      }
    
      // 向上遍历DOM树
      while (element) {
        // 检查当前元素的类名是否包含指定的类名
        if (element.classList.contains(className)) {
          return element;
        }
        if(type == 'children'){//移动到子元素
          element = element.querySelector('.'+className);
        }else{
          // 移动到父元素
          element = element.parentElement;
        }
        
      }
    
      // 如果没有找到，返回null
      return null;
    },

    //开始移动
    startMove(e){
      let myDivs = document.getElementsByClassName('dataset-info');
      for(let i = 0;i < myDivs.length; i++){
        let myDiv = myDivs[i];
        myDiv.addEventListener('mouseenter', (event)=>{
          // myDiv.style.border = '1px solid red';
          // myDiv.style.borderImage = 'linear-gradient(270deg, #a07d35 0%, #f3bd51 74%) 2 2 2 2';
          myDiv.style.setProperty('border-image','linear-gradient(270deg, #a07d35 0%, #f3bd51 74%) 2 2 2 2', 'important');
        });
      }
      this.stopMove()
    },
    inDiv(event,myDiv){
      console.log('鼠标在div内');
    },
    //结束移动
    stopMove(e){
      let myDivs = document.getElementsByClassName('dataset-info');
      for(let i = 0;i < myDivs.length; i++){
        let myDiv = myDivs[i];
        myDiv.addEventListener('mouseleave', ()=>{
          
          // myDiv.style.transform="scale(1)"
          myDiv.style.setProperty('border-image','linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2', 'important');
        });
      }
    },
    //change
    changeDrag(e){
      
    },
    openGroupDailog(data1, data2) {
      this.groupAdd = true;
      this.groupName = "";
      this.groupFormIds = [data1.id, data2.id];
    },

    updateDrag() {
      if (this.e) {
        let e = this.e;
        this.tableData.splice(e.draggedContext.index, 1);

        if (e.relatedContext.element.children.length > 0) {
          e.relatedContext.element.children.push(e.draggedContext.element);
        } else {
          let futureIndex = e.draggedContext.futureIndex;
          this.tableData[futureIndex] = {
            urlName: "组1",
            id: Math.random(),
            children: [e.draggedContext.element, e.relatedContext.element],
          };
          // this.$set(this.groupChildren, this.tableData[futureIndex].id, false);
          // this.groupChildren[this.tableData[futureIndex].id] = false;
          console.log(this.groupChildren);
        }
      }
    },
    changeData(e) {
      // console.log(this.tableData);
      // const { to, item, clone } = event;
      // ;
      // const movedItem = this.tableData.find((i) => i.id === parseInt(item.dataset.id));
      // // 如果移动到自己的子项，不做处理
      // if (to.contains(clone)) return;
      // // 更新父子关系
      // const newParentId = to.closest("[data-id]")?.dataset.id;
      // movedItem.parentId = newParentId ? parseInt(newParentId) : null;
    },
    searchGroupFn() {
      this.multipleSelection = [];
      //   "dataset-type dataset-type-select"
      let doms = document.getElementsByClassName("dataset-type");
      for (let i = 0; i < doms.length; i++) {
        if (doms[i].className.includes("dataset-type-select")) {
          doms[i].className = "dataset-type";
        }
      }
      if (!this.cookie.getCookie("username")) {
        this.$router.push("/login");
      }
      this.$get(
        $url.DATA_URL +
          "/gateway/queryPermissionGroupPage?keywords=" +
          this.keywords +
          "&current=" +
          this.pageNum +
          "&size=" +
          this.pageSize
      ).then((res) => {
        if (res.data.code === 200) {
          this.tableData = res.data.data.records;
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].children = [];
          }
          this.total = res.data.data.total;
          if (this.parentData) {
            let id = this.parentData.id;
            this.parentData = this.tableData.filter(function (item) {
              return item.id == id;
            })[0];
            this.childrenData = this.parentData.permissionInfos;
          }
          // this.$nextTick(() => {
          //   this.drawCanvas();
          // });
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
    searchFn() {
      this.multipleSelection = [];
      //   "dataset-type dataset-type-select"
      let doms = document.getElementsByClassName("dataset-type");
      for (let i = 0; i < doms.length; i++) {
        if (doms[i].className.includes("dataset-type-select")) {
          doms[i].className = "dataset-type";
        }
      }
      if (!this.cookie.getCookie("username")) {
        this.$router.push("/login");
      }
      this.$get(
        $url.DATA_URL +
          "/gateway/queryPermission?keywords=" +
          this.keywords +
          "&current=" +
          this.pageNum +
          "&size=" +
          this.pageSize
      ).then((res) => {
        if (res.data.code === 200) {
          this.tableData = res.data.data.records;
          for (let i = 0; i < this.tableData.length; i++) {
            this.tableData[i].permissionInfos = [this.tableData[i]];
            this.tableData[i].groupName = "未分组";
          }
          this.total = res.data.data.total;
          // this.$nextTick(() => {
          //   this.drawCanvas();
          // });
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
    resetFn() {
      this.username = "";
      this.tel = "";
      this.dataStatus = "";
      this.time = [];
      this.searchFn();
    },
    drawCanvas() {
      const canvas = document.getElementById("myCanvas");
      const ctx = canvas.getContext("2d");

      // 设置线条样式
      ctx.strokeStyle = "#fdbb38";

      ctx.lineWidth = 1;
      // 开始绘制直线
      ctx.beginPath();

      ctx.moveTo(20, 50); // 起点
      ctx.lineTo(50, 50); // 起点
      ctx.lineTo(300, 50); // 终点
      ctx.lineTo(300, 90); // 终点
      ctx.lineTo(160, 90);
      ctx.lineTo(160, 130);
      ctx.lineTo(20, 130);
      ctx.lineTo(20, 50);
      ctx.stroke();
    },
    showItemChildren(data, parent) {
      if(parent.id == '0'){
        return;
      }
      this.groupChildren = true;
      this.childrenData = data;
      this.parentData = parent;
      // this.$set(this.groupChildren, item.id, true);
      // console.log(this.groupChildren, this.groupChildren[item.id]);

      // this.groupChildren[item.id] = true;
    },
    // 添加拖拽轨迹效果
    createDragTrail(e) {
      const trail = document.createElement('div');
      trail.className = 'drag-trail';
      trail.style.left = e.clientX + 'px';
      trail.style.top = e.clientY + 'px';
      document.body.appendChild(trail);
      
      setTimeout(() => {
        trail.remove();
      }, 500);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/styles/mixin.scss";
.form-box {
  margin-top: 37px;
}
@font-face {
  font-family: "MyFont";
  src: url("../../../public/css/YouShe.ttf");
}
.authority {
  width: 100%;
  height: 100%;
  // background: #2b315c;
  // background: #171b22;
  @include background_color("bg-color");
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
        // background: #0548a5;
        @include background_color("btn-bg");
        // border-radius: 3px;
        // border: 1px solid #0548a5;
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
      @keyframes glow {
        0% {
          border-color: #393;
          box-shadow: 0 0 5px rgba(0, 255, 0, 0.2), inset 0 0 5px rgba(0, 255, 0, 0.1), 0 1px 0 #393;
        }
        100% {
          border-color: #6f6;
          box-shadow: 0 0 20px rgba(0, 255, 0, 0.6), inset 0 0 10px rgba(0, 255, 0, 0.4),
            0 1px 0 #6f6;
        }
      }
      .edit-btn {
        width: 110px;
        height: 34px;
        margin-right: 19px;
        // background: #0548a5;
        @include background_color("btn-bg");
        // border-radius: 3px;
        // border: 1px solid #0548a5;
        display: flex;
        cursor: pointer;
        transform: skewX(-40deg);

        .del-btn-bg {
          width: 25px;
          height: 25px;
          background: url("../../assets/images/del.png") no-repeat;
          background-size: 100%;
          margin-left: 19px;
          margin-top: 5px;
          margin-right: 5px;
        }
        .edit-btn-name {
          font-size: 14px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #fff;
          margin-top: 7px;
        }
      }
      .edit-btn-grow {
        animation: glow 800ms ease-out infinite alternate;
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
        border: 1px solid; //fdbb38
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
      padding: 30px 100px 20px 100px;
      // height: calc(100% - 130px);
      overflow-y: auto;
      justify-content: space-between;
      position: relative;
      .group-name {
        @include font_color("font-color");
        transform: skewX(40deg);
        width: 80%;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        .group-img {
          width: 35px;
          height: 35px;
          display: inline-block;
          margin-right: 5px;
          vertical-align: middle;
          background: #1c222b url("../../assets/icons/group.png") no-repeat;
          // transform: rotate(180deg);
          background-size: 100%;
          cursor: pointer;
        }
      }
      .group-name span {
        // color: #fff;
        // transform: skewX(40deg) !important;
      }

      .parent-num {
        width: 76px;
        float: right;
        font-size: 13px;
        color: #12b3ff !important;
        span {
          font-size: 30px;
          font-weight: bold;
          font-family: "AlibabaPuHuiTi";
          color: #12b3ff !important;
          margin-right: 3px;
        }
      }
      #myCanvas {
        position: absolute;
        top: 0;
        left: 0;
        pointer-events: none; /* 使 canvas 不干扰鼠标事件 */
        width: 100%;
        height: 300px;
      }
      .dataset-type-active {
        .dataset-type {
          background: linear-gradient(-90deg, #12b3ff 0, #0548a5 100%) !important;
        }
      }
      
    }
  }
  .info-name ,.info-name2{
        cursor: move;
      }
  .main-dataset {
    display: flex;
    align-items: center;
    margin: 16px 0;
    position: relative;
    transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55); // 弹性过渡效果
    
    // 正在拖拽的元素
    &.dragging {
      opacity: 0.7;
      transform: scale(0.85) rotate(-2deg);
      filter: brightness(1.2) contrast(1.1);
      box-shadow: 0 10px 20px rgba(24, 148, 255, 0.2);
      animation: shake 0.5s infinite;
    }
    
    // 可放置目标
    &.drag-over {
      transform: scale(1.05);
      box-shadow: 
        0 0 30px rgba(24, 148, 255, 0.4),
        0 0 60px rgba(24, 148, 255, 0.2);
      animation: pulse 1s infinite;
      border-color: #2681ff;
      background: linear-gradient(
        45deg,
        rgba(38, 129, 255, 0.1) 0%,
        rgba(38, 129, 255, 0.2) 100%
      );
    }

    // 添加发光边框效果
    &.drag-over::before {
      content: '';
      position: absolute;
      top: -2px;
      left: -2px;
      right: -2px;
      bottom: -2px;
      border: 2px solid #2681ff;
      border-radius: inherit;
      animation: borderGlow 1.5s infinite;
      pointer-events: none;
    }

    .dataset-type {
      position: absolute;
      height: 35px;
      line-height: 35px;
      text-align: center;
      width: 120px;
      background-size: cover;
      font-size: 16px;
      font-weight: 600;
      color: #acb3bd;
      letter-spacing: 0.93px;
      cursor: pointer;
      @include background_color("dataset-list-type-bg");
      // background: linear-gradient(90deg, #292f3b 0, #171b22 100%);
      right: -80px;
      z-index: 0;
      transform: skewX(-40deg);
      .dataset-type-select {
        width: 30px;
        height: 30px;
        background: url("../../assets/icons/sn.png") no-repeat;
        background-size: 100% 100%;
        display: inline-block;
        transform: skewX(40deg);
      }
    }
    .dataset-type-select {
      .dataset-type-select {
        background: url("../../assets/icons/s.png") no-repeat;
        background-size: 100% 100%;
      }
    }

    .dataset-operate {
      transition: 0.5s all cubic-bezier(0.65, 0.05, 0.36, 1);
      -webkit-backface-visibility: hidden;
      backface-visibility: hidden;
      position: absolute;
      left: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 35px;
      width: 80px;
      transform: skewX(-40deg);
      font-size: 16px;
      @include background_color("operate-list-bg");
      z-index:1;
      // background-image: linear-gradient(-90deg, #12b3ff 0, #0548a5 100%);
      .dataset-edit,
      .dataset-delete {
        cursor: pointer;
        transform: skewX(40deg);
      }
      .dataset-edit:hover,
      .dataset-delete:hover {
        color: #fff;
      }
    }
    .dataset-info {
      @include background_color("dataset-list-bg");
      margin-left: 35px;
      margin-right: 20px;
      width: 530px;
      display: flex;
      flex-wrap: wrap;
      flex: 1;
      justify-content: space-between;
      line-height: 44px;
      padding: 0 35px;
      // background: #1c222b;
      border: 1px solid;
      // @include border_color("list-border");
      border-left: 3px solid #1894ff;
      transform: skewX(-40deg);
      z-index: 1;
      font-size: 18px;
      border-radius: 7px;
      overflow: hidden;
      .info-name {
        // color: #bcc9d4;
        @include font_color("font-color");
        transform: skewX(40deg);
        width: 200px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .info-url {
        // color: #bcc9d4;
        @include font_color("font-color");
        transform: skewX(40deg);
        width: 200px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .info-open {
        transform: skewX(40deg);
        // color: #1894ff;
        @include font_color("active-font");
        width: 80px;
      }
    }

    .ellipsis {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .main-dataset:hover .dataset-operate {
    left: -50px;
  }
  .main-dataset:hover .dataset-info {
    @include background_color("dataset-list-hover-bg");
    box-shadow: 0 0 20px 0 #6e6e6e;
    border-top: 1px solid #1894ff;
    border-right: 1px solid #1894ff;
    border-bottom: 1px solid #1894ff;
  }
  .main-dataset:hover .group-img {
    // background: url("../../assets/icons/collapse.png") no-repeat,
    //   linear-gradient(to right, #9ad5f3, #18aefa) !important;
  }
}
.group-list {
  // width: 80%;
  height: 80%;
  padding: 30px;
  margin: auto;
  // border: 1px solid #1893ff7a;
  border-radius: 3px;
  overflow-y: auto;
  padding: 10px 100px 10px 100px;
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
.group-children {
  position: absolute;
  left: 500px;
  top: 200px;
  width: 50%;
  height: 50%;
  // height: 300px;
  background: rgba(48, 54, 64, 0.8);
  z-index: 888;
  display: flex;
  justify-content: space-around;
  border-radius: 3px;
  padding: 10px 50px 10px 10px;
  box-sizing: border-box;
  flex-direction: column;
  overflow-y: auto;
  border-top: 2px solid;
  border-image: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2;
  // border: 1px solid #5189b9;
  // align-content: row;
}
.dialog-add {
  width: 95%;
  height: 44px;
  line-height: 44px;
  margin-left: 40px;
  padding: 0 35px;
  // background: #1c222b;
  border: 2px solid;
  @include border_color("list-border");
  // border-left: 3px solid #1894ff;
  transform: skewX(-40deg);
  z-index: 1;
  font-size: 18px;
  border-radius: 7px;
  border-image: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2;
  color: #fff;
  text-align: center;
  cursor: pointer;
  display: flex;
  justify-content: center;
  .img {
    width: 45px;
    height: 44px;
    transform: skewX(40deg);
    background: url("../../assets/icons/addG.png") no-repeat center;
  }
  .font {
    color: #1894ff;
    text-align: center;
    transform: skewX(40deg);
  }
}
.close-group {
  // width: 45px;
  // height: 45px;
  // position: absolute;
  // right: 10px;
  // top: 10px;
  // background: url("@/assets/icons/close.png") no-repeat center;
  // cursor: pointer;
}
.group-children-name {
  width: 400px;
  height: 35px;
  line-height: 35px;
  padding: 0 35px;
  // background: #1c222b;
  border: 1px solid;
  @include border_color("list-border");
  border-left: 3px solid #1894ff;
  transform: skewX(-40deg);
  z-index: 1;
  font-size: 18px;
  border-radius: 7px;
  display: flex;
  margin: 10px 0;
  background: linear-gradient(
    -90deg,
    rgba(0, 222, 255, 0) 10%,
    rgba(0, 221, 255, 0.03) 69%,
    #1894ff 110%
  );
  span {
    color: #fff;
    // transform: skewX(40deg) !important;
  }
  .info-name {
    // color: #bcc9d4;
    @include font_color("font-color");
    transform: skewX(40deg);
    width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .info-url {
    // color: #bcc9d4;
    @include font_color("font-color");
    transform: skewX(40deg);
    width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .info-open {
    transform: skewX(40deg);
    // color: #1894ff;
    @include font_color("active-font");
    width: 80px;
  }
}
.confirm-btn {
  @include border_color("list-border");
  @include background_color("btn-bg");
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
  z-index: 999;
  // top: 18px;
}
.mini-pagination .el-pagination {
  position: absolute;
  right: 13px;
  // top: 18px;
}
// .el-pagination__total {
//   color: #ddd !important;
// }
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
  height: 520px;
  overflow-y: auto;
}
.role-ul li {
  list-style-type: none;
  width: 100%;
  height: 65px;
  margin: 10px 0;
  background: #40455c;
  // border-radius: 15px;
  color: #fff;
  padding: 10px 40px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  cursor: pointer;
  border-left: 2px solid;
  @include border_color("btn-bg");
}
// .role-ul li:hover {
//   background: linear-gradient(
//     -90deg,
//     rgba(0, 222, 255, 0) 10%,
//     rgba(0, 221, 255, 0.03) 49%,
//     #1894ff 110%
//   );
// }
.role-ul li:hover .font-desc .font-desc-name {
  color: #fff;
}
.role-ul .font-desc-url {
  width: 100%;
  line-height: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.role-ul .active {
  background: linear-gradient(-90deg, #46c5ff 0%, #0a2e6d 100%);
}
.role-ul .span-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: inline-block;
  background: #fff url("../../assets/icons/sn.png") no-repeat center;
  background-size: 80%;
  margin-top: 10px;
}
.role-ul .font-desc {
  width: calc(100% - 50px);
}
.role-ul .font-desc .font-desc-name {
  //color: #edcb23;
  // color: #8590b6;
  font-size: 16px;
  line-height: 25px;
}
.role-ul .active .font-desc .font-desc-name {
  color: #fff;
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
//
/* 跳入动画 */
.jump-enter-active, .jump-leave-active {
  transition: transform 0.5s ease, opacity 0.5s ease;
}

.jump-enter, .jump-leave-to {
  transform: scale(0.5) translateY(20px); /* 元素缩放并向下位移 */
  opacity: 0;
}

.jump-enter-to, .jump-leave {
  transform: scale(1) translateY(0); /* 元素恢复原位 */
  opacity: 1;
}

// 抖动动画
@keyframes shake {
  0%, 100% { transform: scale(0.85) rotate(-2deg); }
  25% { transform: scale(0.85) rotate(-1deg); }
  75% { transform: scale(0.85) rotate(-3deg); }
}

// 脉冲动画
@keyframes pulse {
  0% { transform: scale(1.05); }
  50% { transform: scale(1.08); }
  100% { transform: scale(1.05); }
}

// 边框发光动画
@keyframes borderGlow {
  0% { 
    box-shadow: 0 0 5px #2681ff,
                0 0 10px #2681ff,
                0 0 15px #2681ff;
    opacity: 1;
  }
  50% { 
    box-shadow: 0 0 20px #2681ff,
                0 0 35px #2681ff,
                0 0 50px #2681ff;
    opacity: 0.8;
  }
  100% { 
    box-shadow: 0 0 5px #2681ff,
                0 0 10px #2681ff,
                0 0 15px #2681ff;
    opacity: 1;
  }
}

// 添加拖拽轨迹效果
.drag-trail {
  position: fixed;
  pointer-events: none;
  width: 10px;
  height: 10px;
  background: #2681ff;
  border-radius: 50%;
  opacity: 0.6;
  animation: fadeOut 0.5s forwards;
}

@keyframes fadeOut {
  to {
    transform: scale(0.3);
    opacity: 0;
  }
}
</style>
<style lang="scss">
@import "@/styles/mixin.scss";
.el-pagination__total {
  @include font_color("logo-font-color");
  /* color: #fff !important; */
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
  @include font-color("font-color");
}
.el-input__inner,
.el-textarea__inner,
.el-select-dropdown__empty,
.el-select-dropdown {
  /* background-color: #1c222b !important; */
  @include background_color("bg-color");
  border: 1px solid #0b0c0d !important;
  @include font_color("font-color");
  //   color: #ddd !important;
}
.search-input .el-input__inner {
  border: 1px solid !important;
  @include border_color("btn-bg");
  /* border-top-left-radius: 5px;
    border-bottom-left-radius: 30px; */
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  //   color: #fff;
  @include font_color("font-color");
}
.el-input__inner:hover {
  border: 1px solid #2681ff !important;
}
.el-pagination.is-background .el-pager li:not(.disabled).active {
  //   background: #0548a5 !important;
  @include background_color("btn-bg");
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
  @include background_color("btn-bg");
  //   background: #2681ff !important;
}
.el-select-dropdown__item,
.el-input.is-disabled .el-input__inner,
.el-textarea.is-disabled .el-textarea__inner,
.el-dialog__title {
  //   color: #fff !important;
  @include font_color("font-color");
}
.scale-up-center {
  -webkit-animation: scale-up-center 0.4s cubic-bezier(0.39, 0.575, 0.565, 1) both;
  animation: scale-up-center 0.4s cubic-bezier(0.39, 0.575, 0.565, 1) both;
}
@-webkit-keyframes scale-up-center {
  0% {
    -webkit-transform: scale(0.5);
    transform: scale(0.5);
  }
  100% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }
}
@keyframes scale-up-center {
  0% {
    -webkit-transform: scale(0.5);
    transform: scale(0.5);
  }
  100% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }
}
.left_item .step_card {
  display: none;
}
//分组
.group-data {
  height: 45px;
  width: 100%;
  border: 1px solid;
  display: flex !important;
  border-radius: 0 !important;
  border-image: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2 !important;
  cursor:auto;
  // border-image: linear-g radient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2;
  // border-left: none !important;
  // padding-left: 0 !important;
}
.child-group {
  width: 100%;
  // position: relative;
  .child-item {
    // transform: skewX(-40deg);
    // width: 90%;
    height: 35px;
    margin: auto;
    border: 1px solid;
    border-image: linear-gradient(270deg, #74f0ff 0%, #2681ff 74%) 2 2 2 2;

    // border-left: 3px solid #1894ff;
    border-radius: 5px;
    padding-left: 50px;
    // position: absolute;
    background: #1c222b;
    text-align: center;
    margin: 10px;
    span {
      display: inline-block;
      transform: skewX(40deg);
      color: #fff;
    }
  }
  .child-item:nth-child(1) {
    left: 10px;
    top: 10px;
  }
  .child-item:nth-child(2) {
    left: 10px;
    top: 60px;
  }
  .child-item:nth-child(3) {
    left: 40px;
    top: 10px;
    display: none;
  }
  .child-item:nth-child(4) {
    left: 60px;
    top: 10px;
    display: none;
  }
  .child-item:nth-child(5) {
    left: 80px;
    top: 10px;
    display: none;
  }
}
.left_item {
  cursor: move;
}
.icon-box {
  display: flex;
  margin-left: -35px;
  div {
    width: 6px;
    height: 45px;
    border: 1px solid #2681ff;
    background: linear-gradient(to left, #a2ca0f, #2681ff);
  }
  div:nth-child(1) {
    margin: 0 3px 0 0;
    background: #74f0ff;
  }
  div:nth-child(2) {
    margin: 0 3px;
    background: #4db9ff;
  }
  div:nth-child(3) {
    margin: 0 3px;
    background: #2681ff;
  }
}
@font-face {
  font-family: "AlibabaPuHuiTi";
  src: url("../../assets/fonts/YouSheBiaoTiHei-2.ttf");
}
</style>
