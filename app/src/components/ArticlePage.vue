<template>
  <div id="content">
    <ul id="list">
      <li class="li-article"  v-for="(item,index) in articlePages" :key="index"@click="toDetail(item.id)">
        <el-card class="article-card" >
          <h3 style="margin-top: -10px;color: white">{{item.title}}</h3>
          <div style="height: 36px;">
          <div style="float: left;height: 34px" v-if="item.userVo">
            <ul>
              <li>    <el-image
                  style="width: 30px; height: 30px;border-radius: 50%"
                  :src="'http://'+item.userVo.avatar"></el-image></li>
              <li style="font-size: 30px">·</li>
              <li>{{item.userVo.nickname}}</li>
              <li style="font-size: 30px">·</li>
              <li>{{item.updateTime}}</li>
            </ul>
          </div>
          <div style="float: right;height: 34px">
            <ul>
              <li><img style="width: 30px;height: 30px" src="../static/images/view.svg"></li>
              <li>{{item.views}}</li>
              <li style="margin-left: 6px"><img style="width: 30px;height: 30px" src="../static/images/comment.svg"></li>
              <li>{{item.comments}}</li>
            </ul>
          </div></div>
          <div style="text-align: left;height: 40px;color: white"><h4>{{item.description}}</h4></div>
          <div class="tag" style="float: left;margin-top: 36px;margin-left: -40px">
            <ul>
              <li v-for="(v,i) in item.tag">
                <el-tag style="margin-left: 4px">{{v}}</el-tag>
              </li>
            </ul>
          </div>
        </el-card>
      </li>
    </ul>
  </div>
</template>
<script>
export default {
name: "ArticlePage",
  props:{
  articlePages:{
    type:Array
  }
  },
  data(){
  return{
    cover:"cover"
  }
  },
  mounted() {
    console.log("articlePages")
    console.log(this.articlePages)
  },
  methods:{
    toDetail(id){
      this.$router.push("/detail?id="+id);
    }
  },
  watch: {
    articlePages: {
      handler(newValue, oldValue) {
        console.log('articlePages prop updated:', newValue);
      },
      deep: true
    }
  }
}
</script>

<style scoped>
ul{
  text-align: center;
  line-height: 34px;
}
li{
  list-style-type: none;
  clear: both;
  height: 25px;
  display: inline-block;
  vertical-align: top;
  color: white;
}
#content{
  width: 100%;
  text-align: center;
}
.li-article{
  width: 90%;
  margin: 0 auto;
  border-radius: 10px;
  height: 240px;
  margin-top: 20px;
}

.article-info span{
  height: 30px;
  vertical-align: top;
  float: top;
}
.article-card{
  border-radius: 10px;
  text-align: center;
  height: 240px;
  background-color:#24242c;
  border: none;
}
svg g polyline {
  stroke: white;
}
</style>
