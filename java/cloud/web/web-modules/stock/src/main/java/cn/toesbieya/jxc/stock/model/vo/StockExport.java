package cn.toesbieya.jxc.stock.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockExport {
    @ExcelIgnore
    private int cid;

    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(20)
    private int id;

    @ExcelProperty(value = "分类名称", index = 1)
    @ColumnWidth(20)
    private String cname;

    @ExcelProperty(value = "库存总数", index = 2)
    @ColumnWidth(20)
    private BigDecimal totalNum;

    @ExcelProperty(value = "库存总值", index = 3)
    @ColumnWidth(20)
    private BigDecimal totalPrice;

    @ExcelProperty(value = "采购订单号", index = 4)
    @ColumnWidth(20)
    private String cgddid;

    @ExcelProperty(value = "采购单价", index = 5)
    @ColumnWidth(20)
    private BigDecimal cgPrice;

    @ExcelProperty(value = "采购数量", index = 6)
    @ColumnWidth(20)
    private BigDecimal cgNum;

    @ExcelProperty(value = "采购入库单号", index = 7)
    @ColumnWidth(20)
    private String cgrkid;

    @ExcelProperty(value = "入库时间", index = 8)
    @ColumnWidth(20)
    private String ctime;

    @ExcelProperty(value = "入库数量", index = 9)
    @ColumnWidth(20)
    private BigDecimal rkNum;
}
