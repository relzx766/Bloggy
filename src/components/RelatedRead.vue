<template>
  <el-container>
    <el-main>
      <el-row v-for="item in articles" style="text-align: left;overflow: hidden" class="relate">
        <el-link :href="'/detail?id='+item.id">
          <el-row style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
            {{ item.title }}
          </el-row>
        </el-link>
        <el-row style="margin-top: 4px">
          <el-breadcrumb separator="·" style="font-size: 12px">
            <el-breadcrumb-item class="text-item">{{ item.views }}阅读</el-breadcrumb-item>
            <el-breadcrumb-item class="text-item">{{ item.likeNum }}点赞</el-breadcrumb-item>
          </el-breadcrumb>
        </el-row>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import {getByTag} from "@/api/Article";

export default {
  name: "RelatedRead",
  props: {
    tags: Array,
    size: Number
  },
  data() {
    return {
      articles: []
    }
  },
  methods: {
    getRelate() {
      let data = new FormData();
      data.append("tags", this.tags);
      data.append("page", 1)
      getByTag(data).then(res => {
        this.articles = res.data.articles
        console.log(this.articles)
      })
    }
  },
  created() {
    this.getRelate();
  }
}
</script>

<style scoped>
.relate + .relate {
  margin-top: 8px;
}
</style>