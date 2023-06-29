<template>
  <div>
    <el-table
        ref="multipleTable"
        :data="users"
        tooltip-effect="dark"
        style="width: 100%"
        >
      <el-table-column
          label="id"
          prop="id"
          width="120">
      </el-table-column>
      <el-table-column
          prop="username"
          label="用户名"
          width="120">
      </el-table-column>
      <el-table-column
          prop="nickname"
          label="昵称"
          width="120"
          show-overflow-tooltip>
      </el-table-column>
      <el-table-column label="头像" width="120">
        <template slot-scope="scope">
          <el-image
              fit="cover"
              style="width: 50px; height: 50px;border-radius: 50%"
              :src="users[scope.$index].avatar"
              :preview-src-list="[users[scope.$index].avatar]">
          </el-image>
        </template>
      </el-table-column>
      <el-table-column
          label="邮箱"
          prop="email"
          width="120"
          show-overflow-tooltip
      ></el-table-column>
      <el-table-column
          label="角色"
          width="120"
      >
        <template slot-scope="scope">
          <el-tag v-if="users[scope.$index].role===1">普通用户</el-tag>
          <el-tag v-if="users[scope.$index].role===0">管理员</el-tag>

        </template>
      </el-table-column>
      <el-table-column
          label="注册时间"
          width="120"
      >
        <template slot-scope="scope">
          {{getTime(users[scope.$index].registrationTime)}}
        </template>
      </el-table-column>
      <el-table-column
          label="最后登录时间"
          width="120"
      >
        <template slot-scope="scope">
          {{getTime(users[scope.$index].lastLoginTime)}}
        </template>
      </el-table-column>
      <el-table-column
          label="状态"
          width="120"
      >
        <template slot-scope="scope">
          <el-tag type="success" v-if="users[scope.$index].status===1">激活</el-tag>
          <el-tag type="info" v-if="users[scope.$index].status===0">注销</el-tag>

        </template>
      </el-table-column>
      <el-table-column
          align="right">
        <template slot="header" slot-scope="scope">
          <el-input
              size="mini"
              style="width: 150px"
              placeholder="输入关键字搜索"/>
          <el-button size="mini">搜索</el-button>
        </template>
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="danger"
              @click="closeAccount(scope.$index)"
              v-if="users[scope.$index].status===1&&users[scope.$index].role!==0"
          >注销</el-button>
          <el-button
              size="mini"
              type="primary"
              @click="activeAccount(scope.$index)"
              v-if="users[scope.$index].status===0&&users[scope.$index].role!==0"
          >激活</el-button>
        </template>
      </el-table-column>
    </el-table>
<div style="text-align: center;margin-top: 10px">
  <el-pagination
      background
      @current-change="handleCurrentChange"
      :current-page.sync="page"
      :page-size="15"
      layout="prev, pager, next, jumper"
      :total="count">
  </el-pagination>
</div>
  </div>
</template>

<script>
import {getUserList,closeAccount,activeAccount} from "@/admin/api/User";
export default {
  name: "UserManage",
  data(){
    return{
      users:[],
      multipleSelection: [],
      page:1,
      count:0
    }
  },
  methods:{
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleCurrentChange(){
      this.getUserPage();
    }
    ,
    getUserPage(){
      getUserList(this.page).then((res)=>{
        this.users=res.data.page.records
        this.count=res.data.page.total
        console.log("dasasdsd")
        console.log(this.users)
      })
    },
    getTime(time){
      return new Date(time).toLocaleDateString()
    },
    closeAccount(index){
      let id=this.users[index].id
      closeAccount(id).then((res)=>{
        if (res.code===2001){
          this.users[index].status=0;
          this.$message({
            message: '注销用户'+id+'成功',
            type: 'success'
          });
        }
      })
    },
    activeAccount(index){
      let id=this.users[index].id
      activeAccount(id).then((res)=>{
        if (res.code===2001){
          this.users[index].status=1;
          this.$message({
            message: '激活用户'+id+'成功',
            type: 'success'
          });
        }
      })
    }
  },
  created() {
    this.getUserPage();
  }
}
</script>

<style scoped>

</style>