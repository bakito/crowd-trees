package ch.bakito.crowd.service;

import ch.bakito.crowd.constants.ImageType;
import ch.bakito.crowd.model.Diagram;
import ch.bakito.crowd.model.GroupNode;
import ch.bakito.crowd.model.UserNode;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.apache.commons.io.IOUtils.copy;

@Component("crowdTreeService")
public class CrowdTreeService implements ICrowdTreeService {

  private static final long serialVersionUID = 1L;
  private final ClientProperties clientProperties;

  @Value("${crowd-trees.color.active}")
  private String colorActive;
  @Value("${crowd-trees.color.users}")
  private String colorUsers;
  @Value("${crowd-trees.color.groups}")
  private String colorGroups;

  @Autowired
  public CrowdTreeService(ClientProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  @Override
  public void generateUserGroupTree(String userId, OutputStream targetStream, ImageType imageType) throws ObjectNotFoundException {
    Diagram diagram = new Diagram("crowd-user-group-tree-" + userId);
    try {
      CrowdClient client = new RestCrowdClientFactory().newInstance(clientProperties);
      User cwdUser = client.getUser(userId);
      UserNode user = new UserNode(cwdUser, colorActive);

      diagram.addNode(user);

      List<Group> nestedChildGroupsOfGroup = client.getGroupsForUser(userId, 0, -1);
      for (Group group : nestedChildGroupsOfGroup) {
        GroupNode groupNode = new GroupNode(group, colorGroups);
        diagram.addTransition(groupNode, user);
        loadParents(client, groupNode, diagram);

      }
    } catch (ObjectNotFoundException e) {
      throw e;
    } catch (ApplicationPermissionException | CrowdException e) {
      throw new RuntimeException(e);
    }

    generateImage(diagram.toString(), targetStream, imageType);
  }

  public void generateGroupUserTree(String groupId, OutputStream targetStream, ImageType imageType) throws ObjectNotFoundException {
    Diagram diagram = new Diagram("crowd-group-user-group-" + groupId);
    try {
      CrowdClient client = new RestCrowdClientFactory().newInstance(clientProperties);
      Group cwdGroup = client.getGroup(groupId);
      GroupNode requestedGroup = new GroupNode(cwdGroup, colorActive);

      diagram.addNode(requestedGroup);

      List<User> uersOfGroup = client.getUsersOfGroup(requestedGroup.getName(), 0, -1);
      uersOfGroup.forEach(u -> {
        UserNode user = new UserNode(u, colorUsers);

        diagram.addTransition(user, requestedGroup);
      });

      loadChildren(groupId, diagram, client, requestedGroup);
    } catch (ObjectNotFoundException e) {
      throw e;
    } catch (ApplicationPermissionException | CrowdException e) {
      throw new RuntimeException(e);
    }

    generateImage(diagram.toString(), targetStream, imageType);

  }

  private void loadChildren(String groupId, Diagram diagram, CrowdClient client, GroupNode requestedGroup) {
    try {
      List<Group> childGroups = client.getChildGroupsOfGroup(groupId, 0, -1);
      for (Group childGroup : childGroups) {
        GroupNode childGroupNode = new GroupNode(childGroup, colorGroups);
        diagram.addTransition(childGroupNode, requestedGroup);

        List<User> nestedUsersOfGroup = client.getUsersOfGroup(childGroup.getName(), 0, -1);
        nestedUsersOfGroup.forEach(u -> {
          UserNode user = new UserNode(u, colorUsers);

          diagram.addTransition(user, childGroupNode);
          loadChildren(childGroup.getName(), diagram, client, childGroupNode);
        });
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void generateImage(String dot, OutputStream targetStream, ImageType imageType) {
    try (ByteArrayInputStream input = new ByteArrayInputStream(dot.getBytes("UTF-8"))) {
      if (imageType == ImageType.dot) {
        copy(input, targetStream);
      } else {
        generateDiagram(input, targetStream, imageType);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void generateDiagram(InputStream input, OutputStream targetStream, ImageType imageType) throws IOException {

    Executor exec = new DefaultExecutor();

    exec.setStreamHandler(new PumpStreamHandler(targetStream, null, input));

    CommandLine cli = new CommandLine("dot");

    cli.addArguments(new String[]{"-Kdot", "-T" + imageType.getExtension()});

    exec.execute(cli);
  }

  private void loadParents(CrowdClient client, GroupNode groupNode, Diagram diagram) throws CrowdException, ApplicationPermissionException {
    List<Group> parentGroupsForGroup = client.getParentGroupsForGroup(groupNode.getName(), 0, -1);
    for (Group parent : parentGroupsForGroup) {
      GroupNode parentGroup = new GroupNode(parent, colorGroups);
      diagram.addTransition(parentGroup, groupNode);
      loadParents(client, parentGroup, diagram);
    }
  }
}
