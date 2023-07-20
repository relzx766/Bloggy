<template>
  <el-container style="width: 100%;">
    <el-main style="width: 100%;">
      <el-row>
        <el-col :span="11" :offset="0">
          <el-card>
            <div id="rc" class="view-box">
              <div class="view-toolbar">
                <span class="view-title">Rc</span>
                <el-select class="view-select" v-model="rcMonthValue" @change="refreshRcChart" size="mini">
                  <el-option
                      v-for="item in rcMonthOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
                <el-select class="view-select" @change="handlerRcYearChange" v-model="rcYearValue" size="mini">
                  <el-option
                      v-for="item in rcYearOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
              </div>
              <div ref="rcChart" class="chart"></div>
            </div>

          </el-card>
        </el-col>
        <el-col :span="11" :offset="2">
          <el-card>
            <div id="uv" class="view-box">
              <div class="view-toolbar">
                <span class="view-title">Uv</span>
                <el-select class="view-select" v-model="uvMonthValue" @change="refreshUvChart" size="mini">
                  <el-option
                      v-for="item in uvMonthOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
                <el-select class="view-select" @change="handlerUvYearChange" v-model="uvYearValue" size="mini">
                  <el-option
                      v-for="item in uvYearOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
              </div>
              <div ref="uvChart" class="chart"></div>
            </div>

          </el-card>
        </el-col>
      </el-row>
      <el-row style="margin-top: 30px">
        <el-col :span="11" :offset="0">
          <el-card>
            <div id="articleRange" class="view-box">
              <el-row>
                <el-col :span="12" class="view-bar-left">
                  <span class="view-title">文章发布统计</span>
                </el-col>
                <el-col :span="12" class="view-bar-right">
                  <span class="view-title">最近</span>
                  <el-input v-model="articleDayRange"  @change="artRangeChange" style="width: 30%;
margin-left: 6px;margin-right: 6px;
" size="mini"/>
                  <span class="view-title">天</span>
                </el-col>
              </el-row>
              <div ref="articleChart" class="chart"></div>
            </div>

          </el-card>
        </el-col>
        <el-col :span="11" :offset="2">
          <el-card>
            <div id="uv" class="view-box">
              <el-row>
                <el-col :span="12" class="view-bar-left">
                  <span class="view-title">用户注册统计</span>
                </el-col>
                <el-col :span="12" class="view-bar-right">
                  <span class="view-title">最近</span>
                  <el-input v-model="userDayRange"  @change="userRangeChange" style="width: 30%;
margin-left: 6px;margin-right: 6px;
" size="mini"/>
                  <span class="view-title">天</span>
                </el-col>
              </el-row>
              <div ref="userChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>

    </el-main>
  </el-container>
</template>

<script>
import {getViews} from "@/admin/api/Common";
import {getPublishCountByDay} from "@/admin/api/Article";
import {getRegCountByDay} from "@/admin/api/User";
import {descArray, getCount,getDaysBetween} from "@/util/tools";

