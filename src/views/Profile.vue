<template>
  <el-container>
    <el-main style="width: 80%;margin: 0 auto">
      <el-card style="height: 150px">
        <el-row style="height: 120px">
          <el-col :span="3" style="text-align: left">
            <el-avatar
                :fit="'cover'"
                :size="100"
                :src="user.avatar"
                shape="square"
            >
            </el-avatar>
          </el-col>
          <el-col :span="10" style="text-align: left;height: 100px">
            <el-row>
              <span style="font-size: 20px;font-weight: bolder;line-height: 60px"> {{ user.nickname }}</span>
            </el-row>
            <el-row>
              <span style="color: darkgrey">{{ '@' + user.username }}</span>
            </el-row>
          </el-col>
          <el-col :span="11" style="text-align: right;line-height: 150px">
            <el-button v-if="currentUserId===id" icon="el-icon-user" type="primary" @click="editProfileDialog=true">
              编辑个人资料
            </el-button>
          </el-col>
        </el-row>
      </el-card>
      <el-row>
        <el-col :span="18">
          <el-tabs v-model="activeName">
            <el-tab-pane label="动态" name="first">
              <active/>
            </el-tab-pane>
            <el-tab-pane label="文章" name="second">
              <article-view :user-id="id"/>
            </el-tab-pane>
            <el-tab-pane label="收藏" name="third">
              <sort :id="id"/>
            </el-tab-pane>
          </el-tabs>
        </el-col>
        <el-col :span="6">

        </el-col>
      </el-row>
      <!-- 修改个人资料弹窗 -->
      <el-dialog
          :close-on-click-modal="false"
          :visible.sync="editProfileDialog"
          title="个人资料"
          width="30%">
        <el-form ref="form" :model="editProfile" label-width="80px">
          <el-row style="text-align:center">
            <el-upload
                :auto-upload="false"
                :before-upload="beforeAvatarUpload"
                :data="{nickname:editProfile.nickname}"
                :file-list="fileList"
                :http-request="submitProfile"
                :on-change="handleChange"
                :on-success="handleAvatarSuccess"
                :show-file-list="false"
                action="#" class="avatar-uploader" name="avatar"><img
                v-if="fileList.length> 0" :src="fileList[0].url" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-row>
          <el-form-item label="用户名">
            <el-input v-model="user.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="editProfile.nickname" :placeholder="user.nickname"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
    <el-button @click="editProfileDialog = false">取 消</el-button>
    <el-button type="primary" @click="submitProfile">确 定</el-button>
  </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import Navigation from "@/components/Navigation";
import {getUserProfile, postProfile} from "@/api/User";
import UserActive from "@/components/UserActive";
import UserArticle from "@/components/UserArticle";
import UserSort from "@/components/UserSort.vue";

export default {
  name: "Profile",
  data() {
    return {
      id: '',
      activeName: 'first',
      user: {},
      editProfile: {},
      fileList: [],
      editProfileDialog: false,
      currentUserId: ''
    }
  },
  methods: {
    getUserInfo() {
      let user = {}
      if (this.id === this.currentUserId) {
        user.avatar = this.$cookie.get("avatar");
        user.username = this.$cookie.get("username");
        user.nickname = this.$cookie.get("nickname");
        this.$set(this, 'user', user);
      } else {
        getUserProfile(this.id).then((res) => {
          user = res.data.user;
          this.$set(this, 'user', user);
        })
      }
    },
    submitProfile(fileObject) {
      let data = new FormData();
      console.log(this.fileList[0])
      data.append("avatar", this.fileList[0].raw);
      data.append("nickname", this.editProfile.nickname ? this.editProfile.nickname : this.user.nickname)
      console.log(data.get("avatar"))
      postProfile(data).then((res) => {
        this.user.nickname = res.data.user.nickname;
        this.user.avatar = res.data.user.avatar
        this.editProfileDialog = false
        this.$cookie.set("nickname", this.user.nickname, 24 * 60 * 60 * 30)
        this.$cookie.set("avatar", this.user.avatar, 24 * 60 * 60 * 30)
      })
    },
    handleChange(file, fileList) {
      console.log(typeof fileList[0])
      this.fileList = fileList;
      if (this.fileList.length > 1) {
        this.fileList.splice(0, this.fileList.length - 1);
      }
      this.fileList[0].url = URL.createObjectURL(file.raw)
    }
    ,
    handleAvatarSuccess(res, file) {

    }
    ,
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  }
  ,
  components: {
    'navigation': Navigation,
    'active': UserActive,
    'article-view': UserArticle,
    'sort': UserSort
  },
  created() {
    this.currentUserId = this.$cookie.get("id")
    this.id = this.$route.query.id
    this.getUserInfo()
  }
}
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
