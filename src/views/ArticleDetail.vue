<template>
  <el-container>
    <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
      <navigation :active-index="'0'"/>
    </el-header>
    <el-main style="margin-top: 80px;margin-bottom: 80px">
      <div id="main">
        <div id="userInfo">
          <el-card class="info-card">
            <el-row >
              <el-col :span="12">
                <el-row>
                  <el-col :span="5">
                    <el-avatar :size="100" shape="circle" :src="article.userVo.avatar"/>
                  </el-col>
                  <el-col :span="14" style="text-align: left">
                    <el-row><h3>{{ article.userVo.nickname }}</h3></el-row>
                    <el-row>{{ '@' + article.userVo.username }}</el-row>
                  </el-col>
                </el-row>
              </el-col>
              <el-col :span="12" style="text-align: right">
                <el-button style="margin-top: 26px" type="primary" @click="changeRelation">{{
                    relation
                  }}
                </el-button>

              </el-col>
            </el-row>

          </el-card>
        </div>
        <div id="title" style="margin-bottom: 8px;text-align: center">
          <h1 style="font-size: 28px">{{ article.title }}</h1>
        </div>
        <div id="article-info" style="font-size: 10px;font-weight: lighter;height: 14px;margin-top: 6px;text-align: center">
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
          <el-col :span="8" style="margin-top: 10px"><span>最后编辑于:{{ time }}</span></el-col>
              <el-col :span="16" style="text-align: right;">
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

        <el-link  v-for="tag in article.tags" @click.native="searchByTag(tag)"><el-tag>{{ tag }}
        </el-tag></el-link>
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
import {getArticleDetail, likeArticle, cancelLikeArticle} from "@/api/Article";
import {marked} from "marked"
import Navigation from "@/components/Navigation";
import Unfold from "@/components/Unfold";
import {getDate,getCount} from "@/util/tools";
import Comment from "@/components/Comment";

export default {
  name: "ArticleDetail",
  data() {
    return {
      id: String,
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
        console.log(this.article.isLike)
        this.article.createTime = (new Date(this.article.createTime)).toLocaleString()
        if (this.article.updateTime == null) {
          this.time = this.article.createTime
        } else {
          this.article.updateTime = (new Date(this.article.createTime)).toLocaleString()
          this.time = this.article.updateTime
        }
        this.article.views=getCount( this.article.views);
        this.article.likeNum=getCount(this.article.likeNum);
        this.article.comments=getCount(this.article.comments)
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
    getDate(date) {
      return getDate(date);
    },
    changeType(){
      this.type=1;
      this.type=0;
    },
    searchByTag(tag){
      tag='#'+tag+'#'
      this.$router.push("/search?keyword="+encodeURIComponent(tag));
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
  }
}
</script>

<style scoped>
li {
  list-style-type: none;
  float: left;
  text-align: left;
}
.el-link+.el-link{
  margin-left: 4px;
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
::v-deep .el-icon-view{
  background: url('../static/images/view.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-view:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
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
