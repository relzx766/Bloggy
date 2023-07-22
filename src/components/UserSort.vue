<template>
  <el-container>
    <el-main style="text-align: center">
      <el-dialog
          :visible.sync="addDialog"
          title="新建"
          width="40%">
        <div>
          <el-container>
            <el-main>
              <el-empty v-if="!sorts.length>0" description="空空如也"></el-empty>
              <el-row style="text-align:center;margin-bottom: 100px">
                <div style="width: 200px;height: 150px;border: 1px  black solid;margin: 0 auto">
                  <el-upload
                      :auto-upload="false"
                      :file-list="cover"
                      :on-change="handleChange"
                      :show-file-list="false"
                      action="#"
                      class="avatar-uploader" name="avatar">
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
                <el-input v-model="sortForAdd.title" maxlength="15" placeholder="收藏夹标题"></el-input>

              </el-row>
            </el-main>
          </el-container>
        </div>
        <span slot="footer" class="dialog-footer">
    <el-button @click="addDialog=false">取 消</el-button>
    <el-button type="primary" @click="createSort">确 定</el-button>
  </span>
      </el-dialog>
      <el-row style="text-align: right">
        <el-button v-if="id===userId" icon="el-icon-circle-plus" type="primary" @click="()=>{addDialog=true;cover=[]}">
          新建收藏夹
        </el-button>

      </el-row>
      <div v-for="(sort,index) in sorts">
        <el-row style="text-align: left">
          <el-row>
            <el-link :href="'/detail/sort?id='+sort.id"><h3>{{ sort.title }}</h3></el-link>
          </el-row>
          <el-row>
            <el-col :span="6">
              {{ getTime(sort.createTime) }}
            </el-col>
            <el-col :span="8">
              &nbsp;
            </el-col>
            <el-col :span="8">
              <el-link @click.native="()=>{dialogVisible=true;choose=sort;cover[0]={};cover[0].url=sort.cover}">
                <i class="el-icon-edit-outline"/>
                编辑
              </el-link>
              <el-link style="margin-left: 20px" @click.native="deleteSort(index)">
                <i class="el-icon-delete-solid"/>
                删除
              </el-link>
            </el-col>
          </el-row>
        </el-row>
        <el-divider></el-divider>
        <el-dialog
            :visible.sync="dialogVisible"
            title="编辑"
            width="40%"
        >
          <div>
            <el-container>
              <el-main>
                <el-row style="text-align:center;margin-bottom: 100px">
                  <el-upload
                      :auto-upload="false"
                      :file-list="cover"
                      :on-change="handleChange"
                      :show-file-list="false"
                      action="#"
                      class="avatar-uploader" name="avatar">
                    <el-image
                        v-if="cover.length>0"
                        :src="cover[0].url"
                        style="width: 200px;height: 150px"
                    ></el-image>
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
                </el-row>

                <el-row>
                  <el-input v-model="choose.title" maxlength="15"></el-input>

                </el-row>
              </el-main>
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
import {createSort, deleteSort, getByUser, updateSort} from "@/api/Sort";

export default {
  name: "UserSort",
  props: {
    id: String
  },
  data() {
    return {
      userId: String,
      sorts: [],
      dialogVisible: false,
      choose: {},
      cover: [],
      addDialog: false,
      sortForAdd: {}
    }
  },
  methods: {
    getByUser() {
      getByUser(this.id).then((res) => {
        this.sorts = res.data.sorts
      })
    }, getTime(date) {
      return new Date(date).toLocaleString()
    },
    deleteSort(index) {
      deleteSort(this.sorts[index].id).then((res) => {
        if (res.code === 2001) {
          this.sorts.splice(index, 1)
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
    handleChange(file, fileList) {
      this.cover[0] = file
      this.cover[0].url = URL.createObjectURL(file.raw)
      this.$forceUpdate()
    },
    updateSort() {
      updateSort(this.choose.id, this.choose.title, this.cover[0].raw).then((res) => {
        if (res.code === 2001) {
          this.dialogVisible = false;
          this.choose = {}
          this.cover = []
          this.getByUser();
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
    createSort() {
      createSort(this.sortForAdd.title, this.cover[0].raw).then((res) => {
        if (res.code === 2001) {
          this.addDialog = false;
          this.cover = []
          this.sortForAdd = {}
          this.getByUser();
        } else {
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
  created() {
    this.userId = this.$cookie.get("id")
    this.getByUser()
  }
}
</script>

<style scoped>


</style>