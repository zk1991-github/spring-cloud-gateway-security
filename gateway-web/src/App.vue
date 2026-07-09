<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>


export default {
  name: 'App',
  components: {

  }
}
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
::-webkit-scrollbar {
  width: 4px;
}
::-webkit-scrollbar-thumb {
  border-radius: 2px;
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  background: rgba(0, 0, 0, 0.2);
}
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: 0;
  background: rgba(0, 0, 0, 0.1);
}
.warning-row {
  background-color: #f9fbff !important;
}
.el-input.is-disabled .el-input__inner,
.el-textarea.is-disabled .el-textarea__inner {
  color: #333 !important;
}
.el-checkbox__input.is-disabled + span.el-checkbox__label {
  color: #333 !important;
}
.el-radio__input.is-disabled + span.el-radio__label {
  color: #333 !important;
}
</style>