export default {
  name: "DashBoard",
  data() {
    return {
      rc: {},
      rcYearOption: [],
      rcYearValue: '',
      rcMonthOption: [],
      rcMonthValue: '',
      uv: {},
      uvYearOption: [],
      uvYearValue: '',
      uvMonthOption: [],
      uvMonthValue: '',
      articlePublishCount: [],
      userRegCount: [],
      //最近x天
      articleDayRange: 10,

      userDayRange: 10,

      rcChart: null,
      rcChartConfig: {
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: "当日rc",
            type: "line",
            tooltip: {
              valueFormatter: function (value) {
                return getCount(value)
              }
            },
            data: []
          }
        ],
        dataZoom: {
          type: 'slider',
          start: 0,
          end: 100,
          height: 14,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },
      uvChart: null,
      uvChartConfig: {
        color: '#96ce7a',
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: "当日uv",
            type: "line",
            data: []
          }
        ],
        dataZoom: {
          type: 'slider',
          start: 0,
          end: 100,
          height: 14,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },
      articleChart: null,
      articleChartConfig: {
        color: '#fccc60',
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: "当日文章发布",
            type: "line",
            data: []
          }
        ],
        dataZoom: {
          type: 'slider',
          start: 0,
          end: 100,
          height: 14,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },
      userChart: null,
      userChartConfig: {
        color: '#ec6464',
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: "当日用户注册",
            type: "bar",
            data: []
          }
        ],
        dataZoom: {
          type: 'slider',
          start: 0,
          end: 100,
          height: 14,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },

    }
  },
  methods: {
    getViews() {
      getViews().then((res => {
        this.rc = res.data.rc;
        this.uv = res.data.uv;
        this.setRcYearOption();
        this.setRcMonthOption();
        this.refreshRcChart();
        this.setUvYearOption();
        this.setUvMonthOption();
        this.refreshUvChart();
      }))
    },
    setRcYearOption() {
      let year = Object.keys(this.rc)
      year = descArray(year)
      this.rcYearValue = year[0]
      this.rcYearOption = year;
    },
    handlerRcYearChange() {
      this.setRcMonthOption();
      this.refreshRcChart();
    }
    ,
    setRcMonthOption() {
      let year = this.rcYearValue;
      let month = Object.keys(this.rc[year]);
      month = descArray(month)
      this.rcMonthValue = month[0]
      this.rcMonthOption = month;
    },
    refreshRcChart() {
      let rc = this.rc;
      let days = rc[this.rcYearValue][this.rcMonthValue];
      let xValues = []
      let yValues = []
      for (let i = days.length - 1; i >= 0; i--) {
        xValues.push(Object.keys(days[i])[0])
        yValues.push(Object.values(days[i])[0])
      }
      if (xValues.length > 10) {
        this.rcChartConfig.dataZoom.show = true
      }
      this.rcChartConfig.xAxis.data = xValues
      this.rcChartConfig.series[0].data = yValues
      this.rcChart.setOption(this.rcChartConfig)
    },
    setUvYearOption() {
      let year = Object.keys(this.uv)
      year = descArray(year)
      this.uvYearValue = year[0]
      this.uvYearOption = year;
    },
    handlerUvYearChange() {
      this.setUvMonthOption();
      this.refreshUvChart();
    },
    setUvMonthOption() {
      let year = this.uvYearValue;
      let month = Object.keys(this.uv[year]);
      month = descArray(month)
      this.uvMonthValue = month[0]
      this.uvMonthOption = month;
    },
    refreshUvChart() {
      let uv = this.uv;
      let days = uv[this.uvYearValue][this.uvMonthValue];
      let xValues = []
      let yValues = []
      for (let i = days.length - 1; i >= 0; i--) {
        xValues.push(Object.keys(days[i])[0])
        yValues.push(Object.values(days[i])[0])
      }
      if (xValues.length > 10) {
        this.uvChartConfig.dataZoom.show = true
      }
      this.uvChartConfig.xAxis.data = xValues
      this.uvChartConfig.series[0].data = yValues
      this.uvChart.setOption(this.uvChartConfig)
    },
    getArticleCount() {
      getPublishCountByDay(this.articleDayRange).then((res) => {
        this.articlePublishCount = res.data.records;
        this.refreshArticleChart()
      })
    },
    artRangeChange(){
      this.articleDayRange= Number(this.articleDayRange) || 10
      this.getArticleCount()
    },
    refreshArticleChart() {
      let articles = this.articlePublishCount;
      let xValues = [];
      let yValues = [];
      for (let i=0;i<articles.length;i++){
        xValues.push(articles[i].date)
        yValues.push(articles[i].count)
      }
      this.articleChartConfig.xAxis.data = xValues;
      this.articleChartConfig.series[0].data = yValues;
      this.articleChart.setOption(this.articleChartConfig);
    },
    getUserCount() {
      getRegCountByDay(this.userDayRange).then((res) => {
        this.userRegCount = res.data.records;
        this.refreshUserChart();
      })
    },
    refreshUserChart() {
      let users = this.userRegCount;
      let xValues = [];
      let yValues = [];
      for (let i=0;i<users.length;i++){
        xValues.push(users[i].date)
        yValues.push(users[i].count)
      }
      this.userChartConfig.xAxis.data = xValues;
      this.userChartConfig.series[0].data = yValues;
      this.userChart.setOption(this.userChartConfig);
    },
    userRangeChange(){
      this.userDayRange= Number(this.userDayRange) || 10
      this.getUserCount();
    }
  },
  mounted() {
    this.rcChart = this.$echarts.init(this.$refs.rcChart);
    this.uvChart = this.$echarts.init(this.$refs.uvChart);
    this.articleChart = this.$echarts.init(this.$refs.articleChart);
    this.userChart=this.$echarts.init(this.$refs.userChart);
    this.getViews();
    this.getArticleCount();
    this.getUserCount();
  }
}
</script>

<style scoped>
el-col {
  text-align: center;
}

.view-box {
  width: 100%;
  height: 400px;
}

.chart {
  width: 100%;
  height: 80%;
}

.view-select {
  width: 16%;
  float: right;
}

.view-title {
  color: #70747c;
  font-size: 16px;
}
.view-bar-right{
  text-align: right;
}
.view-bar-left{
  text-align: left;
}
el-slider {
  width: 50%;
  float: right;
}

</style>