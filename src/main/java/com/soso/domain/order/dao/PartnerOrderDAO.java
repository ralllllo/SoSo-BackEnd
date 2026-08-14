package com.soso.domain.order.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.soso.domain.order.dto.PartnerOrderDetailDTO;
import com.soso.domain.order.dto.PartnerOrderListDTO;


@Repository
public class PartnerOrderDAO {

    @Autowired
    private SqlSession session; 

    
    public List<PartnerOrderListDTO> selectOrderList(Long sellerSeq, String keyword, String status) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("sellerSeq", sellerSeq);
        params.put("keyword", keyword);
        params.put("status", status);
        return session.selectList("partnerOrder.selectOrderList", params);
    }

    
    public List<PartnerOrderDetailDTO> selectOrderDetail(Long orderSeq) {
        return session.selectList("partnerOrder.selectOrderDetail", orderSeq);
    }

    
    public void updateOrderStatus(Long orderSeq, String status) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("orderSeq", orderSeq);
        params.put("status", status);
        session.update("partnerOrder.updateOrderStatus", params);
    }

    
    public Long selectBuyerSeq(Long orderSeq) {
        return session.selectOne("partnerOrder.selectBuyerSeq", orderSeq);
    }

    public int selectTodayNewOrdersCount(Long sellerSeq) {
        return session.selectOne("partnerOrder.selectTodayNewOrdersCount", sellerSeq);
    }

    public int selectShippingOrdersCount(Long sellerSeq) {
        return session.selectOne("partnerOrder.selectShippingOrdersCount", sellerSeq);
    }

    public int selectWaitingPaymentsAmount(Long sellerSeq) {
        return session.selectOne("partnerOrder.selectWaitingPaymentsAmount", sellerSeq);
    }

    public List<java.util.Map<String, Object>> selectMonthlySales(Long sellerSeq) {
        return session.selectList("partnerOrder.selectMonthlySales", sellerSeq);
    }

    public List<java.util.Map<String, Object>> selectMonthlyCollections(Long sellerSeq) {
        return session.selectList("partnerOrder.selectMonthlyCollections", sellerSeq);
    }

    public List<java.util.Map<String, Object>> selectBusinessSalesSummary(Long sellerSeq) {
        return session.selectList("partnerOrder.selectBusinessSalesSummary", sellerSeq);
    }

    public List<java.util.Map<String, Object>> selectGroupOrders(Long sellerSeq) {
        return session.selectList("partnerOrder.selectGroupOrders", sellerSeq);
    }
}
