package ch.bakito.crowd.model;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class DiagramTest extends Assert {
  @Test
  public void addNodeHashCodeTest() {
    Diagram diagram = new Diagram("");

    diagram.addNode(new UserNode("aaa", "skyblue"));
    diagram.addNode(new GroupNode("aaa", "skyblue"));

    assertThat(diagram.getNodes(), Matchers.hasSize(2));

    diagram.addNode(new GroupNode("aaa", "skyblue"));
    diagram.addNode(new GroupNode("bbb", "skyblue"));

    assertThat(diagram.getNodes(), Matchers.hasSize(3));
  }

  @Test
  public void addTransistion() {
    Diagram diagram = new Diagram("");

    UserNode user1 = new UserNode("user1", "skyblue");
    UserNode user2 = new UserNode("user2", "skyblue");

    GroupNode group1 = new GroupNode("group1", "skyblue");
    GroupNode group2 = new GroupNode("group2", "skyblue");

    diagram.addTransition(user1, user2);
    diagram.addTransition(user1, group1);
    diagram.addTransition(user2, group2);

    diagram.addTransition(user2, group2);

    assertThat(diagram.getNodes(), Matchers.hasSize(4));
    assertTrue(diagram.getNodes().contains(user1));
    assertTrue(diagram.getNodes().contains(user2));
    assertTrue(diagram.getNodes().contains(group1));
    assertTrue(diagram.getNodes().contains(group2));

    assertThat(diagram.getTransitions(), Matchers.hasSize(3));
  }
}
