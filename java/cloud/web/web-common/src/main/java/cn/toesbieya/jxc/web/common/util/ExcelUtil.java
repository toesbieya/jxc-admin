package cn.toesbieya.jxc.web.common.util;

import cn.toesbieya.jxc.common.model.vo.R;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class ExcelUtil {
    private static final WriteCellStyle defaultCellStyle = new WriteCellStyle();

    static {
        defaultCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        defaultCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    public static void exportSimply(
            List list,
            HttpServletResponse response,
            String filename) throws Exception {
        export(list, response, filename, null);
    }

    public static void export(
            List list,
            HttpServletResponse response,
            String filename,
            CommonMergeOptions mergeOptions) throws Exception {
        String name = URLEncoder.encode(filename, "utf-8") + ".xlsx";
        response.setHeader("Content-disposition", "attachment;filename=" + name);
        response.setCharacterEncoding("utf-8");

        int size = list.size();
        if (size == 0) {
            WebUtil.responseJson(response, R.fail("当前没有能导出的数据"));
            return;
        }
        else if (size < 10000) {
            WebUtil.responseJson(response, R.success(list));
            return;
        }

        response.setContentType("application/vnd.ms-excel");

        Class clazz = list.get(0).getClass();

        ExcelWriterBuilder writerBuilder = EasyExcel.write(response.getOutputStream(), clazz);

        writerBuilder.registerWriteHandler(getDefaultStyle());

        if (mergeOptions != null) {
            CommonMerge commonMerge = new CommonMerge(list, clazz, mergeOptions);
            writerBuilder.registerWriteHandler(commonMerge);
        }

        writerBuilder.sheet(0, "Sheet1").doWrite(list);
    }

    private static AbstractVerticalCellStyleStrategy getDefaultStyle() {
        return new AbstractVerticalCellStyleStrategy() {
            @Override
            protected WriteCellStyle headCellStyle(Head head) {
                return defaultCellStyle;
            }

            @Override
            protected WriteCellStyle contentCellStyle(Head head) {
                return defaultCellStyle;
            }
        };
    }

    @Data
    @AllArgsConstructor
    public static class CommonMergeOptions {
        private String[] attrs;
        private String primaryKey;
        private String orderKey;
    }


    private static class CommonMerge extends AbstractMergeStrategy {
        private boolean finish = false;
        private int ignoreRows = 1;
        private final Field[] attrs;
        private final Field primaryField;
        private final int[][] merge;
        private final int[] temp;

        public CommonMerge(List data, Class clazz, CommonMergeOptions mergeOptions) throws Exception {
            String[] attrs = mergeOptions.getAttrs();
            String primaryKey = mergeOptions.getPrimaryKey();
            String orderKey = mergeOptions.getOrderKey();

            int length = data.size();
            this.attrs = new Field[attrs.length];
            this.merge = new int[attrs.length][];
            for (int i = 0; i < attrs.length; i++) {
                if (StringUtils.isEmpty(attrs[i])) continue;
                Field field = clazz.getDeclaredField(attrs[i]);
                field.setAccessible(true);

                //获取easyExcel的注解，根据值来确定字段在excel中的位置
                ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                int colIndex = property == null ? i : property.index();
                //当未标明index时，使用循环中的顺序
                if (colIndex == -1) colIndex = i;

                this.attrs[colIndex] = field;

                //初始化merge
                int[] a = new int[length];
                a[0] = 1;
                this.merge[i] = a;
            }
            Field primaryField = clazz.getDeclaredField(primaryKey);
            primaryField.setAccessible(true);
            this.primaryField = primaryField;
            Field orderField = clazz.getDeclaredField(orderKey);
            orderField.setAccessible(true);
            this.temp = new int[attrs.length];
            Arrays.fill(this.temp, 1);

            //序号从1开始
            int indexAfterMerge = 1;
            orderField.set(data.get(0), indexAfterMerge);
            for (int i = 1; i < length; i++) {
                Object currentRow = data.get(i);
                Object lastRow = data.get(i - 1);
                Object nextRow = i == length - 1 ? null : data.get(i + 1);

                //重写序号
                if (primaryField.get(currentRow).equals(primaryField.get(lastRow))) {
                    orderField.set(currentRow, orderField.get(lastRow));
                }
                else orderField.set(currentRow, ++indexAfterMerge);
                if (nextRow != null && primaryField.get(currentRow).equals(primaryField.get(nextRow))) {
                    orderField.set(nextRow, orderField.get(currentRow));
                }

                for (int colIndex = 0; colIndex < this.attrs.length; colIndex++) {
                    if (this.attrs[colIndex] == null) continue;
                    compare(currentRow, lastRow, nextRow, i, colIndex);
                }
            }
        }

        private void compare(Object currentRow, Object lastRow, Object nextRow, int rowIndex, int colIndex) throws Exception {
            Field attr = this.attrs[colIndex];
            //若与上一行的primaryKey相同且属性相同，则temp对应项+1
            if (attr.get(currentRow).equals(attr.get(lastRow))
                    && attr.get(currentRow).equals(attr.get(lastRow))) {
                this.temp[colIndex]++;
                //最后一行特殊处理
                if (nextRow == null) {
                    this.merge[colIndex][rowIndex] = this.temp[colIndex];
                }
                else {
                    //若与下一行相同
                    if (primaryField.get(currentRow).equals(primaryField.get(nextRow))
                            && attr.get(currentRow).equals(attr.get(nextRow))) {
                        this.merge[colIndex][rowIndex] = 1;
                    }
                    //否则存入
                    else this.merge[colIndex][rowIndex] = this.temp[colIndex];
                }
            }
            //否则清零
            else {
                this.temp[colIndex] = 1;
                this.merge[colIndex][rowIndex] = 1;
            }
        }

        private void addMergedRegion(Sheet sheet, int ignoreRows) {
            for (int colIndex = 0; colIndex < merge.length; colIndex++) {
                int[] rowspanArr = merge[colIndex];
                for (int rowIndex = 0; rowIndex < rowspanArr.length; rowIndex++) {
                    int rowspan = rowspanArr[rowIndex];
                    if (rowspan > 1) {
                        sheet.addMergedRegion(
                                new CellRangeAddress(
                                        ignoreRows + rowIndex - (rowspan - 1),
                                        ignoreRows + rowIndex,
                                        colIndex,
                                        colIndex
                                )
                        );
                    }
                }
            }
        }

        public CommonMerge ignore(int rows) {
            this.ignoreRows = rows;
            return this;
        }

        @Override
        protected void merge(Sheet sheet, Cell cell, Head head, Integer rowIndex) {
            if (!finish) {
                addMergedRegion(sheet, ignoreRows);
                finish = true;
            }
        }
    }
}
