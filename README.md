# Basic Kotlin Syntax into Java Converter

# About

The program translates the basic Kotlin Syntax into a Java code. Kotlin.g4 is a grammar written using antl4 that accepts a code written in Kotlin, while [Kotlin2Java.java](http://kotlin2java.java) converts the code using rules specified. However, it does not handle String templates.

# How to run?

run the following command in your terminal

java Kotlin2Java [file].kt  #creates a default [input.java](http://input.java) file where the code is stored

java Kotlin2Java [file].kt [file].java  # creates a file with a specified name

# Example

## Kotlin code

```kotlin
fun main() {
val rectangle = Rectangle(5.0, 2.0)
val triangle = Triangle(3.0, 4.0, 5.0)
println("Area of rectangle is ${rectangle.calculateArea()}, its perimeter is ${rectangle.perimeter}")
println("Area of triangle is ${triangle.calculateArea()}, its perimeter is ${triangle.perimeter}")
}
```

```kotlin
abstract class Shape(val sides: List<Double>) {
val perimeter: Double get() = sides.sum()
abstract fun calculateArea(): Double
}
```

```kotlin
interface RectangleProperties {
val isSquare: Boolean
}
```

```kotlin
class Rectangle(
var height: Double,
var length: Double
) : Shape(listOf(height, length, height, length)), RectangleProperties {
override val isSquare: Boolean get() = length == height
override fun calculateArea(): Double = height * length
}
```

```kotlin
class Triangle(
var sideA: Double,
var sideB: Double,
var sideC: Double
) : Shape(listOf(sideA, sideB, sideC)) {
override fun calculateArea(): Double {
val s = perimeter / 2
return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC))
}
}
```

# Translation

```java
import java.util.*;
public class output27{
public static void main ( String[] args ) {
final Rectangle rectangle = new Rectangle( 5.0 , 2.0 ) ;
final Triangle triangle = new Triangle( 3.0 , 4.0 , 5.0 ) ;
System.out.println( rectangle.calculateArea( ) ) ;
System.out.println( rectangle.getperimeter() ) ;
System.out.println( triangle.calculateArea( ) ) ;
System.out.println( triangle.getperimeter() ) ;
}
static abstract class Shape {
final List<Double> sides;
public Shape( final List<Double> temp0 ){
sides=temp0;
}
private Double perimeter;
public double getperimeter (){
perimeter= sides.stream().mapToDouble(Double::doubleValue).sum() ;
return perimeter; }
abstract public double calculateArea ( ) ;
}
interface RectangleProperties { final Boolean isSquare =null ;
}
static class Rectangle extends Shape implements RectangleProperties {
double height;
double length;
public Rectangle( double temp0 , double temp2 ){
super( List.of( temp0 , temp2 , temp0 , temp2 ) );
length=temp2;
height=temp0;
}
private Boolean isSquare;
public Boolean getisSquare (){
isSquare= length == height ;
return isSquare; }
@Override
public double calculateArea ( ) {
return height * length ; }
}
static class Triangle extends Shape {
double sideA;
double sideB;
double sideC;
public Triangle( double temp0 , double temp2 , double temp4 ){
super( List.of( temp0 , temp2 , temp4 ) );
sideC=temp4;
sideB=temp2;
sideA=temp0;
}
@Override
public double calculateArea ( ) {
final double s = getperimeter() / 2 ;
return Math.sqrt( s * ( s - sideA ) * ( s - sideB ) * ( s - sideC ) ) ;
}
}
}
```

# Result

![Basic%20Kotlin%20Syntax%20into%20Java%20Converter%208874fdbba36d4862ab592d7582c470d2/Untitled.png](Basic%20Kotlin%20Syntax%20into%20Java%20Converter%208874fdbba36d4862ab592d7582c470d2/Untitled.png)