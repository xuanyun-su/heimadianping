package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;

import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getList() {
        List<String> range = stringRedisTemplate.opsForList().range("shopTypes", 0, -1);
        if (range != null && !range.isEmpty()) {
            List<ShopType> shopTypes = new ArrayList<>();
            for (String json : range) {
                ShopType bean = JSONUtil.toBean(json, ShopType.class);
                shopTypes.add(bean);
            }
            return  Result.ok(shopTypes);
        } else {
            List<ShopType> list = query().orderByAsc("sort").list();
            if (list != null && !list.isEmpty()) {
                List<String> jsonList = new ArrayList<>();
                for (ShopType shopType : list) {
                    String json = JSONUtil.toJsonStr(shopType);
                    jsonList.add(json);
                }
                // 存储JSON字符串到Redis列表
                stringRedisTemplate.opsForList().rightPushAll("shopTypes", jsonList);
            }
            return Result.ok(list);
        }
    }
}
