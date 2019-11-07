package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.threadhelp;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理类
 * 
 * @author lenovo
 *
 */
public class ThreadPoolManager {
	private ExecutorService service;

	private ThreadPoolManager() {
		int num = Runtime.getRuntime().availableProcessors() * 20;
		service = Executors.newFixedThreadPool(2);
	}

	private static final ThreadPoolManager manager = new ThreadPoolManager();

	public static ThreadPoolManager getInstance() {
		return manager;
	}

	public void executeTask(Runnable runnable) {
		service.execute(runnable);

	}

	public void executeTasks(LinkedList<Runnable> list){
		for(Runnable runnable:list){
			service.execute(runnable);
		}
	}

	public void killAll(){
		service.shutdownNow();
	}
}
