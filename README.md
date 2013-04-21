programming-thinking
====================


昨晚团队同事放弃周末休息和打游戏的时间加班写程序。问他在做什么，原来是要将以前用列表方式展示的数据
改为以树形方式展示。在帮他一起看代码的过程中发现其编程的思维过程存在一定问题，这导致程序逻辑混乱不
清，虽然基本功能实现了，但数据稍有变化程序就出BUG，他正为这些BUG而耗费脑细胞呢。

为了让他认清问题所在，我同他一起分析问题、一起讨论程序实现逻辑、并重新编写了核心程序。一个多小时的
过程让我们认识到编程思维是比语言语法更重要的东西，而这种思维过程的训练正是编程新手所缺乏的。特撰此
文记录我们的编程过程，希望与大家共同探讨如何用正确的思维方式解决实际的编程问题。

# 一、问题描述

有一组数据（Java对象）先前在界面上以列表方式展示，现在需要将这些数据按所属类别以树形方式展示。数据
类别的名称规范与Java的包名规范相同，即类似于a.b.c的形式。所谓树形展示方式即是将数据类型名称以点拆
分开来做为树的结点，并在这些树形结点下展示数据。

整个问题分两部分：前端展现和后端数据组织。前端需要使用树形组件展示数据，而后端则需要按树形组件的要
求提供相应数据结构的数据。前端树形组件比较成熟，不是本文说明的重点。我们重点解决如何将一组Java对象
按其类别名称（包名）构造出适于树形组件展示的数据。

前面这段描述其实已经隐含了一种分层解决问题的思路。这也是编程过程中常见的思维模式，即先将大的问题划
分为多个小问题，然后逐个解决。而这个过程中最关键的是要定义出各个小问题之间如何传递数据，此即接口的
定义。有经验的程序员由于对常用技术有更深的了解，通常可以直接得出如何划分问题以及如何定义接口的思路，
而没有经验的程序员在这种时候往往会对问题感到无所适从。不论如何，这种以分层的方式将一个大问题划分为
多个小问题的思路是需要我们在编程生涯中不断训练和强化的。


# 二、确定接口


我们在前面的问题描述中将问题划分为前端和后端两部分，开始写代码之前我们首先要定下一个良好的接口。接
口有四个基本要素：名称、输入、输出、异常。


名称是接口的第一要素，也是接口最关键的要素之一。名称要求没有歧义、能清晰地表达功用。当然我们也要认
识到取一个好名称也是非常不容易的，实际编程过程中可以根据接口的适用范围花费适当的精力去取名称，比如
只在局部使用的名称可以要求低一些，而在系统中大面积使用的接口，则需要不断追求找到最合适的名称。


输入是我们要求“客户”（调用者）提供的数据。我们对客户的要求越低，就意味着我们的兼容性越广，容错能力
越强，而客户就越方便。因此设计输入参数时，其类型应该尽量泛化，以支持更多的数据类型。但同时输入又应
该尽量清晰，最好能让客户很容易地知道他应该准备什么样的数据来调用接口。因此输入参数同时又应该尽量明
确，不能过于泛化以至于超出接口的处理范围。具体到Java语言来讲，输入参数应该尽量使用范型、尽量使用明
确的数据类型；尽量避免使用Object、Map等数据类型。


输出是我们返回给“客户”的数据。我们返回的数据应该尽量明确、清晰地表达数据的意图。对于Java语言来讲，
同样要避免返回Object、Map等数据类型的数据。


异常其实也是输出的一部分，但关于异常的声明和处理原则与输出有较大不同，通常异常的定义对于应用型项目
和框架型项目来讲会有不同的处理模式，这里我们不展开叙述。


具体到上面的问题，我们在Java代码中定义出如下程序接口（这里仅给出方法名，就不给出类名了）：

```java
    public static List getTreeData(List list)
```

