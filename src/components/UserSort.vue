<template>
<el-container>
  <el-main style="text-align: center">
    <el-dialog
        title="新建"
        :visible.sync="addDialog"
        width="40%">
      <div>
        <el-container>
          <el-main>
            <el-row style="text-align:center;margin-bottom: 100px">
              <div style="width: 200px;height: 150px;border: 1px  black solid;margin: 0 auto">
                <el-upload
                    class="avatar-uploader"
                    action="#"
                    name="avatar"
                    :show-file-list="false"
                    :auto-upload="false"
                    :file-list="cover" :on-change="handleChange">
                  <el-image
                      v-if="cover.length>0"
                      :src="cover[0].url"
                      style="width: 200px;height: 150px"
                  ></el-image>
                  <i v-else class="el-icon-plus avatar-uploader-icon" style="font-size: 20px;margin-top: 60px"></i>
                </el-upload>

              </div>
            </el-row>

            <el-row>
              <el-input placeholder="收藏夹标题"  v-model="sortForAdd.title" maxlength="15"></el-input>

            </el-row></el-main>
        </el-container>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="addDialog=false">取 消</el-button>
    <el-button type="primary" @click="createSort">确 定</el-button>
  </span>
    </el-dialog>
    <el-row style="text-align: right">
      <el-button type="primary" icon="el-icon-circle-plus" @click="()=>{addDialog=true;cover=[]}">新建收藏夹</el-button>

    </el-row>
<div v-for="(sort,index) in sorts" >
  <el-row style="text-align: left">
     <el-row><el-link :href="'/detail/sort?id='+sort.id"><h3>{{sort.title}}</h3></el-link> </el-row>
      <el-row>
        <el-col :span="6">
          {{getTime(sort.createTime)}}
        </el-col>
        <el-col :span="8">
          &nbsp;
        </el-col>
        <el-col :span="8">
          <el-link @click.native="()=>{dialogVisible=true;choose=sort;cover[0]={};cover[0].url=sort.cover}">
            <i class="el-icon-edit-outline"/>
            编辑</el-link>
          <el-link style="margin-left: 20px" @click.native="deleteSort(index)">
            <i class="el-icon-delete-solid"/>
            删除</el-link>
        </el-col>
      </el-row>
  </el-row>
  <el-divider></el-divider>
  <el-dialog
      title="编辑"
      :visible.sync="dialogVisible"
      width="40%"
  >
    <div>
      <el-container>
        <el-main>
          <el-row style="text-align:center;margin-bottom: 100px">
            <el-upload
                class="avatar-uploader"
                action="#"
                name="avatar"
                :show-file-list="false"
                :auto-upload="false"
                :file-list="cover" :on-change="handleChange">
              <el-image
                  v-if="cover.length>0"
                  :src="cover[0].url"
                  style="width: 200px;height: 150px"
              ></el-image>
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-row>

          <el-row>
            <el-input  v-model="choose.title" maxlength="15"></el-input>

          </el-row></el-main>
      </el-container>
    </div>
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="updateSort">确 定</el-button>
  </span>
  </el-dialog>

</div>
  </el-main>
</el-container>
</template>

<script>
import {getByUser,deleteSort,updateSort,createSort} from "@/api/Sort";
import {getMonthAndDay} from "@/util/tools";

export default {
  name: "UserSort",
  data(){
    return{
      userId:String,
      sorts:[],
      dialogVisible:false,
      choose:{},
      cover:[],
      addDialog:false,
      sortForAdd:{}
    }
  },
  methods:{
    getByUser(){
      getByUser(this.userId).then((res)=>{
        this.sorts=res.data.sorts
      })
    } ,  getTime(date){
      return new Date(date).toLocaleString()
    },
    deleteSort(index){
      deleteSort(this.sorts[index].id).then((res)=>{
        if (res.code===2001){
          this.sorts.splice(index,1)
        }else {
         this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration:1000
          })
        }
      })
    },
    handleChange(file,fileList){
      this.cover[0]=file
      this.cover[0].url=URL.createObjectURL(file.raw)
      this.$forceUpdate()
    },
    updateSort(){
      updateSort(this.choose.id,this.choose.title,this.cover[0].raw).then((res)=>{
        if (res.code===2001){
          this.dialogVisible=false;
          this.choose={}
          this.cover=[]
          this.getByUser();
        }else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration:1000
          })
        }
      })
    },
    createSort(){
      createSort(this.sortForAdd.title,this.cover[0].raw).then((res)=>{
        if (res.code===2001){
          this.addDialog=false;
          this.cover=[]
          this.sortForAdd={}
          this.getByUser();
        }else {
          this.$notify({
            title: "Bloggy",
            message: res.message,
            type: "error",
            duration:1000
          })
        }
      })
    }
  },
  created() {
    this.userId=this.$cookie.get("id")
    this.getByUser()
  }
}
</script>

<style scoped>


</style>