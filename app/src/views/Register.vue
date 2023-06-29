<template>
  <div id="register" v-title data-title="注册 - Bloggy">
    <!--<video preload="auto" class="me-video-player" autoplay="autoplay" loop="loop">
          <source src="../../static/vedio/sea.mp4" type="video/mp4">
      </video>-->

    <div class="me-login-box me-login-box-radius">
      <img src="../static/images/logo.svg" style="height: 100px">
      <h1>Bloggy 注册</h1>

      <el-form ref="userForm" :model="userForm" :rules="rules">
        <el-form-item prop="account">
          <el-input placeholder="用户名" v-model="userForm.username"></el-input>
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input placeholder="密码" v-model="userForm.password"></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input placeholder="邮箱" v-model="userForm.email"></el-input>
        </el-form-item>

        <el-form-item size="small" class="me-login-button">
          <el-button type="primary" @click.native.prevent="register('userForm')">注册</el-button>
        </el-form-item>
        <el-dialog
            title="验证"
            :visible.sync="dialogVisible"
            :close-on-click-modal="false"
            :close-on-press-escape="true"
            :show-close="true"
            width="30%">
          <span>验证码已发至您的邮箱，请注意查收</span>
          <el-input v-model="code" placeholder="请输入验证码"></el-input>
          <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="verify">确 定</el-button>
  </span>
        </el-dialog>
      </el-form>

      <div class="me-login-design">
        <p>已有账号？
          <strong>
            <router-link to="/login" class="me-login-design-color">去登录</router-link>
          </strong>
        </p>
      </div>

    </div>
  </div>
</template>

<script>
import {register, verify} from "@/api/User";

export default {
  name: 'Register',
  data() {
    return {
      userForm: {
        username: '',
        email: '',
        password: ''
      },
      dialogVisible: false,
      code: '',
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {max: 30, message: '不能大于30个字符', trigger: 'blur'}
        ],
        email: [
          {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'},
          {required: true, message: '请输入邮箱', trigger: 'blur'},
        ],
        /* password: [
           {required: true, message: '请输入密码', trigger: 'blur'},
           {min: 6, message: '不能小于6个字符', trigger: 'blur'}
         ]*/
      }

    }
  },
  methods: {
    register(formName) {
      let that = this
      this.$refs[formName].validate((valid) => {
        if (valid) {
          that.dialogVisible = true
          register(that.userForm).then((res) => {
            console.log(res)
            console.log(res.code)
            if (res.code == 2001) {
              that.dialogVisible = true
            } else {
              that.$message({message: res.message, type: 'error', showClose: true})
            }
          })

        } else {
          return false;
        }
      });
    },
    verify() {
      let that = this
      that.dialogVisible = false;
      verify(that.userForm.email, that.code).then((res) => {
        let type = res.code == 2001 ? 'success' : 'error'
        that.$message({message: res.message, type: type, showClose: true});
        that.$router.push({path: '/login'})
      })
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
