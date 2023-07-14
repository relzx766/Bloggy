<template>
  <el-container>
    <el-header>
      <navigation :active-index="'0'"/>
    </el-header>
    <el-main style="width: 70%;margin: 0 auto;">
      <el-tabs v-model="activeName">
        <el-tab-pane label="文章" name="first">
          <el-container v-if="articleTotal>0" >
            <el-main>
              <article-list :articles="articles"/>
            </el-main>
            <el-footer>
              <el-pagination
                  style="color: white;padding-top: 30px;"
                  background
                  @current-change="chooseSearchType"
                  :current-page.sync="articlePage"
                  :page-size="15"
                  layout="total, prev, pager, next,jumper"
                  :total="articleTotal">
              </el-pagination>
            </el-footer>
          </el-container >
          <el-empty description="空空如也" v-else></el-empty>
        </el-tab-pane>
        <el-tab-pane label="用户" name="third">
          <user-list :users="users"  v-if="users.length>0"/>
          <el-empty description="空空如也" v-else></el-empty>

        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>

<script>
import Navigation from "@/components/Navigation";
import {getByTag, fuzzySearch} from "@/api/Article";
import ArticleList from "@/components/ArticleList";
import SortList from "@/components/SortList.vue";
import UserList from "@/components/UserList.vue";
import {searchUser} from "@/api/User";

export default {
  name: "SearchDetail",
  data() {
    return {
      keyword: '',
      activeName: 'first',
      articles: [],
      articlePage: 1,
      articleTotal: 0,
      tags: [],
      users: []
    }
  },
  methods: {
    chooseSearchType() {
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
    searchByTag(tags) {
      let data = new FormData
      data.append("tags", tags)
      data.append("page", this.articlePage)
      console.log(data)
      getByTag(data).then((res) => {
        this.articles = res.data.articles
        this.articleTotal = res.data.count
        console.log(this.articles)
      })
    },
    fuzzySearch() {
      fuzzySearch(encodeURIComponent(this.keyword), this.articlePage).then((res) => {
        this.articles = res.data.articles
        this.articleTotal = res.data.count
      })
    },
    searchUser() {
      searchUser(encodeURIComponent(this.keyword)).then((res) => {
        this.users = res.data.users
        this.$forceUpdate()
      })
    }
  }
  ,
  components: {
    'navigation': Navigation,
    'article-list': ArticleList,
    'user-list': UserList
  }
  ,
  created() {
    this.keyword = this.$route.query.keyword
    this.chooseSearchType()
    this.searchUser()
  },
  watch: {
    '$route'(to, from) {
      this.keyword = to.query.keyword
      this.chooseSearchType()
    }
  }
}
</script>

<style scoped>

</style>
