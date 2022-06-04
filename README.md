# enterprise-archunit

## about

## usage


```xml
<plugin>
    <groupId>pl.klolo.enterprisearchunit</groupId>
    <artifactId>counter-maven-plugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <configuration>
        <ruleSet>IntegrationTestsRuleSet</ruleSet>
    </configuration>
    <executions>s
        <execution>
            <phase>test</phase>
            <goals>
                <goal>archtest</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
        