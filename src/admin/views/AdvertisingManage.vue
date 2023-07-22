<template>
  <el-container>
    <el-main>
      <el-table
          ref="multipleTable"
          :data="ads"
          style="width: 100%"
          tooltip-effect="dark"
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
                :preview-src-list="fileList"
                :src="ads[scope.$index].image"
                fit="cover"
                style="width: 200px; ">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column
            label="创建时间"
            width="180">
          <template slot-scope="scope">
            {{ new Date(ads[scope.$index].createTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column
            label="时长"
            prop="expire"
            width="180">
        </el-table-column>
        <el-table-column
            align="right">
          <template slot="header" slot-scope="scope">
            <el-button size="mini" type="primary" @click="addDialog=true">添加</el-button>
          </template>
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="danger"
                @click="()=>{editDialog=true;ad=ads[scope.$index];image.url=ad.image}"
            >编辑
            </el-button>
            <el-button
                size="mini"
                type="primary"
                @click="delAd(scope.$index)"
            >删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog
          :close-on-click-modal="false"
          :visible.sync="addDialog"
          title="添加广告"
          width="30%">
        <div>
          <el-form ref="form" :model="ad" label-position="left" label-width="80px">
            <el-form-item>
              <el-upload
                  :auto-upload="false"
                  :file-list="[image]"
                  :on-change="handleChange"
                  action="#"
                  class="upload-demo"
                  list-type="picture">
                <el-button size="small" type="primary">添加宣传图</el-button>
                <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过25MB</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="宣传标语">
              <el-input v-model="ad.slogan" placeholder="请输入宣传标语"></el-input>
            </el-form-item>
            <el-form-item label="链接">
              <el-input v-model="ad.url" placeholder="请输入广告链接"></el-input>
            </el-form-item>
            <el-form-item label="时长(天)">
              <el-slider
                  v-model="ad.expire"
                  show-input>
              </el-slider>
            </el-form-item>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
    <el-button @click="addDialog= false">取 消</el-button>
    <el-button type="primary" @click="add">确 定</el-button>
      </span>
      </el-dialog>
      <el-dialog
          :close-on-click-modal="false"
          :visible.sync="editDialog"
          title="修改广告"
          width="30%">
        <div>
          <el-form ref="form" :model="ad" label-position="left" label-width="80px">
            <el-form-item>
              <el-upload
                  :auto-upload="false"
                  :file-list="[image]"
                  :on-change="handleChange"
                  action="#"
                  class="upload-demo"
                  list-type="picture">
                <el-button size="small" type="primary">添加宣传图</el-button>
                <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过25MB</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="宣传标语">
              <el-input v-model="ad.slogan" placeholder="请输入宣传标语"></el-input>
            </el-form-item>
            <el-form-item label="链接">
              <el-input v-model="ad.url" placeholder="请输入广告链接"></el-input>
            </el-form-item>
            <el-form-item label="时长(天)">
              <el-slider
                  v-model="ad.expire"
                  show-input>
              </el-slider>
            </el-form-item>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
    <el-button @click="editDialog= false">取 消</el-button>
    <el-button type="primary" @click="updateAd">确 定</el-button>
      </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import {addAd, delAd, getAdList, updateAd} from "@/admin/api/Common";

export default {
  name: "AdvertisingManage",
  data() {
    return {
      ads: [],
      ad: {
        slogan: '',
        url: '',
        expire: 0
      },
      image: null,
      addDialog: false,
      fileList: [],
      editDialog: false
    }
  },
  methods: {
    getAd() {
      getAdList().then((res) => {
        this.ads = res.data.ads
        for (let i = 0; i < this.ads.length; i++) {
          this.fileList.push(this.ads[i].image)
        }
      })
    },
    add() {
      this.addDialog = false
      addAd(this.ad, this.image.raw).then((res) => {
        if (res.code === 2001) {
          this.ad = {}
          this.ads.push(res.data.ad)
          this.fileList.push(res.data.ad.image)
          this.addDialog = false;
          this.$notify({
            title: '通知',
            message: '添加广告成功，时长为' + res.data.ad.expire + '天',
            type: 'success'
          });
        } else {
          this.$notify({
            title: '通知',
            message: '添加广告失败' + res.message,
            type: 'error'
          });
        }
      })
    },
    updateAd(index) {
      updateAd(this.ad, this.image.raw).then((res) => {
        if (res.code === 2001) {
          this.ad = {}
          this.ads.splice(index, 1, res.data.ad);
          this.fileList.push(res.data.ad.image)
          this.editDialog = false;
          this.$notify({
            title: '通知',
            message: '修改广告成功，时长为' + res.data.ad.expire + '天',
            type: 'success'
          });
        } else {
          this.$notify({
            title: '通知',
            message: '修改广告失败' + res.message,
            type: 'error'
          });
        }
      })
    },
    delAd(index) {
      let id = this.ads[index].id;
      delAd(id).then((res) => {
        if (res.code === 2001) {
          this.ads.splice(index, 1);
          this.fileList.splice(index, 1);
          this.$notify({
            title: '通知',
            message: '删除广告成功',
            type: 'success'
          });
        } else {
          this.$notify({
            title: '通知',
            message: '删除广告失败' + res.message,
            type: 'error'
          });
        }
      })
    },
    handleChange(file, fileList) {
      this.image = file
    }
  },
  created() {
    this.getAd()
  }
}
</script>

<style scoped>
.el-main::-webkit-scrollbar {
  width: 0 !important
}
</style>