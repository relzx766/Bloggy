<template>
  <div id="nav">
    <div id="content">
      <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal"
               style="border-bottom-left-radius: 14px;border-bottom-right-radius: 14px;height: 80px;background-color: #24242c;
               border: none;
"
      background-color="#24242c" active-text-color="#449cfc"
      >
        <el-menu-item><img class="menu-img" src="../static/images/logo.svg"></el-menu-item>
        <el-menu-item style="font-size: 26px;color: #449cfc">Bloggy</el-menu-item>
       <el-menu-item index="1" style="margin-left: 15%;color: white" @click="changePage('1')"> 主页</el-menu-item>
      <el-menu-item index="2" style="color: #ffffff" @click="changePage('2')">趋势</el-menu-item>
        <el-menu-item style="margin-left: 6%">
          <el-input v-model="keyword" placeholder="搜索" style="width: 300px"></el-input>
        </el-menu-item>
        <el-menu-item index="3" @click="changePage('3')"> <img class="menu-img" style="height: 50%" src="../static/images/brush.svg" alt="写文章">
        </el-menu-item>
        <el-menu-item v-if="!isLogin" style="color: #449cfc;float: right">
          <router-link to="/login">登录</router-link>
        </el-menu-item>
        <el-menu-item v-if="isLogin" style="color: #449cfc;float: right">
          <router-link to="/login" style="color: #449cfc"><img @click="signOut" style="width: 30px;height: 30px" src="../static/images/sign-out.svg"></router-link>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="line"></div>
  </div>
</template>

<script>
export default {
  name: "Navigation",
  props:{
    activeIndex:{
      type:String,
      default(){
        return '1'
      }
    }
  },
  data() {
    return {
      keyword: '',
      isLogin: false,
      currentIndex:'1'
    }
  },
  methods: {
    signOut(){
      localStorage.removeItem("satoken")
      this.$cookie.delete("id")
      this.$cookie.delete("username")
      this.$cookie.delete("nickname")
      this.$cookie.delete("avatar")
    },
    changePage(index){
      console.log("改变页面",index)
      this.currentIndex=index
      if (index=='1'){
        this.$router.push("/index")
      }
      if (index=='2'){
        this.$router.push('/trend')
      }
      if (index=='3'){
        this.$router.push('/write')
      }
    }
  },
  beforeMount() {
    let that = this;
    that.isLogin = localStorage.getItem("satoken")
  }
}
</script>

<style scoped>
.menu-img {
  height: 100%;
}
</style>
