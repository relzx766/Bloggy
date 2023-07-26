<template>
  <el-container>
    <el-main style="width: 100%;height:100vh;">
      <el-row style="display: flex">
        <el-card class="count-trend">
          <el-row style="height: 50px">
            <el-col :span="12" class="count-trend-left">
              <el-image :src="require('../../static/images/request_count.svg')" style="width: 40px;"></el-image>
            </el-col>
            <el-col :span="12" class="count-trend-right">
              <span>请求量</span><br>
              <span>{{ monthRcCount }}</span>
            </el-col>
          </el-row>
          <el-row v-if="rcTrendInfo.length>0" style="font-size: 12px">
            <i class="el-icon-s-operation" style="color: #fccc5c"/>
            <span>{{ rcTrendInfo }}</span>
          </el-row>
        </el-card>
        <el-card class="count-trend">
          <el-row style="height: 50px">
            <el-col :span="10" class="count-trend-left">
              <el-image :src="require('../../static/images/request.svg')" style="width: 40px;"></el-image>
            </el-col>
            <el-col :span="14" class="count-trend-right">
              <span>唯一用户访问</span><br>
              <span>{{ monthUvCount }}</span>
            </el-col>
          </el-row>
          <el-row v-if="rcTrendInfo.length>0" style="font-size: 12px">
            <i class="el-icon-s-platform" style="color: coral"/>
            <span>{{ uvTrendInfo }}</span>
          </el-row>
        </el-card>

        <el-card class="count-trend">
          <el-row style="height: 50px">
            <el-col :span="12" class="count-trend-left">
              <el-image :src="require('../../static/images/article.svg')" style="width: 40px;"></el-image>
            </el-col>
            <el-col :span="12" class="count-trend-right">
              <span>新增文章</span><br>
              <span>{{ dayArticleCount }}</span>
            </el-col>
          </el-row>
          <el-row v-if="articleTrendInfo.length>0" style="font-size: 12px">
            <i class="el-icon-edit-outline" style="color: #14ac64"/>
            <span>{{ articleTrendInfo }}</span>
          </el-row>
        </el-card>
        <el-card class="count-trend">
          <el-row style="height: 50px">
            <el-col :span="12" class="count-trend-left">
              <el-image :src="require('../../static/images/userCount.svg')" style="width: 40px;"></el-image>
            </el-col>
            <el-col :span="12" class="count-trend-right">
              <span>新增用户</span><br>
              <span>{{ dayUserCount }}</span>
            </el-col>
          </el-row>
          <el-row v-if="userTrendInfo.length>0" style="font-size: 12px">
            <i class="el-icon-s-promotion" style="color: #209ffc"/>
            <span>{{ userTrendInfo }}</span>
          </el-row>
        </el-card>

      </el-row>
      <el-row style="margin-top: 20px;display: flex">
        <el-card style="width: 74%">
          <div id="articleRange" class="view-box">
            <el-row>
              <el-col :span="12" class="view-bar-left">
                <span class="view-title">文章发布统计</span>
              </el-col>
              <el-col :span="12" class="view-bar-right">
                <span class="view-title">最近</span>
                <el-input v-model="articleDayRange" size="mini" style="width: 20%;
