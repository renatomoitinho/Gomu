package com.app.start.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 07:48
 */
public interface CommonService extends Services{
    void execute(HttpServletRequest request, HttpServletResponse response) ;
}