在上面的接口中，我们将输入参数暂时定义为任意对象的List集合，输出结果定义为Node的List集合。这意味
着我们可以将任意对象转换为树形结构。需要注意的是，我们专门定义了一个Node类表示树中的结点（Node的
定义见后）。实际工作中我们发现有很多程序员似乎感觉创建类是一种奢侈的形为从而避免创建新的类，取而代
之的是习惯于使用Object、Map等通用数据结构表示任意对象。例如有人可能会将这个方法的返回值类型设计
为List。甚至有经验的编程人员也会这么做，他们的理由通常是“写这么小一个功能还要那么多类，麻不麻烦啊”。
这里其实不是麻烦的问题，而是编程思维的问题。

```java
public static class Node {
    public String id; // 供树形组件展示的结点ID
    public String text; // 供树形组件展示的文本
    public List children; // 子结点
}
```


# 三、编写单元测试


有了上面的接口定义，我们就可以先模仿使用者调用一下我们的接口，这可以站在用户的角度感受我们设计的接
口好不好用。我们实现的简单测试用例如下：

```java
@Test public void testGetTreeData() {
    List nodes = getTreeData(Arrays.asList(
       new Params().add("id", "1").add("text", "A").add("package", "a.b.c"),
       new Params().add("id", "2").add("text", "B").add("package", "a.b"),
       new Params().add("id", "3").add("text", "C").add("package", ""),
       new Params().add("id", "4").add("text", "D").add("package", "a.b.c")
    ));    System.out.println(JsonUtil.toJson(nodes));
    // assert 断言
}
```


对于上面的测试，我们期望的树形结构如下所示：

```text
|--a
|   |--b
|   |   |--c
|   |   |   |--1-a.b.c.A
|   |   |--2-a.b.B
|--3-C
        |--4-a.b.c.D
```





#四、总体分析


接口确定，单元测试写完，现在可以开始写代码了吧？


稍等！我们先要分析一下总体实现思路，否则写出来的代码可能漏洞百出，逻辑混乱。这也是编程中比较难的地
方。有人将这种总体分析称为算法，在某种程度上我认可这种说法，但为了避免被“做应用开发的程序员其实不
需要懂算法“的观点所影响，我们称这个过程为总体分析，即确定总体的解决思路。


回头看我们上面的问题，输入是一个任意对象的List集合，返回的结果是一个包含多个根结点的树的集合。输
入集合中的每个对象在结果树中都应该有且只有一个唯一的对应结点。如果我们能找到输入集合中的每个对象在
结果树中的位置，然后将该对象转换为结点“挂接”在指定的位置，就可以完成转换。


按照上述思路，我们编写如下代码：

```java
public static List getTreeData(List list) {
    List ret = new ArrayList();
    for (Object o : list) {
        // 找到o在树中的正确位置并插入
    }
    return ret;
}
```


上面的代码初步确定了程序的主体结构，即通过循环处理每个对象，找到对象在树中的位置并插入相应结点，当
所有对象处理完成时，整个程序就完成了。


# 五、高层抽象


接下来，我们需要进一步细化如何找到对象在树中的位置并插入结点。这里也分为两步：第一步是找到o在树中
的位置，这个位置就是o对应结点的父结点；第二步是将o转换为Node对象并插入到其父结点的子结点集合中。
据此我们进一步完善代码如下：

```java
public static List getTreeData(List list) {
    List ret = new ArrayList();
    for (Object o : list) {
        // 找到对象的父结点
        Node parent = getParentNode(o);
        // 插入对象
        Node node = createLeafNode(o);
        parent.children.add(node);
    }
    return ret;
}
           
public static Node getParentNode(Object o) {
    return null;
}
           
public static Node createLeafNode(Object o) {
    return null;
}
```


在写上面代码的过程中，我们是先写出对getParentNode和createLeafNode方法的调用，由编译器提示错
误，我们再来写这两个方法的声明。强调这样的书写顺序是因为这体现了我们编程的思维过程。


通过上面一步步的分析，我们在前面总体分析方案的基础上，一边写代码骨架，一边继续完善代码细节。这体现
了从抽象思维从粗到细的过程。我们接下来的工作是进一步细化骨架代码中每一个需要完善的点。上面的代码中，
getTreeData方法已基本完成，而getParentNode和createLeafNode两个方法还没有完成，这时我们就
可以分别考虑这两个方法的实现。


