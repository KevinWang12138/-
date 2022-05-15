package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import com.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * MD5加密处理
         */
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        /**
         * 查数据库
         */
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);
        /**
         * 检查数据
         */
        if(emp==null){
            return R.error("用户名错误");
        }
        /**
         * 密码比对
         */
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        /**
         * 查看员工状态
         */
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        /**
         * 用户id放到session中
         */
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        /**
         * 清理session
         */
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping("")
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        /**
         * 设置初始密码
         */
        String password="123456";
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        Long empid=(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        /**
         * 分页查询
         */
        Page pageInfo=new Page(page,pageSize);
        /**
         * 条件查询
         */
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        /**
         * 排序条件
         */
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        /**
         * 执行查询
         */
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 修改员工状态
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long id=(Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(id);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee=employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }else{
            return R.error("没有查询到对应员工信息");
        }
    }
}
