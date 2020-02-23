package com.toesbieya.my.model.vo.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class SellOutboundExport{
    @ExcelProperty(value="单号",index=0)
    @ColumnWidth(20)
    private String id;

    @ExcelProperty(value="销售订单单号",index=1)
    @ColumnWidth(20)
    private String pid;

    @ExcelProperty(value="创建人",index=2)
    @ColumnWidth(20)
    private String cname;

    @ExcelProperty(value="创建时间",index=3)
    @ColumnWidth(20)
    private String ctime;

    @ExcelProperty(value="审核人",index=4)
    @ColumnWidth(20)
    private String vname;

    @ExcelProperty(value="审核时间",index=5)
    @ColumnWidth(20)
    private String vtime;

    @ExcelProperty(value="状态",index=6)
    @ColumnWidth(20)
    private String status;

    @ExcelProperty(value="备注",index=7)
    @ColumnWidth(50)
    private String remark;
}
