# JRootie TCK

[JRootie](https://github.com/GappleX/JRootie) 的技术兼容性测试套件（Technology Compatibility Kit）。

本仓库以**下游消费者的真实使用方式**验证 JRootie 的行为。每个测试类同时是**可执行的用法文档**。

## 覆盖范围

| 测试类 | 范围 |
|---|---|
| `CoreOpsTest` | 字段读写（实例 / 静态，含 `final`）、私有方法调用、私有构造器、`allocateInstance`、声明类枚举 |
| `RedefineTest` | `makeReturn` / `makeThrow` / `replace`，覆盖应用类与 JDK 内部类（`java.util.Objects`） |

两个测试类都验证 undo-log：每个 `acquireTest()` 作用域在 `close()` 时回滚，测试断言回滚结果。

## 前置要求

- **Maven 3.6+**
- **JDK 11 或更高**（编译用）
- **JDK 11 / 17 / 21 / 25** 已通过 Maven Toolchains 验证
- 本地 JDK 安装已注册到 `~/.m2/toolchains.xml`

JRootie 在所有支持的 JDK 上都需要 `-javaagent`。TCK 会把 JRootie jar 复制到 `target/jrootie-agent.jar`，并通过 Surefire 的 `argLine` 传入。无需手动配置。

## 运行

单个 JDK（使用 Maven 当前 JDK）：

```bash
mvn clean test
```

通过 toolchains 指定 JDK：

```bash
mvn clean test -Pjdk11
mvn clean test -Pjdk17
mvn clean test -Pjdk21
mvn clean test -Pjdk25
```

四个 JDK 依次跑：

```bash
for v in 11 17 21 25; do
    echo "==== JDK $v ===="
    mvn clean test -Pjdk$v || exit 1
done
```

## Toolchains 配置

TCK 使用 [Maven Toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html)
来独立于 Maven 自身 JDK 选择测试 JVM。每个 `jdkXX` profile 声明所需主版本号，
Maven 从 `~/.m2/toolchains.xml` 解析。

`~/.m2/toolchains.xml` 条目示例：

```xml
<toolchain>
    <type>jdk</type>
    <provides>
        <version>17</version>
        <vendor>temurin</vendor>
    </provides>
    <configuration>
        <jdkHome>/path/to/jdk-17</jdkHome>
    </configuration>
</toolchain>
```

`<version>` 必须与 profile 中声明的主版本号一致（`11`、`17`、`21`、`25`）。

## 日志输出

测试日志由 `src/test/resources/simplelogger.properties` 裁剪。
默认只显示 JRootie 审计级别的 `WARN` 消息（例如写入 `final` 字段时）。
要查看完整审计记录，降低级别：

```properties
org.slf4j.simpleLogger.log.jrootie=info
```

或在运行时覆盖：

```bash
mvn clean test -Dorg.slf4j.simpleLogger.log.jrootie=debug
```

## 兼容性矩阵

| JRootie | 已验证 JDK | MR-JAR 分支 |
|---|---|---|
| 0.3.0 | 11、17、21、25 | base (`11`)、`versions/17` |

`pom.xml` 中的 `jrootie.version` 属性固定被测的 JRootie 版本。验证新版本：

```bash
mvn versions:set-property -Dproperty=jrootie.version -DnewVersion=0.3.1
mvn clean test -Pjdk11
mvn clean test -Pjdk25
```

## 许可

Apache License 2.0。详见 JRootie 主仓库。