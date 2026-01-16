package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishDTO){
        dishService.addDish(dishDTO);
        String pattern = "dish_" + dishDTO.getCategoryId();
        cleanCache(pattern);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> dishPageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品查询：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.dishPageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.deleteByIds(ids);
        String pattern = "dish_*";
        cleanCache(pattern);
        return Result.success();

    }

    @GetMapping("/{id}")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id){
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result<String> updateByIdWithFlavor(@RequestBody DishDTO dishDTO){
        dishService.updateByIdWithFlavor(dishDTO);
        String pattern = "dish_*";
        cleanCache(pattern);
        return Result.success();
    }

    private void cleanCache(String pattern){
        Set key = redisTemplate.keys(pattern);
        redisTemplate.delete(key);
    }
}
