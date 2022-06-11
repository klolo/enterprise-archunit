# enterprise-archunit

## about

https://archunit.org[Archunit] is great tool for testing your architecture. Works perfectly when you have monolith and architecture tests next to the other kind of tests. Unfortunately, when you work in big company need sometime execute the same set of aerch tests on multiple projects for ensure that all of them are consistent. I created this maven plugin for simplify this proces.  

## project structure

enterprise-archunit
    |- enterprise-archunit-ruleset-api - contains API for implementation tests set, without no external dependencies
    |- enterprise-archunit-mojo - maven plugin for execution tests during build process
    |- enterprise-archunit-example-rules - example of implementation rules with API
    |- enterprise-archunit-example-usage - example of usage plugin in maven


## usage

If you want to use to aproach you have to write some rules, grouped in rule set. Each rule set class have to extend EnterpriseArchTestRuleSet. After defining rules with plain archunit, just register it with some name with method register. This name is important, while you have possibility to skip this test in yaml configuration. I will describe this configuration process later. Example of such rule set:

```java
import com.tngtech.archunit.lang.ArchRule;
import pl.klolo.archtests.ruleset.api.EnterpriseArchTestRuleSet;

public class IntegrationTestsRuleSet extends EnterpriseArchTestRuleSet {

    public final ArchRule testMethodPackagePrivate = methods()
            .that()
            .areAnnotatedWith(Test.class)
            .should()
            .bePackagePrivate();

    public final ArchRule testMethodNameStartWithShould = methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("Test")
            .and()
            .areAnnotatedWith(Test.class)
            .should()
            .haveNameStartingWith("should");

    public IntegrationTestsRuleSet() {
        register("test_method_are_package_private", testMethodPackagePrivate);
        register("test_method_name_start_with_should", testMethodNameStartWithShould);
    }

}
```

When you have created rul set build it into some jar (in this example it is: enterprise-archunit-example-rules) and add maven plugin in pom, where you need to execute your tests:


```xml
 <plugins>
    <plugin>
        <groupId>pl.klolo</groupId>
        <artifactId>enterprise-archunit-mojo</artifactId>
        <dependencies>
            <dependency>
                <groupId>pl.klolo</groupId>
                <artifactId>enterprise-archunit-example-rules</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
        <executions>
            <execution>
                <phase>test</phase>
                <goals>
                    <goal>archtest</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

It is almost all. Last, required step is creating configuration file _archtest-config.yaml_ (you can override name of this by setting _configurationFile_ parameter in plugin configuration). This file will define which rule set should be executed on which package and you can disable some rule if some cases e.g in legacy code. Example of such configuration

```yaml
rulesets:
  - clazz: pl.klolo.archtests.example.rules.IntegrationTestsRuleSet
    applyTo: pl.klolo.archtests.example.it

  - clazz: pl.klolo.archtests.example.rules.DomainModuleTestsRuleSet
    applyTo: pl.klolo.archtests.example.domain
    disabled:
      - domain_classes_implements_Serializable
```

Another way to disable test validation in your code is puting pl.klolo.archtests.ruleset.api.SkipArchitectureValidation annotation before your problematic class.