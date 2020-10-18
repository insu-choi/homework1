package net.class101.homework1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.class101.homework1.mappers.ProductMapper;
import net.class101.homework1.service.ShoppingBasketService;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@SpringBootApplication
@MapperScan("net.class101.homework1.mappers")
@AllArgsConstructor
public class Homework1Application implements CommandLineRunner {

    private ProductMapper productMapper;
    private ShoppingBasketService shoppingBasketService;
//    public Homework1Application (ProductMapper productMapper)
//    {
//        this.productMapper = productMapper;
//    }

	public static void main(String[] args) {

		SpringApplication.run(Homework1Application.class, args);

	}

    @Override
    public void run(String... args) {
        Scanner scan = new Scanner(System.in);
        String menu = null;
//        String menu = "q";

        List<Map<String, Object>> basket = new ArrayList<>();

        while (! StringUtils.equals(menu, "q"))
        {
            System.out.print("입력(o[order]: 주문, q[quit]: 종료) : ");
            menu = scan.nextLine();

            switch (menu)
            {
                case "1":
                case "2":
                case "o":
                    orderProcess(scan, basket);
                    break;
                case "q":
                    System.out.println("고객님의 주문 감사합니다.");
                    // TODO: exit
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
            System.out.format("%-10s %-60s %10.0f %10.0f\n", result.get("ID"), result.get("PRODUCT_NAME"),
                result.get("PRICE"), result.get("STOCK"));
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
            // TODO: Validation check
            productId = scan.nextLine();
            // 결제완료
            Map<String, Object> tmp = productMapper.selectOneById(productId);
            if (tmp == null) {
                System.out.println("해당 상품이 없습니다.");
                continue;
            }

            if (StringUtils.isBlank(productId) ) {
                // 주문내역
                shoppingBasketService.printOrderList(basket);
            }
            else {
                System.out.print("수량 : ");
                productCount = scan.nextBigDecimal();
                scan.nextLine();
                shoppingBasketService.addKitOrKlassInBasket(basket, productId, productCount);
            }
        }
    }

}
