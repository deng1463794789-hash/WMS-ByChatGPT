package com.wms.modules.inventory.controller;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.common.annotation.AuditLog;
import com.wms.common.annotation.Idempotent;
import com.wms.common.api.ApiResponse;
import com.wms.modules.inventory.entity.OutboundItem;
import com.wms.modules.inventory.entity.OutboundRecord;
import com.wms.modules.inventory.service.OutboundService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbound")
public class OutboundController {

    private final OutboundService outboundService;

    public OutboundController(OutboundService outboundService) {
        this.outboundService = outboundService;
    }

    @GetMapping
    public ApiResponse<IPage<OutboundRecord>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                    @RequestParam(defaultValue = "10") long pageSize,
                                                    @RequestParam(required = false) String outboundNo,
                                                    @RequestParam(required = false) String type) {
        return ApiResponse.success(outboundService.pageRecords(pageNum, pageSize, outboundNo, type));
    }

    @GetMapping("/{id}")
    public ApiResponse<OutboundRecord> detail(@PathVariable Long id) {
        return ApiResponse.success(outboundService.getDetail(id));
    }

    @PostMapping
    @Idempotent(expire = 5)
    @AuditLog(value = "创建出库单", module = "库存管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> body) {
        OutboundRecord record = new OutboundRecord();
        record.setType((String) body.get("type"));
        record.setRemark((String) body.get("remark"));
        record.setOperator((String) body.get("operator"));
        if (body.containsKey("outboundTime") && body.get("outboundTime") != null) {
            record.setOutboundTime(java.time.LocalDateTime.parse(((String) body.get("outboundTime")).replace(" ", "T")));
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("items");
        List<OutboundItem> items = null;
        if (itemsRaw != null) {
            items = itemsRaw.stream().map(i -> {
                OutboundItem item = new OutboundItem();
                item.setProductId(i.get("productId") != null ? ((Number) i.get("productId")).longValue() : null);
                item.setQuantity(i.get("quantity") != null ? ((Number) i.get("quantity")).intValue() : 0);
                item.setRemark((String) i.get("remark"));
                return item;
            }).toList();
        }
        return ApiResponse.success(outboundService.createRecord(record, items));
    }
}
