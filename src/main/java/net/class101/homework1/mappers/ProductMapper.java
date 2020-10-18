package net.class101.homework1.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    public List<Map<String, Object>> selectAllProduct();
    public Map<String, Object> selectOneById(@Param("id") String id);
    public void updateStockById(@Param("id") String id, @Param("stock") BigDecimal stock);
}
