<template>
  <el-container>
    <el-main>
      <el-empty v-if="articles[0].year===''" description="空空如也"></el-empty>
      <div v-for="(article,index) in articles">
        <el-link @click.native="getNode(index)">
          <div class="year">{{ article.year }}</div>
        </el-link>
        <el-timeline :reverse="true">
          <el-timeline-item
              v-for="(record,index2) in article.records" :timestamp="getTime(record.createTime)"
              placement="top">
            <el-card>
              <el-row style="width: 100%;height: 40px">
                <el-col :span="18" style="text-align: left">
                  <el-link style="font-size: 16px" @click="toDetail(record.id)"> {{ record.title }}</el-link>
                </el-col>
                <el-col :span="6" style="text-align: right;">
                  <el-button icon="el-icon-edit" type="primary" @click="toEdit(record.id)"></el-button>
                  <el-button icon="el-icon-delete" type="danger" @click="del(index,index2)"></el-button>
                </el-col>
              </el-row>

            </el-card>

          </el-timeline-item>
        </el-timeline>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import {deleteArticle, getUserArticleByYear, getUserTimeline} from "@/api/Article";
import {getMonthAndDay} from "@/util/tools";

export default {
  name: "UserArticle",
  props: {
    userId: String
  },
  data() {
    return {
      articles: [
        {
          year: '',
          records: []
        }
      ]
    }
  },
  methods: {
    getTime(date) {
      return getMonthAndDay(date);
    },
    getTimeLine() {
      getUserTimeline(this.userId).then((res) => {
        let year = res.data.year
        for (let i = 0; i < year.length; i++) {
          if (!this.articles[i]) {
            this.articles[i] = {}
          }
          this.articles[i].year = year[i]
        }
        if (this.articles) {
          this.getNode(0)
        }
        this.$forceUpdate()
      })
    },
    getNode(index) {
      if (this.articles[index].year) {
        getUserArticleByYear(this.userId, this.articles[index].year).then((res) => {
          this.articles[index].records = res.data.articles
          this.$forceUpdate()
        })
      }
    },
    del(index1, index2) {
      let id = this.articles[index1].records[index2].id
      deleteArticle(id).then((res) => {
        if (res.code === 2001) {
          this.articles[index1].records.splice(index2, 1)
          this.$forceUpdate()
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "success",
            duration: 1000
          })
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
    toDetail(id) {
      this.$router.push("/detail?id=" + id);
    },
    toEdit(id) {
      this.$router.push("/write?id=" + id);

    }
  },
  created() {
    this.getTimeLine(this.userId)
  }
}
</script>

<style scoped>
.year {
  font-size: 20px;
  margin-bottom: 10px;
}
</style>
