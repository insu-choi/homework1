package net.class101.homework1.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.class101.homework1.config.ErrorMessage;
import net.class101.homework1.config.SoldOutException;
import net.class101.homework1.mappers.ProductMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@AllArgsConstructor
public class ShoppingBasketService {
    private static final String Kit = "KIT";
    private static final BigDecimal DeliveryCharge = new BigDecimal(5000);
    private static final BigDecimal KitDeliveryChargeCondition = new BigDecimal(50000);

    private ProductMapper productMapper;

    /**
     * 장바구니 담기
     * @param basket
     * @param productId
     * @param productCount
     */
    public void addKitOrKlassInBasket(List<Map<String, Object>> basket, String productId, BigDecimal productCount) {

        Map<String, Object> productInfo = productMapper.selectOneById(productId);
        if (productInfo == null || productInfo.isEmpty())
            throw new RuntimeException(ErrorMessage.NoProductInfo);
        String productKind = (String)productInfo.get("KIND");

        // 장바구니에 동일 상품 있는지 확인
        Map<String, Object> basketInfo = new HashMap<>();
        for(Map<String, Object> productInBasket:basket) {
            if (StringUtils.equals((String)productInBasket.get("ID"), productId)) {
                basketInfo = productInBasket;
                break;
            }
        }

        // 키트, 클래스 구분
        if (StringUtils.equals(productKind, Kit)) {
            BigDecimal orderCount = productCount;
            // 장바구니에 있으면
            if (! basketInfo.isEmpty())
                orderCount = orderCount.add((BigDecimal)basketInfo.get("COUNT"));

            // 재고 확인
            BigDecimal productStock = (BigDecimal)productInfo.get("STOCK");
            if (productStock.subtract(orderCount).compareTo(BigDecimal.ZERO) <= 0){
                throw new SoldOutException("Basket - " + ErrorMessage.SoldOut);
            }
            else {
                // 없으면 넣고
                if (basketInfo.isEmpty()) {
                    basketInfo.putAll(productInfo);
                    basketInfo.put("COUNT", orderCount);
                    basket.add(basketInfo);
                }
                // 있으면 수정만
                else
                    basketInfo.put("COUNT", orderCount);
            }
        }
        // 클래스
        else {
            if (! basketInfo.isEmpty()) {
                System.out.println("이미 상품이 있습니다.");
            }
            else {
                basketInfo.putAll(productInfo);
                basketInfo.put("COUNT", new BigDecimal("1"));
                basket.add(basketInfo);
            }
        }
    }

    /**
     * Br 출력
     */
    public void printBr() {
        System.out.println("--------------------------------------------------");
    }

    /**
     * 주문 완료 Process
     * @param basket
     */
    @Transactional
    public synchronized void orderCompleteProcess(List<Map<String, Object>> basket) {
        if (basket.size() > 0) {
            // 주문내역 출력
            printBasket(basket);
            // 주문, 지불 금액 출력
            printPrice(basket);
            // 결제
            pay(basket);
            // 장바구니 비우기
            emptyBasket(basket);
        }
        else {
            System.out.println("장바구니에 물품이 없습니다.");
        }
    }

    /**
     * 장바구니 비우기
     * @param basket
     */
    public void emptyBasket(List<Map<String, Object>> basket) {
        basket.clear();
    }

    /**
     * 결제
     * @param basket
     */
    @Transactional
    void pay(List<Map<String, Object>> basket) {
        for (Map<String, Object> basketItem:basket) {
            String productId = (String)basketItem.get("ID");
            BigDecimal productCount = (BigDecimal)basketItem.get("COUNT");
            Map<String, Object> latestProductInfo = productMapper.selectOneById(productId);
            String latestProductType = (String)latestProductInfo.get("KIND");

            if (StringUtils.equals(latestProductType, Kit)) {
                BigDecimal latestProductStock = (BigDecimal)latestProductInfo.get("STOCK");
                latestProductStock = latestProductStock.subtract(productCount);
                if (latestProductStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new SoldOutException("Pay - " + ErrorMessage.SoldOut);
                }
                else {
                    productMapper.updateStockById(productId, latestProductStock);
                }
            }
            // Klass면 테이블 업데이트 실행 안 함.
            else {
                // Nothing to do.
            }
        }
    }

    /**
     * 장바구니 출력
     * @param basket
     */
    public void printBasket(List<Map<String, Object>> basket) {
        System.out.println("주문 내역:");
        printBr();
        for(Map<String, Object> productInfo:basket) {
            System.out.println(String.format("%s - %d개",
                productInfo.get("PRODUCT_NAME"), ((BigDecimal)productInfo.get("COUNT")).intValue()));
        }
    }

    /**
     * 주문금액 계산
     * @param basket
     */
    public void printPrice(List<Map<String, Object>> basket) {
        BigDecimal productTotal = BigDecimal.ZERO;
        BigDecimal deliveryTotal = BigDecimal.ZERO;
        boolean isAnyKlass = false;
        for(Map<String, Object> productInfo:basket) {
            productTotal = productTotal.add(((BigDecimal)productInfo.get("PRICE")).multiply((BigDecimal)productInfo.get("COUNT")));
            if (! StringUtils.equals((String)productInfo.get("KIND"), Kit))
                isAnyKlass = true;
        }
        printBr();
        System.out.println("주문금액: " + productTotal);
        if (! isAnyKlass && (productTotal.compareTo(KitDeliveryChargeCondition) < 0))
        {
            System.out.println("배송비: " + DeliveryCharge);
            deliveryTotal = DeliveryCharge;
        }
        printBr();
        System.out.println("지불금액: " + productTotal.add(deliveryTotal));
        printBr();
    }
}
