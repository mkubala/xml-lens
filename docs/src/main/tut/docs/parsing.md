---
layout: docs
title:  "Parsing"
position: 5
---

## Parsing

You need to have `xml-lens-io` included.

For JVM platform `xml-lens-io` uses `javax.xml.stream.XMLStreamReader`. To include it in your build add the following to 
your `build.sbt`:

```
libraryDependencies += "pl.msitko" %% "xml-lens-io" % xmlLensVersion
```

For JS platform slightly modified version of [sax-js](https://github.com/isaacs/sax-js) is used underneath. To include it
in your build add the following to your `build.sbt`:

```
libraryDependencies += "pl.msitko" %%% "xml-lens-io" % xmlLensVersion
```

After you included `io` module to your project parsing XML boils down to:

```tut:book
import pl.msitko.xml.parsing.XmlParser

val input = "<a><b>this is xml</b></a>"

XmlParser.parse(input)
```

### Differences between JVM and JS

#### Parsing entity references

On the JVM for the following input:

```tut:silent
val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><html><body><p>abc &test-entity; def</p></body></html>""".stripMargin
```

Element `p` will have 3 children:

```(Text("abc "), EntityReference("test-entity", "This <em>is</em> an entity."), Text(" def"))```

With scala-js for the same input `p` will also have 3 children but the content of the second child differs:

```(Text("abc "), EntityReference("test-entity", ""), Text(" def"))```

As you can see with scala-js `EntityReference`'s second field (namely `replacement`) is not being
filled. That's due to the fact that JS parser does not read entities declarations.

This behavior can be configured further on JVM. Read more about configuring this behavior at
[parsing configuration](#parsing-configuration).

### Parsing configuration

*At the moment only JVM parser is configurable.* Configuration is done by passing implicit parameter of type
`ParserConfig` to `XmlParser.parse` method. If no configuration is accessible in scope `ParserConfig.Default` is used.

#### `replaceEntityReferences`

As of now the only `ParserConfig` has only one property - `replaceEntityReferences`. It controls how entity references
are parsed. The default value is `false`. What result is expected in that case was described in [Parsing entity references](#parsing-entity-references).
Here we focus on `replaceEntityReferences = true` case.

```tut:silent
val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><html><body><p>abc &test-entity; def</p></body></html>""".stripMargin
      
import pl.msitko.xml.parsing.ParserConfig

implicit val cfg = ParserConfig.Default.copy(replaceEntityReferences = true)

XmlParser.parse(input)
```

When parsed, element `p` will have just one child:

```Text("abc This <em>is</em> an entity. def")```
