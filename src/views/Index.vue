<template>
<div id="index" style="background-color: #0c0c14;width: 100%">
<el-container direction="vertical" style="background-color: #0c0c14">
  <div style="width: 100%;height: 500px;overflow: hidden;margin-bottom: -400px">
    <img style="width: 100%;height: 500px;object-fit: cover" src="../static/images/banner.png">
  </div>
  <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
<navigation :active-index="'1'"/>
  </el-header>
  <el-container style="margin-top: 200px">
  <el-aside style="margin-top: 60px;width:300px;margin-left: 20px" class="aside">
      <div id="sidebar-wrapper">
        <div id="info-card">
        <el-card style="border-radius: 20px;border: none;background-color:#24242c" :body-style="{ padding: '0px'}" shadow="always">
          <el-image
              style="width:100%; height: 100%;border-radius: 20px"
              :src="user.avatar"
              ></el-image>
          <div style="padding: 14px;">
            <h3 style="color: white">{{user.nickname}}</h3>
            <span style="color:#848286 ">@{{user.username}}</span>
          </div>
        </el-card></div>
      </div>
    <el-card class="count-card" style="border-radius: 20px;padding: 0px;width: 280px;background-color: #24242c;border: none">
      <div  class="text item">
        <ul>
        <li>
          <span class="count-left">获得点赞</span>
          <span class="count-right">{{getCount(state.beLikeCount)}}</span>
        </li>
        <li>
          <span class="count-left">点击</span>
          <span class="count-right">{{getCount(state.viewCount)}}</span>
        </li>
        <li>
          <span class="count-left">文章</span>
          <span class="count-right">{{getCount(state.articleCount)}}</span>
        </li>
        <li>
          <span class="count-left">归档</span>
          <span class="count-right">{{getCount(state.sortCount)}}</span>
        </li>
        <li>
          <span class="count-left">点赞</span>
          <span class="count-right">{{getCount(state.likeCount)}}</span>
        </li>
        <li>
          <span class="count-left">评论</span>
          <span class="count-right">{{getCount(state.commentCount)}}</span>
        </li>
        </ul>
      </div>
    </el-card>
    <el-card style="border-radius: 20px;padding: 0px;width: 280px;background-color: #24242c;border: none;
    margin-top:60px;color: white;text-align: left ">
      <ul>
        <li style="margin-top: -20px;margin-left: -20px"><h3 style="float: left">参考</h3></li>
        <li>@hexo/vivia-preview</li>
        <li>@github/shimh-develop</li>
      </ul>
    </el-card>
  </el-aside>
  <el-main width="1000px" >
    <div id="main">
      <article-page  :article-pages="articlePages"/>
    </div>
  </el-main>
  </el-container>
  <el-container>
    <el-footer>
      <div class="block" style="background-color: #24242c;height: 80px;border-top-left-radius: 14px;border-top-right-radius: 14px;
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
  </el-container>
</el-container>
</div>
</template>

<script>
import Navigation from "@/components/Navigation";
import {getArticlePage,getUserState} from "@/api";
import ArticlePage from "@/components/ArticlePage";
import {getCount} from "@/util/tools";
import Trend from "@/views/Trend";
import ArticleDetail from "@/views/ArticleDetail";

export default {
  name: "Index",
  data(){
    return{
      currentPage:1,
      user: {
        username: '',
        nickname:'',
        avatar:''
      },
      state:{
        articleCount:0,
        commentCount:0,
        sortCount:0,
        likeCount:0,
        beLikeCount:0,
        viewCount:0
      },
      articleCount:0,
      articlePages:[{}]
    }
  },
    methods: {
    getPage(page){
      getArticlePage(page).then((res)=>{
        this.articleCount=res.data.count
        this.$set(this, 'articlePages', res.data.articles)
        console.log(this.articlePages)
      })
    },
      getCount(num){
      return getCount(num);
      },
      getState(){
      let id=this.$cookie.get("id");
      getUserState(id).then((res)=>{
        this.state=res.data.state
        console.log(this.state)
      })
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
  beforeMount() {
    let that=this;
    that.user.username=that.$cookie.get("username");
    that.user.nickname=that.$cookie.get("nickname");
    that.user.avatar=that.$cookie.get("avatar");
    console.log(that.user)
    that.getPage(1)
    that.getState()
  }
}
</script>

<style scoped>

#info-card{
  width: 280px;
  height: 460px;
}
.count-left{
  float: left;
  width: 40%;
  margin-top: 10px;
  color: #303133;
  font-size: 16px;
  margin-left: -50px;
  text-align: left;
  color: white;
}
.count-right{
  float: right;
  height: 20px;
  min-width: 25px;
  max-width: none;
  margin-top: 10px;
  color: #000000;
  border-radius: 5px;
  text-align: center;
  background: #449cfc;
}
.text {
  font-size: 14px;
  margin-top: -20px;
}
li{
  list-style-type: none;
  clear: both;
  height: 25px;
}
.item {
}
#content{
  width: 100%;
}
.li-article{
  width: 80%;
}
</style>
