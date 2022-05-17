package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService  extends IService<Setmeal> {
    /**
     *  保存套餐，同时也保存套餐和菜品的联系
     */
    void saveWithDish(SetmealDto setmealDto);
    /**
     * 删除套餐，同时也删除套餐和菜品的联系
     */
    void deleteWithDish(List<Long> ids);
}
