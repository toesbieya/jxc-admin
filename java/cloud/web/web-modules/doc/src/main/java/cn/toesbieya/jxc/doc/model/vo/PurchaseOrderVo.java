package cn.toesbieya.jxc.doc.model.vo;

import cn.toesbieya.jxc.common.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.common.model.entity.BizPurchaseOrderSub;
import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PurchaseOrderVo extends BizPurchaseOrder {
    private List<BizPurchaseOrderSub> data;
    private List<RecAttachment> imageList;
    private List<RecAttachment> uploadImageList;
    private List<String> deleteImageList;

    public PurchaseOrderVo(BizPurchaseOrder parent) {
        BeanUtils.copyProperties(parent, this);
    }
}
