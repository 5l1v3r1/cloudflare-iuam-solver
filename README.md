Cloudflare-IUAM-Solver
======================

A simple *"Pure Java"* library and cli tool to breaking through the Cloudflare's anti-bot mechanism (a.k.a "I'm Under Attack Mode", or IUAM), implemented with [HTMLUnit](http://htmlunit.sourceforge.net/).

![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.ninja-beans.crawler/cloudflare-iuam-solver-parent/0.1.0)
![GitHub](https://img.shields.io/github/license/ninja-beans/cloudflare-iuam-solver?color=blue)
![Java](https://img.shields.io/badge/java-11%2B-yellowgreen?stlye=flat&logo=Java)

### Prerequisites

- JDK 11

CLI Tool
--------

### Install

```shell
$ curl -LO https://github.com/ninja-beans/cloudflare-iuam-solver/releases/download/0.1.0/cfis
$ chmod +x cfis
```

### Usage

Print a cookie string.

```shell
$ ./cfis -c https://www.example.com
cf_clearance=XXXXXXXXXXXXXXXXXXXX-XXXXXXXXXX-X-XXX;__cfduid=XXXXXXXXXXXXXXXXXXXX;
```

Download a html content with curl.

```shell
$ ./cfis -c > cookie.txt
$ ./cfis -u > ua.txt
$ curl -s --cookie "$(cat cookie.txt)" -A "$(cat ua.txt)" https://www.example.com/
```

Extract all images with curl and xmllint.

```shell
$ eval $(./cfis --curl https://www.example.com/) | xmllint --xpath "//img" --html - 2> /dev/null
```

Java Library
------------

### Install
```xml
<dependency>
  <groupId>com.ninja-beans.crawler</groupId>
  <artifactId>cloudflare-iuam-solver-parent</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

### Usage

Scraping with Java 11 HttpClient and Jsoup.

```java
public class App {
  public static void main(final String[] args) throws IOException, InterruptedException {
    var url = args[0];
    var result = IuamSolver.solve(url);

    // 1. Create HttpClient
    var client = HttpClient
        .newBuilder()
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .cookieHandler(result.getCookieManager()).build();

    // 2. Send the request and get the response
    var request = HttpRequest.newBuilder().header("Accept", "*/*")
        .header("User-Agent", result.getResponse().getUserAgent())
        .GET()
        .uri(URI.create(url))
        .build();
    var response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

    // 3. Parse the response
    var doc = Jsoup.parse(response.body(), url);
    var elm = doc.getElementById("title");
    System.out.println(doc.title());
    System.out.println(elm.html());
  }
}
```

