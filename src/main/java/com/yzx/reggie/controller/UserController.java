package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yzx.reggie.common.CustomException;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.User;
import com.yzx.reggie.service.IUserService;
import com.yzx.reggie.utils.SMSUtils;
import com.yzx.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Value("${MyMail.templateCode}")
    private String templateCode;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession) {
        String phone = user.getPhone();
        String mail = user.getMail();
        //随机生成4/6位验证码
        String code = ValidateCodeUtils.generateValidateCode4String(6);
        log.info(code);
        if (StringUtils.isNotBlank(phone)) {
            //TODO: 调用阿里云短信服务
            return R.success("短信发送成功");
        } else if (StringUtils.isNotBlank(mail)) {
//            SMSUtils.sendMessage(templateCode, mail, code);
            httpSession.setAttribute(mail, code);
            return R.success("短信发送成功");
        } else {
            throw new CustomException("手机号或邮箱有误,发送失败");
        }
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession) {
        String account = (String) map.get("account");
        String code = (String) map.get("code");
        Object codeInSession = httpSession.getAttribute(account);
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> lwq = new LambdaQueryWrapper<>();
            lwq.eq(User::getPhone, account)
                    .or().eq(User::getMail, account);
            User user = userService.getOne(lwq);
            if (user == null) {
                user = new User();
                String regx = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
                if (account.matches(regx)) {
                    user.setMail(account);
                } else {
                    user.setPhone(account);
                }
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
