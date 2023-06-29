<template>
<el-container direction="vertical">
  <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
<navigation :active-index="'1'"/>
  </el-header>
  <el-container style="margin-top: 40px">
  <el-main>
    <div id="main">
      <el-card style="width: 80%;margin: 0 auto">
          <el-tabs style="width: 100%" v-model="activeName">
            <el-tab-pane label="最热" name="first">
              <article-page style="width: 100%;margin: 0 auto"/>
            </el-tab-pane>
            <el-tab-pane label="推荐" name="second">推荐</el-tab-pane>
          </el-tabs>
      </el-card>
    </div>
  </el-main>
  </el-container>
<!--  <el-container>
    <el-footer>
      <div class="block" style="height: 80px;border-top-left-radius: 14px;border-top-right-radius: 14px;
margin-top: -20px">
        <el-pagination
            style="color: white;padding-top: 30px;"
            background
            @current-change="getPage"
            :current-page.sync="currentPage"
            :page-size="15"
            layout="total, prev, pager, next,jumper"
            :total=articleCount>
        </el-pagination>
      </div>
    </el-footer>
  </el-container>-->
</el-container>
</template>

<script>
import Navigation from "@/components/Navigation";
import ArticlePage from "@/components/ArticlePage";
import {getCount} from "@/util/tools";
import Trend from "@/views/Trend";

export default {
  name: "Index",
  data(){
    return{
      user: {
        username: '',
        nickname:'',
        avatar:''
      },
      activeName:"first"
    }
  },
    methods: {
  /*  getPage(page){
      getArticlePage(page).then((res)=>{
        this.articleCount=res.data.count
        this.$set(this, 'articlePages', res.data.articles)
        console.log(this.articlePages)
      })
    },*/
      getCount(num){
      return getCount(num);
      },
      changeIndex(index){
        console.log(index)
      this.activeIndex=index
      },
    },
  components:{
    'navigation':Navigation,
    'article-page':ArticlePage,
    'trend':Trend,
  },
 created() {
   let that=this;
   that.user.username=that.$cookie.get("username");
   that.user.nickname=that.$cookie.get("nickname");
   that.user.avatar=that.$cookie.get("avatar");
   console.log(that.user)
/*   that.getPage(1)*/
 }
}
</script>

<style scoped>
li{
  list-style-type: none;
  clear: both;
  height: 25px;
}

</style>
