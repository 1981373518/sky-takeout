package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrdersDetailMapper ordersDetailMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) { //只要 begin 没有超过 end，就继续
            dateList.add(begin);      // 1. 先加进去（防止漏掉第一天）
            begin = begin.plusDays(1);// 2. 再往后推一天
        }

        List<BigDecimal> amountList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            BigDecimal amount = ordersMapper.sumByMap(map);
            amount = amount == null ? new BigDecimal("0.0") : amount;
            amountList.add(amount);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(amountList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) { //只要 begin 没有超过 end，就继续
            dateList.add(begin);      // 1. 先加进去（防止漏掉第一天）
            begin = begin.plusDays(1);// 2. 再往后推一天
        }

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUser = (totalUser == null) ? 0 : totalUser;
            totalUserList.add(totalUser);

            Map<String, Object> mapNew = new HashMap<>();
            mapNew.put("begin", beginTime);
            mapNew.put("end", endTime);

            Integer newUser = userMapper.countByMap(mapNew);
            newUser = (newUser == null) ? 0 : newUser;
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.isAfter(end)) { //只要 begin 没有超过 end，就继续
            dateList.add(begin);      // 1. 先加进去（防止漏掉第一天）
            begin = begin.plusDays(1);// 2. 再往后推一天
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 每日订单数
            Map orderCountMap = new HashMap();
            orderCountMap.put("begin",beginTime);
            orderCountMap.put("end",endTime);
            Integer orderCount = ordersMapper.countByMap(orderCountMap);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);

            // 每日有效订单数
            Map validCountMap = new HashMap();
            validCountMap.put("begin",beginTime);
            validCountMap.put("end",endTime);
            validCountMap.put("status",Orders.COMPLETED);
            Integer validCount = ordersMapper.countByMap(validCountMap);
            validCount = validCount == null ? 0 : validCount;
            validCountList.add(validCount);
        }

        // stream流计算总和，比再去查一次数据库更高效，也避免了逻辑不一致
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validCountList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        Map map = new HashMap();
        map.put("begin" , LocalDateTime.of(begin,LocalTime.MIN));
        map.put("end", LocalDateTime.of(end,LocalTime.MAX));
        map.put("status",Orders.COMPLETED);
        List<GoodsSalesDTO> list = ordersDetailMapper.getSalesTop10(map);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }
}
