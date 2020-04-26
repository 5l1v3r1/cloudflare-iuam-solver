#!/bin/sh

JAVA_OPTS='-Xms512M -Xmx1536M -client -DLOG.com.ninja_beans.crawler.App=error'
java $JAVA_OPTS -jar "$0" "$@"
exit $?

