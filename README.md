# infxnity
JavaFX "to infinity and beyond"

## Features

### MaskTextFilter

Example for create a `TextField` allowing only french phone numbers: 

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
An navigation to the right will do that:  
"+33 6 |00 00 00 00"  
"+33 6 0|0 00 00 00"  
"+33 6 00 |00 00 00"  
"+33 6 00 0|0 00 00"  
"+33 6 00 00 |00 00"  
"+33 6 00 00 0|0 00"  
"+33 6 00 00 00 |00"  
"+33 6 00 00 00 0|0"  
"+33 6 00 00 00 00|"
