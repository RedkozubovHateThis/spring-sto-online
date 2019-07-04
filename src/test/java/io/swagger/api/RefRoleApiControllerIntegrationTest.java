package io.swagger.api;

import io.swagger.model.RefRole;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RefRoleApiControllerIntegrationTest {

    @Autowired
    private RefRoleApi api;

    @Test
    public void refRoleGetTest() throws Exception {
        Integer id = 56;
        ResponseEntity<List<RefRole>> responseEntity = api.refRoleGet(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
