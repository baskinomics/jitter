# jitter

`jitter` is a CLI tool designed to check a developer's git repositories and report if they are in a clean state. The use case envisioned is to utilize `jitter` prior to migrating to a different physical machine or prior to a context switch in projects, where the tool reports on the status of a developer's git repositories at one time.

```bash
$ java -jar build/libs/jitter-0.1.jar -h
Usage: jitter [-hvV] [-c=<config>]
Reports on the status of your git repositories.
  -c, --config=<config>   The configuration file to use.
  -h, --help              Show this help message and exit.
  -v, --verbose           ...
  -V, --version           Print version information and exit.
  ```

## Configuration

At this very early stage, the configuration file is a simple YAML file containing a named sequence, `repositories`, of strings representing the path to a git repository on the local disk. For example, the following `config.yaml` file:

```yaml
# /home/zoo/development/git/config.yaml

repositories:
  - "/home/zoo/development/git/monastery"
  - "/home/zoo/development/git/jitter"
  - "/home/zoo/development/git/git-docs"
  - "/home/zoo/development/git/resume"
```

## Build

To build an artifact, clone the repository and run:

```bash
$ ./gradlew build
```

## Run

To run the application execute the following command:

```bash
$ ./gradlew run --args="--help"
```

## Examples

Running `jitter` with a configuration file:

```bash
$ ./gradlew run --args="-c /path/to/config.yaml"

> Task :run
[monastery:master]
CLEAN

[jitter:master]
modified: src/main/java/jitter/service/impl/ReportServiceImpl.java
modified: .vscode/launch.json
modified: build.gradle

[git-docs:master]
CLEAN

[resume:master]
CLEAN


BUILD SUCCESSFUL in 1s
3 actionable tasks: 1 executed, 2 up-to-date
```

Running `jitter` with the `--verbose` flag:

```bash
$ ./gradlew run --args="-v -c /path/to/config.yaml"

> Task :run
INFO  | 2019-05-20 15:17:43 | [main] jitter.JitterCommand (JitterCommand.java:87) - Checking [-c=<config>] flag.
INFO  | 2019-05-20 15:17:43 | [main] jitter.JitterCommand (JitterCommand.java:91) - Collating reports.
[monastery:master]
CLEAN

[jitter:master]
modified: src/main/java/jitter/service/impl/ReportServiceImpl.java
modified: .vscode/launch.json
modified: build.gradle
modified: README.md

[git-docs:master]
CLEAN

[resume:master]
CLEAN


BUILD SUCCESSFUL in 1s
3 actionable tasks: 1 executed, 2 up-to-date
```
