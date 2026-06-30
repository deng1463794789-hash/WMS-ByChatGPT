package com.wms.modules.inventory.controller;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.common.annotation.AuditLog;
import com.wms.common.annotation.Idempotent;
import com.wms.common.api.ApiResponse;
import com.wms.modules.inventory.entity.InboundItem;
import com.wms.modules.inventory.entity.InboundRecord;
import com.wms.modules.inventory.service.InboundService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundService inboundService;

    public InboundController(InboundService inboundService) {
        this.inboundService = inboundService;
    }

    @GetMapping
    public ApiResponse<IPage<InboundRecord>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                   @RequestParam(defaultValue = "10") long pageSize,
                                                   @RequestParam(required = false) String inboundNo,
                                                   @RequestParam(required = false) String type) {
        return ApiResponse.success(inboundService.pageRecords(pageNum, pageSize, inboundNo, type));
    }

    @GetMapping("/{id}")
    public ApiResponse<InboundRecord> detail(@PathVariable Long id) {
        return ApiResponse.success(inboundService.getDetail(id));
    }

    @PostMapping
    @Idempotent(expire = 5)
    @AuditLog(value = "创建入库单", module = "库存管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> body) {
        InboundRecord record = new InboundRecord();
        record.setType((String) body.get("type"));
        record.setRemark((String) body.get("remark"));
        record.setOperator((String) body.get("operator"));
        if (body.containsKey("inboundTime") && body.get("inboundTime") != null) {
            record.setInboundTime(java.time.LocalDateTime.parse(((String) body.get("inboundTime")).replace(" ", "T")));
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) body.get("items");
        List<InboundItem> items = null;
        if (itemsRaw != null) {
            items = itemsRaw.stream().map(i -> {
                InboundItem item = new InboundItem();
                item.setProductId(i.get("productId") != null ? ((Number) i.get("productId")).longValue() : null);
                item.setQuantity(i.get("quantity") != null ? ((Number) i.get("quantity")).intValue() : 0);
                item.setRemark((String) i.get("remark"));
                return item;
            }).toList();
        }
        return ApiResponse.success(inboundService.createRecord(record, items));
    }
}
