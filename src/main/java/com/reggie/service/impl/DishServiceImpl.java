package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl  extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    //新增菜品，同时保存口味
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品
        this.save(dishDto);
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存口味
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据Id查询菜品和口味
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish=this.getById(id);
        //创建返回对象
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //提取口味信息
        List<DishFlavor> list=dishFlavorService.list(queryWrapper);
        //赋值口味信息
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);
        //清理当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前菜品新加的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }
}
