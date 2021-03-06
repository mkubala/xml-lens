package pl.msitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import pl.msitko.xml.parsing.XmlParser

import scala.xml.XML

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class ParseBench {
  @Benchmark def parseWithLens = XmlParser.parse(SmallRoundtrip.example.input).right.get

  @Benchmark def parseWithStd = XML.loadString(SmallRoundtrip.example.input)
}
