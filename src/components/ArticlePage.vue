<template>
  <el-container>
    <el-main>
      <div id="content">
        <article-list :articles="articles"/>
      </div>
    </el-main>
    <el-footer>
      <div class="block" style="height: 80px;border-top-left-radius: 14px;border-top-right-radius: 14px;
margin-top: -20px">
        <el-pagination
            :current-page.sync="currentPage"
            :page-size="15"
            :total="total"
            background
            layout="total, prev, pager, next,jumper"
            style="color: white;padding-top: 30px;"
            @current-change="getPage">
        </el-pagination>
      </div>
    </el-footer>
  </el-container>

</template>
<script>
import {getArticlePage} from "@/api/Article";
import {getDate} from "@/util/tools";
import ArticleList from "@/components/ArticleList";

export default {
  name: "ArticlePage",
  data() {
    return {
      currentPage: 1,
      total: 0,
      articles: []
    }
  },
  methods: {
    toDetail(id) {
      this.$router.push("/detail?id=" + id);
    },
    getData(date) {
      return getDate(date)
    },
    getPage(page) {
      getArticlePage(page).then((res) => {
        this.total = res.data.count
        console.log(res.data.articles)
        this.$set(this, 'articles', res.data.articles)
      })
    }
  },
  components: {
    'article-list': ArticleList
  }
  ,
  created() {
    this.getPage(1);
  }
}
</script>

<style scoped>
ul {
  text-align: center;
  line-height: 34px;
}

.text-item {
  line-height: 40px;
}

li {
  list-style-type: none;
  clear: both;
  height: 25px;
  display: inline-block;
  vertical-align: top;
}

#content {
  width: 100%;
  text-align: center;
}

.li-article {
  width: 90%;
  margin: 0 auto;
  border-radius: 10px;
  height: 240px;
  margin-top: 20px;
}

.article-info span {
  height: 30px;
  vertical-align: top;
  float: top;
}

.article-card {
  border-radius: 10px;
  text-align: center;
  height: 240px;
  border: none;
}

svg g polyline {
}
</style>
