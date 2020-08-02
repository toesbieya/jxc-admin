package cn.toesbieya.jxc.doc.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class PurchaseOrderExport {
    @ExcelProperty(value = "单号", index = 0)
    @ColumnWidth(20)
    private String id;

    @ExcelProperty(value = "供应商", index = 1)
    @ColumnWidth(30)
    private String sname;

    @ExcelProperty(value = "创建人", index = 2)
    @ColumnWidth(20)
    private String cname;

    @ExcelProperty(value = "创建时间", index = 3)
    @ColumnWidth(20)
    private String ctime;

    @ExcelProperty(value = "审核人", index = 4)
    @ColumnWidth(20)
    private String vname;

    @ExcelProperty(value = "审核时间", index = 5)
    @ColumnWidth(20)
    private String vtime;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(20)
    private String status;

    @ExcelProperty(value = "完成情况", index = 7)
    @ColumnWidth(20)
    private String finish;

    @ExcelProperty(value = "完成时间", index = 8)
    @ColumnWidth(20)
    private String ftime;

    @ExcelProperty(value = "总额", index = 9)
    @ColumnWidth(20)
    private String total;

    @ExcelProperty(value = "备注", index = 10)
    @ColumnWidth(50)
    private String remark;
}
