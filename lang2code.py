

if __name__ == '__main__':
	file_object = open('java_lang_clazzes.txt')
 
	while 1:
		line = file_object.readline()
		if not line:
			break
		print 'case \"' + line.strip() + '\" : ' 
    
