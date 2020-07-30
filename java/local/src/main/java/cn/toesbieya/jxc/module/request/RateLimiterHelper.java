package cn.toesbieya.jxc.module.request;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;

@Data
public class RateLimiterHelper {
    private RateLimiter limiter;
    private int ipRate;

    public RateLimiterHelper(int totalRate, int ipRate) {
        this.limiter = RateLimiter.create(totalRate);
        this.ipRate = ipRate;
    }
}
