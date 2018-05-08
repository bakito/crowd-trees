package ch.bakito.crowd.controller;

import static ch.bakito.crowd.constants.ImageType.ACCEPT;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atlassian.crowd.exception.ObjectNotFoundException;

import ch.bakito.crowd.constants.ImageType;
import ch.bakito.crowd.service.ICrowdTreeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
public class CrowdTreeController {

  private final ICrowdTreeService crowdTreeService;

  @Autowired
  public CrowdTreeController(ICrowdTreeService crowdTreeService) {
    this.crowdTreeService = crowdTreeService;
  }

  @ApiOperation(value = "Produces a tree of all groups for the user with the given id")
  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public @ResponseBody void getUserTree(@ApiParam(value = "Define the requested output type", defaultValue = ACCEPT) @RequestHeader(value = "Accept") String accept,
      @PathVariable("userId") String userId, HttpServletResponse response) throws IOException, ObjectNotFoundException {

    ImageType type = getExtensionFor(accept);
    response.setContentType(type.getResponseContentType());
    response.setHeader("Content-Disposition", "inline;filename=crowd-user-tree-" + userId + "." + type.getExtension());

    crowdTreeService.generateUserGroupTree(userId, response.getOutputStream(), type);
  }

  @ApiOperation(value = "Produces a tree of all users and subgroups for the group with the given id")
  @RequestMapping(value = "/group/{groupId}", method = RequestMethod.GET)
  public @ResponseBody void getGroupTree(@ApiParam(value = "Define the requested output type", defaultValue = ACCEPT) @RequestHeader(value = "Accept") String accept,
      @PathVariable("groupId") String groupId, HttpServletResponse response, @RequestParam(name = "withUsers", defaultValue = "true") boolean withUsers) throws IOException,
      ObjectNotFoundException {
    ImageType type = getExtensionFor(accept);
    response.setContentType(type.getResponseContentType());
    response.setHeader("Content-Disposition", "inline;filename=crowd-group-tree-" + groupId + "." + type.getExtension());

    crowdTreeService.generateGroupHierarchy(groupId, response.getOutputStream(), withUsers, type);
  }

  @ApiOperation(value = "Renders the provided dot file as image")
  @RequestMapping(value = "/dot", method = RequestMethod.POST, consumes = "text/plain; charset=utf-8")
  public @ResponseBody void getImage(@ApiParam(value = "Define the requested output type", defaultValue = ACCEPT) @RequestHeader(value = "Accept") String accept,
      @RequestBody String dot, HttpServletResponse response) throws IOException {
    ImageType type = getExtensionFor(accept);
    response.setContentType(type.getResponseContentType());
    response.setHeader("Content-Disposition", "inline;image." + type.getExtension());

    crowdTreeService.generateImage(dot, response.getOutputStream(), type);
  }

  private ImageType getExtensionFor(String mimeType) {
    if (StringUtils.isNotBlank(mimeType)) {
      for (ImageType type : ImageType.values()) {
        if (mimeType.startsWith(type.getMineType())) {
          return type;
        }
      }
    }
    return ImageType.svg;
  }
}
