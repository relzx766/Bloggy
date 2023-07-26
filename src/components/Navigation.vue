<template>
      <el-menu :default-active="activeIndex" active-text-color="#449cfc" class="el-menu-demo"
               mode="horizontal" style="border-bottom-left-radius: 14px;border-bottom-right-radius: 14px;height: 60px;"
      >
        <el-menu-item><img class="menu-img" src="../static/images/logo.svg"></el-menu-item>
        <el-menu-item style="font-size: 26px;color: #449cfc">Bloggy</el-menu-item>
        <el-menu-item index="1" style="margin-left: 15%;" @click="changePage('1')"> 主页</el-menu-item>
        <el-menu-item index="2" @click="changePage('2')">趋势</el-menu-item>
        <el-menu-item style="margin-left: 6%">
          <el-input v-model="keyword" class="input-with-select" placeholder="输入以搜索">
            <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
          </el-input>

        </el-menu-item>
        <el-menu-item index="3" @click="changePage('3')">
          <i class="el-icon-edit"></i>
        </el-menu-item>
        <el-menu-item>
          <el-badge :value="1">
            <el-button icon="el-icon-message" size="small"></el-button>
          </el-badge>
        </el-menu-item>
        <el-menu-item v-if="!isLogin" style="color: #449cfc;float: right">
          <router-link to="/login">登录</router-link>
        </el-menu-item>
        <el-submenu v-if="isLogin" index="4" style="float: right">
          <template slot="title">
            <el-avatar :size="'medium'" :src="avatar"></el-avatar>
          </template>
          <el-menu-item-group>
            <el-menu-item index="4-1" @click="toProfile(userId)">
              <i class="el-icon-user-solid"/>
              <span>个人中心</span>
            </el-menu-item>
            <el-menu-item index="4-2" @click="signOut">
              <i class="el-icon-switch-button"/>
              <span>退出</span>
            </el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
</template>

<script>
export default {
  name: "Navigation",
  props: {
    activeIndex: {
      type: String,
      default() {
        return '1'
      }
    }
  },
  data() {
    return {
      keyword: '',
      isLogin: false,
      currentIndex: '1',
      avatar: '',
      userId: '',
    }
  },
  methods: {
    signOut() {
      localStorage.removeItem("satoken")
      this.$cookie.delete("id")
      this.$cookie.delete("username")
      this.$cookie.delete("nickname")
      this.$cookie.delete("avatar")
      this.$router.push("/login")
    },
    changePage(index) {
      console.log("改变页面", index)
      this.currentIndex = index
      if (index == '1') {
        this.$router.push("/home")
      }
      if (index == '2') {
        this.$router.push('/trend')
      }
      if (index == '3') {
        this.$router.push('/write')
      }
    },
    toProfile(id) {
      this.$router.push("/profile?id=" + id)
    },
    search() {
      if (this.keyword.length > 0) {
        this.$router.push("/search?keyword=" + encodeURIComponent(this.keyword))
        this.$forceUpdate()
      }
    }
  },
  created() {
    this.avatar = this.$cookie.get("avatar")
    this.userId = this.$cookie.get("id");
  }
  ,
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

