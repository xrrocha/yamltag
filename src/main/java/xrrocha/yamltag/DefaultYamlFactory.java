package xrrocha.yamltag;

import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DefaultYamlFactory implements YamlFactory {
    public static final String DEFAULT_EXTENSION = "yaml";
    public static final String SNAKE_YAML_TYPE_DESCRIPTOR_RESOURCE_NAME = "yamltag.yaml";
    
    private static final ClassLoader DEFAULT_CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    private ConstructorFactory constructorFactory;
    
	@SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(DefaultYamlFactory.class.getName());

    public DefaultYamlFactory() {
        this(null);
    }

    public DefaultYamlFactory(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = DEFAULT_CLASS_LOADER;
        }
        
        constructorFactory = new DefaultConstructorFactory();
        constructorFactory.addDescriptors(TypeDescriptor.fromResources(SNAKE_YAML_TYPE_DESCRIPTOR_RESOURCE_NAME, classLoader));
    }
	
    @Override
    public Yaml newYaml() {
        Constructor constructor = constructorFactory.newConstructor();
        Yaml yaml = new Yaml(constructor);
        return yaml;
    }
}
