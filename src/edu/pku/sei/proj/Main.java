package edu.pku.sei.proj;

import java.io.File;

public class Main {
	public static void main(String[] args){
		
		
		
	}
	
	public static void traverseSrcFolder(File root){
		File[] files = root.listFiles();
		
		if(files == null){
			return;
		}
		
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			
			if (files[i].isDirectory()) {
				
				traverseSrcFolder(files[i]);
				
			} else if (fileName.endsWith(".java")) {
				
				
				
			} else {
				continue;
			}
		}
	}
}
