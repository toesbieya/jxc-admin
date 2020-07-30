package cn.toesbieya.jxc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("异步线程执行失败。方法：[{}],异常信息[{}] : ", method, ex.getMessage(), ex);
        };
    }

    //定时任务线程池
    @Bean
    public Executor scheduledExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(2);
        //是否回收空闲线程
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(5);
        //配置等待队列大小
        threadPoolTaskExecutor.setQueueCapacity(2);
        //配置线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix("scheduled-");
        //拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    //数据库插入线程池
    @Bean
    public Executor dbInsertExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(5);
        //是否回收空闲线程
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(10);
        //配置等待队列大小
        threadPoolTaskExecutor.setQueueCapacity(50);
        //配置线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix("dbInsert-");
        //拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
