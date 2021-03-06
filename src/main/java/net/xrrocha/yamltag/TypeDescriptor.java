package net.xrrocha.yamltag;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.xrrocha.yamltag.YamlFactoryException.ResourceIOException;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;


public class TypeDescriptor {
    private String tagName;
    private Class<?> targetClass;
    private Map<String, ListPropertyDescriptor> listPropertyDescriptors = new HashMap<String, ListPropertyDescriptor>();
    private Map<String, MapPropertyDescriptor> mapPropertyDescriptors = new HashMap<String, MapPropertyDescriptor>();

    private TypeDescription typeDescription;

    public static final String TAG_NAME = "tagName";
    public static final String MAP_PROPERTIES_TAG_NAME = "mapProperties";
    public static final String LIST_PROPERTIES_TAG_NAME = "listProperties";
    
    private final static Logger logger = Logger.getLogger(TypeDescriptor.class.getName());

    public static Collection<TypeDescriptor> fromResources(String resourceLocation) {
        return fromResources(resourceLocation, Thread.currentThread().getContextClassLoader());
    }

    // TODO Allow for multiple tag names for the same class
    public static Collection<TypeDescriptor> fromResources(String resourceLocation, ClassLoader classLoader) {
    	if (logger.isLoggable(Level.INFO))
    		logger.finest(String.format("Using %s to retrieve %s", classLoader, resourceLocation));
        Yaml descriptorYaml = new Yaml();
        Collection<TypeDescriptor> descriptors = new LinkedList<TypeDescriptor>();

        for (InputStream stream : getResourceInputStreams(resourceLocation, classLoader)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) descriptorYaml.load(stream);
            Collection<TypeDescriptor> resourceDescriptors = TypeDescriptor.fromMap(map, classLoader);
            descriptors.addAll(resourceDescriptors);
        }

