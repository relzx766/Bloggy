<template>
  <div>
    <div v-for="(item,index) in articles" @click="toDetail(item.id)">
      <el-row style="height: 40px;">
        <el-col :span="2" style="text-align: left">
          <el-avatar :src="item.userVo.avatar">
          </el-avatar>
        </el-col>
        <el-col :span="16" style="float: left;margin-left: -3%">
          <el-breadcrumb separator="|">
            <el-breadcrumb-item class="text-item">{{ item.userVo.nickname }}</el-breadcrumb-item>
            <el-breadcrumb-item class="text-item">{{ getData(item.createTime) }}</el-breadcrumb-item>
            <el-breadcrumb-item v-for="i in item.tags.length>3?3:item.tags.length"
                                class="text-item">
              {{ item.tags[i - 1] }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </el-col>
      </el-row>
      <el-row style="width: 100%;text-align: left;display: flex">
        <h3>{{ item.title }}</h3>
      </el-row>
      <el-row style="text-align: left">
        {{ item.description }}
      </el-row>
      <el-row style="height: 40px">
        <el-breadcrumb separator="|">
          <el-breadcrumb-item class="text-item">
            <i class="el-icon-view"/>
            {{ item.views }}
          </el-breadcrumb-item>
          <el-breadcrumb-item class="text-item">
            <i class="el-icon-chat-dot-square"/>
            {{ item.comments }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </el-row>
      <el-divider v-if="index<articles.length-1"></el-divider>
    </div>
  </div>

</template>

<script>
import {getDate} from "@/util/tools";

export default {
  name: "ArticleList",
  props: {
    articles: Array
  },
  methods: {
    toDetail(id) {
      this.$router.push("/detail?id=" + id);
    },
    getData(date) {
      return getDate(date)
    }
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
