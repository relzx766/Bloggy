<template>
  <el-container>
    <el-main>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <el-row style="align-items: flex-end;">
            <el-col :span="5">
              <el-image :src="sort.cover" fit="cover"
                        style="width: 150px;height: 150px; border: none; box-shadow: none;"/>
            </el-col>
            <el-col :span="10" style="text-align: left;margin-top: 50px">
              <el-row><h1>{{ sort.title }}</h1></el-row>
              <el-row style="font-weight: lighter;color: #8c939d">
                <span>创建于&nbsp;</span>{{ new Date(sort.createTime).toLocaleDateString() }}
              </el-row>
            </el-col>
            <el-col v-if="userId==sort.owner" :span="8" style="margin-top: 124px;text-align: right">
              <el-link @click.native="()=>{dialogVisible=true;choose=sort;cover[0]={};cover[0].url=sort.cover}">
                <i class="el-icon-edit-outline"/>
                编辑
              </el-link>
              <el-link style="margin-left: 20px" @click.native="deleteSort(index)">
                <i class="el-icon-delete-solid"/>
                删除
              </el-link>
            </el-col>
          </el-row>
        </div>


        <div>
          <article-list :articles="articles"/>
        </div>
      </el-card>
    </el-main>
    <el-dialog
        :visible.sync="dialogVisible"
        title="编辑"
        width="40%"
    >
      <div>
        <el-container>
          <el-main>
            <el-row style="text-align:center;margin-bottom: 100px">
              <el-upload
                  :auto-upload="false"
                  :file-list="cover"
                  :on-change="handleChange"
                  :show-file-list="false"
                  action="#"
                  class="avatar-uploader" name="avatar">
                <el-image
                    v-if="cover.length>0"
                    :src="cover[0].url"
                    style="width: 200px;height: 150px"
                ></el-image>
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-row>

            <el-row>
              <el-input v-model="choose.title" maxlength="15"></el-input>

            </el-row>
          </el-main>
        </el-container>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="updateSort">确 定</el-button>
  </span>
    </el-dialog>
  </el-container>
</template>

<script>
import Navigation from "@/components/Navigation.vue";
import {deleteSort, getDetail, updateSort} from "@/api/Sort";
import ArticleList from "@/components/ArticleList.vue";

export default {
  name: "SortDetail",
  components: {
    'navigation': Navigation,
    'article-list': ArticleList
  },
  data() {
    return {
      id: '',
      sort: {},
      articles: [],
      userId: '',
      dialogVisible: false,
      choose: {},
      cover: [],
      addDialog: false,
      sortForAdd: {}
    }
  },
  methods: {
    getDetail() {
      getDetail(this.id).then((res) => {
        this.sort = res.data.sort;
        this.articles = res.data.articles
      })
    },
    deleteSort(index) {
      deleteSort(this.sorts[index].id).then((res) => {
        if (res.code === 2001) {
          this.$router.push("/index")
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
    updateSort() {
      updateSort(this.choose.id, this.choose.title, this.cover[0].raw).then((res) => {
        if (res.code === 2001) {
          this.dialogVisible = false;
          this.choose = {}
          this.cover = []
          this.getDetail()
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
    handleChange(file, fileList) {
      this.cover[0] = file
      this.cover[0].url = URL.createObjectURL(file.raw)
      this.$forceUpdate()
    },
  },
  created() {
    this.id = this.$route.query.id;
    this.userId = this.$cookie.get("id");
    this.getDetail()
  }
}
</script>

<style scoped>
.box-card {
  margin-left: auto;
  margin-right: auto;
  margin-top: 1%;
}
</style>