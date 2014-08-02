# SnakeYAML Type Descriptor #

SnakeYAML is often used to provide typed configurations to Java application frameworks. In this context, users
frequently need to edit complex Yaml-encoded object graphs and annotate them with Java type information.
SnakeYAML provides the `!!` prefix to supply such property type information. Using this tag, however, quickly becomes
unwieldy for properties of interface, collection or map types.

`yamltag` alleviates this verbosity by providing: 

- User-defined type annotations via tag names that map to Java class names
- Type declarations for collection and map properties
- Java class literals

Another SnakeYAML use case benefiting from these dispensations is the authoring of test data. One such scenario is the
testing of business logic against JPA classes populated from Yaml files rather than from a database.

## A Simple Example ##

Consider the following Yaml fragment corresponding to an event notifier:

```yaml
--- !!com.acme.notifier.Recipient
name: John Doe
filters:
  - !!com.acme.notifier.Filter
    eventType:		NETWORK
    severity:		DISRUPTION
  - !!com.acme.notifier.Filter
    eventType:		APPLICATION
    severity:		SLOW_RESPONSE
notifications:
  - !!com.acme.notifier.SMSNotification
    phoneNumber:	(321)456-7890
  - !!com.acme.notifier.EmailNotification
    emailAddress: 	john@doe.com
```

`yamltag` simplifies these type annotations by associating tag names with Java classes. Thus, given
the appropriate configuration -and without programming,- the above Yaml document can be made to look like:

```yaml
--- !recipient
name: John Doe
filters: # Look ma: no collection type!
  - eventType:		NETWORK
    severity:		DISRUPTION
  - eventType:		APPLICATION
    severity:		SLOW_RESPONSE
notifications:
  - !smsNotification
    phoneNumber:	(321)456-7890
  - !emailNotification
    emailAddress: 	john@doe.com
```

## What's needed? ##

For these annotations to work, all that is needed is a resource file named `yamltag.yaml` at the top
level of the application's classpath.

For the above example our descriptor file would contain:

```yaml
com.acme.notifier.Recipient:
  tagName: recipient
  listProperties: # Look ma: no collection type!
    filters: com.acme.notifier.Filter
com.acme.notifier.SMSNotification:
  tagName: smsNotification
com.acme.notifier.EmailNotification:
  tagName: emailNotification
```

Multiple descriptor resource files can be present at once and are honored in the order of their classpath occurrence.

Note that property `filter` above is of concrete class `com.acme.notifier.Filter`. By declaring this in the descriptor
file, type annotations can be omitted in Yaml files.

Property `notifications`, on the other hand, is of an *interface* type (`com.acme.notifier.Notification`) so it's
necessary to specify what concrete classes implementing that interface are used when populating the property.
This is where tags `!smsNotification` and `!emailNotification` come handy.

## More on Collection Properties ##

In addition to the type of list properties, `yamltag` also supports specifying the key and value types
of map properties. Thus, given the *translations* map property below:

```java
class Language { ... }
class Term {
    ...
    private Map<Language, String> translations;
    ...
}
```

the following Yaml fragment would populate it:

```yaml
languages:
    - &spanish !language
        englishName: Spanish
        nativeName: Español
    - &portuguese !language
        englishName: Portuguese
        nativeName: Português
terms:
    - !term
        englishWord: street
        translations: # Look ma, no map key/value type!
            *spanish: calle
            *portuguese: rua
    - !term
        englishWord: newspaper
        translations: # Look ma, no map key/value type!
            *portuguese: jornal
            *spanish: periódico
```

The `yamltag.yaml` file required to enable the above form is:

```yaml
com.acme.lexicon.Language:
    tagName: language
com.acme.lexicon.Term:
    tagName: term
    mapProperties:
        translations:
            keyClass: com.acme.lexicon.Language
            valueClass: java.lang.String
```

## Class Literals ##

From time to time frameworks need to be configured with a `java.lang.Class` rather than with a class name.

For these cases a class literal tag exists to populate properties of type `java.lang.Class`.

Thus, for example, given the property:

```java
private Map<Class<?>, Class<?>> interfaceImplementations;
```

the following Yaml fragment would populate it: 

```java
interfaceImplementations:
    !class com.acme.loader.Verifier: !class com.acme.loader.CRCVerifier
    !class com.acme.loader.Deflator: !class com.acme.load.S7ZDeflator
```

## Usage ##

To create enhanced `Yaml` instances simply put your descriptor file(s) on your classpath and then use the supplied factory:

```java
YamlFactory factory = new DefaultYamlFactory();
Yaml yaml = factory.newYaml();
// ... business as usual ...
```
