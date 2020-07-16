package cn.toesbieya.jxc.common.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BizPurchaseInbound extends BizDoc {
    private String pid;

    public BizPurchaseInbound(BizPurchaseInbound obj) {
        super(obj);
        this.pid = obj.getPid();
    }
}
