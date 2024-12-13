package com.lcyy.aicloud.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dlwlrma
 * @data 2024年11月13日 16:10
 * @Description
 */
@Configuration
public class XxlJobConfig {

      @Value("${xxl.job.admin.addresses}")
      private String adminAddresses;

      @Value("${xxl.job.accessToken}")
      private String accessToken;

      @Value("${xxl.job.executor.appname}")
      private String appname;

      @Value("${server.port}")
      private int port;

      @Value("${xxl.job.executor.logpath}")
      private String logPath;

      @Value("${xxl.job.executor.logretentiondays}")
      private int logRetentionDays;

        @Bean
      public XxlJobSpringExecutor xxlJobExecutor() {
         XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
         xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
         xxlJobSpringExecutor.setAppname(appname);
         xxlJobSpringExecutor.setPort(port + 10000);
         xxlJobSpringExecutor.setAccessToken(accessToken);
         xxlJobSpringExecutor.setLogPath(logPath);
         xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
         return xxlJobSpringExecutor;
      }
}
