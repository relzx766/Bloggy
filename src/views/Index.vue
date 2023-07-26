<template>
  <el-container>
    <el-header>

        <el-row style="display: flex;height: 60px;border-bottom:#DCDFE6 solid 1px;border-bottom-left-radius: 6px;border-bottom-right-radius: 6px">
          <div  style="width: 6%" class="menu-item">
            <el-image
                fit="cover"
                style="height: 60px"
                :src="require('@/static/images/logo.svg')">
            </el-image>
          </div>
          <div style="font-size: 26px;color: #449cfc;" class="menu-item">
            Bloggy
          </div>
          <div class="menu-left">
            <div ref="home"   class="menu-item" style=";float: left">
              <el-link  @click.native="updateIndexColor('home');to('/home')">主页</el-link>
            </div>
            <div ref="trend"  class="menu-item" style="float: right;margin-left: 10%">
              <el-link  @click.native="updateIndexColor('trend');to('/trend')">趋势</el-link>
            </div>
          </div>
          <div class="menu-right">
            <div ref="search" class="menu-item" style="width: 50%;margin-left: 20%">
              <el-input v-model="keyword" placeholder="输入以搜索" size="small">
                <el-button slot="append" icon="el-icon-search"></el-button>
              </el-input>
            </div>
            <div ref="write" class="menu-item" style="margin-left:6%">
             <el-link @click.native="updateIndexColor('write');to('/write')">
               <i class="el-icon-edit" style="font-size: 18px"/></el-link>
            </div>
            <div ref="message" class="menu-item" style="margin-left: 6%">
              <el-link>
                <i class="el-icon-message" style="font-size: 18px"/>
                <el-badge :is-dot="hasNewMessage" style="top: -10px;left: -4px"/>
              </el-link>
            </div>
            <div class="menu-item" style="float: right;margin-left: 6%" v-if="!user.id">
              <el-link href="/login">
                登录
              </el-link>
            </div>
            <div ref="profile" class="menu-item" style="margin-left: 6%;display: flex;
    align-items: center;" v-else>
              <el-popover
                  placement="top-start"
                  style="border: none"
                  width="100"
                  popper-class="profile-popper"
                  trigger="hover">
                <template slot="reference">
                    <el-avatar fit="cover" :src="user.avatar" style="width: 40px;height: 40px;float: right;">
                    </el-avatar>
                </template>
                <template>

                    <el-row >
                      <el-link @click.native="updateIndexColor('profile');to('/profile?id='+user.id)">
                        <i class="el-icon-user-solid"/>
                        个人中心
                      </el-link>
                    </el-row>
                    <el-row >
                      <el-link @click.native="signOut">
                        <i class="el-icon-switch-button"/>
                        退出
                      </el-link>
                    </el-row>


                </template>
              </el-popover>

            </div>
          </div>


        </el-row>



    </el-header>

    <el-main>


        <el-row>
          <router-view @updateIndex="updateIndexColor"></router-view>
        </el-row>

      </el-main>
  </el-container>
</template>

<script>
import Navigation from "@/components/Navigation.vue";

export default {
  name: "Index",
  components: {
    'navigation':Navigation},
  data() {
    return {
      user: {
        id:'',
        username: '',
        nickname: '',
        avatar: ''
      },
      activeIndex:'home',
      keyword:'',
      hasNewMessage:true
    }
  },
  methods: {
    signOut() {
      localStorage.removeItem("satoken")
      this.$cookie.delete("id")
      this.$cookie.delete("username")
      this.$cookie.delete("nickname")
      this.$cookie.delete("avatar")
      this.$router.push("/login")
    },

    search() {
      if (this.keyword.length > 0) {
        this.$router.push("/search?keyword=" + encodeURIComponent(this.keyword))
        this.$forceUpdate()
      }
    },
    to(url){
      this.$router.push(url);
    },
    updateIndexColor(ref) {
      let box=this.$refs[ref];
      box.style.borderBottom='2px solid #409EFF';

      for (let r of Object.keys(this.$refs)) {
        console.log(r)
        // 如果r不等于当前的ref
        if (r !== ref) {
          // 获取r对应的元素的引用
          let otherElement = this.$refs[r]
          // 恢复r对应的元素的下边框样式为默认值
          otherElement.style.borderBottom = 'none'
        }
      }
    }
  },
  created() {
    this.$router.push("/home")
    let that = this;
    that.user.id=that.$cookie.get("id");
    that.user.username = that.$cookie.get("username");
    that.user.nickname = that.$cookie.get("nickname");
    that.user.avatar = that.$cookie.get("avatar");
  }
}
</script>

<style>
li {
  list-style-type: none;
  clear: both;
  height: 25px;
}
.menu-right{
  width: 50%;
  float: right;
  display: flex;
}
.menu-item{
  line-height: 60px;
  text-align: center;
}
.menu-left{
  width: 30%;
  margin-left: 8%;
  display: flex;
}
.el-popover.profile-popper{
  min-width: 40px;

}
</style>
