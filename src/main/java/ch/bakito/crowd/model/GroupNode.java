package ch.bakito.crowd.model;

import com.atlassian.crowd.model.group.Group;

public class GroupNode extends AbstractDiagramNode {

  public GroupNode(String name, String nodeColor) {
    super(name, nodeColor);
  }

  @Override
  protected String getShape() {
    return "box";
  }

  public GroupNode(Group group, String nodeColor) {
    this(group.getName(), nodeColor);
  }

}
