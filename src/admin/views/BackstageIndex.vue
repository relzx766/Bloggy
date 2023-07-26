<template>
  <div>
    <el-row>
      <navigation/>
    </el-row>
    <el-row>
      <el-col :span="2">
        <el-menu active-text-color="#000000" default-active="1" style="height: 100vh;"
        >
          <el-menu-item index="1" @click="linkTo('/admin/board')">
            仪表盘
          </el-menu-item>
          <el-menu-item index="2" @click="linkTo('/admin/article')">
            文章管理
          </el-menu-item>
          <el-menu-item index="3" @click="linkTo('/admin/user')">
            用户管理
          </el-menu-item>
          <el-menu-item index="4" @click="linkTo('/admin/ad')">
            广告管理
          </el-menu-item>

        </el-menu>
      </el-col>
      <el-col :span="22">
        <router-view style="height: 100vh;background-color: whitesmoke;"></router-view>
      </el-col>
    </el-row>


  </div>
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
  components: {
    'navigation': BackNavigation,
    'user-manage': UserManage,
    'article-manage': ArticleManage,
    'ad-manage': AdvertisingManage,
    'dash-board': DashBoard,
    'left-navigation': LeftNavigation
  },
  methods: {
    linkTo(url) {
      this.$router.push(url);
    }
  },
  created() {
    this.$router.push("/admin/board")
  }
}
</script>

<style scoped>
html {
  padding: 0;
}

.el-menu-item.is-active {
  background-color: #289cfc !important;
}

</style>