<template>
<el-container>
  <el-main>
    <el-card style="width: 80%;margin: 0 auto">
      <el-tabs v-model="active" style="width: 100%">
        <el-tab-pane label="最热" name="hot">
          <article-page style="width: 100%;margin: 0 auto"/>
        </el-tab-pane>
        <el-tab-pane label="推荐" name="refer">推荐</el-tab-pane>
      </el-tabs>
    </el-card>
  </el-main>
</el-container>
</template>

<script>
import ArticlePage from "@/components/ArticlePage.vue";

export default {
  name: "Home",
  data(){
    return{
      active:'hot'
    }
  },
  components:{
    'article-page':ArticlePage
  },

}
</script>

<style scoped>

</style>