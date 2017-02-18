# htags2callgraph

1. Overview of this tool
 htags2callgraph is a tool that generates a callgraph from 'htags' output files as shown in the following figure:

![Big Picture](https://github.com/hiroki-sawano/htags2callgraph/blob/master/images/big_picture.png)

 An extracted graph can help you understand how the files(e.g. class files) you specify in the configuration file are called. 
 In the example below, the graph shows you the relations of classes in this project:

![Call graph example](https://github.com/hiroki-sawano/htags2callgraph/blob/master/images/callgraph.gif)

2. Preparation
 To begin with, you need to create HTML files based on tag files using 'htags' in GNU GLOBAL source code tagging system. In addition, please make sure you have got Graphviz in your machine as the tool leverages it so as to draw a graph.
 Setting up the following XML configuration, you can tell its analyzer how you would like to construct a graph:
````xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <htags_dir>/project/src/HTML</htags_dir>                <!-- htags HTML directory -->
    <output_dir>/project/callgraph</output_dir>             <!-- output directory -->
    <regex>.*</regex>                                       <!-- regular expression that matchs file names you focus on(i.e. callees) -->
    <graphviz>
        <command>/opt/local/bin/dot</command>               <!-- Graphviz commands --> 
        <command>/opt/local/bin/neato</command>
        <command>/opt/local/bin/fdp</command>
        <command>/opt/local/bin/sfdp</command>
        <command>/opt/local/bin/twopi</command>
        <command>/opt/local/bin/circo</command>
        <command>/opt/local/bin/osage</command>
        <nodes>
            <specified>                                     <!-- Graphviz node settings for nodes meeting regex condition -->
                <node shape="box" fillcolor="#33ffff">.*Model.*</node>            <!-- only support shape and fillcolor attributions -->
                <node shape="box" fillcolor="#33ff00">.*View.*</node>
                <node shape="box" fillcolor="#ff6633">.*Controller.*</node>
                <node shape="box" fillcolor="#ffff00">.*</node>
            </specified>
            <unspecified>                                   <!-- Graphviz node settings for unspecified nodes calling specified ones -->
                <node shape="hexagon" fillcolor="#33ffff">.*Model.*</node>
                <node shape="hexagon" fillcolor="#33ff00">.*View.*</node>
                <node shape="hexagon" fillcolor="#ff6633">.*Controller.*</node>
                <node shape="hexagon" fillcolor="#ffff00">.*</node>
            </unspecified>
        </nodes>
    </graphviz>
</configuration>
````
3. Execution
 To run it, simply execute command : 'java -jar <JAR file> <Configuration.xml>'.
