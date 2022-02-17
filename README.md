# Scavenger

Scavenger is a cli application for removing unused variable declarations from java code.  

Variable is unused if it is not used anywhere or is used in unused declarations.  
If `--not-deep` flag is `on`, than variable is unused only if it is not used anywhere.

The app can work with single a file and with a directory.  
Files with java code must compile without errors.  
The app overwrites the files.

---
build:
```
./gradlew build
```
run:
```
java -jar build/libs/scavenger-1.0-SNAPSHOT.jar --help

Usage: cli [OPTIONS] FILE
Options:
  --not-deep   Do not remove deep (iteratively until there are no more)
  -q, --quiet  No output to console
  -h, --help   Show this message and exit
```

<details><summary>Example 1</summary>
  
Before:  
  
```java
class myClass {
    public int myFun() {
        int a = 1;
        int r, z;
        int removeMe = 42;
        int removeMe2 = removeMe + 1;
        if (a > 1) {
            int b = 6;
        }
        if (a > 2) {
            int b = 6;
            b += 3;
        }
        r = a;
        return 5;
    }
}
```
  After:
  ```java
  class myClass {
  
    public int myFun() {
        int a = 1;
        int r;
        if (a > 1) {
        }
        if (a > 2) {
            int b = 6;
            b += 3;
        }
        r = a;
        return 5;
    }
}
  ```
  
  Console:
  ```
java -jar build/libs/scavenger-1.0-SNAPSHOT.jar src/test/resources/test.java
  
File src/test/resources/test.java:
removed on iteration 1 :
variable declaration on line 4 with name z  (scopeId 1)
variable declaration on line 6 with name removeMe2  (scopeId 1)
variable declaration on line 8 with name b  (scopeId 2)
removed on iteration 2 :
variable declaration on line 5 with name removeMe  (scopeId 1)
  ```

</details>


<details><summary>Example 2</summary>
  
Before:  
  
```java
class myClass {
    public int myFun() {
        int a1 = 1;
        int a2 = a1 + 1;
        int a3 = a2 + 1;
        int a4 = a3 + 1;

        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += 1;
        }
        return sum;
    }
}

```
  After:
  ```java
  class myClass {

    public int myFun() {
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += 1;
        }
        return sum;
    }
}

  ```
  
  Console:
  ```
java -jar build/libs/scavenger-1.0-SNAPSHOT.jar src/test/resources/test2.java
  
File src/test/resources/test2.java:
removed on iteration 1 :
variable declaration on line 6 with name a4  (scopeId 1)
removed on iteration 2 :
variable declaration on line 5 with name a3  (scopeId 1)
removed on iteration 3 :
variable declaration on line 4 with name a2  (scopeId 1)
removed on iteration 4 :
variable declaration on line 3 with name a1  (scopeId 1)

  ```

</details>


<details><summary>Example 3</summary>
  
Before:  
  
```java
class myClass {
    public int myFun() {
        int a1 = 1;
        int a2 = a1 + 1;
        int a3 = a2 + 1;
        int a4 = a3 + 1;

        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += 1;
        }
        return sum;
    }
}

```
  After:
  ```java
  class myClass {

    public int myFun() {
        int a1 = 1;
        int a2 = a1 + 1;
        int a3 = a2 + 1;
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += 1;
        }
        return sum;
    }
}


  ```
  
  Console:
  ```
java -jar build/libs/scavenger-1.0-SNAPSHOT.jar --not-deep src/test/resources/test2.java 
  
File src/test/resources/test2.java:
removed on iteration 1 :
variable declaration on line 6 with name a4  (scopeId 1)
  ```

</details>


