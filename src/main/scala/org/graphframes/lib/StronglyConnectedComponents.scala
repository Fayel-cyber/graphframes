/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.graphframes.lib

import org.apache.spark.graphx.{lib => graphxlib}

import org.graphframes.GraphFrame

/**
 * Compute the strongly connected component (SCC) of each vertex and return a graph with each vertex
 * assigned to the SCC containing that vertex.
 *
 * The resulting vertices DataFrame contains one additional column:
 *  - component: (same type as vertex id) the id of some vertex in the connected component,
 *    used as a unique identifier for this component
 *
 * The resulting edges DataFrame is the same as the original edges DataFrame.
 */
class StronglyConnectedComponents private[graphframes] (private val graph: GraphFrame)
  extends Arguments {

  private var numIters: Option[Int] = None

  def numIter(value: Int): this.type = {
    numIters = Some(value)
    this
  }

  def run(): GraphFrame = {
    StronglyConnectedComponents.run(graph, check(numIters, "numIterations"))
  }
}


/** Strongly connected components algorithm implementation. */
private object StronglyConnectedComponents {
  private def run(graph: GraphFrame, numIters: Int): GraphFrame = {
    val gx = graphxlib.StronglyConnectedComponents.run(graph.cachedTopologyGraphX, numIters)
    GraphXConversions.fromGraphX(graph, gx, vertexNames = Seq(COMPONENT_ID))
  }

  private[graphframes] val COMPONENT_ID = "component"
}