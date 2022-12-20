package com.example.springproject.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springproject.domain.Contributor;
import com.example.springproject.mapper.ContributorMapper;
import icu.mhb.mybatisplus.plugln.base.service.impl.JoinServiceImpl;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContributorServiceImpl extends JoinServiceImpl<ContributorMapper, Contributor> implements ContributorService{
    @Autowired
    private ContributorMapper contributorMapper;

    @Override
    public int add(Contributor contributor) {
        return contributorMapper.insert(contributor);
    }

    @Override
    public int edit(Contributor contributor) {
        QueryWrapper<Contributor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", contributor.getId());
        return contributorMapper.update(contributor, queryWrapper);
    }

    @Override
    public Contributor query(Long id) {
        QueryWrapper<Contributor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return contributorMapper.selectOne(queryWrapper);
    }

    @Override
    public int del(Long id) {
        QueryWrapper<Contributor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return contributorMapper.delete(queryWrapper);
    }

    @Override
    public List<JSONObject> topContributions() {
        List<JSONObject> jsonArray = new ArrayList<>();
        QueryWrapper<Contributor> wrapper = new QueryWrapper<>();
        wrapper.select("id, login, contributions, avatar_url, html_url");
        wrapper.orderByDesc("contributions");

        List<Map<String, Object>> topContributionsList = contributorMapper.selectMaps(wrapper);
        for (Map<String, Object> contribution: topContributionsList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", contribution.get("id"));
            jsonObject.put("login", contribution.get("login"));
            jsonObject.put("contributions", contribution.get("contributions"));
            jsonObject.put("avatar_url", contribution.get("avatar_url"));
            jsonObject.put("html_url", contribution.get("html_url"));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public List<Contributor> queryAll() {
        return contributorMapper.selectList(null);
    }
}
