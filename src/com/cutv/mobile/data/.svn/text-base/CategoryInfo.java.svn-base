package com.cutv.mobile.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CategoryInfo implements Serializable,Cloneable {
	private static final long serialVersionUID = -7060210544600464481L;  


	public int id;
	public String title;
	public String more;
	
	public List<ContentInfo> contentList = new ArrayList<ContentInfo>();
	
	public CategoryInfo()
	{
	}
	
	
	 public Object clone()
	 {
		 try 
		 {
			 return super.clone();
		 }
		 catch(Exception e)
		 {
		 	return null;
		 }
	 }	
	
}
