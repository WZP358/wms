package com.ruoyi.system.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.file.MimeTypeUtils;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.system.domain.bo.SysUserBo;
import com.ruoyi.system.domain.bo.SysUserProfileBo;
import com.ruoyi.system.domain.vo.ProfileVo;
import com.ruoyi.system.domain.vo.SysUserVo;
import com.ruoyi.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author Lion Li
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    private final SysUserService userService;

    /**
     * 个人信息
     */
    @GetMapping
    public R<ProfileVo> profile() {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        ProfileVo profileVo = new ProfileVo();
        profileVo.setUser(user);
        profileVo.setRoleGroup(userService.selectUserRoleGroup(user.getUserId()));
        return R.ok(profileVo);
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateProfile(@Validated @RequestBody SysUserProfileBo profile) {
        SysUserBo user = BeanUtil.toBean(profile, SysUserBo.class);
        user.setUserId(LoginHelper.getUserId());
        String username = LoginHelper.getUsername();
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + username + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + username + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(user) > 0) {
            return R.ok();
        }
        return R.fail("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     *
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(String oldPassword, String newPassword) {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        String password = user.getPassword();
        if (StringUtils.equals(oldPassword,newPassword)) {
            return R.fail("新密码不能与旧密码相同");
        }
        if (!BCrypt.checkpw(oldPassword, password)) {
            return R.fail("修改密码失败，旧密码错误");
        }
        if (userService.resetUserPwd(user.getUserId(), BCrypt.hashpw(newPassword)) > 0) {
            return R.ok();
        }
        return R.fail("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     *
     * @param avatarfile 用户头像
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Map<String, Object>> avatar(@RequestPart("avatarfile") MultipartFile avatarfile) {
        log.info("开始上传头像，文件名: {}, 大小: {} bytes", avatarfile.getOriginalFilename(), avatarfile.getSize());
        if (!avatarfile.isEmpty()) {
            String extension = FileUtil.extName(avatarfile.getOriginalFilename());
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            try {
                // 使用本地文件存储，不依赖OSS
                String avatar = uploadAvatarToLocal(avatarfile);
                log.info("头像上传成功，保存路径: {}", avatar);
                if (userService.updateUserAvatar(LoginHelper.getUsername(), avatar)) {
                    log.info("头像URL已更新到数据库: {}", avatar);
                    return R.ok(Map.of("imgUrl", avatar));
                } else {
                    log.error("更新头像URL到数据库失败");
                    return R.fail("更新头像失败，请联系管理员");
                }
            } catch (Exception e) {
                log.error("上传头像异常", e);
                return R.fail("上传图片异常：" + e.getMessage());
            }
        }
        return R.fail("上传图片异常，文件为空");
    }

    /**
     * 上传头像到本地文件系统
     *
     * @param file 头像文件
     * @return 访问URL
     */
    private String uploadAvatarToLocal(MultipartFile file) throws Exception {
        // 获取文件扩展名
        String extension = FileUtil.extName(file.getOriginalFilename());
        if (StringUtils.isBlank(extension)) {
            extension = "png";
        }
        
        // 生成文件名：用户ID_时间戳.扩展名
        String fileName = LoginHelper.getUserId() + "_" + System.currentTimeMillis() + "." + extension;
        log.info("生成文件名: {}", fileName);
        
        // 头像存储目录：backend/upload/avatar/
        String userDir = System.getProperty("user.dir");
        log.info("当前工作目录: {}", userDir);
        // 如果工作目录不是backend目录，则使用backend/upload/avatar
        File uploadDir;
        if (userDir.endsWith("backend")) {
            uploadDir = new File(userDir, "upload/avatar");
        } else {
            uploadDir = new File(userDir, "backend/upload/avatar");
        }
        log.info("头像存储目录: {}", uploadDir.getAbsolutePath());
        
        // 创建目录（如果不存在）
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("创建目录: {}, 结果: {}", uploadDir.getAbsolutePath(), created);
            if (!created) {
                throw new Exception("无法创建上传目录: " + uploadDir.getAbsolutePath());
            }
        }
        
        // 检查目录是否可写
        if (!uploadDir.canWrite()) {
            throw new Exception("上传目录不可写: " + uploadDir.getAbsolutePath());
        }
        
        // 保存文件
        File destFile = new File(uploadDir, fileName);
        log.info("准备保存文件到: {}", destFile.getAbsolutePath());
        file.transferTo(destFile);
        
        // 验证文件是否保存成功
        if (!destFile.exists() || destFile.length() == 0) {
            throw new Exception("文件保存失败，文件不存在或大小为0");
        }
        log.info("文件保存成功，大小: {} bytes, 路径: {}", destFile.length(), destFile.getAbsolutePath());
        
        // 返回访问URL：/upload/avatar/文件名
        return "/upload/avatar/" + fileName;
    }
}
