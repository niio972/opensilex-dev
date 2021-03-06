/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.rest.group.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opensilex.rest.authentication.ApiProtected;
import org.opensilex.rest.authentication.AuthenticationService;
import org.opensilex.rest.group.dal.GroupDAO;
import org.opensilex.rest.group.dal.GroupModel;
import org.opensilex.rest.group.dal.GroupUserProfile;
import org.opensilex.rest.profile.dal.ProfileDAO;
import org.opensilex.rest.user.dal.UserDAO;
import org.opensilex.rest.validation.ValidURI;
import org.opensilex.server.response.ErrorDTO;
import org.opensilex.server.response.ErrorResponse;
import org.opensilex.server.response.ObjectUriResponse;
import org.opensilex.server.response.PaginatedListResponse;
import org.opensilex.server.response.SingleObjectResponse;
import org.opensilex.sparql.service.SPARQLService;
import org.opensilex.sparql.utils.OrderBy;
import org.opensilex.utils.ListWithPagination;

/**
 *
 * @author vidalmor
 */
@Api("Group")
@Path("/group")
public class GroupAPI {

    /**
     * Inject SPARQL service
     */
    @Inject
    private SPARQLService sparql;

    /**
     * Inject Authentication service
     */
    @Inject
    private AuthenticationService authentication;
    
    /**
     * Create a group and return it's URI
     *
     * @see org.opensilex.rest.group.dal.GroupDAO
     * @param dto group model to create
     * @return Group URI
     * @throws Exception if creation failed
     */
    @POST
    @Path("create")
    @ApiOperation("Create a group and return it's URI")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Group sucessfully created")
    })
    @ApiProtected
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(
            @ApiParam("Group creation informations") @Valid GroupCreationDTO dto
    ) throws Exception {
        GroupDAO dao = new GroupDAO(sparql);

        GroupModel group = dao.create(getModel(dto));

        return new ObjectUriResponse(Response.Status.CREATED, group.getUri()).getResponse();
    }

        
    public GroupModel getModel(GroupGetDTO dto) throws Exception {
        GroupModel group = new GroupModel();
        group.setUri(dto.getUri());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        
        UserDAO userDAO = new UserDAO(sparql, authentication);
        ProfileDAO profileDAO = new ProfileDAO(sparql);
        List<GroupUserProfile> userProfilesModel = new ArrayList<>();
        for (GroupUserProfileDTO userProfile :  dto.getUserProfiles()) {
            GroupUserProfile userProfileModel = new GroupUserProfile();
            userProfileModel.setProfile(profileDAO.get(userProfile.getProfileURI()));
            userProfileModel.setUser(userDAO.get(userProfile.getUserURI()));
            userProfileModel.setUri(userProfile.getUri());
            userProfilesModel.add(userProfileModel);
        };
        
        group.setUserProfiles(userProfilesModel);

        return group;
    }
    /**
     * Return a group by URI
     *
     * @see org.opensilex.rest.group.dal.GroupDAO
     * @param uri URI of the group
     * @return Corresponding group
     * @throws Exception Return a 500 - INTERNAL_SERVER_ERROR error response
     */
    @GET
    @Path("{uri}")
    @ApiOperation("Get a group by it's URI")
    @ApiProtected
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return group", response = GroupGetDTO.class),
        @ApiResponse(code = 400, message = "Invalid parameters", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Group not found", response = ErrorDTO.class)
    })
    public Response getGroup(
            @ApiParam(value = "Group URI", example = "dev-groups:admin_group", required = true)
            @PathParam("uri")
            @NotNull URI uri
    ) throws Exception {
        GroupDAO dao = new GroupDAO(sparql);
        GroupModel model = dao.get(uri);

        // Check if group is found
        if (model != null) {
            // Return group converted in GroupGetDTO
            return new SingleObjectResponse<>(
                    GroupGetDTO.fromModel(model)
            ).getResponse();
        } else {
            // Otherwise return a 404 - NOT_FOUND error response
            return new ErrorResponse(
                    Response.Status.NOT_FOUND,
                    "Group not found",
                    "Unknown group URI: " + uri.toString()
            ).getResponse();
        }
    }

    /**
     * Search groups
     *
     * @see org.opensilex.rest.group.dal.GroupDAO
     * @param pattern Regex pattern for filtering list by names or email
     * @param orderByList List of fields to sort as an array of
     * fieldName=asc|desc
     * @param page Page number
     * @param pageSize Page size
     * @return filtered, ordered and paginated list
     * @throws Exception Return a 500 - INTERNAL_SERVER_ERROR error response
     */
    @GET
    @Path("search")
    @ApiOperation("Search groups")
    @ApiProtected
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return group list", response = GroupGetDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid parameters", response = ErrorDTO.class)
    })
    public Response searchGroups(
            @ApiParam(value = "Regex pattern for filtering list by names", example = ".*")
            @DefaultValue(".*")
            @QueryParam("pattern") String pattern,
            @ApiParam(value = "List of fields to sort as an array of fieldName=asc|desc", example = "email=asc")
            @QueryParam("orderBy") List<OrderBy> orderByList,
            @ApiParam(value = "Page number", example = "0")
            @QueryParam("page")
            @DefaultValue("0")
            @Min(0) int page,
            @ApiParam(value = "Page size", example = "20")
            @QueryParam("pageSize")
            @DefaultValue("20")
            @Min(0) int pageSize
    ) throws Exception {
        GroupDAO dao = new GroupDAO(sparql);
        ListWithPagination<GroupModel> resultList = dao.search(
                pattern,
                orderByList,
                page,
                pageSize
        );

        // Convert paginated list to DTO
        ListWithPagination<GroupGetDTO> resultDTOList = resultList.convert(
                GroupGetDTO.class,
                GroupGetDTO::fromModel
        );

        // Return paginated list of user DTO
        return new PaginatedListResponse<>(resultDTOList).getResponse();
    }

    @PUT
    @Path("update")
    @ApiOperation("Update a group")
    @ApiProtected
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Return group uri of the updated group", response = String.class),
        @ApiResponse(code = 400, message = "Invalid parameters")
    })
    public Response updateGroup(
            @ApiParam("Group description")
            @Valid GroupUpdateDTO dto
    ) throws Exception {
        GroupDAO dao = new GroupDAO(sparql);

        GroupModel model = dao.get(dto.getUri());

        if (model != null) {
            GroupModel group = dao.update(getModel(dto));

            return new ObjectUriResponse(Response.Status.OK, group.getUri()).getResponse();
        } else {
            return new ErrorResponse(
                    Response.Status.NOT_FOUND,
                    "Group not found",
                    "Unknown group URI: " + dto.getUri()
            ).getResponse();
        }
    }

    @DELETE
    @Path("{uri}")
    @ApiOperation("Delete a group")
    @ApiProtected
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGroup(
            @ApiParam(value = "Group URI", example = "http://example.com/", required = true)
            @PathParam("uri")
            @NotNull
            @ValidURI URI uri
    ) throws Exception {
        GroupDAO dao = new GroupDAO(sparql);
        dao.delete(uri);
        return new ObjectUriResponse(Response.Status.OK, uri).getResponse();
    }
}
