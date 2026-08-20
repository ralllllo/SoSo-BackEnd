package com.soso.domain.account.dao;

import com.soso.domain.account.dto.AccountRelationRequestDto;
import com.soso.domain.account.dto.AccountRelationResponseDto;
import com.soso.domain.account.dto.AccountSearchResponseDto;
import com.soso.domain.account.dto.ItemResponseDto;
import com.soso.domain.account.dto.PartnerDetailDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class AccountDAO {

    @Autowired
    private SqlSessionTemplate mybatis;

    private static final String NAMESPACE = "com.soso.domain.account.dao.AccountDAO";

    
    public List<AccountSearchResponseDto> searchPartnerStores(String searchTerm) {
        return mybatis.selectList(NAMESPACE + ".searchPartnerStores", searchTerm);
    }

    
    public List<AccountSearchResponseDto> getAllPartnerStores(String searchTerm, String city, String district) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("searchTerm", searchTerm);
        params.put("city", city);
        params.put("district", district);
        return mybatis.selectList(NAMESPACE + ".getAllPartnerStores", params);
    }

    
    public int insertPartnerRelation(AccountRelationRequestDto relationDto) {
        return mybatis.insert(NAMESPACE + ".insertPartnerRelation", relationDto);
    }

    
    public List<AccountRelationResponseDto> getPartnerRelationsByBusinessSeq(int businessSeq, String searchTerm, String city, String district) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("businessSeq", businessSeq);
        params.put("searchTerm", searchTerm);
        params.put("city", city);
        params.put("district", district);
        return mybatis.selectList(NAMESPACE + ".getPartnerRelationsByBusinessSeq", params);
    }

    
    public List<ItemResponseDto> getItemsByPartnerSeq(int partnerSeq) {
        return mybatis.selectList(NAMESPACE + ".getItemsByPartnerSeq", partnerSeq);
    }

    

    
    public int deletePartnerRelation(int relationSeq) {
        return mybatis.delete(NAMESPACE + ".deletePartnerRelation", relationSeq);
    }

    
    public Integer getFirstStoreSeqByUserSeq(int userSeq) {
        return mybatis.selectOne(NAMESPACE + ".getFirstStoreSeqByUserSeq", userSeq);
    }

    
 
    public List<AccountSearchResponseDto> myPartners(Long storeSeq) {
        return mybatis.selectList(NAMESPACE + ".myPartners", storeSeq);
    }

    
    public AccountSearchResponseDto getPartnerDetail(int partnerSeq) {
        return mybatis.selectOne(NAMESPACE + ".getPartnerDetail", partnerSeq);

    }
}
