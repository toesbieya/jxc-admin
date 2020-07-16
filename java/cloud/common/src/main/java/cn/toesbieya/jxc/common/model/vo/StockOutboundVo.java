package cn.toesbieya.jxc.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutboundVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private BigDecimal num;
}
