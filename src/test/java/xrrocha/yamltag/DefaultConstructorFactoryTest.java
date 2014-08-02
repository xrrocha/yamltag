package xrrocha.yamltag;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DefaultConstructorFactoryTest {
    public static class ExampleBean  {
        private Set<Date> set;
        public Set<Date> getSet() { return set; }
        public void setSet(Set<Date> set) { this.set = set; }

        private List<Date> list;
        public List<Date> getList() { return list; }
        public void setList(List<Date> list) { this.list = list; }

        private Map<Class<?>, List<Double>> map;
        public Map<Class<?>, List<Double>> getMap() { return map; }
        public void setMap(Map<Class<?>, List<Double>> map) { this.map = map; }
    }

    private static final String TYPE_DESCRIPTOR_STRING = 
        "xrrocha.yamltag.DefaultConstructorFactoryTest$ExampleBean:\n" +
        "    tagName: exampleBean\n" +
        "    listProperties:\n" +
        "        set: java.util.Date\n" +
        "        list: java.util.Date\n" +
        "    mapProperties:\n" +
        "      map:\n" +
        "        keyClass: java.lang.Class\n" +
        "        valueClass: java.util.List\n";
                
    public static final String YAML_STRING =
        "--- !exampleBean\n" +
        "    set:\n" +
        "        - 1776-07-04\n" +
        "        - 1810-08-07\n" +
        "    list:\n" +
        "        - 2010-12-01\n" +
        "        - 2011-01-31\n" +
        "    map:\n" +
        "        !class java.lang.String:\n" +
        "            - 1.0\n" +
        "            - 2.0\n";
    
    private static ExampleBean exampleBean;
    
    @BeforeClass
    public static void setUp() {
        Yaml descriptorYaml = new Yaml();
        @SuppressWarnings("unchecked")
        Map<String, Object> descriptorMap = (Map<String, Object>) descriptorYaml.load(TYPE_DESCRIPTOR_STRING);
        Collection<TypeDescriptor> descriptors = TypeDescriptor.fromMap(descriptorMap, DefaultConstructorFactoryTest.class.getClassLoader());
        
        DefaultConstructorFactory constructorFactory = new DefaultConstructorFactory();
        constructorFactory.addDescriptors(descriptors);

        Constructor defaultConstructor = constructorFactory.newConstructor();
        Yaml defaultConstructorYaml = new Yaml(defaultConstructor);
        Object myBeanObject = defaultConstructorYaml.load(YAML_STRING);
        
        assertThat(myBeanObject, notNullValue());
        assertThat(myBeanObject, instanceOf(ExampleBean.class));
        exampleBean = (ExampleBean) myBeanObject;
    }
    
    @Test
    public void resolvesMapProperties() {
        assertThat(exampleBean.getMap(), notNullValue());
        assertThat(exampleBean.getMap(), instanceOf(Map.class));
        assertTrue(exampleBean.getMap().containsKey(String.class));
        assertThat(exampleBean.getMap().get(String.class), instanceOf(List.class));
        assertThat(exampleBean.getMap().get(String.class).size(), equalTo(2));
        assertThat(exampleBean.getMap().get(String.class).get(0), instanceOf(Double.class));
        assertThat(exampleBean.getMap().get(String.class).get(0), equalTo(1d));
    }
    
    @Test
    public void resolvesSetProperties() {
        assertThat(exampleBean.getSet(), notNullValue());
        assertThat(exampleBean.getSet(), instanceOf(Set.class));
        assertThat(exampleBean.getSet().size(), equalTo(2));
        assertThat(exampleBean.getSet().iterator().next(), notNullValue());
        assertThat(exampleBean.getSet().iterator().next(), instanceOf(Date.class));
    }
    
    @Test
    public void resolvesListProperties() {
        assertThat(exampleBean.getList(), notNullValue());
        assertThat(exampleBean.getList(), instanceOf(List.class));
        assertThat(exampleBean.getList().size(), equalTo(2));
        assertThat(exampleBean.getList().get(0), notNullValue());
        assertThat(exampleBean.getList().get(0), instanceOf(Date.class));
    }
}

