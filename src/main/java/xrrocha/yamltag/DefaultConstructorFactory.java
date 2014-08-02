package xrrocha.yamltag;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.yaml.snakeyaml.constructor.Constructor;

public class DefaultConstructorFactory implements ConstructorFactory  {
	@SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DefaultConstructorFactory.class.getName());

	private final Map<Class<?>, TypeDescriptor> descriptors = new HashMap<Class<?>, TypeDescriptor>();
	
	@Override
	public Constructor newConstructor() {
		return new CustomConstructor();
	}
	
	@Override
	public Constructor newConstructorFor(Class<?> type) {
		return new CustomConstructor(type);
	}
	
	@Override
	public void addDescriptor(TypeDescriptor descriptor) {
		descriptors.put(descriptor.getTargetClass(), descriptor);
        resolveClass(descriptor.getTargetClass());
	}
	
	@Override
	public void addDescriptors(Iterable<TypeDescriptor> descriptors) {
		for (TypeDescriptor descriptor: descriptors) {
			addDescriptor(descriptor);
		}
	}
    
    private void resolveClass(Class<?> descriptorClass) {
        TypeDescriptor descriptor = descriptors.get(descriptorClass);
        if (descriptor != null) {
            Class<?> superclass = descriptorClass.getSuperclass();
            if (superclass != null && descriptors.containsKey(superclass)) {
                resolveClass(superclass);
            }

            for (Class<?> candidateClass: descriptors.keySet()) {
                if (!descriptorClass.equals(candidateClass)) {
                    Class<?> candidateSuperclass = candidateClass.getSuperclass();
                    if (candidateSuperclass != null) {
                        if (descriptorClass.isAssignableFrom(candidateSuperclass)) {
                            TypeDescriptor subclassDescriptor = descriptors.get(candidateClass);
                            augmentListPropertyDescriptors(subclassDescriptor, descriptor);
                            augmentMapPropertyDescriptors(subclassDescriptor, descriptor);
                        }
                    }
                }
            }
        }
    }
    
    private void augmentListPropertyDescriptors(TypeDescriptor subclassDescriptor, TypeDescriptor superclassDescriptor) {
        for (String listPropertyName: superclassDescriptor.getListPropertyDescriptors().keySet()) {
            if (!subclassDescriptor.getListPropertyDescriptors().containsKey(listPropertyName)) {
                subclassDescriptor.getListPropertyDescriptors().
                    put(listPropertyName, superclassDescriptor.getListPropertyDescriptors().get(listPropertyName));
            }
        }
    }
    
    private void augmentMapPropertyDescriptors(TypeDescriptor subclassDescriptor, TypeDescriptor superclassDescriptor) {
        for (String mapPropertyName: superclassDescriptor.getMapPropertyDescriptors().keySet()) {
            if (!subclassDescriptor.getMapPropertyDescriptors().containsKey(mapPropertyName)) {
                subclassDescriptor.getMapPropertyDescriptors().
                    put(mapPropertyName, superclassDescriptor.getMapPropertyDescriptors().get(mapPropertyName));
            }
        }
    }
    
	private class CustomConstructor extends ClassConstructor {
		private CustomConstructor() {
			super();
			addTypeDescriptors();
		}
		
		private CustomConstructor(Class<?> type) {
			super(type);
			addTypeDescriptors();
		}
		
		private void addTypeDescriptors() {
			for (TypeDescriptor descriptor: descriptors.values()) {
				addTypeDescription(descriptor.toTypeDescription());
			}
		}
	}
}
