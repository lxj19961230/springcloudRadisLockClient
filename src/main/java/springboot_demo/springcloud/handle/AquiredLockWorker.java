package springboot_demo.springcloud.handle;

public interface AquiredLockWorker<T> {

	 T invokeAfterLockAquire() throws Exception;
}
