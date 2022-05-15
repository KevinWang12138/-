package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl  extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，查询是否有关联的菜品/套餐
     * 如果关联，抛出异常
     */
    @Override
    public void remove(Long ids) {
        //菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1=dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            /**
             * 关联了菜品
             * 抛出异常
             */
            throw new CustomException("关联了菜品，不能删除");
        }

        //套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2=setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            /**
             * 关联了套餐
             * 抛出异常
             */
            throw new CustomException("关联了套餐，不能删除");
        }
        /**
         * 判断结束，正常删除分类即可
         */
        super.removeById(ids);
    }
}
