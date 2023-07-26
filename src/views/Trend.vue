<template>
  <el-container>
    <el-main>
      <div style="margin-left: auto;margin-right: auto;width: 70%;min-height: 800px;">
        <div class="advertising-board">
          <el-carousel height="300px" style="border-radius: 10px">
            <el-carousel-item v-for="item in advertising" :key="item">
              <div>
                <el-image
                    fit="cover"
                    :src="item.image"
                    style="width: 100%; height: 300px;border-radius: 10px"
                    @click="toLink(item.url)"></el-image>
                <h4 class="ad-title">{{ item.slogan }}</h4>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <el-container>
          <el-main style="margin-top: 40px;border-radius: 14px">
            <div>
              <el-card>
                <ul style="text-align: center;display: block">
                  <li v-for="(tag,index) in Object.keys(trends)" class="article-trend" @click="toDetail(tag)">
                    <a href="#" style="line-height: 30px">
                      <el-row>
                        <el-col :span="1">
                          <el-tag size="mini">
                            <i class="el-icon-s-marketing"/>
                            {{ index + 1 }}
                          </el-tag>
                        </el-col>
                        <el-col :span="21">
                          {{ tag }}
                        </el-col>
                        <el-col :span="2" style="float: right">
                          <i class="el-icon-s-data"/>
                          {{ trends[tag] }}
                        </el-col>
                      </el-row>
                    </a>
                  </li>
                </ul>
              </el-card>
            </div>
          </el-main>
        </el-container>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import {getAd, getTrend} from "@/api/Common";
import Navigation from "@/components/Navigation";

export default {
  name: "Trend",
  data() {
    return {
      trends: {},
      advertising: []
    }
  },
  methods: {
    getTrend() {
      getTrend().then((res) => {
        this.trends = res.data.trends
      })
    },
    toDetail(tag) {
      tag = '#' + tag + '#'
      this.$router.push("/search?keyword=" + encodeURIComponent(tag));
    },
    getAd() {
      getAd().then((res) => {
        this.advertising = res.data.ads
        console.log(this.advertising)
      })
    },
    toLink(url) {
      window.open(url, '_blank') // 在新窗口打开外链接
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
  },

}
</script>

<style scoped>
a {
  text-decoration: none; /* 去除默认的下划线 */
  outline: none; /* 去除旧版浏览器的点击后的外虚线框 */
  color: #000000; /* 去除默认的颜色和点击后变化的颜色 */
}

li {
  list-style-type: none;
}

.article-trend {
  width: 100%;
  text-align: left;
  clear: both;
  height: 30px;
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

.article-trend-view {
  float: right;
  margin-right: -250px;
  text-align: left;
  width: 100px;
}
</style>
