package com.example.springproject.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "releases")
public class Release implements Serializable {
    @TableId(value = "assets_url")
    private String assets_url;
    @TableField(value = "created_at")
    private Date created_at;
    @TableField(value = "published_at")
    private Date published_at;

    @TableField(value = "name")
    private String name;

    public Release(String assets_url, Date created_at, Date published_at, String name) {
        this.assets_url = assets_url;
        this.created_at = created_at;
        this.published_at = published_at;
        this.name = name;
    }
}
