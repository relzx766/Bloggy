<template>
  <el-container>
    <el-header id="header" style="margin-bottom: 40px;height: auto">
      <div>
        <el-row style="width: 100%;">
          <el-col :span="2" style="text-align: left">
            <el-image
                :fit="'cover'"
                :src="avatar"
                style="width: 66px; height: 66px;border-radius: 50%"></el-image>
          </el-col>
          <el-col :span="16">
            <el-input
                v-model="comment.content"
                :autosize="{ minRows: 2}"
                :placeholder="comment.tempContent"
                style="width: 100%;float: left;margin-top: 6px"
                type="textarea">
            </el-input>
          </el-col>
          <el-col :span="6" style="text-align: center">
            <el-button round style="height: 50px;margin-top: 6px" type="primary" @click="postComment">发布</el-button>
          </el-col>
        </el-row>
      </div>
      <el-divider/>
    </el-header>
    <el-main>
      <div v-for="(item,index) in comments" class="comments">
        <el-row>
          <el-col :span="2">
            <el-image :fit="'cover'" :src=item.avatar style="border-radius: 50%;width: 60px;height: 60px;float: left">
              <div slot="placeholder" class="image-slot">
                加载中<span class="dot">...</span>
              </div>
            </el-image>
          </el-col>
          <el-col :span="6" style="float: left;margin-left: -10px">
            <div style="margin-top: 10px;font-weight: bold;text-align: left">{{ item.nickname }}
              <el-tag v-if="item.userId===userId" size="mini">自己</el-tag>
              <el-tag v-else-if="item.userId===author" size="mini" type="danger">作者</el-tag>
            </div>
            <div style="font-weight: lighter;font-size: 14px;text-align: left">@{{ item.username }}</div>
          </el-col>
        </el-row>
        <div style="margin-left: 30px;margin-top: 6px;">
          <el-row>
            <div @click="changeStatus(1,'回复@'+item.username,index)">
              <unfold :data="item.content"/>
            </div>
          </el-row>
          <el-row style="font-size: 14px;text-align: left;margin-top: 6px;font-weight: lighter">
            <el-col :span="4">{{ getDate(item.createTime) }}</el-col>
            <el-col :span="6">
              <el-row>
                <el-col :class="{'el-icon-like':!item.isLike,'el-icon-liked':item.isLike}"
                        style="width: 14px;height: 14px"
                        @click.native="likeComment(index)"></el-col>

                <span>{{ item.likeNum }}</span></el-row>
            </el-col>
          </el-row>
          <el-row>
            <div v-for="(i,k) in getReplyByPage(replyPage,5,item.reply)"  class="reply">
              <div style="width: 90%;">
                <el-divider></el-divider>
              </div>
              <el-row>
                <el-col :span="1">
                  <el-image :fit="'cover'" :src=i.avatar
                            style="border-radius: 50%;width: 40px;height: 40px;float: left">
                    <div slot="placeholder" class="image-slot">
                      加载中<span class="dot">...</span>
                    </div>
                  </el-image>
                </el-col>
                <el-col :span="4" style="float: left;margin-left: 10px">
                  <div style="margin-top: 4px;font-weight: bold;text-align: left;font-size: 14px">{{ i.nickname }}
                    <el-tag v-if="i.userId===userId" size="mini">自己</el-tag>
                    <el-tag v-else-if="i.userId===author" size="mini" type="danger">作者</el-tag>
                  </div>
                  <div style="font-weight: lighter;font-size: 12px;text-align: left">@{{ i.username }}</div>
                </el-col>
                <el-col :span="15">
                  <el-row>
                    <div @click="changeStatus(2,'@'+i.username+':',index)">
                      <unfold :data="i.content" :type="i.type"/>
                    </div>
                  </el-row>
                  <el-row style="font-size: 14px;text-align: left;margin-top: 6px;font-weight: lighter">
                    <el-col :span="6">{{ getDate(i.createTime) }}</el-col>
                    <el-col :span="6">
                      <el-row>
                        <el-col :class="{'el-icon-like':!i.isLike,'el-icon-liked':i.isLike}"
                                style="width: 14px;height: 14px"
                                @click.native="likeReply(index,k)"></el-col>
                        <span>{{ i.likeNum }}</span></el-row>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
            </div>
            <el-row>
              <el-col :span="24" style="text-align: center">
                <el-pagination
                    :hide-on-single-page="true"
                    :page-size="5"
                    :total="item.reply.length"
                    layout="prev, pager, next"
                    small
                    @current-change="handleCurrentChange">
                </el-pagination>


              </el-col>
            </el-row>
          </el-row>
        </div>
        <el-divider></el-divider>
      </div>
      <div v-if='isEnd'>已经到底了</div>
      <div v-if="isLoading">
        <i class="el-icon-loading"/>
      </div>
    </el-main>
    <el-footer v-if="commentArea" style="position: fixed;bottom: 0px;width: 74%;opacity: 1;margin: 0 auto;background-color:#ffffff;
height: 70px">
      <div>
        <el-row>
          <el-col :span="2" style="text-align: left">
            <el-image
                :fit="'cover'"
                :src="avatar"
                style="width: 66px; height: 66px;border-radius: 50%"></el-image>
          </el-col>
          <el-col :span="17">
            <el-input
                v-model="comment.content"
                :autosize="{ minRows: 2}"
                :placeholder="comment.tempContent"
                style="width: 700px;float: left;margin-top: 6px"
                type="textarea">
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-button round style="height: 50px;margin-top: 6px" type="primary" @click="postComment">发布</el-button>
          </el-col>
        </el-row>
      </div>
    </el-footer>
  </el-container>
</template>

