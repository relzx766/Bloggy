<template>
<span style="width: 100%">
    <span v-if="data.length<maxLen">
        <div class="content"><span v-if="type===2" style="font-weight: bolder">回复</span> {{ data }}</div>
    </span>
    <span v-else>
        <div class="content">{{ showBtn ? sliceStr : data }}
            <span class="btnWord" @click="showBtn=!showBtn">{{ showBtn ? tip : "收起" }}</span>
        </div>
    </span>
</span>
</template>

<script>
export default {
  name: "unfold",
  data() {
    return {
      showBtn: true,
    }
  },
  props: {
    tip: {
      type: String,
      default: '展开'
    },
    // 数据
    data: {
      type: String,
      default: ''
    },
    // 最大长度
    maxLen: {
      type: Number,
      default: 80
    },
    type: Number
  },
  computed: {
    sliceStr() {
      if (this.data != null) {
        return this.data.substring(0, this.maxLen) + "...";
      }
      return '';
    }
  },
}
</script>

<style scoped>
.content {
  width: 90%;
  word-wrap: break-word;
  word-break: normal;
  text-align: left;
  font-size: 14px;
}

.btnWord {
  color: #449cfc;
  font-size: 14px;
}
</style>

