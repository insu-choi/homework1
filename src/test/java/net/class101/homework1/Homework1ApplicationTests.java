package net.class101.homework1;

import lombok.extern.slf4j.Slf4j;
import net.class101.homework1.mappers.ProductMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class Homework1ApplicationTests {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProductMapper productMapper;

	@Test
	void contextLoads() {
	}

	@Test
    public void t1() {
	    List<Map<String, Object>> products = productMapper.selectAllProduct();

	    Map<String, Object> product = products.get(2);
        log.info("ID " + product.get("ID"));
        assertEquals("65625", product.get("ID"));

        product.put("ID", "33333");
        assertEquals("33333", product.get("ID"));

	    Map<String, Object> product1 = products.get(2);
        log.info("ID1 " + product1.get("ID"));
        assertEquals("33333", product1.get("ID"));
    }

	@Test
    public void t2() {
	    Map<String, Object> result = new HashMap<>();
	    log.info("this: " + result.isEmpty());

	    Long a = 99999L;
	    log.info(String.format("%d", a));
    }




}