margin-left: 6px;margin-right: 6px;
" @change="artRangeChange"/>
                <span class="view-title">天</span>
              </el-col>
            </el-row>
            <div ref="articleChart" class="chart" style="width: 100%;height: 80%"></div>
          </div>

        </el-card>


        <el-card style="width: 24%;margin-left: 2%">
          <div class="view-box">
            <div ref="articleCountChart" class="chart"></div>

          </div>

        </el-card>

      </el-row>
      <el-row style="margin-top: 30px;display: flex;">
        <el-card class="view-count">
          <div id="rc" class="view-box">
            <el-row>
              <el-col :span="12" class="view-bar-left">
                <span class="view-title">Rc</span>
              </el-col>
              <el-col :span="12" class="view-bar-right">
                <el-select v-model="rcMonthValue" class="view-select" size="mini" @change="refreshRcChart">
                  <el-option
                      v-for="item in rcMonthOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
                <el-select v-model="rcYearValue" class="view-select" size="mini" @change="handlerRcYearChange">
                  <el-option
                      v-for="item in rcYearOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
              </el-col>
            </el-row>
            <div ref="rcChart" class="chart" style="width: 100%;height: 80%"></div>
          </div>
        </el-card>
        <el-card class="view-count">
          <div id="uv" class="view-box">
            <el-row>
              <el-col :span="12" class="view-bar-left">
                <span class="view-title">Uv</span>
              </el-col>
              <el-col :span="12" class="view-bar-right">
                <el-select v-model="uvMonthValue" class="view-select" size="mini" @change="refreshUvChart">
                  <el-option
                      v-for="item in uvMonthOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
                <el-select v-model="uvYearValue" class="view-select" size="mini" @change="handlerUvYearChange">
                  <el-option
                      v-for="item in uvYearOption"
                      :key="item"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
              </el-col>
            </el-row>
            <div ref="uvChart" class="chart" style="width: 100%;height: 80%"></div>
          </div>

        </el-card>
        <el-card class="view-count">
          <div id="userRange" class="view-box">
            <el-row>
              <el-col :span="12" class="view-bar-left">
                <span class="view-title">用户注册统计</span>
              </el-col>
              <el-col :span="12" class="view-bar-right">
                <span class="view-title">最近</span>
                <el-input v-model="userDayRange" size="mini" style="width: 30%;
margin-left: 6px;margin-right: 6px;
" @change="userRangeChange"/>
                <span class="view-title">天</span>
              </el-col>
            </el-row>
            <div ref="userChart" class="chart" style="width: 100%;height: 80%"></div>
          </div>
        </el-card>
      </el-row>

    </el-main>
  </el-container>
</template>

<script>
import {getViews} from "@/admin/api/Common";
import {getPublishCountByDay, getRecordCount} from "@/admin/api/Article";
import {getRegCountByDay, getUserCount} from "@/admin/api/User";
import {descArray, doPercent, getCount} from "@/util/tools";
import * as moment from 'moment'

