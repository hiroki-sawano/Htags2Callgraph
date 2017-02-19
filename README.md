# htags2callgraph

## Overview  
htags2callgraph is a tool that generates a callgraph from 'htags' output files as shown in the following figure:

![Big Picture](https://github.com/hiroki-sawano/htags2callgraph/blob/master/images/big_picture.png)

## Example  
An extracted graph can help you understand how the files(e.g. class files) you specify in the configuration file are called.  
In the example below, the graph shows you the relations of classes in this project:  

![Call graph example](https://github.com/hiroki-sawano/htags2callgraph/blob/master/images/callgraph.gif)

## Preparation  
To begin with, you need to create HTML files based on tag files using 'htags' in GNU GLOBAL source code tagging system.  
In addition, please make sure you have got Graphviz in your machine as the tool leverages it so as to draw a graph.  
Setting up the following XML configuration, you can tell its analyzer how you would like to construct a graph:  
````xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- htags HTML directory -->
    <htags_dir>/project/src/HTML</htags_dir>
    <!-- output directory -->
    <output_dir>/project/callgraph</output_dir>
    <!-- regular expression that matchs file names you focus on(i.e. callees) -->
    <regex>.*</regex>
    <graphviz>
        <!-- Graphviz commands -->
        <command>/opt/local/bin/dot</command>
        <command>/opt/local/bin/neato</command>
        <command>/opt/local/bin/fdp</command>
        <command>/opt/local/bin/sfdp</command>
        <command>/opt/local/bin/twopi</command>
        <command>/opt/local/bin/circo</command>
        <command>/opt/local/bin/osage</command>
        <nodes>
            <!-- Graphviz node settings for nodes meeting regex condition -->
            <specified shape="box" fillcolor="#ff00ff">                     <!-- default setting -->
                <node shape="box" fillcolor="#33ffff">.*Model.*</node>      <!-- setting for specific nodes -->
                <node shape="box" fillcolor="#33ff00">.*View.*</node>
                <node shape="box" fillcolor="#ff6633">.*Controller.*</node>
            </specified>
            <!-- Graphviz node settings for unspecified nodes calling specified ones -->
            <unspecified shape="hexagon" fillcolor="#ff00ff">
                <node shape="hexagon" fillcolor="#33ffff">.*Model.*</node>
                <node shape="hexagon" fillcolor="#33ff00">.*View.*</node>
                <node shape="hexagon" fillcolor="#ff6633">.*Controller.*</node>
            </unspecified>
        </nodes>
    </graphviz>
</configuration>
````
## Execution  
To run it, simply execute command :  
````sh
java -jar <JAR file> <Configuration.xml>
````
