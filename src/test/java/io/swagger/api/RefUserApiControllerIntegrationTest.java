package io.swagger.api;

import io.swagger.model.RefUser;

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
public class RefUserApiControllerIntegrationTest {

    @Autowired
    private RefUserApi api;

    @Test
    public void refUserGetTest() throws Exception {
        Integer id = 56;
        ResponseEntity<List<RefUser>> responseEntity = api.refUserGet(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
