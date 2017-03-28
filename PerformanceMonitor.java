package proxy;

public class PerformanceMonitor {
	 //①通过一个ThreadLocal保存调用线程相关的性能监视信息  
    private static ThreadLocal<MethodPerformance> performanceRecord =          
                                new ThreadLocal<MethodPerformance>();  
      
    //②启动对某一目标方法的性能监视  
     public static void begin(String method) {  
        System.out.println("begin monitor...");  
        MethodPerformance mp = new MethodPerformance(method);  
        performanceRecord.set(mp);  
    }  
    public static void end() {  
        System.out.println("end monitor...");  
        MethodPerformance mp = performanceRecord.get();  
  
         //③打印出方法性能监视的结果信息。  
        mp.printPerformance();  
    }  
}
