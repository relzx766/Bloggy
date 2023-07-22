export const getCount = (num) => {
    if (num > 1000) {
        return (num / 1000).toFixed(1) + 'k';
    }
    return num;
}
export const getDate = (date) => {
    let before = new Date(date);
    let now = new Date();
    let time = (now - before) / 1000;
    let result;
    if (time < 120) {
        result = '刚刚'
    } else if (time < 3600) {
        result = Math.floor(time / 60) + "分钟前"
    } else if (time < 3600 * 24) {
        result = Math.floor(time / 3600) + "小时前"
    } else {
        result = before.toLocaleString()
    }
    return result;
}
export const getMonthAndDay = (timestamp) => {
    let date = new Date(timestamp);
    let month = date.getMonth() + 1;
    let day = date.getDate();
    month = month.toString().padStart(2, '0');
    day = day.toString().padStart(2, '0');
    return month + '/' + day;
}
export const descArray = (arr) => {
    return arr.sort((a, b) => {
        return b - a;
    })
}
export const ascArray = (arr) => {
    return arr.sort((a, b) => {
        return a - b;
    })
}
export const getDaysBetween = (dateString1, dateString2) => {
    let startDate = Date.parse(dateString1);
    let endDate = Date.parse(dateString2);
    let days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
}
export const doPercent = (num1, num2) => {
    let percent = Math.ceil(num1 / num2 * 100);
    if (Number.isFinite(percent)) {
        return percent
    } else {
        return 0
    }
}

