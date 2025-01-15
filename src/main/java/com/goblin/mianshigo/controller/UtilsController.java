package com.goblin.mianshigo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goblin.mianshigo.annotation.AuthCheck;
import com.goblin.mianshigo.common.BaseResponse;
import com.goblin.mianshigo.common.DeleteRequest;
import com.goblin.mianshigo.common.ErrorCode;
import com.goblin.mianshigo.common.ResultUtils;
import com.goblin.mianshigo.config.WxOpenConfig;
import com.goblin.mianshigo.constant.UserConstant;
import com.goblin.mianshigo.exception.BusinessException;
import com.goblin.mianshigo.exception.ThrowUtils;
import com.goblin.mianshigo.model.dto.user.*;
import com.goblin.mianshigo.model.entity.User;
import com.goblin.mianshigo.model.vo.LoginUserVO;
import com.goblin.mianshigo.model.vo.UserVO;
import com.goblin.mianshigo.service.UserService;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.goblin.mianshigo.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 *
 
 */
@RestController
@RequestMapping("/utils")
@Slf4j
public class UtilsController {

    /**
     * 移除热点缓存
     *
     * @param key
     * @return
     */
    @PostMapping("/hotkey/remove")
    public BaseResponse<Boolean> removeHotKey(String key) {
        JdHotKeyStore.remove(key);
        return ResultUtils.success(true);
    }


}
