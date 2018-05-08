package ch.bakito.crowd.service;

import static org.apache.commons.io.IOUtils.copy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;

import ch.bakito.crowd.constants.ImageType;
import ch.bakito.crowd.exception.CrowdTreeObjectNotFoundException;
import ch.bakito.crowd.exception.CrowdTreeRuntimeException;
import ch.bakito.crowd.model.Diagram;
import ch.bakito.crowd.model.GroupNode;
import ch.bakito.crowd.model.UserNode;

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
  public void generateUserGroupTree(String userId, OutputStream targetStream, ImageType imageType) {
    Diagram diagram = new Diagram("crowd-user-group-tree-" + userId, "Group hierarchy of user '" + userId + "'");
    try {
      CrowdClient client = new RestCrowdClientFactory().newInstance(clientProperties);
      User cwdUser = client.getUser(userId);
      UserNode user = new UserNode(cwdUser, colorActive);

      diagram.addNode(user);

      List<Group> nestedChildGroupsOfGroup = client.getGroupsForUser(userId, 0, -1);
      for (Group group : nestedChildGroupsOfGroup) {
        GroupNode groupNode = new GroupNode(group, colorGroups);
        diagram.addTransition(groupNode, user);
        loadParents(client, groupNode, diagram, false);

      }
    } catch (ObjectNotFoundException e) {
      throw new CrowdTreeObjectNotFoundException(e);
    } catch (ApplicationPermissionException | CrowdException e) {
      throw new CrowdTreeRuntimeException(e);
    }

    generateImage(diagram.toString(), targetStream, imageType);
  }

  public void generateGroupHierarchy(String groupId, OutputStream targetStream, boolean withUsers, ImageType imageType) {
    Diagram diagram = new Diagram("crowd-group-user-group-" + groupId, "Hierarchy of group '" + groupId + "'");
    try {
      CrowdClient client = new RestCrowdClientFactory().newInstance(clientProperties);
      Group cwdGroup = client.getGroup(groupId);
      GroupNode requestedGroup = new GroupNode(cwdGroup, colorActive);

      diagram.addNode(requestedGroup);

      addUsers(withUsers, diagram, client, requestedGroup, requestedGroup.getName());

      loadChildren(client, requestedGroup, diagram, withUsers);
      loadParents(client, requestedGroup, diagram, true);
    } catch (ObjectNotFoundException e) {
      throw new CrowdTreeObjectNotFoundException(e);
    } catch (ApplicationPermissionException | CrowdException e) {
      throw new CrowdTreeRuntimeException(e);
    }

    generateImage(diagram.toString(), targetStream, imageType);

  }

  private void addUsers(boolean withUsers, Diagram diagram, CrowdClient client, GroupNode requestedGroup, String name) throws OperationFailedException, GroupNotFoundException,
      ApplicationPermissionException, InvalidAuthenticationException {
    if (withUsers) {
      List<User> uersOfGroup = client.getUsersOfGroup(name, 0, -1);
      uersOfGroup.forEach(u -> {
        UserNode user = new UserNode(u, colorUsers);

        diagram.addTransition(user, requestedGroup);
      });
    }
  }

  private void loadChildren(CrowdClient client, GroupNode group, Diagram diagram, boolean withUsers) {
    try {
      List<Group> childGroups = client.getChildGroupsOfGroup(group.getName(), 0, -1);
      for (Group childGroup : childGroups) {
        GroupNode childGroupNode = new GroupNode(childGroup, colorGroups);
        diagram.addTransition(childGroupNode, group);

        addUsers(withUsers, diagram, client, childGroupNode, childGroup.getName());
        loadChildren(client, childGroupNode, diagram, withUsers);
      }
    } catch (ObjectNotFoundException e) {
      throw new CrowdTreeObjectNotFoundException(e);
    } catch (InvalidAuthenticationException e) {
      e.printStackTrace();
    } catch (OperationFailedException | ApplicationPermissionException e) {
      throw new CrowdTreeRuntimeException(e);
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

  private void loadParents(CrowdClient client, GroupNode groupNode, Diagram diagram, boolean inverseMapping) throws CrowdException, ApplicationPermissionException {
    List<Group> parentGroupsForGroup = client.getParentGroupsForGroup(groupNode.getName(), 0, -1);
    for (Group parent : parentGroupsForGroup) {
      GroupNode parentGroup = new GroupNode(parent, colorGroups);
      if (inverseMapping) {
        diagram.addTransition(groupNode, parentGroup);
      } else {
        diagram.addTransition(parentGroup, groupNode);
      }
      loadParents(client, parentGroup, diagram, inverseMapping);
    }
  }
}
