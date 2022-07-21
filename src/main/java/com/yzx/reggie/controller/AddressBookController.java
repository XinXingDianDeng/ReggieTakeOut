package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.AddressBook;
import com.yzx.reggie.service.IAddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpSession httpSession) {
        Object userID = httpSession.getAttribute("user");
        LambdaQueryWrapper<AddressBook> lwq = new LambdaQueryWrapper<>();
        lwq.eq(AddressBook::getUserId, userID).orderByDesc(AddressBook::getIsDefault);
        List<AddressBook> addressBooks = addressBookService.list(lwq);
        return R.success(addressBooks);
    }

    @PutMapping("/default")
    @Transactional
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        //更改原先默认地址为0
        UpdateWrapper<AddressBook> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.eq("is_default", 1).set("is_default", 0);
        addressBookService.update(new AddressBook(), updateWrapper1);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("更改默认地址成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> lwq = new LambdaQueryWrapper<>();
        lwq.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(lwq);
        if (addressBook == null) {
            return R.error("没有默认地址");
        }
        return R.success(addressBook);
    }

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpSession httpSession) {
        Long user = (Long) httpSession.getAttribute("user");
        addressBook.setUserId(user);
        addressBookService.save(addressBook);
        return R.success("地址增加成功");
    }

    @GetMapping("/{id}")
    public R<AddressBook> getOne(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            return R.error("没有找到该地址");
        }
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("修改地址成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        addressBookService.removeByIds(ids);
        return R.success("删除地址成功");
    }
}
