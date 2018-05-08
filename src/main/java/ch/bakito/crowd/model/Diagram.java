package ch.bakito.crowd.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Diagram {

  private final Set<IDiagramNode> nodes = new HashSet<>();
  private final Set<DiagramTransition> transitions = new HashSet<>();

  private final String prefix;
  private final String postfix;

  public Diagram(String name, String title) {
    this.prefix = "digraph \"crowd-tree-" + name + "\"\n{\nnode [style=\"rounded,filled\",fontname = \"helvetica\"];";
    this.postfix = "\n // title\n" + " \nlabelloc=\"t\";\nlabel=\"" + title + "\";\nfontname = \"helvetica\"}";
  }

  public void addNode(IDiagramNode node) {
    nodes.add(node);
  }

  public void addTransition(IDiagramNode from, IDiagramNode to) {
    addNode(from);
    addNode(to);
    transitions.add(new DiagramTransition(from, to));
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append(prefix).append("\n");

    for (IDiagramNode node : nodes) {
      sb.append(node.toDot()).append("\n");
    }
    for (DiagramTransition transition : transitions) {
      sb.append(transition.toDot()).append("\n");
    }

    sb.append(postfix);
    return sb.toString();
  }

  public Set<IDiagramNode> getNodes() {
    return Collections.unmodifiableSet(nodes);
  }

  public Set<DiagramTransition> getTransitions() {
    return Collections.unmodifiableSet(transitions);
  }

}
