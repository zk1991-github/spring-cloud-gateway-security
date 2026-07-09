<template>
  <div class="error-page">
    <h1>404</h1>
    <p>页面未找到</p>
    <router-link to="/index">返回首页</router-link>
  </div>
</template>

<script>
export default {
  name: 'Error404'
}
</script>

<style scoped>
.error-page {
  text-align: center;
  margin-top: 50px;
}
</style>
