package org.chat.supportpilot.Tools;

import org.chat.supportpilot.Service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

/// This is the tool that will be called by the model to generate refundRequest

@Component
public class RefundTool {
    private static final Logger log = LoggerFactory.getLogger(RefundTool.class);
    private static final Pattern ORDER_ID = Pattern.compile("^ORD-\\d{1,10}$");
    private static final int max_reason = 300;
    private final RefundService refund;

    public RefundTool(RefundService refund){
        this.refund = refund;
    }

    @Tool(description = "Create a refund REQUEST for one of the current customer's delivered orders. "
            + "This does not refund any money: the customer must confirm the request separately. "
            + "Only call this after the customer has given both an order ID and a reason.")
    public String requestRefund(
            @ToolParam(description = "The order ID, like ORD-1001")String orderId,
            @ToolParam(description = "Short reason the customer gave for the refund") String reason, ToolContext tool){

        Object user = tool.getContext().get("userId");
        if(!(user instanceof String userId) || userId.isBlank()){
            return "The customer could not be identified";
        }

        String id = orderId == null ? "":orderId.trim().toUpperCase(Locale.ROOT);
        if(!ORDER_ID.matcher(id).matches()){
            return "This is not a valid order Id";
        }
        String cleaned = reason == null ? "":reason.replaceAll("\\p{Cntrl}", " ").trim();
        if(cleaned.isEmpty()){
            return "A reason is required";
        }
        if (cleaned.length() > max_reason) {
            cleaned = cleaned.substring(0, max_reason);
        }
        log.info("requestRefund called: orderId={}, reason={}", id, userId);
        return refund.requestRefund(userId, id, cleaned);
    }
}
