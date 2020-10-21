package net.class101.homework1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.class101.homework1.config.SoldOutException;
import net.class101.homework1.mappers.ProductMapper;
import net.class101.homework1.service.ShoppingBasketService;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@SpringBootApplication
@MapperScan("net.class101.homework1.mappers")
@AllArgsConstructor
public class Homework1Application implements CommandLineRunner {

    private ProductMapper productMapper;
    private ShoppingBasketService shoppingBasketService;

	public static void main(String[] args) {

		SpringApplication.run(Homework1Application.class, args);

	}

    @Override
    public void run(String... args) {
        Scanner scan = new Scanner(System.in);
        String menu = null;

        List<Map<String, Object>> basket = new ArrayList<>();
        while (! StringUtils.equals(menu, "q"))
        {
            System.out.print("입력(o[order]: 주문, q[quit]: 종료) : ");
            try {
                menu = scan.nextLine();
            }
            // Unit Test를 위한 try catch 처리
            catch (NoSuchElementException e) {
                menu = "t";
                log.error(e.getMessage());
            }

            switch (menu)
            {
                case "t":
                    menu = "q";
                    break;
                case "o":
                    orderProcess(scan, basket);
                    break;
                case "q":
                    System.out.println("고객님의 주문 감사합니다.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("menu: " + menu);
                    break;
            }
        }
    }

    // 상품 출력
    private void printProducts() {
        List<Map<String, Object>> list = productMapper.selectAllProduct();
        System.out.format("%-10s %-60s %10s %10s\n", "상품번호", "상품명", "판매가격", "재고수");
        for(Map<String, Object> result:list)
        {
            System.out.format("%-10s %-60s %10d %10d\n",
                (String)result.get("ID"),
                (String)result.get("PRODUCT_NAME"),
                ((BigDecimal)result.get("PRICE")).intValue(),
                ((BigDecimal)result.get("STOCK")).intValue());
        }
        System.out.println();
    }

    // 주문 메뉴
    private void orderProcess(Scanner scan, List<Map<String, Object>> basket) {
        printProducts();
        String productId = null;
        BigDecimal productCount = BigDecimal.ZERO;
        while(! StringUtils.equals(productId, " ")) {
            System.out.print("상품번호 : ");
            productId = scan.nextLine();
            // 결제완료

            if (StringUtils.isBlank(productId) ) {
                // 결제
                shoppingBasketService.orderCompleteProcess(basket);
            }
            else {
                // 상품 있는지 확인
                Map<String, Object> checkProduct = productMapper.selectOneById(productId);
                if (checkProduct == null) {
                    System.out.println("해당 상품이 없습니다.");
                    continue;
                }

                System.out.print("수량 : ");
                try {
                    productCount = scan.nextBigDecimal();
                }
                catch (InputMismatchException e) {
                    System.out.println("숫자를 입력해주세요.");
                }
                catch (Exception  e) {
                    System.out.println(e.getMessage());
                }
                scan.nextLine();
                try {
                    shoppingBasketService.addKitOrKlassInBasket(basket, productId, productCount);
                }
                catch (SoldOutException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
