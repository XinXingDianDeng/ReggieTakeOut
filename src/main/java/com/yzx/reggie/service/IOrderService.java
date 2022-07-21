package com.yzx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.entity.Orders;

public interface IOrderService extends IService<Orders> {

    void submit(Orders orders);
}
