<template>
  <div>
    <el-table
        ref="multipleTable"
        :data="ads"
        tooltip-effect="dark"
        style="width: 100%"
    >
      <el-table-column
          label="id"
          prop="id"
          width="180">
      </el-table-column>
      <el-table-column
          label="广告语"
          prop="slogan"
          width="180">
      </el-table-column>
      <el-table-column
          label="链接"
          prop="url"
          width="180">
      </el-table-column>
      <el-table-column
          label="宣传图"
          width="180">
        <template slot-scope="scope">
          <el-image
              fit="cover"
              style="width: 200px; "
              :src="ads[scope.$index].image"
              :preview-src-list="fileList">
          </el-image>
        </template>
      </el-table-column>
      <el-table-column
          align="right">
        <template slot="header" slot-scope="scope">
          <el-button size="mini" type="primary" @click="addDialog=true">添加</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
        title="添加广告"
        :visible.sync="addDialog"
        :close-on-click-modal="false"
        width="30%">
<div>
  <el-row>
    <el-upload
        class="upload-demo"
        action="#"
        :file-list="[image]"
        :auto-upload="false"
        :on-change="handleChange"
        list-type="picture">
      <el-button size="small" type="primary">添加宣传图</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过25MB</div>
    </el-upload>
  </el-row>
  <el-row>

    <el-input v-model="ad.slogan" placeholder="请输入宣传标语" style="margin-top: 50px"></el-input>
  </el-row>
  <el-row>
    <el-input v-model="ad.url" placeholder="请输入广告链接"  style="margin-top: 10px"></el-input>
  </el-row>
</div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="addDialog= false">取 消</el-button>
    <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {getAdList,addAd} from "@/admin/api/Trend";

export default {
  name: "AdvertisingManage",
  data(){
    return{
      ads:[],
      ad:{
        slogan:'',
        url:''
      },
      image:null,
      addDialog:false,
      fileList:[]
    }
  },
  methods:{
    getAd(){
      getAdList().then((res)=>{
        this.ads=res.data.ads
        for (let i=0;i<this.ads.length;i++){
          this.fileList.push(this.ads[i].image)
        }
      })
    },
    add(){
      this.addDialog=false
      addAd(this.ad,this.image.raw).then((res)=>{
        if (res.code===2001){
          this.ads.push(res.data.ad)
          this.fileList.push(res.data.ad.image)
          this.$notify({
            title: '通知',
            message: '添加广告成功，时长为30天',
            type: 'success'
          });
        }
      })
    },
    handleChange(file, fileList){
      this.image=file
    }
  },
  created() {
    this.getAd()
  }
}
</script>

<style scoped>

</style>