package ch.bakito.crowd.model;

import com.atlassian.crowd.model.user.User;
import org.apache.commons.lang3.StringUtils;

public class UserNode extends AbstractDiagramNode {

  private String displayName = null;


  public UserNode(String name, String nodeColor) {
    super(name, nodeColor);
  }

  public UserNode(User cwdUser, String nodeColor) {
    this(cwdUser.getName(), nodeColor);
    displayName = cwdUser.getDisplayName();
  }

  @Override
  protected String getLabel() {
    if (StringUtils.isBlank(displayName)) {
      return super.getLabel();
    }
    return displayName + " (" + getName() + ")";
  }

  @Override
  protected String getShape() {
    return "oval";
  }
}
