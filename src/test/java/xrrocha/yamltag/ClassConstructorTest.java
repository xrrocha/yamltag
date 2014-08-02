package xrrocha.yamltag;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import xrrocha.yamltag.ClassConstructor;

public class ClassConstructorTest {
	@Test
	public void buildsProperClassLiteral() {
		String yamlString = "dateClass: !class java.util.Date";
		Yaml yaml = new Yaml(new ClassConstructor());
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) yaml.load(yamlString);
		
        assertThat(result.get("dateClass"), notNullValue());
        assertThat(result.get("dateClass"), instanceOf(Class.class));
        assertEquals(result.get("dateClass"), Date.class);
	}
}

