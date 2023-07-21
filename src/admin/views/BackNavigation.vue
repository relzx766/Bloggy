<template>
  <el-row>
    <el-col :span="2">
      <el-image src="http://localhost:8080/default/logo.svg" fit="'cover'" style="height: 70px">
      </el-image>
    </el-col>
    <el-col :span="4">
      <h2 style="font-size: 20px">Bloggy后台管理系统</h2>
    </el-col>
    <el-col :span="18" style="text-align: right">
      <el-dropdown>
        <el-button  icon="el-icon-s-custom
">
          <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-setting">设置</el-dropdown-item>
          <el-dropdown-item icon="el-icon-switch-button" @click.native="signOut" >退出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-col>
  </el-row>

</template>

<script>
export default {
  name: "BackNavigation",
  data(){
    return{
      admin:{
        id:0,
        nickname:''
      }
    }
  },
methods:{
    signOut(){
      localStorage.removeItem("satoken")
      this.$cookie.delete("id")
      this.$cookie.delete("username")
      this.$cookie.delete("nickname")
      this.$cookie.delete("avatar")
      this.$router.push("/login")
    }
}
,
  created() {
    let cookie=this.$cookie
    let id=cookie.get("id")
    if (!id){
      this.$router.push("/login")
    }
    this.admin.id=cookie.get("id")
    this.admin.nickname=cookie.get("nickname");
  }
}
</script>

<style scoped>

</style>