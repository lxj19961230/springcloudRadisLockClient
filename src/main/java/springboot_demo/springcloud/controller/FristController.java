package springboot_demo.springcloud.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springboot_demo.springcloud.handle.AquiredLockWorker;
import springboot_demo.springcloud.handle.DistributedLocker;
import springboot_demo.springcloud.service.RedisService;

@RestController
public class FristController {

	@Value("${server.port}")
    String port;
	
	@Autowired
	private DistributedLocker distributedLocker;
	@Autowired
	private RedisService redisService;
	
    @RequestMapping("/hi")
    public String home(@RequestParam String name) {
        return "hi "+name+",i am from port:" +port;
    }
    
    @RequestMapping(value = "/redisData")
    public String redisData(){
    	redisService.set("test1", (new String[]{"1","2","3"}));
    	redisService.set("test2", "test2 value");
    	redisService.set("test3", "test3 value", 30L);
    	List<String> list  = Arrays.asList("test1","test2","test3");
    	list.forEach(n-> System.out.println(redisService.get(n)));
        return "redisData";
    }
    
    @RequestMapping("/deleteRedis")
    public String home() {
    	redisService.removePattern("test*");
        return "delete success!";
    }
    
    @RequestMapping(value = "/redlock")
    public String testRedlock() throws InterruptedException{

        CountDownLatch doneSignal = new CountDownLatch(5);
        for (int i = 0; i < 5; ++i) { // create and start threads
            new Thread(new Worker(doneSignal)).start();
        }
        doneSignal.await();
        System.out.println("All processors done. Shutdown connection");
        return "redlock";
    }
    
    
     class Worker implements Runnable {
        private final CountDownLatch doneSignal;

        Worker( CountDownLatch doneSignal) {
            this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                distributedLocker.lock("test",new AquiredLockWorker<Object>() {

                    public Object invokeAfterLockAquire() {
                        doTask();
                        return null;
                    }

                });
            }catch (Exception e){

            }
        }

        void doTask() {
            System.out.println(Thread.currentThread().getName() + " start");
            Random random = new Random();
            int _int = random.nextInt(200);
            System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
            try {
                Thread.sleep(_int);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " end");
            doneSignal.countDown();
        }
    }
}