        Collection<TypeDescriptor> selectedDescriptors = selectUniqueTagNames(descriptors);
        return selectedDescriptors;
    }
    
    private static Collection<TypeDescriptor> selectUniqueTagNames(Collection<TypeDescriptor> descriptors) {
    	Set<String> tagNames = new HashSet<String>();
    	Collection<TypeDescriptor> selectedDescriptors = new LinkedList<TypeDescriptor>();
    	
    	for (TypeDescriptor descriptor: descriptors) {
    		if (tagNames.contains(descriptor.tagName)) {
    			logger.warning("Duplicate tag name: " + descriptor.tagName);
    		} else {
    			tagNames.add(descriptor.tagName);
    			selectedDescriptors.add(descriptor);
    		}
    	}
    	
    	return selectedDescriptors;
    }
    
    // TODO  Check for duplicate tag names in map
    public static Collection<TypeDescriptor> fromMap(Map<String, Object> classMap, ClassLoader classLoader) {
        Collection<TypeDescriptor> descriptors = new LinkedList<TypeDescriptor>();

        for (String className : classMap.keySet()) {
            TypeDescriptor descriptor = new TypeDescriptor();
            descriptors.add(descriptor);
            descriptor.setTargetClass(ClassConstructor.classFromName(className, classLoader));

            @SuppressWarnings("unchecked")
            Map<String, Object> yamlTypeDescriptorPropertyMap = (Map<String, Object>) classMap.get(className);
            for (String yamlTypeDescriptorPropertyName : yamlTypeDescriptorPropertyMap.keySet()) {
                if (TAG_NAME.equals(yamlTypeDescriptorPropertyName)) {
                    String propertyValue = (String) yamlTypeDescriptorPropertyMap.get(yamlTypeDescriptorPropertyName);
                    descriptor.setTagName(propertyValue);
                } else if (MAP_PROPERTIES_TAG_NAME.equals(yamlTypeDescriptorPropertyName)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, String>> mapProperties = (Map<String, Map<String, String>>) yamlTypeDescriptorPropertyMap
                            .get(yamlTypeDescriptorPropertyName);
                    descriptor.setMapPropertyDescriptors(MapPropertyDescriptor.fromMap(mapProperties, classLoader));
                } else if (LIST_PROPERTIES_TAG_NAME.equals(yamlTypeDescriptorPropertyName)) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> listProperties = (Map<String, String>) yamlTypeDescriptorPropertyMap
                            .get(yamlTypeDescriptorPropertyName);
                    descriptor.setListPropertyDescriptors(ListPropertyDescriptor.fromMap(listProperties, classLoader));
                }
            }
        }

        return descriptors;
    }

    public TypeDescription toTypeDescription() {
        if (typeDescription == null) {
            if (tagName == null) {
                typeDescription = new TypeDescription(targetClass);
            } else {
                typeDescription = new TypeDescription(targetClass, "!" + tagName);
            }

            if (mapPropertyDescriptors != null) {
                for (String propertyName : mapPropertyDescriptors.keySet()) {
                    MapPropertyDescriptor mapPropertyDescriptor = mapPropertyDescriptors.get(propertyName);
                    typeDescription.putMapPropertyType(propertyName, mapPropertyDescriptor.getKeyClass(),
                            mapPropertyDescriptor.getValueClass());
                }
            }

            if (listPropertyDescriptors != null) {
                for (String propertyName : listPropertyDescriptors.keySet()) {
                    ListPropertyDescriptor listPropertyDescriptor = listPropertyDescriptors.get(propertyName);
                    typeDescription.putListPropertyType(propertyName, listPropertyDescriptor.getListClass());
                }
            }

        }

        return typeDescription;
    }
    public static Iterable<InputStream> getResourceInputStreams(final String resourceName, final ClassLoader classLoader)
        throws ResourceIOException
    {
        try {
            final Enumeration<URL> resourceUrls = classLoader.getResources(resourceName);
            return new Iterable<InputStream>() {
                @Override
                public Iterator<InputStream> iterator() {
                    return new Iterator<InputStream>() {
                        @Override
                        public boolean hasNext() {
                            return resourceUrls.hasMoreElements();
                        }

                        @Override
                        public InputStream next() {
                            URL resourceUrl = resourceUrls.nextElement();
                        	if (logger.isLoggable(Level.INFO))
                        		logger.finest(String.format("Using %s to retrieve resource '%s' from %s",
                                         classLoader, resourceName, resourceUrl));
                            try {
                                URLConnection urlConnection = resourceUrl.openConnection();
                                urlConnection.setUseCaches(false);
                            	if (logger.isLoggable(Level.INFO))
                            		logger.finest(String.format("Returning resource: %s", resourceUrl.toExternalForm()));
                                return urlConnection.getInputStream();
                            } catch (IOException ioe) {
                                YamlFactoryException exception = new ResourceIOException(resourceName, ioe);
                                logger.warning(exception.getMessage());
                                throw exception;
                            }
                        }

                        @Override
                        public void remove() {
                            UnsupportedOperationException exception =
                                new UnsupportedOperationException("Unsupported: remove resource input stream for " + resourceName);
                            logger.warning(exception.getMessage());
                            throw exception;
                        }
                    };
                }
            };
        } catch (IOException ioe) {
            YamlFactoryException exception = new ResourceIOException(resourceName, ioe);
            logger.warning(exception.getMessage());
            throw exception;
        }
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Map<String, ListPropertyDescriptor> getListPropertyDescriptors() {
        return listPropertyDescriptors;
    }

    public void setListPropertyDescriptors(Map<String, ListPropertyDescriptor> listPropertyDescriptors) {
        this.listPropertyDescriptors = listPropertyDescriptors;
    }

    public Map<String, MapPropertyDescriptor> getMapPropertyDescriptors() {
        return mapPropertyDescriptors;
    }

    public void setMapPropertyDescriptors(Map<String, MapPropertyDescriptor> mapPropertyDescriptors) {
        this.mapPropertyDescriptors = mapPropertyDescriptors;
    }
}
