package com.area.areaUtil.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.area.areaUtil.dao.SysAreaMapper;
import com.area.areaUtil.po.TSysArea;
import com.area.areaUtil.service.SysAreaService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 地区表Service接口实现。
 *
 * @author shuai.luo
 * @version v1.0.0
 */
@Primary
@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, TSysArea> implements SysAreaService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSysArea(MultipartFile file) {
        // 验证文件是否为空
        if (!file.isEmpty()) {
            // 获取文件流
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<TSysArea> tSysAreas = new ArrayList<>();
            // 读取Excel中名为 导入材料清单 的sheet内容
            if (null != inputStream) {
                // 读取文件数据
                ExcelReader excelReader = ExcelUtil.getReader(inputStream);
                List<List<Object>> read = excelReader.read(0, excelReader.getRowCount());
                for (List<Object> objects : read) {
                    String code = objects.get(0).toString();
                    TSysArea tSysArea = new TSysArea();
                    if (code.endsWith("0000")) {
                        tSysArea.setAreaCode(code);
                        tSysArea.setAreaValue(objects.get(1).toString());
                        tSysArea.setAreaLevel(1);
                        tSysArea.setIsCanUse(1);
                    } else if (code.endsWith("00")) {
                        tSysArea.setAreaCode(code);
                        tSysArea.setAreaValue(objects.get(1).toString());
                        tSysArea.setParentCode(code.substring(0, 2) + "0000");
                        tSysArea.setAreaLevel(2);
                        tSysArea.setIsCanUse(1);
                    } else {
                        // 如果找不到父节点，则需要新建一个父节点
                        HashMap<String, Object> sysAreaMap = new HashMap<>(1);
                        sysAreaMap.put("area_code", code.substring(0, 4) + "00");
                        if (this.selectByMap(sysAreaMap).isEmpty()) {
                            sysAreaMap = new HashMap<>(1);
                            sysAreaMap.put("area_code", code.substring(0, 2) + "0000");
                            TSysArea tSysArea1 = this.selectByMap(sysAreaMap).get(0);
                            TSysArea tSysArea2 = new TSysArea();
                            tSysArea2.setAreaCode(code.substring(0, 4) + "00");
                            tSysArea2.setAreaValue(tSysArea1.getAreaValue() + "直辖行政单位");
                            tSysArea2.setParentCode(code.substring(0, 2) + "0000");
                            tSysArea2.setAreaLevel(2);
                            tSysArea2.setIsCanUse(0);
                            tSysArea2.setCreateId(1);
                            tSysArea2.setModifiedId(1);
                            tSysArea2.setVersion(0);
                            tSysArea2.setIsDeleted(0);
                            tSysArea2.insert();
                        }

                        tSysArea.setAreaCode(code);
                        tSysArea.setAreaValue(objects.get(1).toString());
                        tSysArea.setParentCode(code.substring(0, 4) + "00");
                        tSysArea.setAreaLevel(3);
                        tSysArea.setIsCanUse(1);
                    }
                    tSysArea.setCreateId(1);
                    tSysArea.setModifiedId(1);
                    tSysArea.setVersion(0);
                    tSysArea.setIsDeleted(0);
                    this.insert(tSysArea);
                }
            }
        }
    }
}