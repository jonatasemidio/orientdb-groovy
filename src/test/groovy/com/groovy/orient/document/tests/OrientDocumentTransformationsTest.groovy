package com.groovy.orient.document.tests

import com.groovy.orient.document.orient.Person
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class OrientDocumentTransformationsTest extends Specification {

    @Shared
    def factory

    @Shared
    def db

    def setup() {
        factory = new OPartitionedDatabasePoolFactory()
        db = factory.get('memory:test', 'admin', 'admin').acquire()
        if (!db.exists()) {
            db.create()
        }
    }

    def cleanup() {
        db.close()
    }

    def 'test that transformation applied right'() {
        given: 'test person entity class'
            def person = new Person(firstName: 'Gomer Simpson')
        when: 'start transaction and save document'
            db.begin()
            person.save()
            db.commit()
        then: 'assert that firstName equals to assigned before'
            assert person.firstName == 'Gomer Simpson'
    }

    def 'test query execution method with different params'() {
        given: 'insert new persons'
            db.begin()
            new Person(lastName: 'Simpson', firstName: 'Gomer').save()
            new Person(lastName: 'Simpson', firstName: 'Lara').save()
            new Person(lastName: 'Simpson', firstName: 'Bart').save()
            db.commit()
        when: 'execute queries'
            Person gomer = Person.executeQuery('select from Person where firstName=?', 'Gomer').first()
            Person lara = Person.executeQuery('select from Person where firstName=:firstName and lastName like :lastName', [firstName: 'Lara', lastName: '%impso%']).first()
            Person bart = Person.executeQuery('select from Person where firstName=? and lastName like ?', 'Bart', '%imps%').first()
        then: 'check retrieved values'
            assert gomer.firstName == 'Gomer'
            assert lara.firstName == 'Lara'
            assert bart.firstName == 'Bart'
    }
}
