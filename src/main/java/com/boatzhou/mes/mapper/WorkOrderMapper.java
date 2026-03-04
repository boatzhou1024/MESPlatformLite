package com.boatzhou.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boatzhou.mes.dto.report.WorkOrderReportItem;
import com.boatzhou.mes.entity.WorkOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 工单 Mapper。
 */
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 按日期和设备类型聚合工单数据。
     */
    @Select("""
            select date(w.created_at) as statDate,
                   coalesce(d.device_type, 'UNKNOWN') as deviceType,
                   count(1) as totalCount,
                   sum(case when w.status = 'COMPLETED' then 1 else 0 end) as completedCount
            from work_orders w
            left join devices d on d.id = w.device_id
            where date(w.created_at) between #{startDate} and #{endDate}
              and (#{deviceType} is null or #{deviceType} = '' or d.device_type = #{deviceType})
            group by date(w.created_at), d.device_type
            order by statDate asc
            """)
    List<WorkOrderReportItem> selectWorkOrderReport(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("deviceType") String deviceType);
}
