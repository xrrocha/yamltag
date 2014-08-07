package net.xrrocha.yamltag.example;

import java.util.Map;
import java.util.Set;

public class Person {
    private Name name;
    private Gender gender;
    private Set<Hobby> hobbies;
    private Map<Language, LanguageSkill> languageSkills;

    public Name getName() {
        return name;
    }
    public void setName(Name name) {
        this.name = name;
    }
    public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public Set<Hobby> getHobbies() {
        return hobbies;
    }
    public void setHobbies(Set<Hobby> hobbies) {
        this.hobbies = hobbies;
    }
    public Map<Language, LanguageSkill> getLanguageSkills() {
        return languageSkills;
    }
    public void setLanguageSkills(Map<Language, LanguageSkill> languageSkills) {
        this.languageSkills = languageSkills;
    }
}
