package com.zyq.bloggy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStateVo {
    private Integer beLikeCount;
    private Integer viewCount;
    private Integer articleCount;
    private Integer sortCount;
    private Integer likeCount;
    private Integer commentCount;
}
