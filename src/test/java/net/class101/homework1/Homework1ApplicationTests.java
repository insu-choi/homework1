package net.class101.homework1;

import lombok.extern.slf4j.Slf4j;
import net.class101.homework1.config.SoldOutException;
import net.class101.homework1.service.ShoppingBasketService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class Homework1ApplicationTests {

    @Autowired
	ShoppingBasketService shoppingBasketService;

	@Test
	void contextLoads() {
	}

	@Test
    public void multiThreadStockTest() throws InterruptedException, ExecutionException {
		int numberOfThreads = 5;
		int SuccessResultCount = 2;
		String testProductId = "42031";
		ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		List<List<Map<String, Object>>> baskets = new ArrayList<>();
		final List<Future<String>> futures = new ArrayList<>();

		// 장바구니 or 결제에서 SoldOut 확인
		for (int i = 0; i < numberOfThreads - 1; i++) {
			futures.add(service.submit(() -> {
				String tName = Thread.currentThread().getName();
				Long tId = Thread.currentThread().getId();
				log.info("-------------------------------- " + tId + " --" + tName + " Start");
				List<Map<String, Object>> basket = new ArrayList<>();
				String result = "Success";
				try {
					shoppingBasketService.addKitOrKlassInBasket(basket, testProductId, new BigDecimal(1));
					shoppingBasketService.orderCompleteProcess(basket);
				}
				catch (SoldOutException e) {
					result = "Fail : " + e.getMessage();
					log.error(e.getMessage());
				}
				finally
				{
					baskets.add(basket);
					latch.countDown();
				}
				return tId + "_" + tName + " : " + result;
			}));
		}

		// 장바구니에서 SoldOut 확인
		Thread.sleep(1000);
		futures.add(service.submit(() -> {
			String tName = Thread.currentThread().getName();
			Long tId = Thread.currentThread().getId();
			log.info("-------------------------------- " + tId + " --" + tName + " Start");
			List<Map<String, Object>> basket = new ArrayList<>();
			String result = "Success";
			try {
				shoppingBasketService.addKitOrKlassInBasket(basket, testProductId, new BigDecimal(1));
				shoppingBasketService.orderCompleteProcess(basket);
			}
			catch (SoldOutException e) {
				result = "Fail : " + e.getMessage();
				log.error(e.getMessage());
			}
			finally
			{
				baskets.add(basket);
				latch.countDown();
			}
			return tId + "_" + tName + " : " + result;
		}));

		latch.await();
		System.out.println();
		for (Future<String> result: futures) {
			System.out.println(result.get());
		}
		System.out.println("result size: " + futures.stream().filter((v)->{
			try {
				return StringUtils.contains(v.get(), "Success");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return false;
		}).collect(Collectors.toList()).size());

		assertEquals(SuccessResultCount, futures.stream().filter((v)->{
			try {
				return StringUtils.contains(v.get(), "Success");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return false;
		}).collect(Collectors.toList()).size());
    }




}
