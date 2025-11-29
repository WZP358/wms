package com.ruoyi.system.controller.common;

import com.ruoyi.common.web.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件访问控制器
 * 用于提供上传文件的访问
 *
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class FileController extends BaseController {

    /**
     * 访问上传的文件
     *
     * @param request HTTP请求
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        // 立即记录日志，确认请求是否到达
        log.warn("========== FileController 收到请求 ==========");
        try {
            // 获取请求路径（包含context-path）
            String requestURI = request.getRequestURI();
            // 获取context-path
            String contextPath = request.getContextPath();
            // 移除context-path
            String requestPath = requestURI;
            if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
                requestPath = requestURI.substring(contextPath.length());
            }
            
            log.info("收到文件访问请求: URI={}, ContextPath={}, 处理后路径={}", requestURI, contextPath, requestPath);
            
            // 移除 /upload 前缀
            String filePath = requestPath.replaceFirst("/upload/?", "");
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            
            // 构建文件路径
            String userDir = System.getProperty("user.dir");
            // 如果工作目录不是backend目录，则使用backend/upload/，否则直接使用upload/
            File file;
            File uploadDir;
            if (userDir.endsWith("backend")) {
                file = new File(userDir, "upload/" + filePath);
                uploadDir = new File(userDir, "upload");
            } else {
                file = new File(userDir, "backend/upload/" + filePath);
                uploadDir = new File(userDir, "backend/upload");
            }
            
            log.info("查找文件: 请求URI={}, 文件路径={}, 绝对路径={}, 文件存在={}", 
                    requestURI, filePath, file.getAbsolutePath(), file.exists());
            
            if (!file.exists()) {
                log.warn("文件不存在: {}, 父目录是否存在: {}", file.getAbsolutePath(), file.getParentFile().exists());
                // 列出目录内容以便调试
                File parentDir = file.getParentFile();
                if (parentDir.exists() && parentDir.isDirectory()) {
                    File[] files = parentDir.listFiles();
                    log.warn("目录 {} 中的文件: {}", parentDir.getAbsolutePath(), 
                            files != null ? java.util.Arrays.toString(java.util.Arrays.stream(files).map(File::getName).toArray()) : "null");
                }
                return ResponseEntity.notFound().build();
            }
            
            if (!file.isFile()) {
                log.warn("路径不是文件: {}", file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            // 检查文件是否在upload目录下（安全验证）
            String fileCanonicalPath = file.getCanonicalPath();
            String uploadCanonicalPath = uploadDir.getCanonicalPath();
            
            if (!fileCanonicalPath.startsWith(uploadCanonicalPath)) {
                log.warn("非法文件访问: {}", fileCanonicalPath);
                return ResponseEntity.status(403).build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 获取文件扩展名，设置Content-Type
            String filename = file.getName();
            String extension = "";
            int lastDotIndex = filename.lastIndexOf(".");
            if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
                extension = filename.substring(lastDotIndex + 1).toLowerCase();
            }
            MediaType mediaType = getMediaType(extension);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + 
                            URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("访问文件失败", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 根据文件扩展名获取MediaType
     */
    private MediaType getMediaType(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            case "svg" -> MediaType.parseMediaType("image/svg+xml");
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            case "html" -> MediaType.TEXT_HTML;
            case "css" -> MediaType.parseMediaType("text/css");
            case "js" -> MediaType.parseMediaType("application/javascript");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}

