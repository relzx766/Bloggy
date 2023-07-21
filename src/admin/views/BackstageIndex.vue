<template>
<el-container>
  <el-header >
    <navigation/>
  </el-header>
  <el-main>
    <el-row>
      <el-col :span="2">
        <left-navigation/>
      </el-col>
      <el-col :span="22">
        <router-view></router-view>
      </el-col>
    </el-row>



  </el-main>
</el-container>
</template>

<script>
import BackNavigation from "@/admin/views/BackNavigation.vue";
import UserManage from "@/admin/views/UserManage.vue";
import ArticleManage from "@/admin/views/ArticleManage.vue";
import AdvertisingManage from "@/admin/views/AdvertisingManage.vue";
import DashBoard from "@/admin/views/DashBoard.vue";
import LeftNavigation from "@/admin/components/LeftNavigation.vue";

export default {
  name: "BackstageIndex",
  components:{
    'navigation':BackNavigation,
    'user-manage':UserManage,
    'article-manage':ArticleManage,
    'ad-manage':AdvertisingManage,
    'dash-board':DashBoard,
    'left-navigation':LeftNavigation
  },
  created() {
    let role=this.$cookie.get("role");
    if (role!=="admin"){
      this.$router.push("/index")
    }
  }
}
</script>

<style scoped>
body{
  background-color: #f4f4f4;
}
</style>