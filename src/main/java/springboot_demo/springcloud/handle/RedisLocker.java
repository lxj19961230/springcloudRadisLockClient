package springboot_demo.springcloud.handle;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RedisLocker implements DistributedLocker{

	@Autowired
	private RedissonConnector redissonConnector;
	
	private final static String LOCKER_PREFIX = "lock:";
	
	public <T> T lock(String resourceName, AquiredLockWorker<T> worker) {
		return lock(resourceName, worker, 100);
	}

    public <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime){
        try {
			RedissonClient redisson= redissonConnector.getClient();
			RLock lock = redisson.getLock(LOCKER_PREFIX + resourceName);
			// Wait for 100 seconds seconds and automatically unlock it after lockTime seconds
			boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
			if (success) {
			    try {
			        return worker.invokeAfterLockAquire();
			    } finally {
			        lock.unlock();
			    }
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
    }

}
