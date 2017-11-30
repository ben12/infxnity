# infxnity
JavaFX "to infinity and beyond"

[![GitHub license](https://img.shields.io/github/license/ben12/infxnity.svg)](https://github.com/ben12/infxnity/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/ben12/infxnity.svg?branch=master)](https://travis-ci.org/ben12/infxnity)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.ben12:infxnity)](https://sonarcloud.io/dashboard?id=com.ben12%3Ainfxnity)
[![GitHub version](https://badge.fury.io/gh/ben12%2Finfxnity.svg)](https://github.com/ben12/infxnity/releases)

[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=com.ben12:infxnity&metric=lines)](https://sonarcloud.io/dashboard?id=com.ben12%3Ainfxnity)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=com.ben12:infxnity&metric=coverage)](https://sonarcloud.io/dashboard?id=com.ben12%3Ainfxnity)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=com.ben12:infxnity&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.ben12%3Ainfxnity)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=com.ben12:infxnity&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.ben12%3Ainfxnity)


## Features

### [MaskTextFilter](http://infxnity.ben12.eu/apidocs/com/ben12/infxnity/control/text/MaskTextFilter.html)

Example to create a [`TextField`](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextField.html) allowing only french phone numbers: 

```java
final TextField textField = new TextField();
final MaskCharacter[] mask = MaskBuilder.newBuilder()
	.appendLiteral("+33 ")
	.appendDigit('6')
	.appendLiteral(" ")
	.appendDigit(2)
	.appendLiteral(" ")
	.appendDigit(2)
	.appendLiteral(" ")
	.appendDigit(2)
	.appendLiteral(" ")
	.appendDigit(2)
	.build();
textField.setTextFormatter(new TextFormatter<>(new MaskTextFilter(textField, false, mask)));
```

Default text will be "+33 6 00 00 00 00".  
Caret will be placed in 4th position : "+33 |6 00 00 00 00".  
Navigate to the right will do that:  
"+33 6 |00 00 00 00"  
"+33 6 0|0 00 00 00"  
"+33 6 00 |00 00 00"  
"+33 6 00 0|0 00 00"  
"+33 6 00 00 |00 00"  
"+33 6 00 00 0|0 00"  
"+33 6 00 00 00 |00"  
"+33 6 00 00 00 0|0"  
"+33 6 00 00 00 00|"

### [ObservableListAggregation](http://infxnity.ben12.eu/apidocs/com/ben12/infxnity/collections/ObservableListAggregation.html)

An [`ObservableList`](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)(s) aggregation.  
All events of aggregated [`ObservableList`](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)(s) are forwarded.  
The list of aggregated [`ObservableList`](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)(s) can be modified to add, remove or replace one or more of the aggregated [`ObservableList`](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)(s). An appropriate event will be fired.  
An instance of [`ObservableListAggregation`](http://infxnity.ben12.eu/apidocs/com/ben12/infxnity/collections/ObservableListAggregation.html) is a read only list. Any attempt to modify the list will throw an [`UnsupportedOperationException`](https://docs.oracle.com/javase/8/docs/api/java/lang/UnsupportedOperationException.html).  

Example:

```java
final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
final ObservableList<Integer> list2 = FXCollections.observableArrayList(3, 4);
final ObservableList<Integer> list3 = FXCollections.observableArrayList(5, 6);

final ObservableListAggregation<Integer> aggregation = new ObservableListAggregation<>(list1, list2);
System.out.println(aggregation); // [0, 1, 2, 3, 4]

aggregation.getLists().add(list3);
System.out.println(aggregation); // [0, 1, 2, 3, 4, 5, 6]

list1.add(0, -1);
System.out.println(aggregation); // [-1, 0, 1, 2, 3, 4, 5, 6]

list3.remove(Integer.valueOf(6));
System.out.println(aggregation); // [-1, 0, 1, 2, 3, 4, 5]

aggregation.getLists().remove(list1);
System.out.println(aggregation); // [3, 4, 5]
```

### [IFXContentBinding](http://infxnity.ben12.eu/apidocs/com/ben12/infxnity/binding/IFXContentBinding.html)

Used to bind a list to an [`ObservableList`](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html) with a type conversion.

Example:

```java
ObservableList<Person> personList = FXCollections.observableArrayList(person -> new Observable[] { person.nameProperty() });
ObservableList<String> personNameList = FXCollections.observableArrayList();

IFXContentBinding.bind(personNameList, personList, Person::getName);

System.out.println(personNameList); // []

personList.add(new Person("Bob"));
System.out.println(personNameList); // [Bob]

personList.addAll(new Person("Fred"), new Person("John"));
System.out.println(personNameList); // [Bob, Fred, John]

personList.get(0).setName("George");
System.out.println(personNameList); // [George, Fred, John]

```
