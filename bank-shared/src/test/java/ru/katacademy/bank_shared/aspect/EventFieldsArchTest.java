package ru.katacademy.bank_shared.aspect;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

public class EventFieldsArchTest {
    @Test
    void eventsShouldNotContainPasswordFields() {
        JavaClasses classes = new ClassFileImporter().importPackages("ru.katacademy");

        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..event..")
                .should().haveNameMatching(".*password.*|.*oldPassword.*|.*newPassword.*");

        rule.check(classes);
    }
}

