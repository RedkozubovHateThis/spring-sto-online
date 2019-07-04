package io.swagger.api;

import io.swagger.model.RefCarState;

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
public class RefCarStateApiControllerIntegrationTest {

    @Autowired
    private RefCarStateApi api;

    @Test
    public void refCarStateGetTest() throws Exception {
        Integer id = 56;
        ResponseEntity<List<RefCarState>> responseEntity = api.refCarStateGet(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
