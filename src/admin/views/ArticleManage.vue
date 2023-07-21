<template>
<div>
  <el-table
      ref="multipleTable"
      :data="articles"
      tooltip-effect="dark"
      style="width: 100%"
  >
    <el-table-column
        label="id"
        prop="id"
        width="120">
    </el-table-column>
    <el-table-column
        label="作者"
        prop="userVo.id"
        width="120">
    </el-table-column>
    <el-table-column
        label="标题"
        prop="title"
        show-overflow-tooltip
        width="120">
    </el-table-column>
    <el-table-column
        label="描述"
        show-overflow-tooltip
        prop="description"
        width="120">
    </el-table-column>
    <el-table-column
        label="点击"
        prop="views"
        width="120">
    </el-table-column>
    <el-table-column
        label="点赞"
        prop="likeNum"
        width="120">
    </el-table-column>
    <el-table-column
        label="评论"
        prop="comments"
        width="120">
    </el-table-column>
    <el-table-column
        label="发布时间"
        width="120">
      <template slot-scope="scope">
        {{getTime(articles[scope.$index].createTime)}}
      </template>
    </el-table-column>
    <el-table-column
        label="更新时间"
        width="120">
      <template slot-scope="scope">
        {{getTime(articles[scope.$index].updateTime)}}
      </template>
    </el-table-column>
    <el-table-column
        align="right">
      <template slot="header" slot-scope="scope">
        <el-input
            size="mini"
            style="width: 120px"
            placeholder="输入关键字搜索"/>
        <el-button size="mini">搜索</el-button>
      </template>
      <template slot-scope="scope">
        <el-button  type="info"  size="mini" @click="toDetail(scope.$index)">
          详情
        </el-button>
        <el-button
            size="mini"
            type="danger"
            v-if="articles[scope.$index].status===1"
            @click="articleDelete(scope.$index)"
        >删除</el-button>
        <el-button
            size="mini"
            type="primary"
            v-if="articles[scope.$index].status===0"
            @click="active(scope.$index)"
        >激活</el-button>

      </template>
    </el-table-column>
  </el-table>
  <div style="text-align: center">
    <el-pagination
        background
        @current-change="handleCurrentChange"
        :current-page.sync="page"
        :page-size="15"
        layout="prev, pager, next, jumper"
        :total="count">
    </el-pagination>
  </div>
  <el-dialog
      title="文章详情"
      :visible.sync="detailDialog"
      width="60%">
    <div v-if="!articleDetail.isEmpty">
      <div id="title" style="margin-bottom: 8px;text-align: center">
        <h1 style="font-size: 28px">{{ articleDetail.title }}</h1>
      </div>
      <div id="article-info" style="font-size: 10px;font-weight: lighter;height: 14px;margin-top: 6px;text-align: center">
        <span>{{ articleDetail.views }}阅读</span>
        <span>{{ articleDetail.comments }}评论</span>
        <span>{{ articleDetail.likeNum }}喜欢</span>
        <span>{{new Date(articleDetail.createTime).toLocaleString()}}</span>
      </div>
      <div id="content">
        <mavon-editor
            :value="articleDetail.content"
            :subfield="false"
            defaultOpen="preview"
            :toolbarsFlag="false"
            :editable="false"
            style="z-index: 5"
        ></mavon-editor>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      </span>
  </el-dialog>

</div>
</template>

<script>
import {getArticlePage,articleRemove,activeArticle,getDetail} from "@/admin/api/Article";
import {marked} from "marked"

export default {
  name: "ArticleManage",
  data(){
    return{
      articles:[],
      page:1,
      count:0,
      articleDetail:{},
      detailDialog:false,
    }
  },
  methods:{
    handleCurrentChange(){
      this.getPage()
    },
    getPage(){
      getArticlePage(this.page).then((res)=>{
        this.articles=res.data.articles
        this.count=res.data.count
      })
    },
    getHtml(content) {
        return marked(content)
    },
    getTime(time){
      return new Date(time).toLocaleDateString()
    },
  articleDelete(index){
    this.$prompt('请输入删除原因', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      this.remove(index,value)
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '取消操作'
      });
    });
  }
  ,
    remove(index,reason){
      let id=this.articles[index].id;
      articleRemove(id,reason).then((res)=>{
        if (res.code===2001){
          this.articles[index].status=0
          this.$message({
            message: '删除成功',
            type: 'success'
          });
        }
      })
    },
    active(index){
      let id=this.articles[index].id;
      activeArticle(id).then((res)=>{
        if (res.code===2001){
          this.articles[index].status=1
          this.$message({
            message: '激活成功',
            type: 'success'
          });
        }
      })
    },
    toDetail(index){
      let id=this.articles[index].id;
      getDetail(id).then((res)=>{
        this.articleDetail=res.data.article
        this.articleDetail.content=this.getHtml(this.articleDetail.content);
        this.detailDialog=true
      })
    }
  },
  created() {
    this.getPage()
  }
}
</script>

<style scoped>

</style>