# 六、逐步细化1


上一步留下来两个问题需要细化，我们先解决根据对象创建叶子结点的问题，因为这个问题看似比较容易一些。
我们直接写出如下代码：

```java
public static Node createLeafNode(Object o) {
    Node ret = new Node();
    ret.id = null; // 如何从对象中获取结点ID？
    ret.text = null; // 如何从对象中获取结点显示文本？
    return ret;
}
```

写代码的过程中我们忽然发现这个看似简单的方法也不好实现，原因是我们不知道如何从Object中获取结点的
ID和显示文本。


怎么办呢？实际过程中我们最好将这个问题先放在一边，因为我们要避免自己延着一条路深入下去从而迷失了方
向。这也是编程中过程中非常重要的一点，特别是在做大型架构或框架开发且对其整体思路不是特别有把握的时候。


# 七、逐步细化2


上一步编写了createLeafNode的总体实现代码，接下来我们再继续细化getParentNode方法。


这个方法是要根据一个对象，找出其对应的父结点。从问题描述中我们可以知道，每个对象都有一个数据类别属
性，类似于a.b.c的形式。这个对象的父结点其实就是对象类别在树中所对应的结点。如果树中的每个结点都有
一个类似a.b.c的包含其所有祖先结点的ID（我们称为fullID），那实现getParentNode就非常容易了。


我们可以准备一个Map的数据对象，用于存放结点的fullID和结点的对应关系，那么getParentNode方法就
可以以类别名称为key从这个map中获取父结点。同时还需要考虑，当父结点不存在的时候，我们就需要根据结
点的fullID创建所有的父结点。当然创建结点的过程中同样需要检查结点是否已经存在。根据这个思路，我们
编写了如下代码：

```java
private static Node getParentNode(Map map, Object o) {
    String category = null; // 获取对象类别
    Node ret = map.get(category);
    if (ret == null) {
        ret = createParentNode(map, category);
    }
    return ret;
}
         
private static Node createParentNode(Map map, String category) {
    int index = category.lastIndexOf(".");
    Node ret = new Node();
    if (index != -1) {
        String parentFullId = category.substring(0, index);
        Node parent = map.get(parentFullId);
        if (parent == null) {
            parent = createParentNode(map, parentFullId);
        }
        parent.children.add(ret);
        ret.id = category;
         ret.text = category.substring(index + 1);
         ret.children = new ArrayList();
    }
    else {
         ret.id = category;
        ret.text = category;
        ret.children = new ArrayList();
    }
    map.put(category, ret);
    return ret;
}
```

由于存放所有结点的map需要被在整个方法期间被共享，因此需要修改getParentNode方法接口。这也是我们
在开发过程中经常碰到的现象，即前期设计的接口因为后期的细化而面临不断的调整，这是正常的，也是我们不
断深入理解系统和领域的必然过程。


# 八、低层抽象


经过前面两次细化，我们留下了一些问题，即如何获取对象的特定属性（如对象的ID、显示文本、所属类别等）。
这其实又是一个共性问题，这个问题也涉及两个方面：一是如何获取属性，二是如何定义属性映射关系（即ID
和对象中的哪一个或几个属性对应）。前一个问题可以使用反射、接口等方式实现；后一个问题则属于配置机制，
可以通过注解、外部XML等方式实现。考虑本文篇幅，这里不予展开。


我们这里使用一种临时写死的方式实现，代码如下：

```java
private static Node getParentNode(Map map, Object o) {
    String category = (String)((Params)o).get("category"); // 获取对象类别
    Node ret = map.get(category);
    if (ret == null) {
        ret = createParentNode(category);
    }
    return ret;
}
         
private static Node createLeafNode(Object o) {
    Node ret = new Node();
    ret.id = (String)((Params)o).get("id"); // 如何从对象中获取结点ID：暂时写死
    ret.text = (String)((Params)o).get("text"); // 如何从对象中获取结点显示文本：暂时写死
    return ret;
}
```

