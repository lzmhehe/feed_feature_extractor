package com.dishan.ffe.demo;

/**
 * @author liyong
 * @version 1.0
 * @date 2022/3/8 15:07
 * @Copyright Ⓒ 2021-2022 北京数势云创科技有限公司
 * @since 1.0
 */

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dishan.ffe.demo.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理参数异常，一般用于校验body参数
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handleValidationBodyException(MethodArgumentNotValidException e) {
        for (ObjectError s : e.getBindingResult().getAllErrors()) {
            return new ErrorMessage("Invalid_Request_Parameter", s.getObjectName() + ": " + s.getDefaultMessage());
        }
        return new ErrorMessage("Invalid_Request_Parameter", "未知参数错误");
    }

    /**
     * 主动throw的异常
     *
     * @param e
     * @param response
     * @return
     */
    @ExceptionHandler(Exception.class)//如果系统抛出了ServiceException异常,此方法会进行拦截
    public ErrorMessage handleUnProccessableServiceException(Exception e, HttpServletResponse response) {
        System.out.println("进入了全局异常处理类");
        return new ErrorMessage(e.getMessage(), e.getMessage());
    }
}
