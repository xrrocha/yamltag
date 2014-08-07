package net.xrrocha.yamltag;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapPropertyDescriptor {
	private Class<?> keyClass;
	private Class<?> valueClass;
	
    public static final String KEY_CLASS_TAG_NAME = "keyClass";
    public static final String VALUE_CLASS_TAG_NAME = "valueClass";
	
	public static Map<String, MapPropertyDescriptor> fromMap(Map<String, Map<String, String>> mapProperties, ClassLoader classLoader) {
		Map<String, MapPropertyDescriptor> mapPropertyDescriptors = new LinkedHashMap<String, MapPropertyDescriptor>();
		for (String targetPropertyName: mapProperties.keySet()) {
			Map<String, String> descriptorProperties = mapProperties.get(targetPropertyName);
			MapPropertyDescriptor mapPropertyDescriptor = new MapPropertyDescriptor();
			
			String keyClassName = descriptorProperties.get(KEY_CLASS_TAG_NAME);
			Class<?> keyClass = ClassConstructor.classFromName(keyClassName, classLoader);
			mapPropertyDescriptor.setKeyClass(keyClass);

			String valueClassName = descriptorProperties.get(VALUE_CLASS_TAG_NAME);
			Class<?> valueClass = ClassConstructor.classFromName(valueClassName, classLoader);
			mapPropertyDescriptor.setValueClass(valueClass);
			
			mapPropertyDescriptors.put(targetPropertyName, mapPropertyDescriptor);
		}
		return mapPropertyDescriptors;
	}
	
	public Class<?> getKeyClass() {
		return keyClass;
	}
	public void setKeyClass(Class<?> keyClass) {
		this.keyClass = keyClass;
	}
	public Class<?> getValueClass() {
		return valueClass;
	}
	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}
}
