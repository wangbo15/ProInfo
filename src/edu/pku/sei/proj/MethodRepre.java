package edu.pku.sei.proj;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MethodRepre implements Serializable  {

	private static final long serialVersionUID = -3430329033484796237L;
	private ClassRepre cls;
	private int flag;
	private String name;
	private String returnType;
	
	private boolean isConstructor;
	private List<LocalRepre> params = new ArrayList<>(0);
	
	public MethodRepre(ClassRepre cls, int flag, String name, String returnType, boolean isConstructor) {
		super();
		this.cls = cls;
		this.flag = flag;
		this.name = name;
		this.returnType = returnType;
		this.isConstructor = isConstructor;
	}
	
	public ClassRepre getCls() {
		return cls;
	}
	public void setCls(ClassRepre cls) {
		this.cls = cls;
	}
	
	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public List<LocalRepre> getParams() {
		return params;
	}
	
	public void insertParam(LocalRepre param){
		params.add(param);
	}

	@Override
	public String toString() {
		return "MethodRepre [flag=" + flag + ", name=" + name + ", returnType=" + returnType + "]";
	}
	
	public boolean isInherable(){
		return Modifier.isPublic(this.flag) || Modifier.isProtected(this.flag);
	}
	
	public boolean isSameTo(MethodRepre that){
		if(this.name.equals(that.getName())){
			if(this.params.size() == that.getParams().size()){
				for(int i = 0; i < this.params.size(); i++){
					if(!params.get(i).getType().equals(that.getParams().get(i).getType())){
						return false;
					}
				}
				
			}else{
				return false;
			}
		}else{
			return false;
		}
		
		return true;
	}

}
