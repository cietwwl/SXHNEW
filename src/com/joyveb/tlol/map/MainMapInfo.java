package com.joyveb.tlol.map;

public class MainMapInfo {
   private int id;
   private String name;
   private boolean enable;
   private int level;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public boolean getEnable() {
	return enable;
}
public void setEnable(boolean enable) {
	this.enable = enable;
	
}
public int getHexColor()
{
	 if(this.enable) {
		 return 0x000000;
	 }
	 else {
		 return 0x0c0c0c; 
	 }
}
public int getLevel() {
	return level;
}
public void setLevel(int level) {
	this.level = level;
}


   
   
}