export default {
  name: "DashBoard",
  data() {
    return {
      rc: {},
      rcYearOption: [],
      rcYearValue: '',
      monthRcCount: 0,
      rcTrendInfo: '',
      rcMonthOption: [],
      rcMonthValue: '',
      uv: {},
      uvYearOption: [],
      uvYearValue: '',
      monthUvCount: 0,
      uvTrendInfo: '',
      uvMonthOption: [],
      uvMonthValue: '',
      articlePublishCount: [],
      articleInactiveCount: 0,
      articleActiveCount: 0,
      dayArticleCount: 0,
      articleTrendInfo: '',
      userRegCount: [],
      dayUserCount: 0,
      userTrendInfo: '',
      //最近x天
      articleDayRange: 10,

      userDayRange: 10,

      rcChart: null,
      rcChartConfig: {
        tooltip: {},
        grid: {
          left: 10,
          right: 10,
          bottom: '10%',
          containLabel: true
        },
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
          height: 10,
          bottom: 4,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },
      uvChart: null,
      uvChartConfig: {
        color: '#96ce7a',
        tooltip: {},
        grid: {
          left: 10,
          right: 10,
          bottom: '10%',
          containLabel: true
        },
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
          bottom: 4,
          showDataShadow: true,
          showDetail: false,
          show: false
        }
      },
      articleChart: null,
      articleChartConfig: {
        color: '#fccc60',
        tooltip: {},
        grid: {
          left: 10,
          right: 10,
          bottom: '2%',
          containLabel: true
        },
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
      articleSector: null,
      articleSectorConfig: {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: 0,
          left: 'center'
        },
        grid: {
          top: '30%',
          left: 'center'
        },
        series: [
          {
            name: '文章统计',
            type: 'pie',
            radius: ['30%', '70%'],
            center: ['50%', '50%'],
            roseType: 'area',
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: []
          }
        ]
      },
      userChart: null,
      userChartConfig: {
        color: '#ec6464',
        tooltip: {},
        grid: {
          left: 10,
          right: 10,
          bottom: 2,
          containLabel: true
        },
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
        this.setMonthRcCount();
        this.setRcYearOption();
        this.setRcMonthOption();
        this.refreshRcChart();
        this.setMonthUvCount();
        this.setUvYearOption();
        this.setUvMonthOption();
        this.refreshUvChart();
      }))
    }
    ,
    setRcYearOption() {
      let year = Object.keys(this.rc)
      year = descArray(year)
      this.rcYearValue = year[0]
      this.rcYearOption = year;
    },
    handlerRcYearChange() {
      this.setRcMonthOption();
      this.refreshRcChart();
    },
    setMonthRcCount() {
      let year = new Date().getFullYear().toString();
      let month = new Date().getMonth() + 1;
      let count = [0, 0];
      let lastMonth = this.rc[year][(month - 1).toString().padStart(2, '0')];
      let thisMonth = this.rc[year][month.toString().padStart(2, '0')];
      console.log(lastMonth)
      console.log(thisMonth)
      lastMonth.forEach(map => {
        count[0] += Object.values(map)[0]
      })
      thisMonth.forEach(map => {
        count[1] += Object.values(map)[0];
      })
      this.monthRcCount = count[1];
      let percent = doPercent(count[1], count[0])
      console.log(percent)
      if (percent > 100) {
        this.rcTrendInfo = "较上月提升" + percent + "%";
      } else if (percent === 100) {
        this.rcTrendInfo = "较上月提升0%";
      } else {
        this.rcTrendInfo = "较上月下降" + percent + "%";
      }
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
        xValues.push(Object.keys(days[i])[0] + '号')
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
    setMonthUvCount() {
      let record = this.uv;
      console.log(record)
      let date = new Date();
      let year = date.getFullYear().toString();
      let month = (date.getMonth() + 1).toString().padStart(2, "0");
      let today = date.getDate().toString().padStart(2, "0");
      let yesterday = (date.getDate() - 1).toString().padStart(2, "0");
      let thisMonthData = new Map();
      record[year][month].forEach(map => {
        thisMonthData.set(Object.keys(map)[0], Object.values(map)[0])
      })
      this.monthUvCount = thisMonthData.get(today);
      let percent = doPercent(this.monthUvCount, thisMonthData.get(yesterday));
      if (percent > 100) {
        this.uvTrendInfo = "较昨日增加" + percent + "%";
      } else if (percent === 10) {
        this.uvTrendInfo = "较昨日增加0%";
      } else {
        this.uvTrendInfo = "较昨日减少" + percent + "%";

      }
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
        xValues.push(Object.keys(days[i])[0] + '号')
        yValues.push(Object.values(days[i])[0])
      }
      if (xValues.length > 10) {
        this.uvChartConfig.dataZoom.show = true
      }
      this.uvChartConfig.xAxis.data = xValues
      this.uvChartConfig.series[0].data = yValues
      this.uvChart.setOption(this.uvChartConfig)
    },
    getArticleRecordCount() {
      getRecordCount().then((res) => {
        let data = res.data;
        let all = data.all;
        let active = data.active;
        this.articleActiveCount = active;
        this.articleInactiveCount = all - active;
        this.refreshArticleCountChart();
      })
    },
    refreshArticleCountChart() {
      let data = [{
        value: this.articleActiveCount, name: '有效',
        itemStyle: {color: '#98cc74'}
      },
        {
          value: this.articleInactiveCount, name: '冻结',
          itemStyle: {color: '#ec6464'}
        }];
      this.articleSectorConfig.series[0].data = data;
      this.articleSector.setOption(this.articleSectorConfig)
    },
    getArticleCount() {
      getPublishCountByDay(this.articleDayRange).then((res) => {
        this.articlePublishCount = res.data.records;
        this.setDayArticleCount();
        this.refreshArticleChart()
      })
    },
    setDayArticleCount() {
      let date = new Date();
      let dateFormat = "YYYY-MM-DD";
      let forMatDate = moment(date).format(dateFormat)
      let lastRecord = this.articlePublishCount[this.articlePublishCount.length - 1]
      let secondLastRecord = this.articlePublishCount[this.articlePublishCount.length - 2]
      let count = [0, 0];
      if (lastRecord && lastRecord.date === forMatDate) {
        count[0] = lastRecord.count;
      }
      date = moment(date).subtract(1, "days");
      forMatDate = moment(date).format(dateFormat)
      if (secondLastRecord && secondLastRecord.date === forMatDate) {
        count[1] = secondLastRecord.count;
      }
      console.log(lastRecord, secondLastRecord, count)
      this.dayArticleCount = count[0];
      let percent = doPercent(count[0], count[1])
      if (percent > 100) {
        this.articleTrendInfo = "较昨天增加" + percent + "%";
      } else if (percent === 100) {
        this.articleTrendInfo = "较昨天增加0%";

      } else {
        this.articleTrendInfo = "较昨天减少" + percent + "%";
      }
    },
    artRangeChange() {
      this.articleDayRange = Number(this.articleDayRange) || 10
      this.getArticleCount()
    },
    refreshArticleChart() {
      let articles = this.articlePublishCount;
      let xValues = [];
      let yValues = [];
      for (let i = 0; i < articles.length; i++) {
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
        this.setUserCount();
        this.refreshUserChart();
      })
      getUserCount().then((res) => {
        let count = res.data.all;
        this.userTrendInfo = "共有用户" + count + "位"
      })
    },
    setUserCount() {
      let record = this.userRegCount;
      let lastRecord = record[record.length - 1];
      let date = new Date();
      let dateFormat = "YYYY-MM-DD";
      let formatDate = moment(date).format(dateFormat);
      let num = 0;
      if (lastRecord && lastRecord.date === formatDate) {
        num = lastRecord.count;
      }
      this.dayUserCount = num;
      let count = 0;

    },
    refreshUserChart() {
      let users = this.userRegCount;
      let xValues = [];
      let yValues = [];
      for (let i = 0; i < users.length; i++) {
        xValues.push(users[i].date)
        yValues.push(users[i].count)
      }
      this.userChartConfig.xAxis.data = xValues;
      this.userChartConfig.series[0].data = yValues;
      this.userChart.setOption(this.userChartConfig);
    },
    userRangeChange() {
      this.userDayRange = Number(this.userDayRange) || 10
      this.getUserCount();
    }
  },
  mounted() {
    this.rcChart = this.$echarts.init(this.$refs.rcChart);
    this.uvChart = this.$echarts.init(this.$refs.uvChart);
    this.articleChart = this.$echarts.init(this.$refs.articleChart);
    this.userChart = this.$echarts.init(this.$refs.userChart);
    this.articleSector = this.$echarts.init(this.$refs.articleCountChart)
    this.getViews();
    this.getArticleCount();
    this.getUserCount();
    this.getArticleRecordCount();
  }
}
</script>

<style scoped>
el-col {
  text-align: center;
}

.view-count {
  width: 32%;
  float: left;
  height: 320px;
}

.view-count + .view-count {
  margin-left: 2%;
}

.view-box {
  width: 100%;
  height: 320px;
  text-align: center;
}

.chart {
  width: 90%;
  height: 90%;
}

.view-select {
  width: 40%;
  float: right;
}

.view-title {
  color: #70747c;
  font-size: 16px;
}

.view-bar-right {
  text-align: right;
}

.view-bar-left {
  text-align: left;
}

el-slider {
  width: 50%;
  float: right;
}

.el-popper[x-placement^=bottom] .popper__arrow::after {

  border-bottom-color: #03204e !important;

}

.count-trend {
  width: 22%;
}

.count-trend + .count-trend {
  margin-left: 4%;
}

.count-trend-left {
  text-align: left;
}

.count-trend-right {
  text-align: right;
}

.el-main::-webkit-scrollbar {
  width: 0 !important
}
</style>