package MVC;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class DispathcherServlet implements Servlet{
	
	private static HashMap<String, MapValueOb> map = new HashMap<>();
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
        String Tomcat  = System.getProperty("catalina.home");
		
		File dir = new File(Tomcat+"/webapps/TheFuck/WEB-INF/classes/test");//Contorller�Ĳ���
		
		for(File f:dir.listFiles()){
			String className = f.getName();
			
			Class clAss = null;
			
			try {				
				clAss = Class.forName("test."+ className.substring(0,className.length() - 6));//��testĿ¼�µ�.class
				
				if(clAss.isAnnotationPresent(Controller.class)){//�ж��Ƿ���controller
					
					Object object = clAss.newInstance();//ʵ��������
					
					for(Method m:clAss.getDeclaredMethods()){//��ȡ����
						
						if(m.isAnnotationPresent(RequestMapping.class)){//�ж��Ƿ���requestmapping��ǵķ���
							
							MapValueOb mapobjectandvalue = new MapValueOb();
							mapobjectandvalue.setMethod(m);
							mapobjectandvalue.setTarget(object);
							RequestMapping requestmapping = (RequestMapping)m.getAnnotation(RequestMapping.class);
							
							map.put(requestmapping.value(), mapobjectandvalue);//�൱�ڽ�@RequestMapping("/hello")�е�hello
						}
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		ModelAndView mav = new ModelAndView();
		
		Map m = arg0.getParameterMap();//���ύ�ı�
		
		for(String key:(Set<String>)m.keySet()){//������Ϣ��ӵ���model��
			
			String[] values = (String[]) m.get(key);
			
			for(String value:values){
				System.out.println(value);
				mav.addObject(key, value);
			}
		
		}
		
		Object result = null;
		
		try {
			MapValueOb set = (MapValueOb)map.get(((HttpServletRequest) arg0).getServletPath());//����URLȡ����value�������Ӧ�ķ�����ʵ�������
			
			result= set.getMethod().invoke(set.getTarget(), mav);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result instanceof ModelAndView){
			mav = (ModelAndView) result;
			for(String p:mav.getParameters()){
				System.out.println(p);
				arg0.setAttribute(p, mav.getMap(p));//����keyȡֵ
			}
		}
		
		arg0.getRequestDispatcher(mav.getViewName()+".jsp").forward(arg0, arg1);//������Ϣ		
	}
		
}
