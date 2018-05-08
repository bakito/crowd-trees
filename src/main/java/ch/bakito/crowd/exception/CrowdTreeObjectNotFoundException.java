package ch.bakito.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;

public class CrowdTreeObjectNotFoundException extends RuntimeException {

  public CrowdTreeObjectNotFoundException(ObjectNotFoundException e) {
    super(e);
  }
}
