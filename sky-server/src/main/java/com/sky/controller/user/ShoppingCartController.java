package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> selectAll(){
        List<ShoppingCart> list = shoppingCartService.selectAll();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result deleteAll(){
        shoppingCartService.deleteAll();
        return Result.success();
    }

    @DeleteMapping("sub")
    public Result deletesub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.deleteSub(shoppingCartDTO);
        return Result.success();
    }
}
