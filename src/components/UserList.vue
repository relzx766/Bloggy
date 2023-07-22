<template>
  <el-container>
    <el-main>
      <el-row v-for="index in Math.ceil(users.length/2)" style="height: 110px;">
        <el-col :span="11" style="margin-top: 10px">
          <el-card shadow="hover" @click.native="toProfile(users[index*2-2].id)">
            <el-row>
              <el-col :span="6">
                <el-avatar :src="users[index*2-2].avatar" class="user-avatar"
                           fit="cover"
                />
              </el-col>
              <el-col :span="18">
                <el-row>
                  <h2>{{ users[index * 2 - 2].nickname }}</h2>
                </el-row>
                <el-row>
                  <span style="font-weight: lighter;color: #8c939d">@{{ users[index * 2 - 2].username }}</span>
                </el-row>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col v-if="index*2-1< users.length" :span="11" style="margin-left: 20px; margin-top: 10px">
          <el-card shadow="hover" @click.native="toProfile(users[index*2-1].id)">
            <el-row>
              <el-col :span="6">
                <el-avatar :src="users[index*2-1].avatar" class="user-avatar"
                           fit="cover"
                />
              </el-col>
              <el-col :span="18">
                <el-row>
                  <h2>{{ users[index * 2 - 1].nickname }}</h2>
                </el-row>
                <el-row>
                  <span style="font-weight: lighter;color: #8c939d">@{{ users[index * 2 - 1].username }}</span>
                </el-row>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "UserList",
  props: {
    users: Array
  },
  methods: {
    toProfile(id) {
      this.$router.push("/profile?id=" + id)
    }
  }
}
</script>

<style scoped>
.user-avatar {
  width: 100px;
  height: 100px;
}

</style>