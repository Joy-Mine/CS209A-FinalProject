package com.example.springproject.service;

import com.example.springproject.domain.Contributor;
import icu.mhb.mybatisplus.plugln.base.service.JoinIService;
import net.minidev.json.JSONObject;

import java.util.List;

public interface ContributorService extends JoinIService<Contributor> {

    public int add (Contributor contributor);

    public int edit (Contributor contributor);

    public Contributor query(Long id);

    public int del (Long id);

    public List<JSONObject> topContributions();

    public List<Contributor> queryAll();


}
