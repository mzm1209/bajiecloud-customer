package com.bajiezu.cloud.customer.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.labelvo.LabelListReqVO;
import com.bajiezu.cloud.customer.controller.labelvo.LabelRespVO;
import com.bajiezu.cloud.excel.export.AbstractExportService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LabelExportService extends AbstractExportService {

    @Autowired
    private LabelService labelService;

    @Override
    protected String generateFileName() {
        return "标签_" + System.currentTimeMillis() + EXPORT_FILE_TYPE;
    }

    @Override
    protected String createExportFile(String s, Object o) {
        log.info("开始导出标签,params:{}", o);
        int pageNo = 1;

        LabelListReqVO reqVO = (LabelListReqVO) o;
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(100);
        PageResult<LabelRespVO> pageResult = labelService.list(reqVO);
        List<LabelRespVO> respVOList = Lists.newArrayList();
        while (CollectionUtil.isNotEmpty(pageResult.getList())) {
            pageNo++;
            reqVO.setPageNo(pageNo);
            pageResult = labelService.list(reqVO);
        }
        String filePath = EXPORT_DIR + s;
        String sheetName = "标准商品";
        ExcelWriter excelWriter = EasyExcelFactory.write(filePath, LabelRespVO.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
        excelWriter.write(respVOList, writeSheet);
        excelWriter.finish();
        return filePath;
    }
}
