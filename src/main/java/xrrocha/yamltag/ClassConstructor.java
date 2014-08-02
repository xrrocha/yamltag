package xrrocha.yamltag;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import xrrocha.yamltag.YamlFactoryException.ClassLoadingException;

public class ClassConstructor extends Constructor {
    public static final String CLASS_TAG_NAME = "!class";
    
	public ClassConstructor(Class<?> type) {
		super(type);
		populateConstructors();
	}
	public ClassConstructor() {
		populateConstructors();
	}
	
	protected void populateConstructors() {
		yamlConstructors.put(new Tag(CLASS_TAG_NAME), new ConstructClass());
	}

	protected class ConstructClass extends AbstractConstruct {
		public Object construct(Node node) {
			String className = (String) constructScalar((ScalarNode) node);
			return classFromName(className, Thread.currentThread().getContextClassLoader());
		}
	}
	
	public static Class<?> classFromName(String className, ClassLoader classLoader) {
		try {
			return classLoader.loadClass(className);
		} catch (Exception e) {
			throw new ClassLoadingException(className, e);
		}
	}
}
