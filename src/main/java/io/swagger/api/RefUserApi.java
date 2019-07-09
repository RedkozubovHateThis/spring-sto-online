/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.8).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.RefUser;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@Api(value = "ref_user", description = "the ref_user API")
public interface RefUserApi {

    @ApiOperation(value = "Создание пользователя", nickname = "createUser", notes = "", tags={ "ref_user", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Операция успешно завершена") })
    @RequestMapping(value = "/ref_user",
            consumes = { "application/json" },
            method = RequestMethod.POST)
    ResponseEntity<Void> createUser(@ApiParam(value = "Создание пользователя" ,required=true )  @Valid @RequestBody RefUser body);

    @ApiOperation(value = "Получение данных о пользователя по идентификатору.", nickname = "refUserGet", notes = "", response = RefUser.class, responseContainer = "List", tags={ "ref_user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Операция успешно завершена.", response = RefUser.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Неверное имя пользователя."),
        @ApiResponse(code = 404, message = "Пользователь не найден.") })
    @RequestMapping(value = "/ref_user",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<RefUser>> refUserGet(@ApiParam(value = "id пользователя.") @Valid @RequestParam(value = "id", required = false) Integer id);

    @ApiOperation(value = "Получение данных о пользователях.", nickname = "refUsersGet", notes = "", response = RefUser.class, responseContainer = "List", tags={ "ref_user", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Операция успешно завершена.", response = RefUser.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "Пользователи не найдены.") })
    @RequestMapping(value = "/ref_user/all",
        produces = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<List<RefUser>> refUsersGet();

}
