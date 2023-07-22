<template>
  <div id="nav">
    <el-menu :default-active="activeIndex">
      <el-menu-item index="1">
        111
      </el-menu-item>

    </el-menu>
  </div>
</template>

<script>
export default {
  name: "LeftNavigation",
  props: {
    activeIndex: String
  }
}
</script>

<style scoped>

</style>