package com.area.areaUtil.service;

import com.area.areaUtil.po.TSysArea;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 地区表Service接口。
 *
 * @author shuai.luo
 * @version v1.0.0
 */
public interface SysAreaService extends IService<TSysArea> {

    void initSysArea(MultipartFile file);
}