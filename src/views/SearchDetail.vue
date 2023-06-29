<template>
<el-container>
  <el-header>
    <navigation :active-index="'0'"/>
  </el-header>
  <el-main style="width: 70%;margin: 0 auto;">
    <el-tabs v-model="activeName">
      <el-tab-pane label="文章" name="first">
      <el-container>
        <el-main>
          <article-list :articles="articles"/>
        </el-main>
        <el-footer>
          <el-pagination
              style="color: white;padding-top: 30px;"
              background
              @current-change="chooseSearchType"
              :current-page.sync="page"
              :page-size="15"
              layout="total, prev, pager, next,jumper"
              :total="total">
          </el-pagination>
        </el-footer>
      </el-container>
      </el-tab-pane>
      <el-tab-pane label="收藏夹" name="second">{{keyword}}</el-tab-pane>
      <el-tab-pane label="用户" name="third">{{keyword}}</el-tab-pane>
    </el-tabs>
  </el-main>
</el-container>
</template>

<script>
import Navigation from "@/components/Navigation";
import {getByTag,fuzzySearch} from "@/api/Article";
import ArticleList from "@/components/ArticleList";

export default {
  name: "SearchDetail",
  data(){
    return{
    keyword:'',
      activeName:'first',
      articles:[],
      page:1,
      total:0,
      tags:[]
    }
  },
  methods:{
    chooseSearchType(){
      let str = this.keyword;
      const regex = /#\s*([\u4e00-\u9fa5a-zA-Z]+)\s*#/g;
      const tags = [];
      let match;
      while ((match = regex.exec(str)) !== null) {
        tags.push(match[1]);
      }
      if (tags.length === 0) {
        this.fuzzySearch();
      } else {
        this.searchByTag(tags);
      }
    },
    searchByTag(tags){
      let data=new FormData
      data.append("tags",tags)
      data.append("page",this.page)
      console.log(data)
      getByTag(data).then((res)=>{
        this.articles=res.data.articles
        this.total=res.data.count
        console.log(this.articles)
      })
    },
    fuzzySearch(){
      fuzzySearch(encodeURIComponent(this.keyword),this.page).then((res)=>{
        this.articles=res.data.articles
        this.total=res.data.count
      })
    }
  }
  ,
  components:{
    'navigation':Navigation,
    'article-list':ArticleList
  }
  ,
  created() {
    this.keyword=this.$route.query.keyword

 this.chooseSearchType()
  }
}
</script>

<style scoped>

</style>
