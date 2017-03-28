package proxy;

public class CglibTest {
	
	public static void main(String[] args) {
		 CglibProxy proxy = new CglibProxy();  
	      ForumServiceImpl forumService = (ForumServiceImpl )proxy.getProxy(ForumServiceImpl.class);  
	      forumService.removeForum(10);  
	      forumService.removeTopic(1023);  
	}
	
}
