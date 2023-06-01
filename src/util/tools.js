import Vue from "vue";

export const getCount=(num)=>{
    if (num>1000){
        return num/1000+'k';
    }
   return num;
}
export const getDate=(date)=>{
    let before=new Date(date);
    let now=new Date();
    let time=(now-before)/1000;
    let result;
    if (time<120){
       result='刚刚'
    }else if (time<3600){
        result=Math.floor(time/60)+"分钟前"
    }else if (time<3600*24){
        result=Math.floor(time/3600)+"小时前"
    }else{
        result=before.toLocaleString()
    }
    return result;
}
