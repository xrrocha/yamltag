package net.xrrocha.yamltag;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import net.xrrocha.yamltag.example.Term;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

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
	
	@Test
	public void allowsConstructorArgsWithTag() {
	    String yamlString = "!term ['term', {\n    russian: срок,\n  spanish: término,\n \n}]";
	    System.out.println(yamlString);
        Yaml yaml = new Yaml(new ClassConstructor());
        Term term = yaml.loadAs(yamlString, Term.class);
        assertThat(term.getEnglishWord(), equalTo("term"));
        assertThat(term.getTranslations().get("spanish"), equalTo("término"));
        assertThat(term.getTranslations().get("russian"), equalTo("срок"));
	}
}

