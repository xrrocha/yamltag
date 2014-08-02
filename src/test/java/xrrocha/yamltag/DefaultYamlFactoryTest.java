package xrrocha.yamltag;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import xrrocha.yamltag.example.Hobby;
import xrrocha.yamltag.example.Language;
import xrrocha.yamltag.example.LanguageSkill;
import xrrocha.yamltag.example.Name;
import xrrocha.yamltag.example.Person;

public class DefaultYamlFactoryTest {
    @Test
    public void loadsBulkDataCorrectly() {
        DefaultYamlFactory factory = new DefaultYamlFactory();
        Yaml yaml = factory.newYaml();
        
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml-data-files/people.yaml");
        Object loadedObject = yaml.load(is);

        assertThat(loadedObject, notNullValue());
        assertThat(loadedObject, instanceOf(Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object>  objectMap = (Map<String, Object>) loadedObject;
        
        Object peopleObject = objectMap.get("people");
        assertThat(peopleObject, notNullValue());
        assertThat(peopleObject, instanceOf(List.class));
        @SuppressWarnings("unchecked")
        List<Person> people = (List<Person>) peopleObject;
        assertThat(people.size(), equalTo(2));
        
        Person johnDoe = people.get(0);
        assertThat(johnDoe, notNullValue());
        
        assertThat(johnDoe.getName(), notNullValue());
        assertThat(johnDoe.getName(), equalTo(new Name("John", "Doe")));

        assertThat(johnDoe.getHobbies(), notNullValue());
        assertThat(johnDoe.getHobbies().size(), equalTo(2));
        assertThat(johnDoe.getHobbies().iterator().next(), notNullValue());
        assertThat(johnDoe.getHobbies().iterator().next(), instanceOf(Hobby.class));

        assertThat(johnDoe.getLanguageSkills(), notNullValue());
        assertThat(johnDoe.getLanguageSkills().size(), equalTo(2));
        
        Object languageObject = johnDoe.getLanguageSkills().keySet().iterator().next();
        assertThat(languageObject, notNullValue());
        assertThat(languageObject, instanceOf(Language.class));
        Language language = (Language) languageObject;

        Object languageSkillObject = johnDoe.getLanguageSkills().get(language);
        assertThat(languageSkillObject, notNullValue());
        assertThat(languageSkillObject, instanceOf(LanguageSkill.class));
        LanguageSkill languageSkill = (LanguageSkill) languageSkillObject;
        assertThat(languageSkill.getSpeaking(), notNullValue());
        assertThat(languageSkill.getReading(), notNullValue());
        assertThat(languageSkill.getWriting(), notNullValue());
    }
}
