<template>
  <el-container>
    <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
      <navigation :active-index="'0'"/>
    </el-header>
    <el-main style="margin-top: 80px;margin-bottom: 80px">
      <div id="main">
        <div id="userInfo">
          <el-card class="info-card">
            <ul>
              <li>
                <el-image style="border-radius: 50%;width: 100px;height: 100px" :src="article.userVo.avatar">
                  <div slot="placeholder" class="image-slot">
                    加载中<span class="dot">...</span>
                  </div>
                </el-image>
              </li>
              <li><h3>{{ article.userVo.nickname }}</h3>
                <span>{{ '@' + article.userVo.username }}</span>
              </li>

            </ul>

            <el-button style="float: right;margin-top: 16px" type="primary" @click="changeRelation">{{
                relation
              }}
            </el-button>
          </el-card>
        </div>
        <div id="title" style="height: 40px">
          <h1 style="font-size: 36px">{{ article.title }}</h1>
        </div>
        <div id="article-info" style="font-size: 10px;font-weight: lighter;height: 14px;margin-top: 6px">
          <span>{{ article.views }}阅读</span>
          <span>{{ article.comments }}评论</span>
          <span>{{ article.likeNum }}喜欢</span>
          <span>{{ article.createTime }}</span>
        </div>
        <div id="content">
          <mavon-editor
              :value="getHtml()"
              :subfield="false"
              defaultOpen="preview"
              :toolbarsFlag="false"
              :editable="false"
              style="z-index: 5"
          ></mavon-editor>
        </div>
      </div>
      <div id="tag">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <el-row>
          <el-col :span="5" style="margin-top: 10px"><span>最后编辑于:{{ time }}</span></el-col>
              <el-col :span="18" style="text-align: right;">
                <el-button circle @click="like">
                <i :class="{'el-icon-like':!article.isLike,'el-icon-liked':article.isLike}"></i>
              </el-button>
                <span>{{ article.likeNum }}</span>
                <el-button circle>
                  <i class="el-icon-comment"></i>

                </el-button>
                <span>{{ article.comments }}</span>
                <el-button circle>
                  <i class="el-icon-sort"></i>
                </el-button></el-col>
              </el-row>
          </div>
          <el-tag v-for="tag in article.tags"
          >{{ tag }}
          </el-tag>
        </el-card>
      </div>
      <el-card class="box-card" style="width: 80%;margin: 0 auto;">
        <div slot="header" class="clearfix" style="height: 30px" @click="changeType">
          <span style="float: left;font-weight: bolder;font-size: 20px">评论</span>
        </div>
        <comment :id="article.id" :author="article.userVo.id" :type="type"/>
      </el-card>
    </el-main>

  </el-container>

</template>

<script>
import {getArticleDetail, getArticleIsLike, likeArticle, cancelLikeArticle, postComment, postReply} from "@/api";
import {marked} from "marked"
import Navigation from "@/components/Navigation";
import Unfold from "@/components/Unfold";
import {getDate} from "@/util/tools";
import Comment from "@/components/Comment";

export default {
  name: "ArticleDetail",
  data() {
    return {
      id: Number,
      article: {},
      relation: '+ 关注',
      flag: 0,
      time: '',
     type:0
    }
  },
  methods: {
    getDetail(id) {
      getArticleDetail(id).then((res) => {
        this.article = res.data.article
        this.article.tags = this.article.tags.split(",")
        this.article.createTime = (new Date(this.article.createTime)).toLocaleString()
        if (this.article.updateTime == null) {
          this.time = this.article.createTime
        } else {
          this.article.updateTime = (new Date(this.article.createTime)).toLocaleString()
          this.time = this.article.updateTime
        }
        console.log(this.article)
      })
    },
    getHtml() {
      return marked(this.article.content)
    },
    changeRelation() {
      let that = this
      if (that.flag == 0) {
        that.flag = 1
        this.relation = '关注中'
      } else {
        that.flag = 0;
        this.relation = "+ 关注"
      }
    },
    like() {
      if (this.article.isLike) {
        cancelLikeArticle(this.id)
        this.article.isLike = false
        this.article.likeNum -= 1
      } else {
        likeArticle(this.id)
        this.article.isLike = true
        this.article.likeNum += 1
      }
    },
    getIsLikeArticle(id) {
      getArticleIsLike(id).then((res) => {
        this.article.isLike = res.data.isLike
      })
    },
    getDate(date) {
      return getDate(date);
    },
    changeType(){
      this.type=1;
      this.type=0;
    }
  },
  components: {
    'navigation': Navigation,
    'unfold': Unfold,
    'comment': Comment
  },
  beforeMount() {
    let id = this.$route.query.id
    this.id = id
    this.getDetail(id)
    this.getIsLikeArticle(id)
  }
}
</script>

<style scoped>
li {
  list-style-type: none;
  float: left;
  text-align: left;
}

li + li {
  margin-left: 10px;
}

#content {
  margin-top: 10px;
}

span + span {
  margin-left: 10px;
}

#userInfo {
  margin-top: 20px;
}

#main {
  width: 80%;
  margin: 0 auto;
}

#tag {
  width: 80%;
  margin: 0 auto;
  padding-top: 10px;
  text-align: left;
  margin-bottom: 20px;
}

.comments {
  margin-left: 10px;
}

::v-deep .el-icon-like {
  background: url('../static/images/like.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-like:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-liked {
  background: url('../static/images/like_fill.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-liked:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-comment {
  background: url('../static/images/interactive.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-comment:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-sort {
  background: url('../static/images/document.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-sort:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

.el-button {
  border: none;
  margin-left: 10px;
}
</style>
