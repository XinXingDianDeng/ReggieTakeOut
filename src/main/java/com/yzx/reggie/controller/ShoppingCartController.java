package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.ShoppingCart;
import com.yzx.reggie.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> lwq = new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart cart = shoppingCartService.getOne(lwq);
        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shoppingCartService.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }
        return R.success(cart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> lwq = new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId, userId).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> carts = shoppingCartService.list(lwq);
        return R.success(carts);
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> lwq = new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(lwq);
        return R.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map map, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        Object dishId = map.get("dishId");
        Object setmealId = map.get("setmealId");
//        ShoppingCart shoppingCart = new ShoppingCart();

        LambdaQueryWrapper<ShoppingCart> lwq = new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId, userId);
        if (dishId != null) {
            lwq.eq(dishId != null, ShoppingCart::getDishId, dishId);
        } else {
            lwq.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart cart = shoppingCartService.getOne(lwq);
        Integer number = cart.getNumber();
        if (number > 1) {
            cart.setNumber(number - 1);
            shoppingCartService.updateById(cart);
        } else {
            shoppingCartService.removeById(cart);
        }
        return R.success("减少数量成功");
    }
}
