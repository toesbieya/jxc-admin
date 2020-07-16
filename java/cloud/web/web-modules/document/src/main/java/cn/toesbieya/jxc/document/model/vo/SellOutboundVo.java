package cn.toesbieya.jxc.document.model.vo;

import cn.toesbieya.jxc.common.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.common.model.entity.BizSellOutboundSub;
import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SellOutboundVo extends BizSellOutbound {
    private List<BizSellOutboundSub> data;
    private List<RecAttachment> imageList;
    private List<RecAttachment> uploadImageList;
    private List<String> deleteImageList;

    public SellOutboundVo(BizSellOutbound parent) {
        super(parent);
    }
}
