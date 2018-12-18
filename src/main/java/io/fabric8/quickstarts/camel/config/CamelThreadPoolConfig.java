package io.fabric8.quickstarts.camel.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Rejectable;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.builder.ThreadPoolBuilder;
import org.apache.camel.builder.ThreadPoolProfileBuilder;
import org.apache.camel.spi.ThreadPoolProfile;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelThreadPoolConfig  implements CamelContextAware {

	private CamelContext camelContext;

    @Override
    public void setCamelContext(final CamelContext camelContext) {
      this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
      return this.camelContext;
    }

    
    @Bean(name = "defaultCamelThreadPool")
    ExecutorService defaultCamelThreadPool() {
    	customDefaultPoolProfile();	
      ThreadPoolBuilder builder = new ThreadPoolBuilder(this.camelContext);
      ThreadPoolExecutor defaultCamelThreadPool = null;
		try {
			defaultCamelThreadPool = (ThreadPoolExecutor)builder.poolSize(10).maxPoolSize(100).maxQueueSize(100)
			  		.rejectedPolicy(ThreadPoolRejectedPolicy.Abort).build("defaultCamelThreadPool");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return defaultCamelThreadPool;
    }

    @Bean(name = "customWireTapPoolExecutorService")
    ExecutorService customExecutor() {
      ThreadPoolBuilder builder = new ThreadPoolBuilder(this.camelContext);
      ThreadPoolExecutor crmpWireTapPool = null;
		try {
			crmpWireTapPool = (ThreadPoolExecutor)builder.poolSize(1).maxPoolSize(1).maxQueueSize(0)
			  		.rejectedPolicy(ThreadPoolRejectedPolicy.Abort).build("customWireTapPool");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        crmpWireTapPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof Rejectable) {
                    ((Rejectable)r).reject();
                } else {
                    System.out.println("Task " + r.toString() + " rejected from " + executor.toString());
                }
            }

            @Override
            public String toString() {
                return "Abort";
            }
        });

    	return crmpWireTapPool;
    }

    ThreadPoolProfile customDefaultPoolProfile() {
    	
    	// ThreadPoolProfile[defaultThreadPoolProfile (true) size:10-20, keepAlive: 60 SECONDS
    	//, maxQueue: 1000, allowCoreThreadTimeOut:false, rejectedPolicy:CallerRuns]
        ThreadPoolProfileBuilder pbuilder = new ThreadPoolProfileBuilder("customCrmpThreadPoolProfile");
        ThreadPoolProfile poolProfile = pbuilder.poolSize(10).maxPoolSize(100).maxQueueSize(100)
          .keepAliveTime(60L, TimeUnit.SECONDS).rejectedPolicy(ThreadPoolRejectedPolicy.Discard).build();
        poolProfile.setDefaultProfile(true);
        
        this.camelContext.getExecutorServiceManager().registerThreadPoolProfile(poolProfile);
        this.camelContext.getExecutorServiceManager().setDefaultThreadPoolProfile(poolProfile);
        this.camelContext.getExecutorServiceManager().setThreadNamePattern("Camel (#camelId#) CRMP thread ##counter# - #name#");          

        return poolProfile;
    }

}
