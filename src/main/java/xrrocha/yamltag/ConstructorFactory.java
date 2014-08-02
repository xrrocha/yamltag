package xrrocha.yamltag;

import org.yaml.snakeyaml.constructor.Constructor;

public interface ConstructorFactory {
	Constructor newConstructor();
	Constructor newConstructorFor(Class<?> type);
	void addDescriptor(TypeDescriptor descriptor);
	void addDescriptors(Iterable<TypeDescriptor> descriptors);
}
