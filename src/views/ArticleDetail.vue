<template>
  <el-container>
    <el-main style="margin: 80px auto;width: 90%;">
      <el-row style="display: flex">
       <div style="width: 76%">
         <el-card id="main">
           <el-row><span style="font-size: 24px;font-weight: bold">{{article.title}}</span></el-row>
           <el-row style="margin-top: 4%;margin-bottom: 4%">
             <el-breadcrumb separator="|">
               <el-breadcrumb-item class="text-item" style="font-weight: bolder">{{ article.userVo.nickname }}</el-breadcrumb-item>
               <el-breadcrumb-item class="text-item">
               {{article.views}}阅读
               </el-breadcrumb-item>
               <el-breadcrumb-item class="text-item">
                 {{article.likeNum}}点赞
               </el-breadcrumb-item>
             </el-breadcrumb>
           </el-row>
           <div id="content">
             <mavon-editor
                 ref="md"
                 v-model="article.content"
                 :editable="false"
                 :subfield="false"
                 :toolbarsFlag="false"
                 defaultOpen="preview"
                 style="z-index: 5;border: none"
             ></mavon-editor>
           </div>
         </el-card>
         <div id="tag">
           <el-card>
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
                   <el-button circle @click="chooseSortAction">
                     <i :class="{'el-icon-sort':!article.isSort,'el-icon-sorted':article.isSort}" class="el-icon-sort"></i>
                   </el-button>
                 </el-col>
               </el-row>
               <el-dialog
                   :visible.sync="dialogVisible"
                   title="收藏"
                   width="20%"
               >
                 <div>
                   <el-container>
                     <el-main>
                       <el-radio-group v-model="choose">
                         <el-row v-for="item in sorts" style="margin-top: 8px">
                           <el-radio :label="item.id">{{ item.title }}</el-radio>
                         </el-row>
                       </el-radio-group>
                     </el-main>
                     <el-footer>
                       <el-input v-model="sortTitle" maxlength="15" placeholder="新建收藏夹">
                         <el-button slot="append" icon="el-icon-circle-plus" @click="createSort"></el-button>
                       </el-input>
                     </el-footer>
                   </el-container>
                 </div>
                 <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="appendArticleToSort">确 定</el-button>
  </span>
               </el-dialog>
             </div>

             <el-link v-for="tag in article.tags" @click.native="searchByTag(tag)">
               <el-tag>{{ tag }}
               </el-tag>
             </el-link>
           </el-card>
         </div>
         <el-card class="box-card" style="margin: 0 auto;">
           <div slot="header" class="clearfix" style="height: 30px" @click="changeType">
             <span style="float: left;font-weight: bolder;font-size: 20px">评论</span>
           </div>
           <comment :id="article.id" :author="article.userVo.id" :type="type"/>
         </el-card>
       </div>
          <div style="width: 22%;margin-left: 2%">
            <el-card >
              <author-introduce :author="article.userVo"/>
              <el-divider/>
              <h3>相关阅读</h3>
              <related-read :tags="article.tags" :size="5"/>
            </el-card>
          </div>




      </el-row>

    </el-main>

  </el-container>

</template>

<script>
import {cancelLikeArticle, getArticleDetail, likeArticle} from "@/api/Article";
import Navigation from "@/components/Navigation";
import Unfold from "@/components/Unfold";
import {getCount, getDate} from "@/util/tools";
import Comment from "@/components/Comment";
import {append, cancelSort, createSort, getByUser} from "@/api/Sort";
import AuthorIntroduce from "@/components/AuthorIntroduce.vue";
import RelatedRead from "@/components/RelatedRead.vue";

export default {
  name: "ArticleDetail",
  data() {
    return {
      id: String,
      article: {},
      relation: '+ 关注',
      flag: 0,
      time: '',
      type: 0,
      dialogVisible: false,
      choose: String,
      userId: String,
      sorts: [],
      sortTitle: '',
      sortPlaceholder: '新建收藏夹',

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
        this.article.views = getCount(this.article.views);
        this.article.likeNum = getCount(this.article.likeNum);
        this.article.comments = getCount(this.article.comments)
        console.log(this.article)
      })
    },
    getHtml() {
      return this.$refs.md.$render(this.article.content)
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
    changeType() {
      this.type = 1;
      this.type = 0;
    },
    searchByTag(tag) {
      tag = '#' + tag + '#'
      this.$router.push("/search?keyword=" + encodeURIComponent(tag));
    },
    getSorts() {
      this.dialogVisible = true;
      getByUser(this.userId).then((res) => {
        this.sorts = res.data.sorts
      })
    },
    createSort() {
      createSort(this.sortTitle).then((res) => {
        if (res.code === 2001) {
          this.sortTitle = ''
          this.sortPlaceholder = '新建收藏夹'
          this.sorts.push(res.data.sort)
        } else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration: 1000
          })
        }
      })
    },
    appendArticleToSort() {
      append(this.choose, this.id).then((res) => {
        if (res.code === 2001) {
          this.article.isSort = true
          this.dialogVisible = false
        } else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration: 1000
          })
        }
      })
    },
    cancelSort() {
      cancelSort(this.id).then((res) => {
        if (res.code === 2001) {
          this.article.isSort = false
        } else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration: 1000
          })
        }
      })
    },
    chooseSortAction() {
      if (this.article.isSort) {
        this.cancelSort()
      } else {
        this.getSorts()
      }
    }
  },
  components: {
    'navigation': Navigation,
    'unfold': Unfold,
    'comment': Comment,
    'author-introduce':AuthorIntroduce,
    'related-read':RelatedRead
  },
  beforeMount() {
    let id = this.$route.query.id
    this.id = id
    this.getDetail(id)
  },
  created() {
    this.userId = this.$cookie.get("id")
  }
}
</script>

<style scoped>
li {
  list-style-type: none;
  float: left;
  text-align: left;
}

.el-link + .el-link {
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

#tag {
  padding-top: 10px;
  text-align: left;
  margin: 0 auto 20px;
}

.comments {
  margin-left: 10px;
}

::v-deep .el-icon-view {
  background: url('@/static/images/view.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-view:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-like {
  background: url('@/static/images/like.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-like:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-liked {
  background: url('@/static/images/like_fill.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-liked:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-comment {
  background: url('@/static/images/interactive.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-comment:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-sort {
  background: url('@/static/images/document.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-sort:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

::v-deep .el-icon-sorted {
  background: url('@/static/images/document_fill.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-sorted:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}

.el-button {
  border: none;
  margin-left: 10px;
}
</style>