#九、运行测试


至此程序的总体框架和细节基本实现，现在可以喝一杯水，并享受单元测试变绿的满足感了。


... ...


测试失败，控制台什么都没有输出？一定是我们漏掉了什么。


再回头梳理程序结构，我们发现我们没有向返回结果的集合中放置我们创建好的任何结点，返回的结点集合为空。
原来如此！我们会心一笑，这个问题好解决。


# 十、查漏补缺


由于我们前面的“粗心大意”，返回集合为空。事实上，这种现象很正常。因为我们前面整个过程是一个从高级抽
象到低层实现、逐层递进的过程。在我们不断向“下”走的过程中，我们很容易忽略高层的一些细节问题。这时
候，单元测试和整个系统的测试的作用就显现出来了。


单元测试暴露的问题让我们再次仔细从上到下程序的实现逻辑。通过分析我们发现创建的结点放到在map中但是
没有放到ret中，同时我们注意到只有map中的顶层根结点需要到ret中，而创建结点的时候最清楚该结点是否
是顶层结点。我们修改后完整代码如下：

```java
@Test public void testGetTreeData() {
    List nodes = getTreeData(Arrays.asList(
        new Params().add("id", "1").add("text", "A").add("category", "a.b.c"),
        new Params().add("id", "2").add("text", "B").add("category", "a.b"),
        new Params().add("id", "3").add("text", "C").add("category", ""),
        new Params().add("id", "4").add("text", "D").add("category", "a.b.c")
    ));
    System.out.println(JsonUtil.format(nodes));
    // assert 断言
}
         
public static class Node {
    public String id; // 供树形组件展示的结点ID
    public String text; // 供树形组件展示的文本
    public List children; // 子结点
}
         
public static List getTreeData(List list) {
    List ret = new ArrayList();
    Map map = new HashMap();
    for (Object o : list) {
        // 找到对象的父结点
        Node parent = getParentNode(ret, map, o);
        // 插入对象
        Node node = createLeafNode(o);
        parent.children.add(node);
    }
    return ret;
}
         
private static Node getParentNode(List roots, Map map, Object o) {
    String category = (String)((Params)o).get("category"); // 获取对象类别
    Node ret = map.get(category);
    if (ret == null) {
        ret = createParentNode(roots, map, category);
    }
    return ret;
}
         
private static Node createParentNode(List roots, Map map, String category) {
    int index = category.lastIndexOf(".");
    Node ret = new Node();
    if (index != -1) {
        String parentFullId = category.substring(0, index);
        Node parent = map.get(parentFullId);
        if (parent == null) {
            parent = createParentNode(roots, map, parentFullId);
        }
        parent.children.add(ret);
        ret.id = category;
        ret.text = category.substring(index + 1);
        ret.children = new ArrayList();
    }
    else { // 顶层结点
        ret.id = category;
        ret.text = category;
        ret.children = new ArrayList();
        roots.add(ret);
    }
    map.put(category, ret);
    return ret;
}
         
private static Node createLeafNode(Object o) {
    Node ret = new Node();
    ret.id = (String)((Params)o).get("id"); // 如何从对象中获取结点ID：暂时写死 
    ret.text = (String)((Params)o).get("text"); // 如何从对象中获取结点显示文本：暂时写死
    return ret;
}
```

再次运行单元测试，终于变绿了！


# 十一：扩展性思考


通过上面层层递进、逐步细化的过程，我们一步步将大问题拆小，并分别解决，最终达到目标。


上面的例子可能非常简单，实际开发过程中可能还有很多问题留待我们进一步解决，如各种异常情况的处理等。


除此之外，还需要考虑可能存在的扩展性需求程序结构的影响，如空结点的自动归并等，当有这样的新的需求我
们的程序结构是否要推翻重来？


总之，程序开发的过程有时看似无章可循，有时又存在着某种必然的逻辑思维过程。掌握并灵活运用这种思维过
程，是程序开发的必修课。

