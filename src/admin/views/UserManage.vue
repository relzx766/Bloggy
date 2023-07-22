<template>
  <el-container>
    <el-main>
      <el-table
          ref="multipleTable"
          :data="users"
          style="width: 100%"
          tooltip-effect="dark"
      >
        <el-table-column
            label="id"
            prop="id"
            width="120">
        </el-table-column>
        <el-table-column
            label="用户名"
            prop="username"
            width="120">
        </el-table-column>
        <el-table-column
            label="昵称"
            prop="nickname"
            show-overflow-tooltip
            width="120">
        </el-table-column>
        <el-table-column label="头像" width="120">
          <template slot-scope="scope">
            <el-image
                :preview-src-list="[users[scope.$index].avatar]"
                :src="users[scope.$index].avatar"
                fit="cover"
                style="width: 50px; height: 50px;border-radius: 50%">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column
            label="邮箱"
            prop="email"
            show-overflow-tooltip
            width="120"
        ></el-table-column>
        <el-table-column
            label="角色"
            width="120"
        >
          <template slot-scope="scope">
            <el-tag v-if="users[scope.$index].role===1">普通用户</el-tag>
            <el-tag v-if="users[scope.$index].role===0" type="danger">管理员</el-tag>

          </template>
        </el-table-column>
        <el-table-column
            label="注册时间"
            width="120"
        >
          <template slot-scope="scope">
            {{ getTime(users[scope.$index].registrationTime) }}
          </template>
        </el-table-column>
        <el-table-column
            label="最后登录时间"
            width="120"
        >
          <template slot-scope="scope">
            {{ getTime(users[scope.$index].lastLoginTime) }}
          </template>
        </el-table-column>
        <el-table-column
            label="状态"
            width="120"
        >
          <template slot-scope="scope">
            <el-tag v-if="users[scope.$index].status===1" type="success">激活</el-tag>
            <el-tag v-if="users[scope.$index].status===0" type="info">注销</el-tag>

          </template>
        </el-table-column>
        <el-table-column
            align="right">
          <template slot="header" slot-scope="scope">
            <el-input
                placeholder="输入关键字搜索"
                size="mini"
                style="width: 150px"/>
            <el-button size="mini">搜索</el-button>
          </template>
          <template slot-scope="scope">
            <el-button
                v-if="users[scope.$index].status===1&&users[scope.$index].role!==0"
                size="mini"
                type="danger"
                @click="closeAccount(scope.$index)"
            >注销
            </el-button>
            <el-button
                v-if="users[scope.$index].status===0&&users[scope.$index].role!==0"
                size="mini"
                type="primary"
                @click="activeAccount(scope.$index)"
            >激活
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: center;margin-top: 10px">
        <el-pagination
            :current-page.sync="page"
            :page-size="15"
            :total="count"
            background
            layout="prev, pager, next, jumper"
            @current-change="handleCurrentChange">
        </el-pagination>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import {activeAccount, closeAccount, getUserList} from "@/admin/api/User";

export default {
  name: "UserManage",
  data() {
    return {
      users: [],
      multipleSelection: [],
      page: 1,
      count: 0
    }
  },
  methods: {
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleCurrentChange() {
      this.getUserPage();
    }
    ,
    getUserPage() {
      getUserList(this.page).then((res) => {
        this.users = res.data.page.records
        this.count = res.data.page.total
      })
    },
    getTime(time) {
      return new Date(time).toLocaleDateString()
    },
    closeAccount(index) {
      let id = this.users[index].id
      closeAccount(id).then((res) => {
        if (res.code === 2001) {
          this.users[index].status = 0;
          this.$message({
            message: '注销用户' + id + '成功',
            type: 'success'
          });
        }
      })
    },
    activeAccount(index) {
      let id = this.users[index].id
      activeAccount(id).then((res) => {
        if (res.code === 2001) {
          this.users[index].status = 1;
          this.$message({
            message: '激活用户' + id + '成功',
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
.el-main::-webkit-scrollbar {
  width: 0 !important
}
</style>