<template>

  <div id="login" v-title data-title="登录 - Bloggy">
    <!--<video preload="auto" class="me-video-player" autoplay="autoplay" loop="loop">
          <source src="../../static/vedio/sea.mp4" type="video/mp4">
      </video>-->

    <div class="me-login-box me-login-box-radius">
      <img src="../static/images/logo.svg" style="height: 100px">
      <h1>Bloggy 登录</h1>
      <el-form ref="userForm" :model="userForm" :rules="rules">

        <el-form-item prop="account">
          <el-input v-model="userForm.account" placeholder="用户名或邮箱"></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="userForm.password" placeholder="密码" type="password"></el-input>
        </el-form-item>

        <el-form-item class="me-login-button" size="small">
          <el-button type="primary" @click.native.prevent="login('userForm')">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="me-login-design">
        <p>还没有账号？
          <strong>
            <router-link class="me-login-design-color" to="/register">去注册</router-link>
          </strong>
        </p>
      </div>

    </div>
  </div>
</template>

<script>
import {login} from "@/api/User";

export default {
  name: 'Login',
  data() {
    return {
      userForm: {
        account: '',
        password: ''
      },
      rules: {
        /*  password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {min: 6, message: '不能小于6个字符', trigger: 'blur'}
          ]*/
      }
    }
  },
  methods: {
    login(formName) {
      let that = this;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          login(that.userForm).then((res) => {
            if (res.code === 2001) {
              let role = res.data.user.role
              localStorage.setItem(res.data.token.tokenName, res.data.token.tokenValue)
              that.$cookie.set("id", res.data.user.id, 24 * 60 * 60 * 30)
              that.$cookie.set("username", res.data.user.username, 24 * 60 * 60 * 30)
              that.$cookie.set("nickname", res.data.user.nickname, 24 * 60 * 60 * 30)
              that.$cookie.set("avatar", res.data.user.avatar, 24 * 60 * 60 * 30)
              that.$cookie.set("role", res.data.user.role, 24 * 60 * 60 * 30)
              that.$notify({
                title: "Bloggy",
                message: res.message,
                type: "success",
                duration: 1000
              })
              if (role === "member") {
                that.$router.push("/index")
              } else if (role === "admin") {
                that.$router.push("/admin")
              } else {
                that.$notify({
                  title: "Bloggy",
                  message: "error",
                  type: "error",
                  duration: 1000
                })
              }
            } else {
              that.$notify({
                title: "Bloggy",
                message: res.message,
                type: "error",
                duration: 1000
              })
            }
          })
        } else {
          return false;
        }
      });
    }
  }
}
</script>

<style scoped>
#login {
  min-width: 100%;
  min-height: 100%;
}

.me-video-player {
  background-color: transparent;
  width: 100%;
  height: 100%;
  object-fit: fill;
  display: block;
  position: absolute;
  left: 0;
  z-index: 0;
  top: 0;
}

.me-login-box {
  position: absolute;
  width: 300px;
  height: auto;
  background-color: white;
  margin-top: 150px;
  margin-left: -180px;
  left: 50%;
  padding: 30px;
  text-align: center;
}

.me-login-box-radius {
  border-radius: 10px;
  box-shadow: 0px 0px 1px 1px rgba(161, 159, 159, 0.1);
}

.me-login-box h1 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
  vertical-align: middle;
}

.me-login-design {
  text-align: center;
  font-family: 'Open Sans', sans-serif;
  font-size: 18px;
}

.me-login-design-color {
  color: #449cfc !important;
}

.me-login-button {
  text-align: center;
}

.me-login-button button {
  width: 100%;
}

</style>
