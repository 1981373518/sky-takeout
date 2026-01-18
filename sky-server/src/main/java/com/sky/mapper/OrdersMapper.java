package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    void insert(Orders orders);

    @Select("select * from orders where number = #{number}")
    Orders getByNumber(@Param("number") String outTradeNo);

    void update(Orders orders);


    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("time") LocalDateTime time);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByDeliveryStatus(@Param("status") Integer status,@Param("time") LocalDateTime time);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(@Param("status") Integer toBeConfirmed);


    BigDecimal sumByMap(Map map);


    Integer countByMap(Map Map);
}

