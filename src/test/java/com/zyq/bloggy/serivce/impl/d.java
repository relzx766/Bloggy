package com.zyq.bloggy.serivce.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

public class d {
    public static void main(String[] args) throws ParseException {

    }

    void generate() {

        File file = new File("D:\\a.sql");
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            int j = 0;
            Long begin = System.currentTimeMillis();
            for (int i = 0; i < 30000; i++) {
                if (j >= 1000) {
                    j--;
                } else {
                    j++;
                }
                bw.write(String.format("insert into tb_reply_comment(reply_comment_id,comment_id,user_id,type,content,like_num,status,create_time)" +
                        "values(%d,%d,%d,%d,\"%s\",%d,%d,now());\n", IdWorker.getId(), i, j, 1, "测试二级评论", 0, 1));
            }
            bw.close();
            fw.close();
            System.out.println((System.currentTimeMillis() - begin));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int j = 0;
        Byte[] bytes = new Byte[1024];
        for (int i = 0; i < 30000; i++) {
            if (j >= 1000) {
                j--;
            } else {
                j++;
            }
        }
    }
}
