package springboot_demo.springcloud.handle;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

@Component
public class RedissonConnector {

	private static RedissonClient redisson = null;
	
	@PostConstruct
    public void init(){
		Config config = new Config();
		config.useSingleServer().setAddress("redis://192.168.0.55:6379");
		config.useSingleServer().setPassword("lvxun123456");
        redisson = Redisson.create(config);
    }
	
    public RedissonClient getClient(){
        return redisson;
    }
}
