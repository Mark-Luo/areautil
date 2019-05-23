package com.area.areaUtil.controller;


import com.area.areaUtil.service.SysAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "行政地区管理接口")
@RequestMapping(path = "/areaUtil/sysArea", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Log4j2
public class SysAreaController {

    @Autowired
    private SysAreaService sysAreaService;

    @ApiOperation(nickname = "initSysArea", value = "Excel导入初始化地区表", httpMethod = "POST")
    @PostMapping(value = "/initSysArea", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void initSysArea(@RequestBody MultipartFile file) {
         this.sysAreaService.initSysArea(file);
    }
}