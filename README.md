# What can Groovy make for OrientDB?

In this project I am trying to provide Groovy way to work with orientdb as simple as possible.
This project contains Groovy AST Transformations trying to mimic grails-entity style.
All useful information you can find in Spock tests dir.

I know about OrientDB object-api, but anyway I started to create this one. I will adopt it for managing relationships also.

##Example usage
I cant name it User in java because it is wrapper :)

```java
public class UserWrapper {

    private ODocument document;

    UserWrapper() {
        this.document = new ODocument()
    }

    UserWrapper(ODocument document) {
        this.document = document
    }

    String getId() {
        return this.document.getIdentity().toString()
    }

    String getFirstName() {
        return (String) this.document.field("firstName")
    }

    void setFirstName(String firstName) {
        this.document.field("firstName", firstName)
    }

    Date getBirthday() {
        return (Date) this.document.field("birth_date") // different "birth_date" field name
    }

    // etc..
}
```
With this library we can simplify it

```groovy
@OrientDocument
class User implements AsDocument {
    String id
    String firstName
    Date birthDay

    List<String> strings

    static transients = ['strings'] // this property will not be persisted into database

    static mapping = {
        id field: 'rid'
        birthDay field: 'birth_date'
    }
}
```
###Quering
```groovy
    List<User> userList = User.executeQuery('select from User where firstName=?', 'Bart')
    List<User> userList2 = User.executeQuery('select from User where firstName=:a and lastName=:b', [a: 'Bart', b: 'Simpson'])
```
###Document creation

```groovy
 // default groovy lang style constructors supported :)
 def person = new Person(firstName: 'First Name', lastName: 'LastName', birthDay: new Date())
 person.save() // or person.document.save()
 // you can access document properties with groovy syntax sugar
 assert person.firstName == 'First Name' // this will call a getter getFirstName()
```

## How it works in details
``` @OrientDocument ``` annotation will apply AST Transformation which will transform your class similar to java one showed before.
##### 1. Take mapping closure and read mapping properties from it
##### 2. Create empty constructor and ODocument one
##### 3. Delete properties and create needed getters and setters
##### 4. As you implement AsDocument trait, your class would have delegate methods to ODocument, so you can simply call user.save() or access document instance directly with user.document
