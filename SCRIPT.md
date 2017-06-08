# 脚本语言 （持续完善中...）
我个人感觉命令行虽然比界面操作和学习曲线要高一些，但是它最大的优点是可自动化，可定制特性，也许通过界面直接操作上手比较快，但是一旦程序员掌握的了某种脚本后，可以通过自己编写脚本程序将日常的工作自动化，也可以通过编写脚本解决特定的问题，想像一下如果linux中没有shell, windows中没有bat，我们的工作会是什么情况，所以作为一个命令行调试器，通常都会支持某种脚本（或简单或复杂），例如：linux中的gdb，windows中的windbg，但是JDK中自带的JDB不仅功能比较少，对于自动化处理能力更是比较差，基于以上的原因我专门定义了larva脚本，一方面减少调试人员的重复劳动，另一方面也允许调试人员面对特定问题的时候可以做自动化的定制。
## 样例：
jdb目录中有几个larva文件，是我调试基于JVM的不同语言时作测试使用的，基本中包含了脚本的一些常用用法，可以参考：
java.jdb 调试普通java工程时使用的脚本；
jython.jdb 调试jython工作时使用的脚本；
scala.jdb 调试scala工程时使用的脚本；
## 表达式
larva脚本的表达式基本上与标准的java表达式是一致的，所有如果熟悉java语言就可以很快掌握，但是一方面是为调试特殊定制的，另一方面受限于JVM所提供的功能，还有几处不同。
### 类型
#### 基本类型 
larva脚本支持8种基本数据类型，完全符合java虚拟机规范<br>
byte, char, short, int, long, float, double, boolean<br>
#### 构造类型 
构造类型不支持new操作，也不支持任何对象方法的调用<br>
ArrayType 与java语言类似<br>
String 也是与java语言类似，根据JVM规范支持常用的转义字符，但是不支持"\uxxx"方式<br>
ClassType 使用方式与java语言类似<br>
### 常量
支持8种基本类型的常量也支持String类型的常量<br>
整形：10, 12L<br>
实数：12.03F, 20.89D, 1.0E10, 1.0E10F, 1.0E10D<br>
字符串: "hello larva!", "hello\tlarva!" "hello larva!\n"<br>
### 自动变量
larva脚本只支持一种变量，称为自动变量，自动变量名称必须以@符号开头（这样定义主要是为了不与被调试目标<br>
程序的变量产生冲突）自动变量之所以被称为自动变量主要是因为其类型在运行期可变（通过赋值可以改变其类型），<br>
这与java语言不同，但是除了赋值操作之外，其它的操作都与java语言保持一致，必须是合法的类型；<br>
例如：@var = 10；// 变量为整形<br>
&emsp;&emsp;&emsp;print.value @var + 20; // 合法操作<br>
&emsp;&emsp;&emsp;@var = "hello larva"; // 变量为字符串类型<br>
&emsp;&emsp;&emsp;print.value @var + ":)"; // 合法操作<br>
&emsp;&emsp;&emsp;@var = true; // 变量为布尔类型<br>
&emsp;&emsp;&emsp;print.value @var + 20; // 不合法操作<br>
自动变量作用域是其定义的块之中，首次赋值会先到外层查找，如果找到已经定义则会使用这个已经定义，反之则新创建<br>
一个自动变量（作用域类似python）<br>
### 操作符
larva支持所有java语言所支持的操作符（除了new操作符），包括操作符的优先级<br>
## 语句
larva支持表达式、命令、控制三种控制语句：<br>
### 表达式语句
一个表达式组成的语句，必须以分号结束<br>
例如：@var = 10; @var += 10; <br>
&emsp;&emsp;&emsp;@str = "hello larva!"; @str += @var;<br>
&emsp;&emsp;&emsp;@var = 12.36F * 10;<br>
### 命令语句
一条命令组成的命令，必须以分号结果<br>
例如：class.method "com.runbox.debug.Demo..*";<br>
&emsp;&emsp;&emsp;method.bytecode "java.lang.String.<init>";<br>
&emsp;&emsp;&emsp;@id = 10L; print.value @id;<br>
### 控制语句
控制语句分为分支、循环、跳转三种：<br>
#### 分支语句
分支语句只有两种形式：<br>
第一种：<br>
if (expr) {<br>
} <br>
第二种：<br>
if (expr) {<br>
} else {<br>
}<br>
expr与java语言一样，运算结果必须是布尔类型，花括号是不能省略的，即使花括号中只有一条语句；<br>
#### 循环语句
while (expr) {<br>
}<br>
expr与java语言一样，运算结果必须是布尔类型，花括号是不能省略的，即使花括号中只有一条语句；<br>
#### 跳转语句
break, continue, return[expr]<br>
## 例程
larva允许调试人员自定义例程（函数），分内建、自定义两种：<br>
### 自定义例程
以抽象重复的部分，定义形式如下：<br>
def $routine([@arg1[, @arg2]]) {<br>    
}<br>
例程名称必须以$（美元符号）开头，每个参数都是自动变量，花括号是必须的，例程体中如果没有return语句<br>
则不存在返回值，例程参数的作用域与普通自动变量不同，不管外层是否已经定义过同名变量，都会新创建一个新的<br>
自动变量如果使用return语句且带有表达式，则将表达式的运算结果作为例程的返回结果，例如：<br>
def $routine() {  // 例程返回为整形数 <br>
&emsp;&emsp;&emsp;return 10 + 20;<br>
}<br>
print.value $routine();<br>
def $routine() { // 例程返回为对象类型<br>
&emsp;&emsp;&emsp;return this.map;<br>
}<br>
print.value $routine();<br>
def $routine() { // 例程无返回类型<br>
&emsp;&emsp;&emsp;return;<br>
}<br>
例程支持内嵌定义且其作用域只限于其所定义的例程中，例如：<br>
def $routine1() {<br>
&emsp;&emsp;&emsp;@var = 10;<br>
&emsp;&emsp;&emsp;def $routine2(@arg) {<br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;print.value @arg;<br>
&emsp;&emsp;&emsp;}<br>
&emsp;&emsp;&emsp;def $routine3(@arg) {<br>
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;$routine(@arg); <br>
&emsp;&emsp;&emsp;}<br>
&emsp;&emsp;&emsp;$routine2(@var);<br>
}<br>
print.value $routine1();<br>
### 内建例程
larva内部已经内建了几个例程（当前只有几个），方便使用：<br>
$time()<br>
说明：获取当前系统时间戳<br>
参数：无<br>
$sleep(@timeout)<br>
说明：暂停被调试的目标程序段时间<br>
参数：@timeout<br>
$equals(@string1, @string2)<br>
说明：判定两个字符串是否相等<br>
参数：@string1 字符串1<br>
&emsp;&emsp;&emsp;@string2 字符串2<br>
$find(@string1, @string2)<br>
说明：子字符串查找<br>
参数：@string1 被查找的字符串<br>
&emsp;&emsp;&emsp;@string2 要查找的字符串<br>
$length(@string)<br>
说明：获取一个字符串长度<br>
参数：@string 字符串<br>
