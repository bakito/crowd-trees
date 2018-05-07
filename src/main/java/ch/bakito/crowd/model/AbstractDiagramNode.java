package ch.bakito.crowd.model;

public abstract class AbstractDiagramNode implements IDiagramNode {

  private final String name;
  private final String nodeColor;

  protected AbstractDiagramNode(String name, String nodeColor) {
    this.name = name;
    this.nodeColor = nodeColor;
  }

  @Override
  public String getName() {
    return name;
  }

  protected String getLabel() {
    return getName();
  }

  @Override
  public String toDot() {
    return "\"" + getName() + "\" [label=\"" + getLabel() + "\",color=" + nodeColor + ",shape=" + getShape() + "];";
  }

  protected abstract String getShape();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractDiagramNode other = (AbstractDiagramNode) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
