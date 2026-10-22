package org.chat.supportpilot.Tools;

import java.util.Locale;
import org.chat.supportpilot.Repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class OrderTool {
    private static final Logger log = LoggerFactory.getLogger(OrderTool.class);
    private static final Pattern order_Id = Pattern.compile("^ORD-\\d{1,10}$");

    private final OrderRepository order;

    public OrderTool(OrderRepository order){
        this.order = order;
    }

    @Tool(description= "Look up the status of one of the current customer's orders "
    +"by order ID, for example ORD-1001. Use this whenever the customer asks about an order.")
    public String getOrderStatus(@ToolParam(description="The order ID, like ORD-1001")String orderId, ToolContext tool){
        if(tool.getContext().get("toolUsed") instanceof java.util.concurrent.atomic.AtomicBoolean used){
            /// This if block tells if the tool is used by the model or not for caching purposes
            used.set(true);
        }
        System.out.println("inside the getOrderStatus tool");
        Object user = tool.getContext().get("userId");
        if(!(user instanceof String userId) || userId.isBlank()){
            return "The customer could not be identified.";
        }
        String id = orderId == null ? "":orderId.trim().toUpperCase(Locale.ROOT);
        log.info("getOrderStatus called: orderId={}, userId={}",id,userId);

        if(!order_Id.matcher(id).matches()){
            return "The passed order Id is not valid orderId";
        }
        return order.findByIdAndUserId(id,userId)
                .map(o->"order: "+o.getId()+", status: "+o.getStatus()
                +", total "+o.getTotal()+", placed on "+o.getPlacedOn())
                .orElse("No order found for the passed order Id:-"+id);
    }
}
