<template>
  <el-container>
    <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
     <navigation :active-index="'2'"/>
    </el-header>
  <div style="margin: 0 auto;width: 70%;min-height: 800px">
    <div class="advertising-board">
      <el-carousel height="300px" style="border-radius: 10px">
        <el-carousel-item v-for="item in advertising" :key="item">
            <div>
              <el-image
                  style="width: 100%; height: 300px;border-radius: 10px"
                  :src="'http://'+item.image"
                  @click="toLink(item.url)"
                  :fit="'cover'"></el-image>
              <h4 class="ad-title">{{ item.slogan }}</h4>
            </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <el-container>
      <el-main style="background-color: #24242c;margin-top: 40px;border-radius: 14px">
        <div>
          <ul style="text-align: center;display: block">
            <li class="article-trend" v-for="(item,index) in trend.article" @click="toDetail(item.id)">
              <a href="#">
              <span class="article-trend-index">{{ index+1 }}</span>
              <span class="article-trend-value">{{ item.title }}</span>
              <span class="article-trend-view"><img style="width: 14px;height: 14px" src="../static/images/trend.svg">{{item.trend}}</span></a>
            </li>
          </ul>
        </div>
      </el-main>
    </el-container>
  </div>
  </el-container>
</template>

<script>
import {getArticleTrend,getAd} from "@/api";
import Navigation from "@/components/Navigation";

export default {
  name: "Trend",
  data() {
    return {
      trend: {
        article: [{
          id: 0,
          title: '',
          views:0
        }]
      },
      advertising: [
      ]
    }
  },
  methods: {
    getTrend() {
      getArticleTrend().then((res) => {
        this.trend.article = res.data.articles
        console.log(this.trend.article)
      })
    },
    toDetail(id){
      this.$router.push("/detail?id="+id);
    },
    getAd(){
      getAd().then((res)=>{
        this.advertising=res.data.ads
        console.log(this.advertising)
      })
    },
    toLink(url){
      window.open(url,'_blank') // 在新窗口打开外链接
    }
  },
  components: {
    'navigation': Navigation
  }
  ,
  beforeMount() {
    let that = this;
    that.getTrend()
    this.getAd()
  }
}
</script>

<style scoped>
a {
  text-decoration: none; /* 去除默认的下划线 */
  outline: none;	/* 去除旧版浏览器的点击后的外虚线框 */
  color: #ffffff;	/* 去除默认的颜色和点击后变化的颜色 */
}
li {
  list-style-type: none;
}
html{
  background-color: #0c0c14;
}
.article-trend {
  width: 700px;
  text-align: left;
  clear: both;
  height: 25px;
  color: white;
}

.article-trend-index {
  background-color: #449cfc;
  min-width: 24px;
  display: inline-block;
  text-align: center;
  border-radius: 6px;
}

.article-trend-value {
  margin-left: 6px;
}

.advertising-board {
  margin-top: 100px;
}

.el-carousel-item {
  position: relative;
}

.ad-title {
  position: absolute;
  z-index: 10;
  color: white; /* you can adjust the position and size of the text as needed */
  left: 51%;
  top: 86%;
  transform: translate(-50%, -50%);
  font-size: 20px;
  text-align: left;
  width: 100%;
}
.article-trend-view{
  float: right;
  margin-right: -250px;
  text-align: left;
  width: 100px;
}
</style>