<script>
import {getDate} from "@/util/tools";
import Unfold from "@/components/Unfold";
import {
  cancelLikeComment,
  cancelLikeReply,
  getComment,
  getReply,
  likeComment,
  likeReply,
  postComment,
  postReply
} from "@/api/Comment";

export default {
  name: "Comment",
  components: {Unfold},
  props: {
    id: Number,
    author: Number,
    type: Number
  },
  data() {
    return {
      userId: '',
      comments: [],
      avatar: '',
      comment: {
        id: Number,
        content: '',
        type: 0,
        tempContent: '给作者一个回复吧！'
      },
      index: Number,
      page: 1,
      isEnd: false,
      total: 0,
      isLoading: false,
      commentArea: true,
      replyPage: 1
    }
  },
  methods: {
    getDate(date) {
      return getDate(date);
    },
    async getComment() {
      try {
        let res = await getComment(this.id, this.page);
        this.total = res.data.comment.total;
        let comments = res.data.comment.records;
        for (let i = 0; i < comments.length; i++) {
          //定义一个延时函数，返回一个Promise对象
          let delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
          await delay(20);
          let reply = (await getReply(comments[i].commentId)).data.reply;
          comments[i].reply = reply
        }
        this.comments.push(...comments);
        this.isLoading = false
        console.log(this.comments.length)
      } catch (err) {
        console.error(err);
      }
    },
    loadComment() {
      const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
      const clientHeight = document.documentElement.clientHeight
      const scrollHeight = document.documentElement.scrollHeight
      if (scrollTop + clientHeight >= scrollHeight) {
        if (this.comments.length >= this.total) {
          console.log(this.comments.length)
          this.isLoading = true
          setTimeout(() => {
            this.isLoading = false;
            this.isEnd = true
          }, 2000);
        } else {
          this.isLoading = true
          this.page++
          this.getComment()
        }
      }
    }
    ,
    async updateReply(index) {
      let reply = await getReply(this.comments[index].commentId);
      this.comments[index].reply = reply.data.reply;
    }

    ,
    postComment() {
      let http;
      if (this.comment.type === 0) {
        http = postComment(this.id, this.comment.content, this.comment.type);
      } else {
        //如果type为2即为三级评论，此时将tempContent插入content头部
        if (this.comment.type === 2) {
          this.comment.content = this.comment.tempContent + this.comment.content
        }
        http = postReply(this.comment.id, this.comment.content, this.comment.type);
      }
      http.then((res) => {
        if (res.code == 2001) {
          if (this.comment.type === 0) {
            let c = res.data.comment;
            //必须要初始化reply,不如会报各种错误
            c.reply = []
            c.username = this.$cookie.get("username");
            c.nickname = this.$cookie.get("nickname");
            c.avatar = this.$cookie.get("avatar");
            this.comments.unshift(c)
          } else {
            this.updateReply(this.index)
          }
          this.comment.type = 0
          this.comment.content = ''
          this.comment.id = Number
          this.comment.tempContent = '给作者一个回复吧！'
          console.log(this.comment)
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "success",
            duration: 2000
          })
        } else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration: 1000
          })
        }
      })
    },
    changeStatus(type, temp, index) {
      this.comment.id = this.comments[index].commentId
      this.comment.type = type;
      this.comment.tempContent = temp;
      this.index = index
      console.log(this.comment, index)
    },
    handleScroll() {
      // 获取目标元素
      let target = document.getElementById('header');
      let rect = target.getBoundingClientRect();
      let windowHeight = window.innerHeight || document.documentElement.clientHeight;
      if (rect.top >= 0 && rect.top <= windowHeight) {
        this.commentArea = false
      } else {
        if ((rect.top - windowHeight) >= 0) {
          this.commentArea = false
        } else {
          this.commentArea = true
        }
      }
    },
    handleCurrentChange(val) {
      this.replyPage = val; // 更新当前回复页码
    },
    getReplyByPage(page, size, reply) {
      console.log(reply)
      let start = (page - 1) * size;
      let end = page * size;
      return reply.slice(start, end)
    },
    likeComment(index) {
      console.log("评论id")
      console.log(this.comments[index].commentId)
      if (this.comments[index].isLike) {
        this.comments[index].isLike = false
        this.comments[index].likeNum--
        cancelLikeComment(this.comments[index].commentId)
      } else {
        this.comments[index].isLike = true
        this.comments[index].likeNum++
        likeComment(this.comments[index].commentId)
      }
    }
    ,
    likeReply(i, j) {
      if (this.comments[i].reply[j].isLike) {
        this.comments[i].reply[j].isLike = false;
        this.comments[i].reply[j].likeNum--
        cancelLikeReply(this.comments[i].reply[j].replyId)
      } else {
        this.comments[i].reply[j].isLike = true
        this.comments[i].reply[j].likeNum++
        likeReply(this.comments[i].reply[j].replyId)
      }
    }
  },
  created() {
    this.avatar = this.$cookie.get("avatar")
    this.userId = this.$cookie.get("id")
    this.getComment();
  },
  mounted() {
    // 事件监听
    window.addEventListener('scroll', this.loadComment);
    window.addEventListener('scroll', this.handleScroll);
  },
  destroyed() {
    // 离开页面取消监听
    window.removeEventListener('scroll', this.loadComment, false);
    window.removeEventListener('scroll', this.handleScroll, false);
  }
}
</script>

<style scoped>
li {
  list-style-type: none;
}

.comments + .comments {
  margin-top: 8px;
}

::v-deep .el-icon-liked {
  background: url('../static/images/like_fill.svg') center no-repeat;
  font-size: 20px;
  background-size: cover;
}

::v-deep .el-icon-liked:before {
  content: "替";
  font-size: 20px;
  visibility: hidden;
}
</style>
