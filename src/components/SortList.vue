<template>
  <el-container>
    <el-main>
      <el-row v-for="index in Math.ceil(sorts.length/2)" style="margin-top: 20px">
        <el-col :span="12">
          <el-row>
            <el-col :span="7">
              <el-image :src="sorts[index * 2 - 2].cover" class="sort-image" fit="cover"/>
            </el-col>
            <el-col :span="17">
              <el-row><h1>{{ sorts[index * 2 - 2].title }}</h1></el-row>
              <el-row style="bottom: -30px">
                <el-tag size="mini" type="info"><i class="el-icon-user"/></el-tag>
                {{ sorts[index * 2 - 2].owner }}
              </el-row>
            </el-col>
          </el-row>
        </el-col>
        <el-col v-if="index * 2 - 1 < sorts.length" :span="12">
          <el-row>
            <el-col :span="6">
              <el-image :src="sorts[index * 2 - 1].cover" class="sort-image" fit="cover"/>
            </el-col>
            <el-col :span="18">
              <el-row><h1>{{ sorts[index * 2 - 1].title }}</h1></el-row>
              <el-row>
                <el-tag size="mini" type="info"><i class="el-icon-user"/></el-tag>
                {{ sorts[index * 2 - 1].owner }}
              </el-row>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "SortList",
  props: {
    sorts: Array
  }
}
</script>

<style scoped>
.sort-image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
}
</style>