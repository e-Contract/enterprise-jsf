# Quarkus Enterprise JSF Demo

Run via:
```
mvn clean compile quarkus:dev
```
Tap `w` to open the web browser.
Tap `q` to quit the runtime.

Tested with the following versions:
| Quarkus    | MyFaces-Quarkus | PrimeFaces | Java    |
| ---------- | --------------- | ---------- | ------- |
| 3.30.4     | 4.1.1           | 13.0.10    | 17.0.17 |
| 3.27.1 LTS | 4.1.1           | 13.0.10    | 17.0.17 |
| 3.22.3     | 4.1.1           | 13.0.10    | 17.0.17 |
| 3.21.4     | 4.0.3/4.1.1     | 13.0.10    | 17.0.14 |
| 3.20.4 LTS | 4.0.3/4.1.1     | 13.0.10    | 17.0.17 |
| 3.19.4     | 4.0.3           | 13.0.10    | 17.0.14 |
| 3.18.4     | 4.0.3           | 13.0.10    | 17.0.14 |
| 3.17.8     | 4.0.3/4.1.1     | 13.0.10    | 17.0.14 |
| 3.16.4     | 4.0.3           | 13.0.10    | 17.0.14 |
| 3.15.4 LTS | 4.0.3           | 13.0.10    | 17.0.14 |
| 3.14.4     | 4.0.3           | 13.0.10    | 17.0.14 |
| 3.8.6 LTS  | 4.0.3           | 13.0.10    | 17.0.14 |

We cannot upgrade to `myfaces-quarkus` version 4.1.2 due to:
https://issues.apache.org/jira/browse/MYFACES-4735
