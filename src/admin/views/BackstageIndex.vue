<template>
<el-container>
  <el-header >
    <navigation/>
  </el-header>
  <el-main>
    <el-tabs tab-position="left">
      <el-tab-pane label="用户管理"><user-manage/></el-tab-pane>
      <el-tab-pane label="文章管理"><article-manage/></el-tab-pane>
      <el-tab-pane label="广告管理"><ad-manage/></el-tab-pane>

    </el-tabs>


  </el-main>
</el-container>
</template>

<script>
import BackNavigation from "@/admin/components/BackNavigation.vue";
import UserManage from "@/admin/components/UserManage.vue";
import ArticleManage from "@/admin/components/ArticleManage.vue";
import AdvertisingManage from "@/admin/components/AdvertisingManage.vue";

export default {
  name: "BackstageIndex",
  components:{
    'navigation':BackNavigation,
    'user-manage':UserManage,
    'article-manage':ArticleManage,
    'ad-manage':AdvertisingManage
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

</style>