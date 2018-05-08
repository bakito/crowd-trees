package ch.bakito.crowd.service;

import java.io.OutputStream;
import java.io.Serializable;

import com.atlassian.crowd.exception.ObjectNotFoundException;

import ch.bakito.crowd.constants.ImageType;

public interface ICrowdTreeService extends Serializable {

  void generateUserGroupTree(String userId, OutputStream targetStream, ImageType imageType) throws ObjectNotFoundException;

  void generateGroupHierarchy(String groupId, OutputStream targetStream, boolean withUsers, ImageType imageType) throws ObjectNotFoundException;

  void generateImage(String dot, OutputStream targetStream, ImageType imageType);

}
