package xrrocha.yamltag;

import java.util.LinkedHashMap;
import java.util.Map;

public class ListPropertyDescriptor {
	private Class<?> listClass;
	
	public static Map<String, ListPropertyDescriptor> fromMap(Map<String, String> listProperties, ClassLoader classLoader) {
		Map<String, ListPropertyDescriptor> listPropertyDescriptors = new LinkedHashMap<String, ListPropertyDescriptor>();
		for (String targetPropertyName: listProperties.keySet()) {
			String listPropertyClassName = listProperties.get(targetPropertyName);
			Class<?> listClass = ClassConstructor.classFromName(listPropertyClassName, classLoader);
			ListPropertyDescriptor listPropertyDescriptor = new ListPropertyDescriptor();
			listPropertyDescriptor.setListClass(listClass);
			listPropertyDescriptors.put(targetPropertyName, listPropertyDescriptor);
		}
		return listPropertyDescriptors;
	}

	public Class<?> getListClass() {
		return listClass;
	}

	public void setListClass(Class<?> listClass) {
		this.listClass = listClass;
	}
}
