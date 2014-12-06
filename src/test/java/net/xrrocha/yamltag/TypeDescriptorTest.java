package net.xrrocha.yamltag;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.xrrocha.yamltag.DefaultYamlFactory;
import net.xrrocha.yamltag.ListPropertyDescriptor;
import net.xrrocha.yamltag.MapPropertyDescriptor;
import net.xrrocha.yamltag.TypeDescriptor;
import net.xrrocha.yamltag.example.Term;

import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

public class TypeDescriptorTest {
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
        "net.xrrocha.yamltag.TypeDescriptorTest$ExampleBean:\n" +
        "    tagName: exampleBean\n" +
        "    listProperties:\n" +
        "        set: java.util.Date\n" +
        "        list: java.util.Date\n" +
        "    mapProperties:\n" +
        "      map:\n" +
        "        keyClass: java.lang.Class\n" +
        "        valueClass: java.util.List\n";
    
    private Yaml descriptorYaml = new Yaml();
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> descriptorMap = (Map<String, Object>) descriptorYaml.load(TYPE_DESCRIPTOR_STRING);
    private Collection<TypeDescriptor> descriptors = TypeDescriptor.fromMap(descriptorMap, getClass().getClassLoader());
    
    private TypeDescriptor descriptor = descriptors.iterator().next();

    private Map<String, MapPropertyDescriptor> mapPropertyDescriptors = descriptor.getMapPropertyDescriptors();

    private Map<String, ListPropertyDescriptor> listPropertyDescriptors = descriptor.getListPropertyDescriptors();

    @Test
    public void loadsTagName() {
        assertTrue(descriptors.iterator().hasNext());
        
        assertThat(descriptor, notNullValue());
        assertThat(descriptor.getTagNames().get(0), equalTo("exampleBean"));
    }
    
    @Test
    public void loadsMapProperties() {
        assertThat(mapPropertyDescriptors, notNullValue());
        assertThat(mapPropertyDescriptors.size(), equalTo(1));
    }

    @Test
    public void loadsMapProperty() {
        MapPropertyDescriptor linksMapPropertyDescriptor = mapPropertyDescriptors.get("map");

        assertThat(linksMapPropertyDescriptor, notNullValue());
		
        assertThat(linksMapPropertyDescriptor.getKeyClass(), notNullValue());
        assertEquals(linksMapPropertyDescriptor.getKeyClass(), Class.class);
        
        assertThat(linksMapPropertyDescriptor.getValueClass(), notNullValue());
		assertEquals(linksMapPropertyDescriptor.getValueClass(), List.class);
	}
    
    @Test
    public void loadsListProperties() {
        assertThat(listPropertyDescriptors, notNullValue());
        assertThat(listPropertyDescriptors.size(), equalTo(2));
    }
    
    @Test
    public void loadsSetProperty() {
        ListPropertyDescriptor setPropertyDescriptor = listPropertyDescriptors.get("set");
        assertThat(setPropertyDescriptor, notNullValue());
        assertEquals(setPropertyDescriptor.getListClass(), Date.class);
    }
    
    @Test
    public void loadsListProperty() {
        ListPropertyDescriptor listPropertyDescriptor = listPropertyDescriptors.get("list");
        assertThat(listPropertyDescriptor, notNullValue());
        assertEquals(listPropertyDescriptor.getListClass(), Date.class);
    }
    
    @Test
    public void convertsProperlyToTypeDescription() {
        TypeDescription typeDescription = descriptor.toTypeDescription();
        
        assertEquals(typeDescription.getType(), ExampleBean.class);
        
        assertEquals(typeDescription.getMapKeyType("map"), Class.class);
        assertEquals(typeDescription.getMapValueType("map"), List.class);
        
        assertEquals(typeDescription.getListPropertyType("set"), Date.class);
        
        assertEquals(typeDescription.getListPropertyType("list"), Date.class);
    }

    @Test
    public void scansAllOfClasspath() throws Exception {
        File directoryFile = new File("target/moreClasspath");
        directoryFile.mkdirs();
        
        InputStream descriptorIS =
            getClass().getClassLoader().getResourceAsStream("yaml-configuration-files/term-type-descriptors.yaml");
        File descriptorFile = new File(directoryFile, DefaultYamlFactory.SNAKE_YAML_TYPE_DESCRIPTOR_RESOURCE_NAME);
        FileOutputStream descriptorFOS = new FileOutputStream(descriptorFile);
        copy(descriptorIS, descriptorFOS);
        descriptorFOS.flush();
        descriptorFOS.close();
        
        ClassLoader classLoader = new URLClassLoader(new URL[] { directoryFile.toURI().toURL() }, getClass().getClassLoader());
        
        DefaultYamlFactory factory = new DefaultYamlFactory(classLoader);
        Yaml yaml = factory.newYaml();
        InputStream termIS = getClass().getClassLoader().getResourceAsStream("yaml-data-files/terms.yaml");
        
        Object loadedObject = yaml.load(termIS);
        assertThat(loadedObject, notNullValue());
        assertThat(loadedObject, instanceOf(Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object>  objectMap = (Map<String, Object>) loadedObject;
        
        Object termObject = objectMap.get("terms");
        assertThat(termObject, notNullValue());
        assertThat(termObject, instanceOf(List.class));
        @SuppressWarnings("unchecked")
        List<Term> terms = (List<Term>) termObject;
        assertThat(terms.size(), equalTo(2));
        
        assertThat(terms.get(0), notNullValue());
        assertThat(terms.get(0), instanceOf(Term.class));
        assertThat(terms.get(0).getEnglishWord(), notNullValue());
        assertThat(terms.get(0).getEnglishWord(), equalTo("street"));
    }
    
    private long copy(InputStream in, OutputStream out) throws IOException {
    	long count = 0l;
    	byte[] buffer = new byte[4096];
    	int len;
    	while ((len = in.read(buffer)) > 0) {
    		count += len;
    		out.write(buffer, 0, len);
    	}
    	out.flush();
    	return count;
    }
}
