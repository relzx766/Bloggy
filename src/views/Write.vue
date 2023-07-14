<template>
  <el-container>
    <el-header style="position: fixed;top: 0;width: 96%;z-index: 10;margin-left: 20px">
   <navigation :active-index="'3'"/>
    </el-header>
    <el-container style="margin-top: 50px">
      <el-aside style="margin-top: 50px;width: 15%;margin-left: 20px;overflow: visible" >
        <el-tag class="article-tag"
            :key="tag"
            v-for="tag in tags"
            closable
            :disable-transitions="false"
                effect="dark"
            @close="handleClose(tag)">
          {{tag}}
        </el-tag>
        <el-input
            class="input-new-tag"
            v-if="inputVisible"
            v-model="inputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
        >
        </el-input>
        <el-button v-else  class="button-new-tag" size="small" style="position: absolute;margin-top: 10px"   @click="showInput">+ New Tag</el-button>
      </el-aside>
      <el-main>
        <div id="title">
          <el-input placeholder="请输入标题" type="textarea" autosize maxlength="50" show-word-limit
                    v-model="article.title" style="width: 70%;float: left;font-size: 18px"></el-input>
          <el-button style="float: right" type="primary" round @click="postArticle">发布</el-button>
        </div>

        <div id="editor">
          <mavon-editor   @imgAdd="imgAdd"
                           style="width: 100%;margin-top: 30px" ref="md" v-model="editor.value"/>
        </div>

      </el-main>
    </el-container>

  </el-container>
</template>

<script>
import Navigation from "@/components/Navigation";
import {getArticleDetail, postArticle,update} from "@/api/Article";
import {uploadFile} from "@/api/Common";

export default {
  name: "Write",

  data() {
    return {
      id:String,
      inputVisible: false,
      inputValue: '',
      article: {
        title: '',
        content: '',
        tags: [],
        description: ''
      },
      tags:[],
      editor: {
        value: '',
      },
      flag:0
    }
  },
  methods:{
    handleClose(tag) {
      this.tags.splice(this.tags.indexOf(tag), 1);
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.tags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    postArticle(){
      this.article.content=this.$refs.md.d_value
      this.article.tags=this.tags
      console.log(this.article)
      if (this.id){
        this.article.id=this.id
        update(JSON.stringify(this.article)).then((res)=>{
          if (res.code==2001) {
            this.$notify({
              title: "Bloggy",
              message: res.message,
              type: "success",
              duration: 2000
            })
            this.flag=1
            this.$router.push("/index")
          }else {
            this.$notify({
              title: "Bloggy",
              message: res.message,
              type: "error",
              duration: 1000
            })
          }
        })
      }else {
        postArticle(JSON.stringify(this.article)).then((res)=>{
          if (res.code==2001) {
            this.$notify({
              title: "Bloggy",
              message: res.message,
              type: "success",
              duration: 2000
            })
            this.flag=1
            this.$router.push("/index")
          }else {
            this.$notify({
              title: "Bloggy",
              message: res.message,
              type: "error",
              duration: 1000
            })
          }
        })

      }
    },
    imgAdd (pos, file) {
// 上传图片
      let formData = new FormData()
      formData.append('image', file)
      uploadFile(formData).then((res) => {
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        /**
         * $vm 指为mavonEditor实例，可以通过如下两种方式获取
         * 1.  通过引入对象获取: `import {mavonEditor} from ...` 等方式引入后，`$vm`为`mavonEditor`
         * 2. 通过$refs获取: html声明ref : `<mavon-editor ref=md ></mavon-editor>，`$vm`为 `this.$refs.md`
         *
         */
        this.$refs.md.$img2Url(pos,res.data.path)
      })
    },
    getDetail() {
      getArticleDetail(this.id).then((res) => {
        this.article = res.data.article
        this.tags= res.data.article.tags
        this.editor.value=this.article.content
      })
    }
  },
  components: {
    'navigation': Navigation
  },
  beforeDestroy() {
    let value=this.$refs.md.d_value
    if (this.flag==0) {
      this.$cookie.set("draft", value, 24 * 60 * 60 * 30)
    }else {
      this.$cookie.delete("draft")
    }
  },
  created() {
    this.id=this.$route.query.id
    if (this.id){
      this.getDetail()
    }
    return
    this.editor.value=this.$cookie.get("draft")
  }
}
</script>

<style scoped>
#editor {
  width: 80%;
  text-align: center;
  margin: 100px auto 20px;
}

#title {
  width: 80%;
  text-align: center;
  background-color: #42b983;
  margin: 30px auto 0;
}

li {
  list-style-type: none;
}
.el-tag {
  white-space: normal;
  height: auto;
  text-align: left;
  border-radius: 8px;
  float: left;
  margin-left: 10px;
  margin-top: 10px;
}
</style>
