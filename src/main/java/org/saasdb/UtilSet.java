package org.saasdb;

import java.lang.reflect.Method;

public class UtilSet {
	public static class Strings
	{
		public static CharSequence nqm(int n)
		{
			return repeat(n, "," , "?");
		}
		
		public static CharSequence repeat(int n, String sep, String str)
		{
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<n;i++)
			{
				if(i>0)
					sb.append(sep);
				sb.append(str);
			}
			
			return sb;
		}
	}
	
	public static class dbs
	{
		
	}
	
	
	public static void close(Object... resources)
	{
		for(Object res: resources)
		{
			if(res==null)
				continue;
			
			if(res instanceof AutoCloseable)
			{
				try {
					((AutoCloseable)res).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					Method m = res.getClass().getMethod("close", new Class[0]);
					if(m!=null)
						m.invoke(res, new Object[0]);
				}catch(Throwable t){};
			}
		}
	}
}
