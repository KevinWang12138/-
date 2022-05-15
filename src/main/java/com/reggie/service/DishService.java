package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService  extends IService<Dish> {
    //新增菜品，同时增加口味
    void saveWithFlavor(DishDto dishDto);
}
