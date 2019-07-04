package io.swagger.api;

import io.swagger.model.DocOrder;

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
public class DocOrderApiControllerIntegrationTest {

    @Autowired
    private DocOrderApi api;

    @Test
    public void createTaskVisitTest() throws Exception {
        DocOrder body = new DocOrder();
        ResponseEntity<Void> responseEntity = api.createTaskVisit(body);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void docOrderDeleteTest() throws Exception {
        Integer id = 56;
        ResponseEntity<Void> responseEntity = api.docOrderDelete(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void docOrderGetTest() throws Exception {
        Integer id = 56;
        ResponseEntity<List<DocOrder>> responseEntity = api.docOrderGet(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void docOrderPutTest() throws Exception {
        DocOrder body = new DocOrder();
        Integer id = 56;
        ResponseEntity<Void> responseEntity = api.docOrderPut(body, id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
