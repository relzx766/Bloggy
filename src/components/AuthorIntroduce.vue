<template>
<el-main>
  <el-row style="height: 50px">
   <el-col :span="8" style="height: 100%">
     <el-avatar :src="author.avatar" style="width: 50px;height: 50px">
     </el-avatar>
   </el-col>
    <el-col :span="16">
      <el-row>{{author.nickname}}</el-row>
      <el-row style="font-size: 14px;color: #70747c">@{{author.username}}</el-row>
    </el-col>
  </el-row>
  <el-row style="margin-top: 14px">
    <el-col :span="12">
      <el-button  style="width: 80%" type="primary" size="mini" >关注</el-button>
    </el-col>
    <el-col :span="12">
      <el-button style="width: 80%" size="mini" type="primary" plain>私信</el-button>
    </el-col>
  </el-row>
</el-main>
</template>

<script>
export default {
  name: "AuthorIntroduce",
  props:{
    author:{}
  }
}
</script>

<style scoped>

</style>